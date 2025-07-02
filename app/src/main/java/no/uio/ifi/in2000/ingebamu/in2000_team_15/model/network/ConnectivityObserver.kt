package no.uio.ifi.in2000.ingebamu.in2000_team_15.model.network

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<NetworkStatus>
}