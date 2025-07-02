package no.uio.ifi.in2000.ingebamu.in2000_team_15.data.price

import androidx.annotation.VisibleForTesting
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.price.PriceData
import java.net.UnknownHostException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class PriceDataSource @Inject constructor(
    private val ktorHttpClient: HttpClient,
    ){
    private val baseUrl: String = "https://www.hvakosterstrommen.no/api"
    private val endpoint: String = "/v1/prices"
    private val outputFormat: String = ".json"

    suspend fun fetchPriceData(lat: Double, lon: Double) : List<PriceData> {
        val date = LocalDate.now()
        val dates = generateDates(date)

        val formatter = DateTimeFormatter.ofPattern("/yyyy/MM-dd")
        val formattedDates = dates.map { it.format(formatter) }

        val responses = mutableListOf<PriceData>()
        var priceArea = estimatePriceZone(lat, lon)
        if (priceArea == "Unknown") priceArea = "NO1"
        formattedDates.forEach {
            try {
                val fullUrl = baseUrl + endpoint + it + "_" + priceArea + outputFormat
                val response: HttpResponse = ktorHttpClient.get(fullUrl)
                if (response.status.value == 200) responses.addAll(response.body())
            } catch (_: UnknownHostException) { }
        }
        return responses
    }

    private fun estimatePriceZone(lat: Double, lon: Double): String {
        return when {
            lat >= 66.5 -> "NO4" // North
            lat >= 63.0 && lat < 66.5 -> "NO3" // Mid
            lat >= 60.0 && lon < 8 -> "NO5" // West
            lat >= 59.0 && lat < 61.5 && lon < 9.5 -> "NO2" // South West
            lat >= 59.0 && lon >= 9.0 -> "NO1" // South East
            else -> "Unknown"
        }
    }

    @VisibleForTesting
     internal fun generateDates(date: LocalDate): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()

        var currentDate = if (date.dayOfMonth >= 15) {
            date.withDayOfMonth(15)
        } else {
            date.minusMonths(1).withDayOfMonth(15)
        }

        for (i in 1 .. 12) {
            dates.add(currentDate)
            currentDate = currentDate.minusMonths(1)
        }

        return dates.sortedBy { it.monthValue }
    }
}
