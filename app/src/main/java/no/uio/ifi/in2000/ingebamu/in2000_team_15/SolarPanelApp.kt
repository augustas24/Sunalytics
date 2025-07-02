package no.uio.ifi.in2000.ingebamu.in2000_team_15

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.home.HomeScreen
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.home.HomeViewModel
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.home.SplashScreen
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.map.MapScreen
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.map.MapViewModel
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.weather.WeatherScreen

enum class Screen(@StringRes val title: Int) {
    Splash(title = R.string.splash_screen),
    Home(title = R.string.home_screen),
    Map(title = R.string.map_screen),
    Weather(title = R.string.weather_screen)
}

@Composable
fun SolarPanelApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(
        backStackEntry?.destination?.route?.substringBefore("/") ?: Screen.Home.name
    )

    val homeViewModel: HomeViewModel = hiltViewModel()
    val mapViewModel: MapViewModel = hiltViewModel()

    Scaffold(
        modifier = modifier,
        topBar = {
            SolarPanelTopBar(
                title = if (currentScreen == Screen.Splash) "" else stringResource(currentScreen.title),
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.name
        ) {
            composable(route = Screen.Splash.name) {
                SplashScreen(
                    navController = navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = Screen.Home.name) {
                HomeScreen(
                    homeViewModel = homeViewModel,
                    mapViewModel = mapViewModel,
                    onAddressButtonClicked = { navController.navigate(Screen.Map.name) },
                    onWeatherButtonClicked = { navController.navigate(Screen.Weather.name) },
                    modifier = Modifier.padding((innerPadding)),
                )
            }
            composable(route = Screen.Map.name) {
                MapScreen(
                    viewModel = mapViewModel,
                    modifier = Modifier.padding((innerPadding)),
                    onFinishButtonClicked = {
                        navController.popBackStack()
                    }
                )
            }
            composable(route = Screen.Weather.name) {
                WeatherScreen(
                    viewModel = homeViewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolarPanelTopBar(
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        modifier = modifier
            .shadow(
                elevation = dimensionResource(R.dimen.padding_small),
                clip = false
            ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.content_desc_back_button)
                    )
                }
            }
        }
    )
}
