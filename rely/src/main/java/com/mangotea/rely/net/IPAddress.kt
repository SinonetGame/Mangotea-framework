package com.mangotea.rely.net

import com.mangotea.rely.d
import java.util.regex.Matcher
import java.util.regex.Pattern

val ipRegex = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)"

val CharSequence.findIp: String?
    get() {
        val ips: MutableList<String> = ArrayList()
        val p: Pattern = Pattern.compile(ipRegex)
        val m: Matcher = p.matcher(this)
        if (m.find()) {
            return m.group()
        }
        return null
    }

fun CharSequence.findIp(blo: (CharSequence) -> Unit) {
    findIp?.let(blo)
}

val CharSequence.findIps: List<CharSequence>
    get() {
        val ips = arrayListOf<CharSequence>()
        val p: Pattern = Pattern.compile(ipRegex)
        val m: Matcher = p.matcher(this)
        while (m.find()) {
            val result: String = m.group()
            ips.add(result)
        }
        return ips
    }

fun CharSequence.findIps(blo: (List<CharSequence>) -> Unit) {
    val list = findIps
    if (list.isNotEmpty())
        blo(list)
}

val CharSequence.isIPv4: Boolean get() = IPAddressUtil.isIPv4LiteralAddress(this.toString())

val CharSequence.isIPv6: Boolean get() = IPAddressUtil.isIPv6LiteralAddress(this.toString())

val CharSequence?.isPublicIp: Boolean
    get() = if (this == "127.0.0.1" || this == "localhost")
        false
    else !this.isInternalIp

val CharSequence?.isInternalIp: Boolean
    get() = if (isNullOrEmpty()) false else IPAddressUtil.textToNumericFormatV4(this.toString().apply {
        d("InternalIp[$this]", "ADDRESS")
    }).isInternalIp.apply {
        d("isInternalIp[$this]", "ADDRESS")
    }
val ByteArray.isInternalIp: Boolean
    get() {
        val b0 = this[0]
        val b1 = this[1]
        //10.x.x.x/8
        val section1: Byte = 0x0A
        //172.16.x.x/12
        val section2 = 0xAC.toByte()
        val section3 = 0x10.toByte()
        val section4 = 0x1F.toByte()
        //192.168.x.x/16
        val section5 = 0xC0.toByte()
        val section6 = 0xA8.toByte()
        return when (b0) {
            section1 -> true
            section2 -> {
                if (b1 in section3..section4) {
                    return true
                }
                when (b1) {
                    section6 -> return true
                }
                false
            }
            section5 -> {
                when (b1) {
                    section6 -> return true
                }
                false
            }
            else -> false
        }
    }
