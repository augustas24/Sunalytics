package no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.weather

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import no.uio.ifi.in2000.ingebamu.in2000_team_15.R
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.room.WeatherData
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.network.NetworkStatus
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.home.HomeViewModel
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.home.InfoDialog
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.state.LoadingState
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.state.UiState
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.theme.CloudLine
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.theme.Error
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.theme.SaturatedYellow
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.theme.SnowLine
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.theme.SolarPanelBlue
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.theme.TempLine

enum class WeatherPhenomenon(
    val nameId: Int,
    val iconId: Int,
    val color: Color,
    val unitId: Int,
) {
    Temp(R.string.button_temp, R.drawable.temp_icon, TempLine, R.string.celsius),
    Cloud(R.string.button_cloud, R.drawable.cloud_icon, CloudLine, R.string.oktas),
    Snow(R.string.button_snow, R.drawable.snowflake_icon, SnowLine, R.string.centimeter)
}

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
) {
    // Collect the state for the weather data
    val weatherUiState by viewModel.weatherUiState.collectAsState()

    // Safe transformation of UiState into List
    val weatherView = (weatherUiState as? UiState.Success<List<WeatherData>>)?.data ?: emptyList()

    // Network Status
    val networkStatus by viewModel.networkStatus.collectAsState()
    val isNetworkChecked by viewModel.isNetworkChecked.collectAsState()

    // Check if internet is available and if the network status has been stabilized
    val hasInternet = networkStatus == NetworkStatus.Available && isNetworkChecked

    var selectedWeather by remember { mutableStateOf(WeatherPhenomenon.Temp) }
    var showInfo by remember { mutableStateOf(false) }

    // Content Description Labels
    val contentWeatherChart = stringResource(R.string.content_desc_weather_chart)


    Box(modifier = modifier) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
        ) {
            if (!hasInternet) {
                Text(
                    text = stringResource(R.string.error_no_internet),
                    color = Error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
            else {
                when (weatherUiState) {
                    is UiState.Loading -> {
                        LoadingState()
                    }

                    is UiState.Success -> {
                        if (weatherView.isNotEmpty() && weatherView.first().coordinate != "null") {
                            val tempValues = weatherView.mapNotNull {
                                it.airTemperature
                            }

                            val snowValues = weatherView.mapNotNull {
                                it.snow
                            }

                            val cloudValues = weatherView.mapNotNull {
                                it.cloud
                            }

                            println("WeatherScreen, Temp: $tempValues, Snow: $snowValues, Cloud: $cloudValues")

                            val values = when (selectedWeather) {
                                WeatherPhenomenon.Temp -> tempValues
                                WeatherPhenomenon.Cloud -> cloudValues
                                WeatherPhenomenon.Snow -> snowValues
                            }

                            ConstantInfoCard(
                                selectedWeather = selectedWeather,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SaturatedYellow)
                            )

                            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large) * 2))

                            WeatherChart(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .semantics { contentDescription = contentWeatherChart }
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SaturatedYellow)
                                    .padding(dimensionResource(R.dimen.padding_medium)),
                                weatherValues = values,
                                selectedWeather = selectedWeather,
                                onChartSwitcherClick = { selectedWeather = it },
                                onInfoButtonClick = { showInfo = true }
                            )
                        }
                    }

                    is UiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().weight(7f)
                        ) {
                            Column (
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ){
                                Image(
                                    painter = painterResource(R.drawable.dissapointed),
                                    contentDescription = stringResource(R.string.content_desc_dissapointed_expression),
                                )
                                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)))
                                Text(
                                    text = stringResource(R.string.error_loading_weather_data),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = SolarPanelBlue
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showInfo) InfoDialog(
        title = { Text(stringResource(R.string.weather_screen)) },
        text = {
            val weatherMetadata = weatherView.first().stationName!!
            OverlayPopup(weatherMetadata = weatherMetadata)
        },
        onDismissRequest = { showInfo = false }
    )
}

@Composable
fun ConstantInfoCard(
    selectedWeather: WeatherPhenomenon,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when (selectedWeather) {
                WeatherPhenomenon.Temp -> stringResource(R.string.textbox_temp)
                WeatherPhenomenon.Cloud -> stringResource(R.string.textbox_cloud)
                WeatherPhenomenon.Snow -> stringResource(R.string.textbox_snow)
            },
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium)),
            color = SolarPanelBlue
        )
    }
}


@Composable
fun WeatherChart(
    modifier: Modifier = Modifier,
    weatherValues: List<Double>,
    selectedWeather: WeatherPhenomenon,
    onChartSwitcherClick: (WeatherPhenomenon) -> Unit,
    onInfoButtonClick: () -> Unit
) {
    val months: List<String> = listOf(
        stringResource(R.string.month_january),
        stringResource(R.string.month_february),
        stringResource(R.string.month_march),
        stringResource(R.string.month_april),
        stringResource(R.string.month_may),
        stringResource(R.string.month_june),
        stringResource(R.string.month_july),
        stringResource(R.string.month_august),
        stringResource(R.string.month_september),
        stringResource(R.string.month_october),
        stringResource(R.string.month_november),
        stringResource(R.string.month_december)
    )

    // Content Description Labels
    val contentToggleSwitch = stringResource(R.string.content_desc_weather_toggle_switch)
    val unit = stringResource(selectedWeather.unitId)

    Box(modifier = modifier) {
        Column (modifier = Modifier.fillMaxSize()) {
            if (weatherValues.isNotEmpty()) LineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(7f),
                data = remember(weatherValues, selectedWeather) {
                    listOf(
                        Line(
                            label = "",
                            values = weatherValues,
                            color = SolidColor(selectedWeather.color),
                            firstGradientFillColor = selectedWeather.color.copy(alpha =.5f), //LineFillColor.copy(alpha = .5f),
                            secondGradientFillColor = Color.Transparent,
                            curvedEdges = false,
                        ),
                    )
                },
                gridProperties = GridProperties(
                    enabled = true,
                    xAxisProperties = GridProperties.AxisProperties(
                        enabled = false
                    ),
                    yAxisProperties = GridProperties.AxisProperties(
                        lineCount = 12
                    )
                ),
                labelProperties = LabelProperties(
                    enabled = true,
                    labels = months.take(weatherValues.size),
                    rotation = LabelProperties.Rotation()
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    contentBuilder = { "${it.format(1)} $unit" },
                ),
                labelHelperProperties = LabelHelperProperties(
                    enabled = false
                ),
                animationDelay = 0
            ) else Box(
                modifier = Modifier.fillMaxWidth().weight(7f)
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Image(
                        painter = painterResource(R.drawable.dissapointed),
                        contentDescription = stringResource(R.string.content_desc_dissapointed_expression),
                    )
                    Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)))
                    Text(
                        text = stringResource(R.string.error_no_data_for_selected_weather_type),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = SolarPanelBlue
                    )
                }
            }

            WeatherChartSwitcher(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = contentToggleSwitch },
                onClick = { onChartSwitcherClick(it) },
                selectedWeather = selectedWeather
            )
        }

        FilledIconButton(
            onClick = onInfoButtonClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = stringResource(R.string.info)
            )
        }
    }
}

@Composable
fun OverlayPopup(weatherMetadata: String) {
    Text( stringResource(R.string.label_which_station)+" "+  weatherMetadata)
}

@Composable
fun WeatherChartSwitcher(
    modifier: Modifier = Modifier,
    onClick: (WeatherPhenomenon) -> Unit,
    selectedWeather: WeatherPhenomenon
) {
    SingleChoiceSegmentedButtonRow(modifier) {
        WeatherPhenomenon.entries.forEachIndexed { index, phenomenon ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = WeatherPhenomenon.entries.size
                ),
                onClick = { onClick(phenomenon) },
                selected = selectedWeather == phenomenon,
                label = { Text(stringResource(phenomenon.nameId)) },
                icon = { Icon(
                    painter = painterResource(phenomenon.iconId),
                    contentDescription = stringResource(phenomenon.nameId)
                ) },
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = SolarPanelBlue,
                    activeContentColor = SaturatedYellow,
                    inactiveContainerColor = SolarPanelBlue.copy(alpha = 0.6f), // foggy version of SolarPanelBlue
                    inactiveContentColor = Color.White
                )
            )
        }
    }
}