package no.uio.ifi.in2000.ingebamu.in2000_team_15.model.frost
import kotlinx.serialization.Serializable
import com.google.gson.annotations.SerializedName

@Serializable
data class SourceResponse(
    @SerializedName("apiVersion")
    val apiVersion: String,
    @SerializedName("@context")
    val context: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("currentItemCount")
    val currentItemCount: Int,
    @SerializedName("currentLink")
    val currentLink: String,
    @SerializedName("data")
    val `data`: List<SData>,
    @SerializedName("itemsPerPage")
    val itemsPerPage: Int,
    @SerializedName("license")
    val license: String,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("queryTime")
    val queryTime: Double,
    @SerializedName("totalItemCount")
    val totalItemCount: Int,
    @SerializedName("@type")
    val type: String
)

@Serializable
data class SData(
    @SerializedName("country")
    val country: String,
    @SerializedName("countryCode")
    val countryCode: String,
    @SerializedName("county")
    val county: String,
    @SerializedName("countyId")
    val countyId: Int,
    @SerializedName("distance")
    val distance: Double,
    @SerializedName("externalIds")
    val externalIds: List<String>,
    @SerializedName("geometry")
    val geometry: Geometry,
    @SerializedName("id")
    val id: String,
    @SerializedName("masl")
    val masl: Int,
    @SerializedName("municipality")
    val municipality: String,
    @SerializedName("municipalityId")
    val municipalityId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("ontologyId")
    val ontologyId: Int,
    @SerializedName("shortName")
    val shortName: String,
    @SerializedName("stationHolders")
    val stationHolders: List<String>,
    @SerializedName("@type")
    val type: String,
    @SerializedName("validFrom")
    val validFrom: String
)

@Serializable
data class Geometry(
    @SerializedName("coordinates")
    val coordinates: List<Double>,
    @SerializedName("nearest")
    val nearest: Boolean,
    @SerializedName("@type")
    val type: String
)



