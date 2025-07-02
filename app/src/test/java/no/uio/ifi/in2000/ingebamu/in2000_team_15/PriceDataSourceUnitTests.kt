package no.uio.ifi.in2000.ingebamu.in2000_team_15

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.gson.gson
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.price.PriceDataSource
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class PriceDataSourceUnitTests {

    private val lat = 59.9
    private val long = 10.7

    private val priceDataSource = PriceDataSource(mockk())

    @Test
    fun `generateDates returns 12 dates`() {
        val date = LocalDate.of(2025, 4, 20)
        val result = priceDataSource.generateDates(date)

        assertEquals(12, result.size)
    }

    @Test
    fun `generateDates returns 12 dates sorted`() {
        val date = LocalDate.of(2025, 4, 20)
        val result = priceDataSource.generateDates(date)
        val resultSortedByMonth = result.sortedBy{it.monthValue}

        assertEquals(resultSortedByMonth, result)
    }

    @Test
    fun `generateDates returns dates on the 15th every month`() {
        val date = LocalDate.of(2023, 1, 5)
        val result = priceDataSource.generateDates(date)

        assertTrue(result.all {it.dayOfMonth == 15})
    }

    @Test
    fun `fetchPriceData returns expected data from API`() = runTest {

        // arrange
        val mockJson = """
                 [
                    {
                    "NOK_per_kWh": 1.0,
                    "EUR_per_kWh": 0.1,
                    "EXR": 10.0,
                    "time_start": "2024-01-01T00:00:00Z",
                    "time_end": "2024-01-01T01:00:00Z"
                    }
                ]
            """.trimIndent()


        val mockEngine = MockEngine { _ ->
            respond(
                content = mockJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val mockHttpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                gson()
            }
        }

        val dataSource = PriceDataSource(mockHttpClient)

        // act
        val result = dataSource.fetchPriceData(lat, long)

        // assert
        assertEquals(12, result.size) // since fetchPriceData() calls function generateDates() expected output size is 12

        // check if deserialization is correct
        assertEquals(1.0, result[0].nokPerKwh)
        assertEquals(0.1, result[0].eurPerKwh)
        assertEquals("2024-01-01T00:00:00Z", result[0].timeStart)
        assertEquals("2024-01-01T01:00:00Z", result[0].timeEnd)

    }


}