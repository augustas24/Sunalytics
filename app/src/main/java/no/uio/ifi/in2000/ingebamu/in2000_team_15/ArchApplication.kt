package no.uio.ifi.in2000.ingebamu.in2000_team_15

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * This class is required for Hilt dependency injection to work.
 * It serves as the application-level entry point for Hilt.
 * Do not modify or remove this class, as it is necessary for
 * setting up dependency injection across the app.
 */

@HiltAndroidApp
class ArchApplication : Application()