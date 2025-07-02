package no.uio.ifi.in2000.ingebamu.in2000_team_15.data.solar

import android.util.Log
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.room.WeatherData
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.frost.FrostResponse
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.frost.SourceResponse
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

enum class DaylightZones(private val daylightHours: List<Pair<Int, Double>>) {
    SOUTH(listOf(
        Pair(1, 6.9), Pair(2, 9.08), Pair(3, 11.86), Pair(4, 14.62),
        Pair(5, 17.19), Pair(6, 18.68), Pair(7, 17.89), Pair(8, 15.52),
        Pair(9, 12.83), Pair(10, 10.1), Pair(11, 7.55), Pair(12, 6.06)
    )),
    MIDDLE(listOf(
        Pair(1, 5.02), Pair(2, 8.37), Pair(3, 11.81), Pair(4, 15.37),
        Pair(5, 19.1), Pair(6, 22.27), Pair(7, 20.38), Pair(8, 16.59),
        Pair(9, 13.04), Pair(10, 9.55), Pair(11, 6.03), Pair(12, 3.55)
    )),
    NORTH(listOf(
        Pair(1, 1.8), Pair(2, 7.39), Pair(3, 11.76), Pair(4, 16.25),
        Pair(5, 22.17), Pair(6, 24.0), Pair(7, 23.59), Pair(8, 17.95),
        Pair(9, 13.28), Pair(10, 8.93), Pair(11, 3.62), Pair(12, 0.0)
    ));

    fun getHoursForMonth(month: Int): Double {
        return daylightHours.first { it.first == month }.second
    }
}

class SolarRepository @Inject constructor(
    private val solarDatasource: SolarDataSource,
    private val weatherDataSource: WeatherDataSource,
) {
    private val interval = "P1M"

    private val monthString : List<String> = listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")

    // Gets monthly kWh from PVGIS (always called when user wants estimate)
    suspend fun getMonthlySolarProduction(lat: Double, lon: Double, roofArea: Double): List<Pair<Int, Double>> {
        val result = solarDatasource.fetchSolarData(lat, lon, roofArea)

        if (result == null) {
            Log.w("SolarRepository", "Failed to fetch solar data. Returning empty list.")
            return emptyList()
        }

        return result.outputs.monthly.fixed.map { month ->
            month.month to month.Em
        }
    }

    // Gets monthly radiation (influx) from PGVIS
    private suspend fun getMonthlyRadiation(lat: Double, lon: Double): List<Pair<Int, Double>> {
        val result = solarDatasource.fetchRadiationData(lat, lon)

        if (result == null) {
            Log.w("SolarRepository", "Failed to fetch radiation data. Returning empty list.")
            return emptyList()
        }

        return result.outputs.monthly
            .groupBy { it.month }
            .map { (month, entries) ->
                val avg = entries.map { it.irradiation }.average()
                month to avg
            }
            .sortedBy { it.first }
    }

    // Calculates monthly kWh based on radiation (influx) from PVGIS and weather data from Frost
    // Called when Frost has data for temperature, snow and cloud
    suspend fun calculateMonthlySolarProduction(lat: Double, lon: Double, weatherData: List<WeatherData>, roofArea: String): List<Pair<Int, Double>> {
        val radiation = getMonthlyRadiation(lat, lon)
        val weatherDataSorted = weatherData.sortedBy { it.timeReference }
        return radiation.mapIndexed { index, pair ->
            val month = pair.first
            val flux = pair.second
            val weather = weatherDataSorted[index]
            val daylightHours = getDaylightHoursForLat(lat, month)

            Log.d("test", weatherDataSorted.toString())

            // Check in HomeViewModel ensures that these variables are not null
            val air = weather.airTemperature ?: 0.0
            val snow = weather.snow ?: 0.0
            val cloud = weather.cloud ?: 0.0

            if (weatherData.all { it.airTemperature == null || it.snow == null || it.cloud == null })
            {return emptyList()
            }
            val airAdjustment = 1 - maxOf(0.0, air.minus(25)) * 0.004
            val snowAdjustment = 1 - minOf(1.0, snow/100)
            val cloudAdjustment = 1 - (cloud/8 * 0.75)
            val production = flux * 30 * daylightHours * roofArea.toDouble() / 1000 * airAdjustment * snowAdjustment * cloudAdjustment

            month to production
        }
    }

    private fun getDaylightHoursForLat(lat: Double, month:Int): Double {
        val daylightZone = when {
            lat < 62 -> DaylightZones.SOUTH
            lat > 68 -> DaylightZones.NORTH
            else -> DaylightZones.MIDDLE
        }
        return daylightZone.getHoursForMonth(month)
    }

    suspend fun getCompleteObservationDataWithFallback(
        lat: String,
        lon: String,
        REMOVETHIS: String
    ): List<WeatherData> {

        val coordinate = "$lat, $lon"

        //Time is fetched and stored in one variable, so all sections of this method use the same timestamp in their respective API-calls
        val currentTime = getTimeReference()

        //Checks if the data for this coordinate already exists in the database and if it was fetched one month ago or later
        if (weatherDataSource.isRowInDatabase(coordinate, "coordinate") && !isDataTooOld(coordinate, "coordinate", currentTime) ) {
            println("Found station in database that is not older than a month. Returning data for this coordinate")
            return weatherDataSource.getAllRowsWithThisVariable(coordinate, "coordinate")
        }
        else {
            println("Either found no coordinate in database or data was too old. Fetching stationID")

            //Does a source-search to find a weather station that corresponds to our chosen parameters
            val sourceResponse = getDataSourceSource(lat, lon, currentTime, interval)

            val stationID = sourceResponse?.data?.getOrNull(0)?.id ?: return emptyList()

            val stationName = sourceResponse.data.first().name

            //Checks if we already have data for this weather station already. If this is the case, we return data for it to prevent
            //doing another API-call.
            if (weatherDataSource.isRowInDatabase(stationID, "stationID") && !isDataTooOld(stationID, "stationID", currentTime)){
                println("Found stationID in database. Returning data for this weather station")
                return weatherDataSource.getAllRowsWithThisVariable(stationID, "stationID")
            }

            println("Either found no station in database or data was too old. Fetching observation data")

            //Fetch fresh observation data and insert only if it's valid
            getDataSourceObservation(stationID, stationName, interval, coordinate, currentTime)

            return weatherDataSource.getAllRowsWithThisVariable(stationID, "stationID")
        }
    }

    //Does a source-search to get info about weather station ID number, distance from coordinate, etc. from the remote database
    suspend fun getDataSourceSource(
        lat: String,
        lon: String,
        currentTime : String,
        interval : String
    ): SourceResponse? {
        println("Calling fetchSourceData for lat=$lat, lon=$lon, currentTime=$currentTime, interval=$interval")
        val sourceResponse = weatherDataSource.fetchSourceData(lat, lon, currentTime, interval)
        println("Fetched sourceResponse: $sourceResponse")  // We log the source response to validate the result of our fetch
        return sourceResponse
    }

    private suspend fun getDataSourceObservation(
        stationID: String,
        stationName: String,
        interval : String,
        coordinate: String,
        currentTime : String
    ) {

        //We do an observation-search with this combination of parameters
        val response = weatherDataSource.fetchObservationData(currentTime, stationID, interval)

        println("Fetched FrostResponse: $response")

        //Afterwards, we preprocess the response that we get from the method above and add relevant info into our local database
        retrieveDataFromResponse(stationID, response, coordinate, stationName, currentTime)
    }

    private suspend fun retrieveDataFromResponse(
        stationID : String,
        response: FrostResponse?,
        coordinate: String,
        stationName: String,
        currentTime : String
    ) {

        // This map contains  weather observation data for each month of the year. First, the `data` list in the FrostResponse is grouped
        // by the month extracted from each item's `referenceTime` (as a two-character string). Then, for each month, we find the average
        // value for each type of weather observation (e.g., air temperature, wind). The result is a list of Doubles, where each
        // Double-object contains the weather phenomenon’s ID and the the rounded average value (as a string).
        val summarizedValues = response?.data
            ?.groupBy { it.referenceTime.substring(5,7) }
            ?.mapValues { (_, items) ->
                items.first().observations.mapIndexed { index, observation ->
                    val monthlyValue = items.first().observations[index].value
                    Pair(
                        observation.elementId,
                        BigDecimal(monthlyValue).setScale(2, RoundingMode.UP).toDouble().toString()
                    )
                }
        }

        //Adds data from the FrostResponse into our local database. We only add values for weather phenomena that had existing data in
        //the FrostResponse
        monthString.forEach { month ->

            val airValue = summarizedValues?.get(month)?.filter { it.first.startsWith("mean(air_temperature") }
            val snowValue = summarizedValues?.get(month)?.filter { it.first.startsWith("mean(surface_snow_thickness") }
            val cloudValue = summarizedValues?.get(month)?.filter { it.first.startsWith("mean(cloud_area_fraction") }

            weatherDataSource.insertNewStationData(
                    stationID = stationID,
                    timeReference = month,
                    airTemperature = airValue?.firstOrNull()?.second,
                    snow = snowValue?.firstOrNull()?.second,
                    cloud = cloudValue?.firstOrNull()?.second,
                    stationName = stationName,
                    timeOfFetch = currentTime.substring(11,21),
                    coordinate = coordinate
            )

        }

    }

    private suspend fun isDataTooOld(column : String, columnString : String, currentTime : String): Boolean{
        //Convert time variable from database and currentTime-variable to formats we can compare.
        val ageFromDatabase= weatherDataSource.getTimeOfFetchwithVariable(column, columnString).first().timeOfFetch?.split('-') ?: return true
        val timeOfFetch = LocalDateTime.of(ageFromDatabase[0].toInt(), ageFromDatabase[1].toInt(), ageFromDatabase[2].toInt(), 0, 0)
        val timeSubstring= currentTime.substring(11,21).split('-')
        val timeCurrently = LocalDateTime.of(timeSubstring[0].toInt(), timeSubstring[1].toInt(), timeSubstring[2].toInt(), 0 , 0)

        //Checks if the time we fetched data for this coordinate or stationID is older than a month. If this is the case, we replace it with
        //new data
        if (timeOfFetch.plusMonths(1).isBefore(timeCurrently)){
            weatherDataSource.deleteOldStationData(column, columnString) //Deletes the old data for this coordinate in the database to make room
            println("DATA IS TOO OLD. FETCHING NEW DATA TO REPLACE IT")
            return true
        }
        else {
            println("DATA IS RECENT ENOUGH, RETURNING DATA FOR THIS COORDINATE/STATION-ID")
            return false
        }
    }

    //Gets two timestamps: the first one for a month ago and the second for one year ago from the first timestamp
    fun getTimeReference(): String {
        val now = LocalDateTime.now().minusMonths(1).truncatedTo(ChronoUnit.DAYS)
        val oneYearAgo = now.minusYears(1).truncatedTo(ChronoUnit.DAYS)
        return "${oneYearAgo.toLocalDate()}/${now.toLocalDate()}"
    }

}
