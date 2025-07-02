package no.uio.ifi.in2000.ingebamu.in2000_team_15

import androidx.sqlite.db.SimpleSQLiteQuery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.room.WeatherDao
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DatabaseUnitTest {

    private val mockDao = mockk<WeatherDao>()
    private val queryString = "SELECT EXISTS (SELECT 1 FROM WeatherData WHERE stationID == ?)"

    @Test
    fun `doesStationIdExist returns true`() {

        // Arrange
        val stationName = "XYZ"
        val fullQuery = SimpleSQLiteQuery(queryString, arrayOf(stationName))
        every { mockDao.doesDataExist(fullQuery) } returns true

        // Act
        val result = mockDao.doesDataExist(fullQuery)

        // Assert
        assertTrue(result)

        // Verify - checks that the method was called with the correct parameter, normal to do when testing with mocking-tools
        verify { mockDao.doesDataExist(fullQuery) }
    }

    @Test
    fun `doesStationIdExist returns false`() {

        // Arrange
        val stationName = "ABC"
        val fullQuery = SimpleSQLiteQuery(queryString, arrayOf(stationName))
        every { mockDao.doesDataExist(fullQuery) } returns false

        // Act
        val result = mockDao.doesDataExist(fullQuery)

        // Assert
        assertFalse(result)

        // Verify
        verify { mockDao.doesDataExist(fullQuery) }
    }
}