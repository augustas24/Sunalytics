package no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.map

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ingebamu.in2000_team_15.R
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.geocoding.Location
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.home.InfoDialog
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.theme.SaturatedYellow
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.theme.SolarPanelBlue
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.utils.conditional

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    modifier: Modifier = Modifier,
    onFinishButtonClicked: () -> Unit
) {
    //Content Description Labels
    val contentInfoButton = stringResource(R.string.info)
    val contentCheckButton = stringResource(R.string.content_desc_check_button)
    val contentSearchBar = stringResource(R.string.content_desc_address_search_bar)
    val contentMapOfNorway = stringResource(R.string.content_desc_map)

    var showInfo by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val permissionDeniedMessage = stringResource(R.string.permission_denied)

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) scope.launch {
            try {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
                    fusedLocationProviderClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        CancellationTokenSource().token
                    ).addOnSuccessListener { location ->
                        val newLocation = Location(point = Point.fromLngLat(location.longitude, location.latitude))
                        viewModel.setLocation(newLocation, moveToOnMap = true)
                        viewModel.updateToNearestLocation()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(context, permissionDeniedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = modifier
    ) {
        MapScreenMap(
            viewModel = viewModel,
            modifier = Modifier.semantics { contentDescription = contentMapOfNorway }
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(dimensionResource(R.dimen.padding_large))
        ) {
            if (viewModel.location != null) FloatingActionButton(
                onClick = onFinishButtonClicked,
                shape = CircleShape,
                containerColor = SaturatedYellow,
                contentColor = SolarPanelBlue
            ) {
                Icon(Icons.Default.Check, contentCheckButton)
            }

            FloatingActionButton(
                onClick = {
                    locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                },
                shape = CircleShape,
                containerColor = SaturatedYellow,
                contentColor = SolarPanelBlue
            ) {
                Icon(Icons.Default.LocationOn, stringResource(R.string.use_my_position))
            }

            FloatingActionButton(
                onClick = { showInfo = true },
                shape = CircleShape,
                containerColor = SaturatedYellow,
                contentColor = SolarPanelBlue
            ) {
                Icon(Icons.Default.Info, contentInfoButton)
            }
        }
        SearchBackground(
            isSearchOpen = viewModel.isSearchOpen,
            onClick = { viewModel.closeSearch() },
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black.copy(alpha = 0.5f))
        )
        SearchBox(
            viewModel = viewModel,
            modifier = Modifier
                .semantics { contentDescription = contentSearchBar }
        )
    }

    if (showInfo) InfoDialog(
        title = { Text(stringResource(R.string.map)) },
        text = { Text(stringResource(R.string.map_info)) },
        onDismissRequest = { showInfo = false }
    )
}

@Composable
fun MapScreenMap(
    viewModel: MapViewModel,
    modifier: Modifier = Modifier
) {
    val mapState = rememberMapState {
        gesturesSettings = GesturesSettings {
            rotateEnabled = false
        }
    }

    MapboxMap(
        modifier = modifier,
        mapState = mapState,
        mapViewportState = viewModel.mapViewportState,
        style = { MapStyle(style = "mapbox://styles/leonhehasnostyle/cm96v9fca00dn01qq7xgh4n8y") },
        onMapLongClickListener = { clickedPoint ->
            viewModel.setLocation(Location(point = clickedPoint))
            viewModel.updateToNearestLocation()
            true
        },
        scaleBar = { },
        compass = { Compass(alignment = Alignment.BottomEnd) }
    ) {
        SolarPanelMarker(viewModel)
        MapBounds()
    }
}

@Composable
private fun SolarPanelMarker(viewModel: MapViewModel) {
    viewModel.location?.point?.let { point ->
        val marker = rememberIconImage(
            key = R.drawable.red_marker,
            painter = painterResource(R.drawable.red_marker)
        )
        PointAnnotation(point = point) {
            iconImage = marker
            interactionsState.isDraggable = true
            interactionsState.onDragged {
                viewModel.setLocation(Location(point = it.point))
            }
            interactionsState.onDragFinished {
                viewModel.updateToNearestLocation()
            }
        }
    }
}

@Composable
private fun MapBounds() {
    MapEffect(key1 = Unit) { mapView ->
        // Define camera bounds
        val cameraBoundsOptions = CameraBoundsOptions.Builder()
            .bounds(
                CoordinateBounds(
                    // Mainland bounds
//                    Point.fromLngLat(3.66, 56.98),
//                    Point.fromLngLat(32.05, 72.19),
                    // Mainland + some padding
                    Point.fromLngLat(3.66, 51.98),
                    Point.fromLngLat(32.05, 75.19),
                    // Mainland + islands
//                    Point.fromLngLat(-8.72, 56.98),
//                    Point.fromLngLat(33.05, 80.83),
                    // Mainland + islands + some padding
//                    Point.fromLngLat(-10.0, 52.5),
//                    Point.fromLngLat(40.0, 82.5),
                    false
                )
            )
            .build()
        mapView.mapboxMap.setBounds(cameraBoundsOptions)
    }
}

@Composable
private fun SearchBackground(
    isSearchOpen: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isSearchOpen,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = modifier
                .conditional(isSearchOpen) { clickable(
                    onClick = onClick,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) }
        )
    }
}

@Composable
private fun SearchBox(
    viewModel: MapViewModel,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val localFocusManager = LocalFocusManager.current

    Box(modifier) {
        Column(
            Modifier
                .padding(
                    top = dimensionResource(R.dimen.padding_large),
                    start = dimensionResource(R.dimen.padding_large),
                    end = dimensionResource(R.dimen.padding_large)
                )
        ) {
            TextField(
                value = viewModel.searchInput,
                onValueChange = { viewModel.setSearchInput(it) },
                singleLine = true,
                shape = RoundedCornerShape(100),
                colors = TextFieldDefaults.colors().copy(
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { if (it.isFocused) viewModel.openSearch() },
                label = { Text(stringResource(R.string.search)) },
                placeholder = { Text(stringResource(R.string.search_for_a_location)) },
                leadingIcon = {
                    if (viewModel.isSearchOpen) {
                        IconButton(
                            onClick = viewModel::closeSearch
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = stringResource(R.string.content_desc_back_button)
                            )
                        }
                    }
                },
                trailingIcon = {
                        if (viewModel.searchInput.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.setSearchInput("") }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.content_desc_clear_button)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.content_desc_search_button)
                            )
                        }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Go,
                ),
                keyboardActions = KeyboardActions(
                    onGo = {
                        viewModel.goToFirstLocationInSearch()
                        viewModel.closeSearch()
                    }
                )
            )

            Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))

            AnimatedVisibility(viewModel.isSearchOpen) {
                LazyColumn(
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
                ) {
                    items(viewModel.locationResults) {
                        LocationCard(
                            location = it,
                            onClick = { location ->
                                viewModel.setLocation(location, moveToOnMap = true)
                                viewModel.closeSearch()
                            },
                            Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Spacer(Modifier.height(dimensionResource(R.dimen.padding_large)))
                    }
                }
            }
        }
    }

    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (lazyListState.isScrollInProgress) localFocusManager.clearFocus()
    }

    LaunchedEffect(viewModel.isSearchOpen) {
        if (!viewModel.isSearchOpen) localFocusManager.clearFocus()
    }

    if (viewModel.isSearchOpen) BackHandler { viewModel.closeSearch() }
}

@Composable
private fun LocationCard(
    location: Location,
    onClick: (Location) -> Unit,
    modifier: Modifier
) {
    Card(
        onClick = { onClick(location) },
        modifier = modifier,
    ) {
        Column(
            Modifier.padding(dimensionResource(R.dimen.padding_small))
        ) {
            Text(location.name)
            Text(
                text = location.place,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}