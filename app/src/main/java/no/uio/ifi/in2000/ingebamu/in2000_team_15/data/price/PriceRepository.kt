package no.uio.ifi.in2000.ingebamu.in2000_team_15.data.price

import javax.inject.Inject

class PriceRepository @Inject constructor(
    private val priceDataSource: PriceDataSource
){
    suspend fun getPriceData(lat: Double, lon: Double): List<Pair<Int, Double>> {
        val responses = priceDataSource.fetchPriceData(lat, lon)
        return responses
            .groupBy { filterTimeStamp(it.timeStart) } // assuming date is still sorted by months
            .map { it ->
                it.key to it.value.map { it.nokPerKwh }.average()
            }
            .sortedBy { it.first }
    }

    private fun filterTimeStamp(timeStamp: String): Int {
        return timeStamp.substring(5, 7).toInt()
    }

}