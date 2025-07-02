package no.uio.ifi.in2000.ingebamu.in2000_team_15.data.geocoding

import com.mapbox.geojson.Point
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLBuilder
import io.ktor.http.encodedPath
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.mapbox.MapboxResponse
import java.net.UnknownHostException
import javax.inject.Inject

class MapboxDataSource @Inject constructor(
    private val ktorHttpClient: HttpClient
) {
    private val baseUrl = "https://api.mapbox.com/search/geocode/v6"
    private val accessToken = "pk.eyJ1IjoibGVvbmhlaGFzbm9zdHlsZSIsImEiOiJjbTg0ODcyNWYxcGRmMnFzYXFqNzVqaWRuIn0.V6Mh0flFdfXpRi6JDgAMeA"

    suspend fun geocodeReverse(point: Point, maxResults: Int = 5): MapboxResponse? {
        val endpoint = "/reverse"
        val fullUrl = URLBuilder(baseUrl).apply {
            encodedPath += endpoint
            parameters.append("longitude", point.longitude().toString())
            parameters.append("latitude", point.latitude().toString())
            parameters.append("access_token", accessToken)
            parameters.append("country", "no")
            parameters.append("language", "nb")
            parameters.append("limit", maxResults.toString())
        }.build()

        return try {
            val response: HttpResponse = ktorHttpClient.get(fullUrl)
            if (response.status.value != 200) null else response.body()
        } catch (e: UnknownHostException) {
            null
        }
    }
}