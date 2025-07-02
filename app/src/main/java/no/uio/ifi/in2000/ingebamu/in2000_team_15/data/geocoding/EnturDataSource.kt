package no.uio.ifi.in2000.ingebamu.in2000_team_15.data.geocoding

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLBuilder
import io.ktor.http.encodedPath
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.entur.EnturResponse
import java.net.UnknownHostException
import javax.inject.Inject


class EnturDataSource @Inject constructor(
    private val ktorHttpClient: HttpClient
) {
    private val baseUrl: String = "https://api.entur.io/geocoder"

    suspend fun geocodeAutocomplete(text: String, maxResults: Int): EnturResponse? {
        val endpoint = "/v1/autocomplete"
        val fullUrl = URLBuilder(baseUrl).apply {
            encodedPath += endpoint
            parameters.append("text", text)
            parameters.append("size", maxResults.toString())
            parameters.append("lang", "no") // language = norwegian
        }.build()

        return try {
            val response: HttpResponse = ktorHttpClient.get(fullUrl)
            if (response.status.value != 200) null else response.body()
        } catch (e: UnknownHostException) {
            null
        }
    }
}