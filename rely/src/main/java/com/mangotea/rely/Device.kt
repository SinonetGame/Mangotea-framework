package com.mangotea.rely

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import java.util.*

object DeviceIdUtil {
    /**
     * 获得设备硬件标识
     *
     * @param context 上下文
     * @return 设备硬件标识
     */
    val Context.deviceId: String
        get() {
            val sbDeviceId = StringBuilder()

//            //获得设备默认IMEI（>=6.0 需要ReadPhoneState权限）
//            val imei = getIMEI(this)
            //获得AndroidId（无需权限）
            val androidid = androidId
            //获得设备序列号（无需权限）
            val serial = SERIAL
            //获得硬件uuid（根据硬件相关属性，生成uuid）（无需权限）
            val uuid = deviceUUID.replace("-", "")

//            //追加imei
//            if (imei != null && imei.isNotEmpty()) {
//                sbDeviceId.append(imei)
//                sbDeviceId.append("|")
//            }
            //追加androidid
            if (androidid != null && androidid.isNotEmpty()) {
                sbDeviceId.append(androidid)
                sbDeviceId.append("|")
            }
            //追加serial
            if (serial != null && serial.isNotEmpty()) {
                sbDeviceId.append(serial)
                sbDeviceId.append("|")
            }
            //追加硬件uuid
            if (uuid != null && uuid.isNotEmpty()) {
                sbDeviceId.append(uuid)
            }

            //生成SHA1，统一DeviceId长度
            if (sbDeviceId.isNotEmpty()) {
                try {
                    val deviceIdStr = sbDeviceId.toString()
                    if (deviceIdStr != null && deviceIdStr.isNotEmpty()) {
                        //返回最终的DeviceId
                        return UUID.nameUUIDFromBytes(sbDeviceId.toString().toByteArray()).toString()
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            //如果以上硬件标识数据均无法获得，
            //则DeviceId默认使用系统随机数，这样保证DeviceId不为空
            return UUID.randomUUID().toString().replace("-", "")
        }

    //需要获得READ_PHONE_STATE权限，>=6.0，默认返回null
    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getIMEI(context: Context): String {
        try {
            val tm: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.getDeviceId()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

    /**
     * 获得设备的AndroidId
     *
     * @param context 上下文
     * @return 设备的AndroidId
     */
    val Context.androidId: String
        get() = kotlin.runCatching { Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID) }.getOrDefault("")

    /**
     * 获得设备序列号（如：WTK7N16923005607）, 个别设备无法获取
     *
     * @return 设备序列号
     */
    private val SERIAL: String
        private get() {
            var serial = runCatching { Build.SERIAL }.getOrNull()
            if (serial.isNullOrEmpty())
                serial = runCatching { android.os.Build::class.java.getField("SERIAL").get(null).toString() }.getOrNull()
            return serial ?: ""
        }

    /**
     * 获得设备硬件uuid
     * 使用硬件信息，计算出一个随机数
     *
     * @return 设备硬件uuid
     */
    private val deviceUUID: String
        private get() = try {
            val dev = "37" +
                    Build.BOARD.length % 10 + Build.BRAND.length % 10 +
                    Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 +
                    Build.DISPLAY.length % 10 + Build.HOST.length % 10 +
                    Build.ID.length % 10 + Build.MANUFACTURER.length % 10 +
                    Build.MODEL.length % 10 + Build.PRODUCT.length % 10 +
                    Build.TAGS.length % 10 + Build.TYPE.length % 10 +
                    Build.USER.length % 10 //13 位
            UUID(dev.hashCode().toLong(), SERIAL.hashCode().toLong()).toString()
        } catch (ex: Exception) {
            ex.printStackTrace()
            ""
        }
//    private val deviceUUID: String
//        private get() = try {
//            val dev = "3721077" +
//                    Build.BOARD.length % 10 + Build.BRAND.length % 10 +
//                    Build.DEVICE.length % 10 + Build.HARDWARE.length % 10 +
//                    Build.ID.length % 10 + Build.MODEL.length % 10 +
//                    Build.PRODUCT.length % 10 + Build.SERIAL.length % 10
//            UUID(dev.hashCode().toLong(), SERIAL.hashCode().toLong()).toString()
//        } catch (ex: Exception) {
//            ex.printStackTrace()
//            ""
//        }



    private fun uniquePsuedoID(serial: String): String {
        val devIDShort = "35" +
                Build.BOARD.length % 10 + Build.BRAND.length % 10 +
                Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 +
                Build.DISPLAY.length % 10 + Build.HOST.length % 10 +
                Build.ID.length % 10 + Build.MANUFACTURER.length % 10 +
                Build.MODEL.length % 10 + Build.PRODUCT.length % 10 +
                Build.TAGS.length % 10 + Build.TYPE.length % 10 +
                Build.USER.length % 10 //13 位

        //使用硬件信息拼凑出来的15位号码
        return UUID(devIDShort.hashCode().toLong(), SERIAL.hashCode().toLong()).toString() ?: "defaultNumber"
    }

}