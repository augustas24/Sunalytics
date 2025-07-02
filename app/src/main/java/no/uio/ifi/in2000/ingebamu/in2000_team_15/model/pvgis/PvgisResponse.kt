package no.uio.ifi.in2000.ingebamu.in2000_team_15.model.pvgis

import com.google.gson.annotations.SerializedName

data class PvgisResponse (
    val inputs: Inputs,
    val outputs: Outputs,
    val meta: Meta,
)

data class Inputs (
    val location: Location,
    @SerializedName("meteo_data")
    val meteoData: MeteoData,
    @SerializedName("mounting_system")
    val mountingSystem: MountingSystem,
    @SerializedName("pv_module")
    val PvModule: PvModule,
    @SerializedName("economic_data")
    val economicData: EconomicData
)

data class Location(
    val latitude: Double,
    val longitude: Double,
    val elevation: Int
)

data class MeteoData(
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
    val horizonDb: String
)

data class MountingSystem(
    val fixed: Fixed,
    val type: String
)

data class Fixed(
    val slope: Slope,
    val azimuth: Azimuth
)

data class Slope(
    val value: Int,
    val optimal: Boolean
)

data class Azimuth(
    val value: Int,
    val optimal: Boolean
)

data class PvModule(
    val technology: String,
    @SerializedName("peak_power")
    val peakPower: Double,
    @SerializedName("system_loss")
    val systemLoss: Double
)

data class EconomicData(
    @SerializedName("system_cost")
    val systemCost: Double?,
    val interest: Double?,
    val lifetime: Int?
)

data class Outputs(
    val monthly: Monthly,
    val totals: Totals
)

data class Monthly(
    val fixed: List<Month>
)

data class Month(
    val month: Int,
    @SerializedName("E_d")
    val Ed: Double,
    @SerializedName("E_m")
    val Em: Double,
    @SerializedName("H(i)_d")
    val Hid: Double,
    @SerializedName("H(i)_m")
    val Him: Double,
    @SerializedName("SD_m")
    val SDm: Double
)

data class Totals(
    val fixed: TotalsFixed
)

data class TotalsFixed(
    @SerializedName("E_d")
    val Ed: Double,
    @SerializedName("E_m")
    val Em: Double,
    @SerializedName("E_y")
    val Ey: Double,
    @SerializedName("H(i)_d")
    val Hid: Double,
    @SerializedName("H(i)_m")
    val Him: Double,
    @SerializedName("H(i)_y")
    val Hiy: Double,
    @SerializedName("SD_m")
    val SDm: Double,
    @SerializedName("SD_y")
    val SDy: Double,
    @SerializedName("l_aoi")
    val laoi: Double,
    @SerializedName("l_spec")
    val lspec: String,
    @SerializedName("l_tg")
    val ltg: Double,
    @SerializedName("l_total")
    val lTotal: Double
)

data class Meta(
    val inputs: MetaInputs,
    val outputs: MetaOutputs
)

data class MetaInputs(
    val location: MetaLocation,
    @SerializedName("meteo_data")
    val meteoData: MetaMeteoData,
    @SerializedName("mounting_system")
    val mountingSystem: MetaMountingSystem,
    @SerializedName("pv_module")
    val PvModule: MetaPvModule,
    @SerializedName("economic_data")
    val economicData: MetaEconomicData
)

data class MetaLocation(
    val description: String,
    val variables: LocationVariables
)

data class LocationVariables(
    val latitude: DescriptionAndUnits,
    val longitude: DescriptionAndUnits,
    val elevation: DescriptionAndUnits
)

data class DescriptionAndUnits(
    val description: String,
    val units: String
)

data class MetaMeteoData(
    val description: String,
    val variables: MeteoDataVariables
)

data class MeteoDataVariables(
    @SerializedName("radioation_db")
    val radiationDb: Description,
    @SerializedName("meteo_db")
    val meteoDb: Description,
    @SerializedName("year_min")
    val yearMin: Description,
    @SerializedName("year_max")
    val yearMax: Description,
    @SerializedName("use_horizon")
    val useHorizon: Description,
    @SerializedName("horizon_db")
    val horizonDb: Description
)

data class Description(
    val description: String
)

data class MetaMountingSystem(
    val description: String,
    val choices: String,
    val fields: Fields
)

data class Fields(
    val slope: DescriptionAndUnits,
    val azimuth: DescriptionAndUnits
)

data class MetaPvModule(
    val description: String,
    val variables: PvModuleVariables
)

data class PvModuleVariables(
    val technology: Description,
    @SerializedName("peak_power")
    val peakPower: DescriptionAndUnits,
    @SerializedName("system_loss")
    val systemLoss: DescriptionAndUnits
)

data class MetaEconomicData(
    val description: String,
    val variables: EconomicVariables
)

data class EconomicVariables(
    @SerializedName("system_cost")
    val systemCost: DescriptionAndUnits,
    val interest: DescriptionAndUnits,
    val lifetime: DescriptionAndUnits
)

data class MetaOutputs(
    val monthly: MetaMonthly,
    val totals: MetalTotals
)

data class MetaMonthly(
    val type: String,
    val timestamp: String,
    val variables: MonthlyVariables
)

data class MonthlyVariables(
    @SerializedName("E_d")
    val Ed: DescriptionAndUnits,
    @SerializedName("E_m")
    val Em: DescriptionAndUnits,
    @SerializedName("H(i)_d")
    val Hid: DescriptionAndUnits,
    @SerializedName("H(i)_m")
    val Him: DescriptionAndUnits,
    @SerializedName("SD_m")
    val SDm: DescriptionAndUnits
)

data class MetalTotals(
    val type: String,
    val variables: TotalsVariables
)

data class TotalsVariables(
    @SerializedName("E_d")
    val Ed: DescriptionAndUnits,
    @SerializedName("E_m")
    val Em: DescriptionAndUnits,
    @SerializedName("E_y")
    val Ey: DescriptionAndUnits,
    @SerializedName("H(i)_d")
    val Hid: DescriptionAndUnits,
    @SerializedName("H(i)_m")
    val Him: DescriptionAndUnits,
    @SerializedName("H(i)_y")
    val Hiy: DescriptionAndUnits,
    @SerializedName("SD_m")
    val SDm: DescriptionAndUnits,
    @SerializedName("SD_y")
    val SDy: DescriptionAndUnits,
    @SerializedName("l_aoi")
    val laoi: DescriptionAndUnits,
    @SerializedName("l_spec")
    val lspec: DescriptionAndUnits,
    @SerializedName("l_tg")
    val ltg: DescriptionAndUnits,
    @SerializedName("l_total")
    val lTotal: DescriptionAndUnits
)













