package com.mangotea.rely.net

interface NetworkCallback {

    fun onNetworkChanged(oldConnection: Boolean, newConnection: Boolean, oldWifi: Boolean, newWifi: Boolean)

}