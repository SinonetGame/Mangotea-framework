package com.mangotea.rely.net

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LifecycleOwner
import com.mangotea.rely.d
import com.mangotea.rely.lifecycle.onDestory


internal object NetworkManager {
    const val TAG = "NetworkManager"
    private val callbacks by lazy { hashMapOf<LifecycleOwner, HashSet<NetworkCallback>>() }
    var wifi = false
        private set
    var connected = false
        set

    private val defaultCallback by lazy { NetworkChangeCallback() }


    @Synchronized
    fun onNetworkChanged(newConnection: Boolean, networkCapabilities: NetworkCapabilities? = null) {// 此处的参数传递为是否为wifi
        val oldConnection: Boolean = connected
        connected = newConnection
        val oldWIFI: Boolean = wifi
        var newWIFI = if (networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true) {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            app.isWifiConnected
        }
        wifi = newWIFI
        "networkChanged:oldConnection:$oldConnection, newConnection:$newConnection, oldWIFI:$oldWIFI, newWIFI:$newWIFI".d(TAG)
        callbacks.values.forEach {
            it.forEach { callback ->
                callback.onNetworkChanged(oldConnection, newConnection, oldWIFI, newWIFI)
            }
        }
    }

    @Synchronized
    fun register(owner: LifecycleOwner, callback: NetworkCallback) {
        if (!callbacks.containsKey(owner) || callbacks[owner] == null) {//用以保障每一个LifecycleOwner实例仅仅注册一次
            callbacks[owner] = hashSetOf()
            owner.onDestory {
                owner.destory(callback)//此处的this 不一定就是owner，当owner为Fragment时，this为Fragment所在的Activity，而owner仍然是Fragment
            }
        }
        callbacks[owner]?.let {
            "add[${callback.hashCode()}] owner:${owner.javaClass.canonicalName} callback:${callback.javaClass.canonicalName}".d(TAG)
            it.add(callback)
        }
    }

    @Synchronized
    private fun LifecycleOwner.destory(callback: NetworkCallback) {
        callbacks[this]?.let {
            it.remove(callback)
            if (it.isNullOrEmpty()) null
        } ?: kotlin.run {
            callbacks.remove(this)
        }
    }

    private lateinit var app: Application
    val connectivityManager
        get() = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    fun init(application: Application) {
        //为App注册NetworkCallback，仅注册一次。
        app = application
        application.apply {
            connected = isConnected
            wifi = isWifiConnected
            (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?)?.let { connectivityManager ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    connectivityManager.registerDefaultNetworkCallback(defaultCallback)
                } else {
                    val builder = NetworkRequest.Builder()
                    val request = builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                            .build()
                    connectivityManager.registerNetworkCallback(request, defaultCallback)
                }
            }
        }
    }

    fun destory(application: Application) {
        //为App注销NetworkCallback。
        (application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?)?.apply {
            unregisterNetworkCallback(defaultCallback)
        }
    }

}


