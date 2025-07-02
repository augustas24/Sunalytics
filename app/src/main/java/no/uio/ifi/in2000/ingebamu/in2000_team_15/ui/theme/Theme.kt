package no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = SolarPanelBlue,
    onBackground = SaturatedYellow,
    primary = SaturatedYellow,
    onPrimary = SolarPanelBlue,
    secondary = SaturatedYellow,
    surface = SolarPanelBlue,
    onSurface = SaturatedYellow
)

@Composable
fun IN2000team15Theme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}