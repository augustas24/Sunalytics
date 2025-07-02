package no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.geocoding.GeocodingRepository
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.geocoding.Location
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val geocodingRepository: GeocodingRepository
): ViewModel() {

    var location: Location? by mutableStateOf(null)
        private set

    val mapViewportState = MapViewportState()

    var isSearchOpen by mutableStateOf(false)
        private set
    var searchInput by mutableStateOf("")
        private set
    var locationResults: List<Location> by mutableStateOf(listOf())
        private set

    private var stoppedTypingJob: Job = Job()

    var locationChanged = false

    fun setLocation(
        location: Location,
        moveToOnMap: Boolean = false
    ) {
        this.location = location
        searchInput = location.name
        locationChanged = true

        if (!moveToOnMap) return
        mapViewportState.flyTo(
            cameraOptions = cameraOptions {
                center(location.point)
                zoom(18.0)
            },
            MapAnimationOptions.mapAnimationOptions { duration(2000) }
        )
    }

    fun openSearch() { isSearchOpen = true }

    fun closeSearch() { isSearchOpen = false }

    @JvmName("setSearchInputPublic")
    fun setSearchInput(
        string: String
    ) {
        searchInput = string

        stoppedTypingJob.cancel()
        stoppedTypingJob = viewModelScope.launch {
            delay(500)
            searchForLocations()
        }
    }

    private fun searchForLocations(onFinished: (List<Location>) -> Unit = {}) {
        locationResults = listOf()
        if (searchInput.isBlank()) return

        viewModelScope.launch {
            locationResults = geocodingRepository.getLocations(searchInput, 10)
            onFinished(locationResults)
        }
    }

    fun updateToNearestLocation() {
        viewModelScope.launch {
            location?.point?.let { point ->
                geocodingRepository.getNearestLocation(point)?.let { newLocation ->
                    setLocation(
                        if (location == null)
                            newLocation
                        else
                            newLocation.copy(point = location!!.point)
                    )
                }
            }
        }
    }

    fun goToFirstLocationInSearch() {
        if (!stoppedTypingJob.isActive) {
            locationResults.firstOrNull()?.let {
                setLocation(it, moveToOnMap = true)
            }
            return
        }

        stoppedTypingJob.cancel()
        searchForLocations { results ->
            results.firstOrNull()?.let {
                setLocation(it, moveToOnMap = true)
            }
        }
    }
}