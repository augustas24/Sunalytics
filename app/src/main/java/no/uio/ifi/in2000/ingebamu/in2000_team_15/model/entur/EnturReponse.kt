package no.uio.ifi.in2000.ingebamu.in2000_team_15.model.entur

import com.google.gson.annotations.SerializedName

data class EnturResponse(
    val features: List<Feature>,
    @SerializedName("bbox")
    val boundingBox: List<Double>,
) {
    data class Feature(
        val geometry: Geometry,
        val properties: Properties,
        val type: String
    )

    data class Geometry(
        val coordinates: List<Double>,
        val type: String
    )

    // Many of these may be unnecessary to us. We can remove them later if that's the case
    data class Properties(
        val id: String,
        val gid: String,
        val layer: String,
        val source: String,
        @SerializedName("source_id")
        val sourceId: String,
        val name: String,
        @SerializedName("housenumber")
        val houseNumber: String,
        val street: String,
        @SerializedName("postalcode")
        val postalCode: String?,
        val confidence: Double, // Only used in reverse geocoding
        val distance: Double, // Only used in reverse geocoding
        val accuracy: String,
        @SerializedName("country_a")
        val countryAbbreviation: String,
        val county: String, // "Fylke"
        @SerializedName("county_gid")
        val countyGid: String,
        @SerializedName("locality")
        val municipality: String?, // "Kommune"
        @SerializedName("locality_gid")
        val municipalityGid: String,
        val borough: String, // Seems to correspond to "bydel"
        @SerializedName("borough_gid")
        val boroughGid: String,
        val label: String,
        val category: List<String>,
        @SerializedName("tariff_zones")
        val tariffZones: List<String>
    )
}