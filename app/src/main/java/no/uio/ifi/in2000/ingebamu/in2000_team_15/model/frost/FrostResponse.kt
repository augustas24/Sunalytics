package no.uio.ifi.in2000.ingebamu.in2000_team_15.model.frost

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class FrostResponse(
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
    var `data`: List<Data>,
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
data class Data(
    @SerializedName("observations")
    val observations: List<Observation>,
    @SerializedName("referenceTime")
    val referenceTime: String,
    @SerializedName("sourceId")
    val sourceId: String
)

@Serializable
data class Observation(
    @SerializedName("elementId")
    val elementId: String,
    @SerializedName("exposureCategory")
    val exposureCategory: String,
    @SerializedName("level")
    val level: Level,
    @SerializedName("performanceCategory")
    val performanceCategory: String,
    @SerializedName("qualityCode")
    val qualityCode: Int,
    @SerializedName("timeOffset")
    val timeOffset: String,
    @SerializedName("timeResolution")
    val timeResolution: String,
    @SerializedName("timeSeriesId")
    val timeSeriesId: Int,
    @SerializedName("unit")
    val unit: String,
    @SerializedName("value")
    var value: Double
)

@Serializable
data class Level(
    @SerializedName("levelType")
    val levelType: String,
    @SerializedName("unitId")
    val unit: String,
    @SerializedName("value")
    val value: Int
)



