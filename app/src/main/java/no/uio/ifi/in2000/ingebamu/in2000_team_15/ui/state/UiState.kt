package no.uio.ifi.in2000.ingebamu.in2000_team_15.ui.state

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}