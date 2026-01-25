package com.newpaper.somewhere.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

interface ConnectivityObserver {
    fun observe(): Flow<Status>

    enum class Status{
        AVAILABLE,
        UNAVAILABLE,
        LOSING,
        LOST
    }
}

class NetworkConnectivityObserver(
    private val context: Context,
    private val scope: CoroutineScope
): ConnectivityObserver {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _networkStatus = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(ConnectivityObserver.Status.AVAILABLE)
            }
            override fun onLosing(network: Network, maxMsToLive: Int) {
                trySend(ConnectivityObserver.Status.LOSING)
            }
            override fun onLost(network: Network) {
                trySend(ConnectivityObserver.Status.LOST)
            }
            override fun onUnavailable() {
                trySend(ConnectivityObserver.Status.UNAVAILABLE)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
    .distinctUntilChanged()
    .shareIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000L),
        replay = 1
    )

    override fun observe(): Flow<ConnectivityObserver.Status> {
        return _networkStatus
    }
}



val Context.currentConnectivityState: ConnectivityObserver.Status
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return getCurrentConnectivityState(connectivityManager)
    }

private fun getCurrentConnectivityState(
    connectivityManager: ConnectivityManager
): ConnectivityObserver.Status {
    val connected = connectivityManager.allNetworks.any { network ->
        connectivityManager.getNetworkCapabilities(network)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }

    return if (connected) ConnectivityObserver.Status.AVAILABLE
    else ConnectivityObserver.Status.LOST
}