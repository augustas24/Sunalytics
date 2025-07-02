package no.uio.ifi.in2000.ingebamu.in2000_team_15.data.solar

import android.util.Log
import androidx.annotation.VisibleForTesting
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLBuilder
import io.ktor.http.encodedPath
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.pvgis.PvgisResponse
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.pvgis.RadiationResponse
import java.net.UnknownHostException
import javax.inject.Inject

class SolarDataSource @Inject constructor(
    private val ktorHttpClient: HttpClient,
){

    private val baseUrl: String = "https://re.jrc.ec.europa.eu/api"
    private val outputFormat: String = "json"
    private val optimalAngles: String = "1"
    private val energyLossFactor: String = "15.0"

    suspend fun fetchSolarData(lat: Double, lon: Double, roofArea: Double) : PvgisResponse?{
        val fullUrl = URLBuilder(baseUrl).apply {
            encodedPath += "/v5_3/PVcalc"
            parameters.append("lat", lat.toString())
            parameters.append("lon", lon.toString())
            parameters.append("peakpower", calculatePeakPower(roofArea).toString()) // calculates nominal power of PV systems, depends on roof area
            parameters.append("outputformat", outputFormat)
            parameters.append("loss", energyLossFactor) // sum of system losses in percent
            parameters.append("optimalangles", optimalAngles) // optimal inclination and orientation angles
        }.build()

        val result: PvgisResponse? = try {
            val response: HttpResponse = ktorHttpClient.get(fullUrl)
            if (response.status.value != 200) null else response.body()
        } catch (e: UnknownHostException){
            Log.e("SolarDataSource", "Network error: Unable to fetch solar data", e)
            null
        }

        return result

    }

    /* Peak power is the maximum amount electricity (Watt) a solar panel can generate under ideal conditions.
    * Formula:
    * PeakPower = RoofArea * Solar irradiance on a clear day * Efficiency of the solar panel
    */
    companion object {
        @VisibleForTesting
        internal fun calculatePeakPower(roofArea: Double, solarIrradiance: Double = 1000.0, efficiency: Double = 0.20): Double {
            return roofArea * (solarIrradiance/1000) * efficiency // m^2 * (kW/m^2) * % = result is in kW
        }
    }


    suspend fun fetchRadiationData(lat: Double, lon: Double) : RadiationResponse? {
        val fullUrl = URLBuilder(baseUrl).apply {
            encodedPath += "/v5_3/MRcalc"
            parameters.append("lat", lat.toString())
            parameters.append("lon", lon.toString())
            parameters.append("outputformat", outputFormat)
            parameters.append("horirrad", "1")
        }.build()

        val result: RadiationResponse? = try {
            val response: HttpResponse = ktorHttpClient.get(fullUrl)
            if (response.status.value != 200) null else response.body()
        } catch (e: UnknownHostException){
            Log.e("SolarDataSource", "Network error: Unable to fetch radiation data", e)
            null
        }

        Log.d("RADIATION", "Response: $result")

        return result
    }
}