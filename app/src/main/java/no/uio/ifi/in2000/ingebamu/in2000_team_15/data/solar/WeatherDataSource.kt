package no.uio.ifi.in2000.ingebamu.in2000_team_15.data.solar

import android.util.Base64
import androidx.sqlite.db.SimpleSQLiteQuery
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.room.WeatherDao
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.room.WeatherData
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.frost.FrostResponse
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.frost.SourceResponse
import javax.inject.Inject

class WeatherDataSource @Inject constructor(
    private val ktorHttpClient: HttpClient, // injects HttpClient via Hilt

    private val weatherDao : WeatherDao
){
    private val sourceURL = "https://frost.met.no/sources/v0.jsonld?" //Is used to do a Source-search

    private val observationURL = "https://frost.met.no/observations/v0.jsonld?" //Is used to do an Observation-search

    //Retrieve username and password from string.xml file later? R.string is only possible from a composable function.
    private val username = "444b66e8-8cd0-45a8-ad3a-a45334ee1648"
    private val password = "4b94de78-2613-4e08-b34f-a862ff08da08"

    //Generates basic auth header
    private val basicAuth = "Basic " + Base64.encodeToString("$username:$password".toByteArray(), Base64.NO_WRAP)

    suspend fun fetchSourceData(lat: String, lon: String, time: String, interval : String): SourceResponse? {
        val fullUrl = URLBuilder(sourceURL).apply {
            parameters.append("geometry", "nearest(POINT($lon $lat))") // The user's coordinates
            parameters.append("nearestmaxcount", "1")  // Only retrieve the closest weather station
            parameters.append("validtime", time) // Only retrieve a weather station with observations within this period
            parameters.append("elements",
                "mean(air_temperature ${interval})," +
                    "mean(surface_snow_thickness ${interval})," +
                    "mean(cloud_area_fraction ${interval})") // The weather elements we want data for
        }.build()

        println("Fetching data from URL: $fullUrl")  // Log the full URL

        return try {
            val response: HttpResponse = ktorHttpClient.get(fullUrl) {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(HttpHeaders.Authorization, basicAuth)
                }
            }

            if (response.status.value != 200) {
                println("API response error: ${response.status.value}")
                null
            } else {
                val body = response.body<SourceResponse>()
                println("Fetched response: $body")
                body
            }
        } catch (e: Exception) {
            println("Error fetching source data: $e")
            null
        }
    }

    suspend fun fetchObservationData(time: String, where: String, interval : String): FrostResponse? {
            val fullUrl = URLBuilder(observationURL).apply { //Creates the full URL based on parameters
                parameters.append("referencetime", time) //
                parameters.append("elements",
                    "mean(air_temperature ${interval})," +
                            "mean(surface_snow_thickness ${interval})," +
                            "mean(cloud_area_fraction ${interval})") //Names of elements we fetch data for
                parameters.append("sources", where) //The ID of the weather station that we are using
                parameters.append("levels", "default") //Is set to "default" to avoid uneccessary values in response
                parameters.append("timeoffsets", "default") //Is set to "default" to avoid uneccessary values in response
                parameters.append("fields", "referenceTime,elementId,value,unit") //Used to filter out uneccessary data we don't need
            }.build()

            return try { //Makes an API-call
                val response: HttpResponse = ktorHttpClient.get(fullUrl) {
                    headers {
                        append(HttpHeaders.Accept, "application/json")
                        append(HttpHeaders.Authorization, basicAuth)
                    }
                }

                if (response.status.value != 200) null else response.body()
            } catch (e: Exception) {
                null
            }
        }

    //Functions which exposes our Data Source to the internal room database
    suspend fun isRowInDatabase(column : String, columnString : String): Boolean {
        //We call dispatch threads in methods involving our local database, as work involving a database should not be done
        //on the main thread.
        return withContext(Dispatchers.IO) {
            println("isRowInDatabase")
            val queryString = "SELECT EXISTS (SELECT 1 FROM WeatherData WHERE $columnString == ?)"
            val fullQuery = SimpleSQLiteQuery(queryString, arrayOf(column))
            weatherDao.doesDataExist(fullQuery)
        }
    }

    suspend fun getAllRowsWithThisVariable(column : String, columnString : String): List<WeatherData> {
        return withContext(Dispatchers.IO) {
            println("getAllRowsWithThisVariable")
            val queryString = "SELECT * FROM WeatherData WHERE $columnString == ?"
            val fullQuery = SimpleSQLiteQuery(queryString, arrayOf(column))
            weatherDao.getDataRows(fullQuery)
        }
    }

    suspend fun getTimeOfFetchwithVariable(column : String, columnString : String): List<WeatherData>{
        return withContext(Dispatchers.IO) {
            println("getTimeOfFetchwithVariable")
            val queryString = "SELECT StationID, timeReference, timeOfFetch FROM WeatherData WHERE $columnString == ?"
            val fullQuery = SimpleSQLiteQuery(queryString, arrayOf(column))
            weatherDao.getTimeOfFetch(fullQuery)
        }
    }

    suspend fun deleteOldStationData(column : String, columnString : String) {
        withContext(Dispatchers.IO) {
            when (columnString) {
                "coordinate" -> withContext(Dispatchers.IO) {
                    weatherDao.deleteOldDataWithCoordinate(column)
                }

                "stationID" -> withContext(Dispatchers.IO) {
                    weatherDao.deleteOldDataWithStationID(column)
                }
            }
        }
    }

    suspend fun insertNewStationData(
        stationID : String,
        timeReference: String,
        airTemperature: String?,
        snow: String?,
        cloud: String?,
        stationName: String?,
        timeOfFetch : String?,
        coordinate : String?
    ) {
        withContext(Dispatchers.IO) {
            val weatherData = WeatherData(
                stationID = stationID,
                timeReference = timeReference,
                airTemperature = airTemperature?.toDouble(),
                snow = snow?.toDouble(),
                cloud = cloud?.toDouble(),
                stationName = stationName,
                timeOfFetch = timeOfFetch,
                coordinate = coordinate
            )
            weatherDao.insertNewObservationData(weatherData)
        }
    }

}


