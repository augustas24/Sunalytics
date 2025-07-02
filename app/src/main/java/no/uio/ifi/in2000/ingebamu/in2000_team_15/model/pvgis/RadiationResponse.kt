package no.uio.ifi.in2000.ingebamu.in2000_team_15.model.pvgis

import com.google.gson.annotations.SerializedName

data class RadiationResponse(
    val inputs: RadiationInputs,
    val outputs: RadiationOutputs,
    val meta: RadiationMeta
)

data class RadiationInputs(
    val location: RadiationLocation,
    @SerializedName("meteo_data")
    val meteoData: RadiationMeteoData,
    val plane: Plane
)

data class RadiationLocation(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double
)

data class RadiationMeteoData(
    @SerializedName("radiation_db")
    val radiationDb: String,
    @SerializedName("meteo_db")
    val meteoDb: String,
    @SerializedName("year_min")
    val yearMin: Int,
    @SerializedName("year_max")
    val yearMax: Int,
    @SerializedName("use_horizon")
    val useHorizon: Boolean,
    @SerializedName("horizon_db")
    val horizonDb: String?,
    @SerializedName("horizon_data")
    val horizonData: String
)

data class Plane(
    @SerializedName("fixed_horizontal")
    val fixedHorizontal: FixedHorizontal
)

data class FixedHorizontal(
    val slope: RadiationSlope,
    val azimuth: RadiationAzimuth
)

data class RadiationSlope(
    val value: Int,
    val optimal: String
)

data class RadiationAzimuth(
    val value: String,
    val optimal: String
)

data class RadiationOutputs(
    val monthly: List<MonthlyData>
)

data class MonthlyData(
    val year: Int,
    val month: Int,
    @SerializedName("H(h)_m")
    val irradiation: Double
)

data class RadiationMeta(
    val inputs: RadiationMetaInputs,
    val outputs: RadiationMetaOutputs
)

data class RadiationMetaInputs(
    val location: RadiationMetaLocation,
    @SerializedName("meteo_data")
    val meteoData: RadiationMetaMeteoData,
    val plane: MetaPlane
)

data class RadiationMetaLocation(
    val description: String,
    val variables: RadiationLocationVariables
)

data class RadiationLocationVariables(
    val latitude: MetaVariable,
    val longitude: MetaVariable,
    val elevation: MetaVariable
)

data class MetaVariable(
    val description: String,
    val units: String
)

data class RadiationMetaMeteoData(
    val description: String,
    val variables: MeteoVariables
)

data class MeteoVariables(
    @SerializedName("radiation_db")
    val radiationDb: MetaFieldDescription,
    @SerializedName("meteo_db")
    val meteoDb: MetaFieldDescription,
    @SerializedName("year_min")
    val yearMin: MetaFieldDescription,
    @SerializedName("year_max")
    val yearMax: MetaFieldDescription,
    @SerializedName("use_horizon")
    val useHorizon: MetaFieldDescription,
    @SerializedName("horizon_db")
    val horizonDb: MetaFieldDescription
)

data class MetaFieldDescription(
    val description: String
)

data class MetaPlane(
    val description: String,
    val fields: PlaneFields
)

data class PlaneFields(
    val slope: PlaneFieldDescription,
    val azimuth: PlaneFieldDescription
)

data class PlaneFieldDescription(
    val description: String,
    val units: String
)

data class RadiationMetaOutputs(
    val monthly: MonthlyOutput
)

data class MonthlyOutput(
    val type: String,
    val timestamp: String,
    val variables: RadiationMonthlyVariables
)

data class RadiationMonthlyVariables(
    @SerializedName("H(h)_m")
    val irradiationVariable: IrradiationVariable
)

data class IrradiationVariable(
    val description: String,
    val units: String
)
