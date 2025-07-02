package no.uio.ifi.in2000.ingebamu.in2000_team_15.model.mapbox

import com.google.gson.annotations.SerializedName

data class MapboxResponse(
    val features: List<Feature>
) {
    data class Feature(
        val properties: Properties
    )

    data class Properties(
        val name: String,
        @SerializedName("name_preferred")
        val namePreferred: String,
        @SerializedName("feature_type")
        val featureType: String,
        val coordinates: Coordinates,
        @SerializedName("place_formatted")
        val placeFormatted: String,
        @SerializedName("full_address")
        val fullAddress: String,
//        val context: Context // If this is needed, look here: https://docs.mapbox.com/api/search/geocoding/#the-context-object
    )

    data class Coordinates(
        val latitude: Double,
        val longitude: Double
    )
}