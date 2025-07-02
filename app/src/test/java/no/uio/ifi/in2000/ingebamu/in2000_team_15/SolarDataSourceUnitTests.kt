package no.uio.ifi.in2000.ingebamu.in2000_team_15

import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.solar.SolarDataSource
import org.junit.Test
import kotlin.test.assertEquals


class SolarDataSourceUnitTests {

    @Test
    fun `test calculatePeakPower with default values`(){
        val result = SolarDataSource.calculatePeakPower(50.0)
        val expected = 50.0 * (1000.0 / 1000) * 0.20

        assertEquals(expected, result)
    }

    @Test
    fun `test calculatePeakPower with custom solar irradiance`() {
        val result = SolarDataSource.calculatePeakPower(50.0, solarIrradiance = 800.0)
        val expected = 50.0 * (800.0 / 1000) * 0.20

        assertEquals(expected, result, 0.0001)
    }

    @Test
    fun `test calculatePeakPower with custom efficiency`() {
        val result = SolarDataSource.calculatePeakPower(50.0, efficiency = 0.15)
        val expected = 50.0 * (1000.0 / 1000) * 0.15

        assertEquals(expected, result, 0.0001)
    }

    @Test
    fun `test calculatePeakPower with custom roof area`() {
        val result = SolarDataSource.calculatePeakPower(1000.0)
        val expected = 1000.0 * (1000.0 / 1000) * 0.20

        assertEquals(expected, result, 0.0001)
    }
}
