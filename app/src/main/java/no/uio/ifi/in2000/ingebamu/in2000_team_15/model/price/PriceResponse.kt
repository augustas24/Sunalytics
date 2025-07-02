package no.uio.ifi.in2000.ingebamu.in2000_team_15.model.price

import com.google.gson.annotations.SerializedName

data class PriceData (
    @SerializedName("NOK_per_kWh")
    val nokPerKwh: Double,
    @SerializedName("EUR_per_kWh")
    val eurPerKwh: Double,
    @SerializedName("EXR")
    val exr: Double,
    @SerializedName("time_start")
    val timeStart: String,
    @SerializedName("time_end")
    val timeEnd: String
)