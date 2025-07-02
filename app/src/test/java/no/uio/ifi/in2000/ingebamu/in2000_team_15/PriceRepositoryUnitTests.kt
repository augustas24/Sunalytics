package no.uio.ifi.in2000.ingebamu.in2000_team_15

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.price.PriceDataSource
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.price.PriceRepository
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.price.PriceData
import org.junit.Test

import org.junit.Assert.*
import kotlin.test.assertFailsWith


class PriceRepositoryUnitTests {

    private val mockDataSource = mockk<PriceDataSource>()
    private val repository = PriceRepository(mockDataSource)

    private val lat = 59.9
    private val long = 10.7


    // test happy-path
    @Test
    fun `getPriceData returns correct average per month`() = runTest {

        // arrange
        val mockData = listOf(
            PriceData(
                nokPerKwh = 1.0,
                eurPerKwh = 0.1,
                exr = 10.0,
                timeStart = "2025-04-01T00:00:00Z",
                timeEnd = "2025-04-01T01:00:00Z"
            ),
            PriceData(
                nokPerKwh = 3.0,
                eurPerKwh = 0.3,
                exr = 10.0,
                timeStart = "2025-04-02T00:00:00Z",
                timeEnd = "2025-04-02T01:00:00Z"
            ),
            PriceData(
                nokPerKwh = 2.0,
                eurPerKwh = 0.2,
                exr = 10.0,
                timeStart = "2025-05-01T00:00:00Z",
                timeEnd = "2025-05-01T01:00:00Z"
            )
        )

        coEvery { mockDataSource.fetchPriceData(lat, long) } returns mockData

        // act
        val result = repository.getPriceData(lat, long)

        // assert
        val expected = listOf(
            4 to 2.0,
            5 to 2.0
        )
        assertEquals(expected, result)
    }

    // test edge case
    @Test
    fun `getPriceData handles empty list`() = runTest {
        coEvery { mockDataSource.fetchPriceData(lat, long) } returns emptyList()

        val result = repository.getPriceData(lat, long)

        assertTrue(result.isEmpty())
    }

    // test expected error
    @Test
    fun `getPriceData throws exception on wrong timestamp`() = runTest {
        val badData = listOf(
            PriceData(
                nokPerKwh = 1.0,
                eurPerKwh = 0.1,
                exr = 10.0,
                timeStart = "wrong-date",
                timeEnd = "2025-04-01T01:00:00Z"
            )
        )
        coEvery { mockDataSource.fetchPriceData(lat, long) } returns badData

        assertFailsWith<NumberFormatException> {
            repository.getPriceData(lat, long)
        }
    }

    // test if values (nokPerKwH) are null or negative
    @Test
    fun `getPriceData is sorted and grouped correctly`() = runTest {

        val mockData = listOf(
            PriceData(
                nokPerKwh = 1.0,
                eurPerKwh = 0.1,
                exr = 10.0,
                timeStart = "2025-03-01T00:00:00Z",
                timeEnd = "2025-03-01T01:00:00Z"
            ),
            PriceData(
                nokPerKwh = 3.0,
                eurPerKwh = 0.3,
                exr = 10.0,
                timeStart = "2025-01-02T00:00:00Z",
                timeEnd = "2025-01-02T01:00:00Z"
            ),
            PriceData(
                nokPerKwh = 2.0,
                eurPerKwh = 0.2,
                exr = 10.0,
                timeStart = "2025-04-01T00:00:00Z",
                timeEnd = "2025-04-01T01:00:00Z"
            )
        )
        coEvery { mockDataSource.fetchPriceData(lat, long) } returns mockData

        val result = repository.getPriceData(lat, long)

        val expected = listOf(
            1 to 3.0,
            3 to 1.0,
            4 to 2.0
        )
        assertEquals(expected, result)
    }
}