package com.newpaper.somewhere.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.newpaper.somewhere.util.ConnectivityObserver
import com.newpaper.somewhere.util.WindowSizeClass
import com.newpaper.somewhere.util.currentConnectivityState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberExternalState(
    context: Context,
    windowSizeClass: WindowSizeClass,
    connectivityObserver: ConnectivityObserver,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): ExternalState {
    return remember(
        context,
        coroutineScope,
        windowSizeClass,
        connectivityObserver
    ){
        ExternalState(
            context = context,
            coroutineScope = coroutineScope,
            windowSizeClass = windowSizeClass,
            connectivityObserver = connectivityObserver
        )
    }
}

@Stable
class ExternalState(
    private val context: Context,
    private val coroutineScope: CoroutineScope,

    val windowSizeClass: WindowSizeClass,

    connectivityObserver: ConnectivityObserver
) {
    private val _internetEnabled = mutableStateOf(context.currentConnectivityState == ConnectivityObserver.Status.AVAILABLE)
    val internetEnabled: Boolean by _internetEnabled

    init {
        observeConnectivity(connectivityObserver)
    }

    private fun observeConnectivity(connectivityObserver: ConnectivityObserver) {
        coroutineScope.launch {
            connectivityObserver.observe().collect { status ->
                _internetEnabled.value = status == ConnectivityObserver.Status.AVAILABLE
            }
        }
    }
}