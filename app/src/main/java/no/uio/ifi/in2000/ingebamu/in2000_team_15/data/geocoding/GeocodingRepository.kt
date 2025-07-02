package no.uio.ifi.in2000.ingebamu.in2000_team_15.data.geocoding

import com.mapbox.geojson.Point
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.entur.EnturResponse
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.mapbox.MapboxResponse
import javax.inject.Inject

class GeocodingRepository @Inject constructor(
    private val enturDataSource: EnturDataSource,
    private val mapboxDataSource: MapboxDataSource
) {
    suspend fun getLocations(name: String, maxResults: Int = 5) =
        enturDataSource.geocodeAutocomplete(name, maxResults)?.toLocations() ?: listOf()

    suspend fun getNearestLocation(point: Point) =
        mapboxDataSource.geocodeReverse(point, 1)?.toLocations()?.firstOrNull()
}

data class Location(
    val name: String = "...",
    val point: Point = Point.fromLngLat(0.0, 0.0),
    val place: String = "..."
)

private fun EnturResponse.toLocations() = features
    .filter { feature ->
        feature.properties.municipality != null // Filters out places on Svalbard, which Frost has no data on
    }
    .map { feature ->
        Location(
            point = feature.geometry.coordinates.let {
                Point.fromLngLat(it[0], it[1])
            },
            name = feature.properties.name,
            place = buildString {
                feature.properties.postalCode?.let {
                    append(it)
                    append(" ")
                }
                append(feature.properties.municipality)
            }
        )
    }

private fun MapboxResponse.toLocations() = features
    .map { feature ->
        Location(
            point = feature.properties.coordinates.let {
                Point.fromLngLat(it.longitude, it.latitude)
            },
            name = feature.properties.name,
            place = feature.properties.placeFormatted
        )
    }