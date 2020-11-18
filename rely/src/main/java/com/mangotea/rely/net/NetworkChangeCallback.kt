package com.mangotea.rely.net

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities


internal class NetworkChangeCallback : ConnectivityManager.NetworkCallback() {

    override fun onAvailable(network: Network) {
//        NetworkManager.onConnectChanged(network, true)
    }


    override fun onLost(network: Network) {
        NetworkManager.onNetworkChanged(false, null)
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        NetworkManager.onNetworkChanged(true, networkCapabilities)
    }
}