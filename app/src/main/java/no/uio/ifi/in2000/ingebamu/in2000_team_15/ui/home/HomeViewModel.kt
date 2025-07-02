package no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.home


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.price.PriceRepository
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.room.WeatherData
import no.uio.ifi.in2000.ingebamu.in2000_team_15.data.solar.SolarRepository
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.network.ConnectivityObserver
import no.uio.ifi.in2000.ingebamu.in2000_team_15.model.network.NetworkStatus
import no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.state.UiState
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val solarRepository: SolarRepository,
    private val priceRepository: PriceRepository,
    private val connectivityObserver: ConnectivityObserver
): ViewModel() {

    private val _solarDataPvgisUiState = MutableStateFlow<UiState<List<Pair<Int, Double>>>>(UiState.Loading)
    val solarDataPvgisUiState: StateFlow<UiState<List<Pair<Int, Double>>>> = _solarDataPvgisUiState.asStateFlow()

    private val _solarDataFrostUiState = MutableStateFlow<UiState<List<Pair<Int, Double>>>>(UiState.Loading)
    val solarDataFrostUiState: StateFlow<UiState<List<Pair<Int, Double>>>> = _solarDataFrostUiState.asStateFlow()

    private val _priceDataUiState = MutableStateFlow<UiState<List<Pair<Int, Double>>>>(UiState.Loading)
    val priceDataUiState: StateFlow<UiState<List<Pair<Int, Double>>>> = _priceDataUiState.asStateFlow()

    private val _roofArea = MutableStateFlow("")
    val roofArea: StateFlow<String> = _roofArea.asStateFlow()

    private val _showMoneySaved = MutableStateFlow(false)
    val showMoneySaved: StateFlow<Boolean> = _showMoneySaved.asStateFlow()

    private var _weatherUiState = MutableStateFlow<UiState<List<WeatherData>>>(UiState.Loading)
    val weatherUiState: StateFlow<UiState<List<WeatherData>>> = _weatherUiState.asStateFlow()

    // Network status state
    private val _networkStatus = MutableStateFlow<NetworkStatus>(NetworkStatus.Unavailable)
    val networkStatus: StateFlow<NetworkStatus> = _networkStatus.asStateFlow()

    // Flag to track if the network has been checked and stabilized
    private val _isNetworkChecked = MutableStateFlow(false)
    val isNetworkChecked: StateFlow<Boolean> = _isNetworkChecked.asStateFlow()

    var frostCheckedAndEmpty by mutableStateOf(false)
    var pvGisCheckedAndEmpty by mutableStateOf(false)

    init {
        observeNetworkStatus()
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            connectivityObserver.observe()
                .collect { status ->
                    _networkStatus.value = status
                    if (status != NetworkStatus.Unavailable) {
                        // Network is available, set the flag to true
                        _isNetworkChecked.value = true
                    }
                }
        }
    }
    fun refreshNetworkStatus() {
        viewModelScope.launch {
            connectivityObserver.observe().take(1).collect { status ->
                _networkStatus.value = status
                _isNetworkChecked.value = true
            }
        }
    }

    fun fetchChartData(lat: Double, lon: Double) {
        viewModelScope.launch {
            _solarDataPvgisUiState.value = UiState.Loading
            _priceDataUiState.value = UiState.Loading
            _solarDataFrostUiState.value = UiState.Loading

            try {
                val roofAreaValue = roofArea.value.toDouble()

                val solar = solarRepository.getMonthlySolarProduction(lat, lon, roofAreaValue)
                _solarDataPvgisUiState.value = UiState.Success(solar)

                val price = priceRepository.getPriceData(lat, lon)
                val savings = calculateSavings(solar, price, roofAreaValue)
                _priceDataUiState.value = UiState.Success(savings)

            } catch (e: Exception) {
                _solarDataPvgisUiState.value = UiState.Error("${e.message}")
                pvGisCheckedAndEmpty = true
                _priceDataUiState.value = UiState.Error("${e.message}")
                return@launch
            }

            try {
                val weatherState = weatherUiState.value

                if (weatherState is UiState.Success) {
                    val weatherData = weatherState.data
                    val isValid = weatherData.isNotEmpty()

                    if (isValid) {
                        val frostData = solarRepository.calculateMonthlySolarProduction(
                            lat, lon, weatherData, roofArea.value
                        )
                        _solarDataFrostUiState.value = UiState.Success(frostData)
                    } else {
                        _solarDataFrostUiState.value = UiState.Success(emptyList())
                        frostCheckedAndEmpty = true
                    }
                } else if (weatherState is UiState.Error) {
                    _solarDataFrostUiState.value = UiState.Error(weatherState.message)
                }

            } catch (e: Exception) {
                _solarDataFrostUiState.value = UiState.Error("${e.message}")
            }
        }
    }

    private fun calculateSavings(
        solarData: List<Pair<Int, Double>>,
        priceData: List<Pair<Int, Double>>,
        roofArea: Double
    ): List<Pair<Int, Double>> {
        val enovaSupportTotal = minOf((roofArea * 0.20), 20.0) * 1250
        val enovaSupportPerMonth = enovaSupportTotal / 12

        return solarData.zip(priceData) { solar, price ->
            solar.first to (solar.second * price.second + enovaSupportPerMonth)
        }
    }

    fun updateRoofArea(roofArea: String) {
        _roofArea.value = roofArea
    }

    fun setShowMoneySaved(value: Boolean) {
        _showMoneySaved.value = value
    }

    fun fetchStationAndData(lat: String, lon: String) {
        viewModelScope.launch {
            try {
                val sourceResult = solarRepository.getDataSourceSource(lat, lon, solarRepository.getTimeReference(),"P1M")
                sourceResult?.data?.firstOrNull()?.let { station ->
                    fetchWeatherData(
                        station.geometry.coordinates[1].toString(),
                        station.geometry.coordinates[0].toString(),
                        station.name
                    )
                }
            } catch (e: Exception) {
                println("$e")
            }
        }
    }

    private fun fetchWeatherData(
        lat: String,
        lon: String,
        stationName: String,
    ) {
        viewModelScope.launch {
            _weatherUiState.value = UiState.Loading
            try {
                val weatherData = solarRepository.getCompleteObservationDataWithFallback(
                    lat, lon, stationName
                )
                _weatherUiState.value = UiState.Success(weatherData)
                Log.d("test", _weatherUiState.value.toString())
            } catch (e: Exception) {
                _weatherUiState.value = UiState.Error( "${e.message}")
            }
        }
    }

}