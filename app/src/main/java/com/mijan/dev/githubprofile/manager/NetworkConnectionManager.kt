package com.mijan.dev.githubprofile.manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConnectionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkRequest: NetworkRequest,
) {
    private val _isNetworkAvailable = MutableStateFlow<Boolean?>(null)
    val isNetworkAvailable = _isNetworkAvailable.asStateFlow()

    init {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            // network is available for use
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                _isNetworkAvailable.value = true
            }

            // lost network connection
            override fun onLost(network: Network) {
                super.onLost(network)
                _isNetworkAvailable.value = false
            }
        }

        val connectivityManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        } else {
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
        connectivityManager.requestNetwork(networkRequest, networkCallback)
        _isNetworkAvailable.value = connectivityManager.getInitialNetworkAvailability()
    }

    private fun ConnectivityManager.getInitialNetworkAvailability(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = activeNetwork
            val connection = getNetworkCapabilities(network)
            connection != null && (
                    connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        } else {
            val activeNetwork = activeNetworkInfo
            if (activeNetwork != null) {
                (activeNetwork.type == ConnectivityManager.TYPE_WIFI ||
                        activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
            }
            false
        }
    }

}