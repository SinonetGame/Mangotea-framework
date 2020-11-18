package com.mangotea.rely.net

/**
 * Created by shuyu on 2016/8/10.
 *
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/8/2
</pre> *
 *
 */

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import com.github.pwittchen.reactivenetwork.library.rx2.ConnectivityPredicate
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


val NETWORK_WIFI = 1    // wifi network
val NETWORK_5G = 5      // "5G" networks
val NETWORK_4G = 4      // "4G" networks
val NETWORK_3G = 3      // "3G" networks
val NETWORK_2G = 2      // "2G" networks
val NETWORK_UNKNOWN = 6    // unknown network
val NETWORK_NO = -1   // no network

private val NETWORK_TYPE_GSM = 16
private val NETWORK_TYPE_TD_SCDMA = 17
private val NETWORK_TYPE_IWLAN = 18

/**
 * 打开网络设置界面
 *
 * 3.0以下打开设置界面
 *
 * @param context 上下文
 */
fun Context.openWirelessSettings() {
    if (android.os.Build.VERSION.SDK_INT > 10) {
        startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
    } else {
        startActivity(Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS))
    }
}

/**
获取活动网路信息
 *
 * @param context 上下文
 * @return NetworkInfo
 */
val Context.activeNetworkInfo: NetworkInfo?
    get() = (getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.activeNetworkInfo

/**
 * 判断网络是否可用
 *
 * 需添加权限 android.permission.ACCESS_NETWORK_STATE
 */
val Context.isAvailable: Boolean
    get() {
        val info = activeNetworkInfo
        return info != null && info.isAvailable
    }

/**
 * 判断网络是否连接
 *
 * 需添加权限 android.permission.ACCESS_NETWORK_STATE
 *
 * @param context 上下文
 * @return true: 是<br></br>false: 否
 */
val Context.isConnected: Boolean
    get() {
        val info = activeNetworkInfo
        return info?.isConnected ?: false
    }

/**
 * 判断wifi是否连接状态
 *
 * 需添加权限 android.permission.ACCESS_NETWORK_STATE
 *
 * @param context 上下文
 * @return true: 连接<br></br>false: 未连接
 */
val Context.isWifiConnected: Boolean
    get() {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return cm?.activeNetworkInfo?.type == ConnectivityManager.TYPE_WIFI
    }

/**
 * 获取移动网络运营商名称
 *
 * 如中国联通、中国移动、中国电信
 *
 * @param context 上下文
 * @return 移动网络运营商名称
 */
val Context.networkOperatorName: String?
    get() {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        return tm?.networkOperatorName
    }

/**
 * 获取移动终端类型
 *
 * @param context 上下文
 * @return 手机制式
 *
 *  * PHONE_TYPE_NONE  : 0 手机制式未知
 *  * PHONE_TYPE_GSM   : 1 手机制式为GSM，移动和联通
 *  * PHONE_TYPE_CDMA  : 2 手机制式为CDMA，电信
 *  * PHONE_TYPE_SIP   : 3
 *
 */
val Context.phoneType: Int
    get() {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        return tm?.phoneType ?: -1
    }


/**
 * 获取当前的网络类型(WIFI,2G,3G,4G)
 *
 * 需添加权限 android.permission.ACCESS_NETWORK_STATE
 *
 * @param context 上下文
 * @return 网络类型
 *
 *  * NETWORK_WIFI    = 1;
 *  * NETWORK_5G      = 5;
 *  * NETWORK_4G      = 4;
 *  * NETWORK_3G      = 3;
 *  * NETWORK_2G      = 2;
 *  * NETWORK_UNKNOWN = 6;
 *  * NETWORK_NO      = -1;
 *
 */
val Context.netWorkType: Int
    get() {
        var netType = NETWORK_NO
        val info = this.activeNetworkInfo
        if (info != null && info.isAvailable) {
            netType = when {
                info.type == ConnectivityManager.TYPE_WIFI -> NETWORK_WIFI
                info.type == ConnectivityManager.TYPE_MOBILE -> when (info.subtype) {
                    NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN ->
                        NETWORK_2G
                    NETWORK_TYPE_TD_SCDMA, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP ->
                        NETWORK_3G
                    NETWORK_TYPE_IWLAN, TelephonyManager.NETWORK_TYPE_LTE -> NETWORK_4G
                    20 -> NETWORK_5G
//TelephonyManager.NETWORK_TYPE_NR==20
                    else -> {
                        val subtypeName = info.subtypeName
                        if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
                                || subtypeName.equals("WCDMA", ignoreCase = true)
                                || subtypeName.equals("CDMA2000", ignoreCase = true)
                        ) {
                            NETWORK_3G
                        } else {
                            NETWORK_UNKNOWN
                        }
                    }
                }
                else -> NETWORK_UNKNOWN
            }
        }
        return netType
    }

/**
 * 获取当前的网络类型(WIFI,2G,3G,4G)
 *
 * 依赖上面的方法
 *
 * @param context 上下文
 * @return 网络类型名称
 *
 *  * NETWORK_WIFI
 *  * NETWORK_4G
 *  * NETWORK_3G
 *  * NETWORK_2G
 *  * NETWORK_UNKNOWN
 *  * NETWORK_NO
 *
 */
val Context.netWorkTypeName: String
    get() = when (this.netWorkType) {
        NETWORK_WIFI -> "WIFI"
        NETWORK_5G -> "5G"
        NETWORK_4G -> "4G"
        NETWORK_3G -> "3G"
        NETWORK_2G -> "2G"
        NETWORK_NO -> "NONE"
        else -> "UNKNOWN"
    }

var connected = false
    private set

var wifi = false
    private set

//fun LifecycleOwner.onNetworkChanged(networkChanged: ((oldConnection: Boolean, newConnection: Boolean, oldWifi: Boolean, newWifi: Boolean) -> Unit)) {
//    NetworkManager.register(this, object : NetworkCallback {
//        override fun onNetworkChanged(oldConnection: Boolean, newConnection: Boolean, oldWifi: Boolean, newWifi: Boolean) {
//            networkChanged(oldConnection, newConnection, oldWifi, newWifi)
//        }
//    })
//}

@SuppressLint("CheckResult")
fun Context.onNetworkChanged(networkChanged: ((newConnection: Boolean, newWifi: Boolean) -> Unit)) {
    kotlin.runCatching {
        ReactiveNetwork.observeNetworkConnectivity(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .filter(ConnectivityPredicate.hasState(NetworkInfo.State.CONNECTED, NetworkInfo.State.DISCONNECTED))
                .subscribe({
                    val isWifi = it.type() == ConnectivityManager.TYPE_WIFI
                    wifi = isWifi
                    val cc = it.state() == NetworkInfo.State.CONNECTED
                    connected = cc
                    networkChanged(cc, isWifi)
                }, {
                    com.mangotea.rely.e(it)
                })
    }
}

fun Application.initNetworkManager() = NetworkManager.init(this)

fun Application.destoryNetworkManager() = NetworkManager.destory(this)
