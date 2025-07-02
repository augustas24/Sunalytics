package no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.mapbox.geojson.Point
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import no.uio.ifi.in2000.ingebamu.in2000_team_15.R
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.geocoding.Location
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.network.NetworkStatus
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.map.MapViewModel
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.state.LoadingState
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.state.UiState
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.theme.FrostLine
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.theme.MoneyGreen
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.theme.PvgisLine
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.theme.SaturatedYellow
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.theme.SolarPanelBlue
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.utils.PrefixSuffixTransformer

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    mapViewModel: MapViewModel,
    onAddressButtonClicked: () -> Unit,
    onWeatherButtonClicked: () -> Unit
) {
    //viewModel state
    val solarDataPvgisUiState by homeViewModel.solarDataPvgisUiState.collectAsState()
    val solarDataFrostUiState by homeViewModel.solarDataFrostUiState.collectAsState()
    val priceDataUiState by homeViewModel.priceDataUiState.collectAsState()
    val showMoneySaved by homeViewModel.showMoneySaved.collectAsState()
    val roofArea by homeViewModel.roofArea.collectAsState()

    //Network
    val networkStatus by homeViewModel.networkStatus.collectAsState()
    val isNetworkChecked by homeViewModel.isNetworkChecked.collectAsState()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Check if internet is available and if the network status has been stabilized
    val hasInternet = networkStatus == NetworkStatus.Available && isNetworkChecked

    //Keyboard
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val location = mapViewModel.location
    var showInfo by remember { mutableStateOf(false) }
    var showRoofAreaInfo by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var isEstimateClicked by remember { mutableStateOf(false) }
    val frostCheckedButEmpty = homeViewModel.frostCheckedAndEmpty
    val pvGisCheckedButEmpty = homeViewModel.pvGisCheckedAndEmpty

    // Accessibility content descriptions
    val contentToAddressScreen = stringResource(R.string.content_desc_choose_address)
    val contentEnterArea = stringResource(R.string.content_desc_enter_area)
    val contentEstimateButton = stringResource(R.string.content_desc_estimate_button)
    val contentEstimateChart = stringResource(R.string.content_desc_estimate_chart)
    val contentToWeatherScreen = stringResource(R.string.content_desc_weather_button)
    val contentGuideCard = stringResource(R.string.content_desc_guide_card)

    if (mapViewModel.locationChanged) {
        homeViewModel.fetchStationAndData(
            location!!.point.latitude().toString(),
            location.point.longitude().toString()
        )
        if (roofArea.isNotBlank()) {
            validateAndCalculate(
                point = location.point,
                viewModel = homeViewModel,
                setShowError = { showError = it },
                roofArea = roofArea
            )
        }
        mapViewModel.locationChanged = false
    }

    // Trigger a network re-check whenever the screen resumes
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                homeViewModel.refreshNetworkStatus()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = modifier
        .pointerInput(Unit) { detectTapGestures( onTap =  {
            focusManager.clearFocus()
            keyboardController?.hide()
        }) }
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.Top
        ) {
            AddressWidget(
                location = location,
                onAddressButtonClicked = onAddressButtonClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = contentToAddressScreen }
            )

            Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)))

            Row(verticalAlignment = Alignment.Bottom) {
                InputTextField(
                    label = stringResource(R.string.label_area),
                    value = roofArea,
                    onValueChange = {
                        homeViewModel.updateRoofArea(it)
                        showError = it.isBlank()
                    },
                    onInfoButtonClicked = { showRoofAreaInfo = true },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (location != null) {
                            validateAndCalculate(
                                point = location.point,
                                viewModel = homeViewModel,
                                setShowError = { showError = it },
                                roofArea = roofArea
                            )
                        }
                    }),
                    isRequired = true,
                    showError = showError,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = contentEnterArea }
                )

                Spacer(Modifier.width(dimensionResource(R.dimen.padding_medium)))

                Button(
                    onClick = {
                        Log.d("calculateSavings", "Button clicked")
                        if (location == null) return@Button
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        validateAndCalculate(
                            point = location.point,
                            viewModel = homeViewModel,
                            setShowError = { showError = it },
                            roofArea = roofArea
                        )
                        isEstimateClicked = true
                    },
                    modifier = Modifier
                        .height(54.dp)
                        .semantics { contentDescription = contentEstimateButton },
                    enabled = (roofArea != "" && location != null) // clickable when address and roof area is given
                ) {
                    Text(stringResource(R.string.button_get_estimate))
                }

            }

            Box(
                modifier = Modifier.height(dimensionResource(R.dimen.padding_large) * 2)
            ) {
                if (showError) Text(
                    text = stringResource(R.string.error_required_field),
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            if (!hasInternet) {
                Text(
                    text = stringResource(R.string.error_no_internet),
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                when (val pvgisState = solarDataPvgisUiState) {
                    is UiState.Loading ->
                        if (!isEstimateClicked) {
                            AppPreview(
                                painter = painterResource(R.drawable.app_overview),
                                modifier = Modifier
                                    .wrapContentSize()
                                    .semantics { contentDescription = contentGuideCard }
                                    .clip(RoundedCornerShape(16.dp))
                                    .padding(dimensionResource(R.dimen.padding_small))
                            )

                        } else {
                            LoadingState()
                        }

                    is UiState.Success -> {
                        val solarDataPvgis = pvgisState.data
                        val solarDataFrost =
                            (solarDataFrostUiState as? UiState.Success)?.data ?: emptyList()

                        // Handle both loading states
                        val pvgisDataLoaded = solarDataPvgis.isNotEmpty()
                        val frostDataLoaded = solarDataFrost.isNotEmpty()
                        Log.d("hei", solarDataFrost.toString())


                        when {
                            (!pvgisDataLoaded && pvGisCheckedButEmpty) || (!frostDataLoaded && frostCheckedButEmpty) -> {
                                // Show loading state if either of the datasets is not loaded
                                LoadingState()
                            }

                            else -> {
                                // Both datasets are loaded, show the chart
                                ChartWithInfoButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .semantics { contentDescription = contentEstimateChart }
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(SaturatedYellow)
                                        .padding(dimensionResource(R.dimen.padding_medium)),
                                    valuesPvgis = solarDataPvgis,
                                    valuesFrost = solarDataFrost,
                                    valuesPrice = (priceDataUiState as? UiState.Success)?.data
                                        ?: emptyList(),
                                    homeViewModel = homeViewModel,
                                    showMoneySaved = showMoneySaved,
                                    onInfoButtonClicked = { showInfo = true }
                                )

                                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))

                                Button(
                                    onClick = onWeatherButtonClicked,
                                    modifier = Modifier
                                        .height(50.dp)
                                        .align(Alignment.CenterHorizontally)
                                        .semantics { contentDescription = contentToWeatherScreen }
                                ) {
                                    Text(stringResource(R.string.button_show_weather_data))
                                }
                            }
                        }
                    }

                    is UiState.Error -> {
                        Box(modifier = Modifier.fillMaxWidth().weight(7f)) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.dissapointed),
                                    contentDescription = stringResource(R.string.content_desc_dissapointed_expression),
                                )
                                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)))
                                Text(
                                    text = stringResource(R.string.error_no_data_from_pvgis),
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

    if (showRoofAreaInfo) {
        InfoDialog(
            title = {
                Text(stringResource(R.string.label_area))
            },
            text = {
                Text(stringResource(R.string.infobox_roofArea))
            },
            onDismissRequest = { showRoofAreaInfo = false }
        )
    }

    if (showInfo) {
        if (showMoneySaved) InfoDialog(
            title = {
                Text(stringResource(R.string.money_saved))
            },
            text = {
                Text(stringResource(R.string.money_info))
            },
            onDismissRequest = { showInfo = false }
        )
        else InfoDialog(
            title = {
                Text(stringResource(R.string.power_generated))
            },
            text = {
                Text(stringResource(R.string.power_info))
            },
            onDismissRequest = { showInfo = false }
        )
    }
}

@Composable
fun InfoDialog(
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(R.string.info)
            )
        },
        title = title,
        text = text,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            FilledIconButton(
                onClick = onDismissRequest
            ) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = stringResource(R.string.ok)
                )
            }
        }
    )
}

@Composable
fun AddressWidget(
    location: Location?,
    onAddressButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (location == null) {
        Button(
            onClick = onAddressButtonClicked,
            modifier = modifier
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.map_screen),
                    style = MaterialTheme.typography.titleMedium,
                )
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = stringResource(R.string.label_pin),
                    modifier = Modifier
                )
            }
        }
        return
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = location.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = location.place,
                style = MaterialTheme.typography.titleSmall
            )
        }

        FilledIconButton(
            onClick = onAddressButtonClicked
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = stringResource(R.string.label_pin),
                modifier = Modifier
            )
        }
    }
}

fun validateAndCalculate(
    point: Point,
    viewModel: HomeViewModel,
    setShowError: (Boolean) -> Unit,
    roofArea: String
) {
    if (roofArea.isBlank()) {
        setShowError(true)
    } else {
        setShowError(false)
        viewModel.fetchChartData(point.latitude(), point.longitude())
    }
}

@Composable
fun ChartWithInfoButton(
    modifier: Modifier = Modifier,
    valuesPvgis: List<Pair<Int, Double>>,
    valuesFrost: List<Pair<Int, Double>>,
    valuesPrice: List<Pair<Int, Double>>,
    homeViewModel: HomeViewModel,
    showMoneySaved: Boolean,
    onInfoButtonClicked: () -> Unit
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
    val indicatorUnit = stringResource(if (!showMoneySaved) R.string.kilowatt_hour else R.string.norwegian_krone)

    // Content Descriptions & Labels
    val contentToggleSwitcher = stringResource(R.string.content_desc_estimate_toggle_switch)
    val labelPvgis = stringResource(R.string.label_pvgis)
    val labelFrost = stringResource(R.string.label_frost)

    Box(modifier = modifier) {
        Column {
            LineChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(7f),
                data = remember(valuesPvgis, valuesFrost, valuesPrice, showMoneySaved) {
                    if (showMoneySaved) listOf(
                        Line(
                            label = labelPvgis,
                            values = valuesPrice.map { it.second },
                            color = SolidColor(MoneyGreen),
                            firstGradientFillColor = MoneyGreen.copy(alpha = .5f),
                            secondGradientFillColor = Color.Transparent,
                            curvedEdges = false
                        )
                    )
                    else if (valuesFrost.isNotEmpty()) listOf(
                        Line(
                            label = labelPvgis,
                            values = valuesPvgis.map { it.second },
                            color = SolidColor(PvgisLine),
                            firstGradientFillColor = PvgisLine.copy(alpha = .5f),
                            secondGradientFillColor = Color.Transparent,
                            curvedEdges = false
                        ),
                        Line(
                            label = labelFrost,
                            values = valuesFrost.map { it.second },
                            color = SolidColor(FrostLine),
                            firstGradientFillColor = FrostLine.copy(alpha = .5f),
                            secondGradientFillColor = Color.Transparent,
                            curvedEdges = false
                        ),
                    )
                    else listOf(
                        Line(
                            label = labelPvgis,
                            values = valuesPvgis.map { it.second },
                            color = SolidColor(PvgisLine),
                            firstGradientFillColor = PvgisLine.copy(alpha = .5f),
                            secondGradientFillColor = Color.Transparent,
                            curvedEdges = false
                        )
                    )
                },
                labelProperties = LabelProperties(
                    enabled = true,
                    labels = months,
                    rotation = LabelProperties.Rotation()
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    contentBuilder = { "${it.format(0)} $indicatorUnit" },
                ),
                gridProperties = GridProperties(
                    enabled = true,
                    xAxisProperties = GridProperties.AxisProperties(
                        enabled = false
                    ),
                    yAxisProperties = GridProperties.AxisProperties(
                        lineCount = 12
                    )
                ),
                animationDelay = 0
            )

            Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))

            HomeChartSwitcher(
                showMoneySaved = showMoneySaved,
                onClick = { homeViewModel.setShowMoneySaved(it)
                          if (it) {
                              Log.d("calculateSavings", "Calculated: $valuesPrice")
                          }
                          },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .semantics { contentDescription = contentToggleSwitcher }
            )
        }

        FilledIconButton(
            onClick = onInfoButtonClicked,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = stringResource(R.string.info)
            )
        }
    }
}

@Composable
fun InputTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onInfoButtonClicked: () -> Unit,
    keyboardOptions: KeyboardOptions,
    modifier : Modifier = Modifier,
    keyboardActions: KeyboardActions = KeyboardActions(),
    isRequired: Boolean = false,
    showError: Boolean = false
) {

    val areaSuffix = PrefixSuffixTransformer(suffix = " " + stringResource(R.string.square_meter_symbol))
    val annotatedLabel = buildAnnotatedString {
        append(label)
        if (isRequired) {
            withStyle(style = SpanStyle(color = Color.Red)) { append(" *") }
        }
    }
    OutlinedTextField(
        modifier = modifier,
        label = { Text(annotatedLabel) },
        value = value,
        onValueChange = { input ->
            val filteredInput = input.filter { it.isDigit() || it == '.' }
            onValueChange(filteredInput)
        },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        isError = showError,
        visualTransformation = if (value.isBlank()) VisualTransformation.None else areaSuffix,
        shape = RoundedCornerShape(30),
        trailingIcon = {
            IconButton(
                onClick = onInfoButtonClicked,
                colors = IconButtonDefaults.iconButtonColors(contentColor = SaturatedYellow)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(R.string.content_desc_roofSize_info_icon)
                )
            }
        }
    )
}

@Composable
fun AppPreview(
    painter: Painter,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
    ) {
        Image(
            painter = painter,
            contentDescription = stringResource(R.string.content_desc_app_overview),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // fade overlay
        Box(
            modifier = Modifier.fillMaxSize()
                    .background(SolarPanelBlue.copy(alpha = 0.4f))
        ) {
            // centered info text
            Text(
                text = stringResource(R.string.start_info),
                color = SaturatedYellow,
                fontSize = 20.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun HomeChartSwitcher(
    showMoneySaved: Boolean,
    onClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        Triple(false, stringResource(R.string.power_generated), painterResource(R.drawable.energy_icon)),
        Triple(true, stringResource(R.string.money_saved), painterResource(R.drawable.dollar_icon))
    )

    SingleChoiceSegmentedButtonRow(modifier) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = { onClick(option.first) },
                selected = showMoneySaved == option.first,
                label = { Text(option.second) },
                icon = { Icon(
                    painter = option.third,
                    contentDescription = option.second
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