package no.uio.ifi.in2000.ingebamu.in2000_team_15.model.network

sealed class NetworkStatus {
    data object Available : NetworkStatus()
    data object Unavailable : NetworkStatus()
}