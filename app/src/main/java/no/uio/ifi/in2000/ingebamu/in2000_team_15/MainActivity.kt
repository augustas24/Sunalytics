package no.uio.ifi.in2000.ingebamu.in2000_team_15

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import dagger.hilt.android.AndroidEntryPoint
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.theme.IN2000team15Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            IN2000team15Theme {
                // make sure text in status bar is in readable color, because our app is dark
                val view = LocalView.current
                if (!view.isInEditMode) {
                    SideEffect {
                        val window = (view.context as Activity).window
                        WindowInsetsControllerCompat(window, view).isAppearanceLightStatusBars = false
                    }
                }
                SolarPanelApp()
            }
        }
    }
}