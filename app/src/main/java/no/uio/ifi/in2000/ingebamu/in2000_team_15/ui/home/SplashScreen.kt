package no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import no.uio.ifi.in2000.ingebamu.in2000_team_15.R
import no.uio.ifi.in2000.ingebamu.in2000_team_15.Screen

@Composable
fun SplashScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate(Screen.Home.name) {
            navController.popBackStack()
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "App logo",
            modifier = Modifier.fillMaxWidth(0.5f)
        )
    }
}
