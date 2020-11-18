package com.mangotea.ipfs

import java.io.Serializable

data class BandWidthInfo(
        val TotalIn: Long,
        val TotalOut: Long,
        val RateIn: Double,
        val RateOut: Double
)

data class PinRm(val Pins: List<String>)

data class BitswapStats(val BlocksReceived: Long,
                        val BlocksSent: Long,
                        val DataReceived: Long,
                        val DataSent: Long,
                        val DupBlksReceived: Long,
                        val DupDataReceived: Long,
                        val MessagesReceived: Long,
                        val Peers: List<String>,
                        val ProvideBufLen: Long,
                        val Wantlist: List<Wantlist>)

data class Wantlist(val key: String?, val value: String?)

data class Link(val Name: String, val Hash: String, val Size: Long, val Type: Int)

data class LinkObject(val Hash: String, val Links: List<Link>)

data class LinkObjects(val Objects: List<LinkObject>)

data class PinList(val Objects: List<LinkObject>)

data class MessageWithCode(val Message: String, val Code: Int)

data class NamedHash(val Name: String, val Hash: String)

data class NameValue(val Name: String, val Value: String)

data class ObjectStat(
        var Hash: String? = null,
        var NumLinks: Int = 0,
        var BlockSize: Long = 0,
        var LinksSize: Int = 0,
        var CumulativeSize: Long = 0,
        var DataSize: Int = 0
)

data class Path(val Path: String)

data class Peer(
        val Addr: String,
        val Peer: String,
        val Latency: String,
        val Muxer: String,
        val Streams: ArrayList<Protocol>?
)

data class PeerID(
        val ID: String,
        val PublicKey: String,
        val Addresses: List<String>,
        val AgentVersion: String,
        val ProtocolVersion: String
) : Serializable

data class Protocol(val Protocol: String)

data class Stat(@JvmField var NumObjects: Long, @JvmField var RepoSize: Long, @JvmField var StorageMax: Long, @JvmField var RepoPath: String, @JvmField var Version: String)

data class SwarmPeers(val Peers: ArrayList<Peer>?)

data class Version(
        val Version: String,
        val Commit: String,
        val System: String,
        val Golang: String,
        val Sn: SinoVersion
)

data class IORate(
        val rate: Long,
        val human: String
)

data class ConnectResult(val Strings: List<String>) {
    val success
        get() = connects.takeIf { it.size == Strings.size }?.let {
            it.find { !it.success }?.let { false } ?: true
        } ?: false
    val connects
        get() = arrayListOf<Connect>().apply {
            Strings.forEach {
                it.toConnect?.let { connect ->
                    add(connect)
                }
            }
        }

    private val String.toConnect
        get() = kotlin.runCatching {
            split(" ").let {
                if (it.size > 2) {
                    val sb = StringBuilder("")
                    for (i in 2 until it.size)
                        sb.append(it[i]).append(" ")
                    Connect(it[1], sb.toString().trim(), it[0] == "connect")
                } else null
            }
        }.getOrNull()

    override fun toString(): String {
        return connects.toString()
    }
}

data class Connect(val Peer: String, val message: String, val isConnect: Boolean) {
    val isDisconnect
        get() = !isConnect
    val success: Boolean
        get() = message == "success"

    override fun toString(): String {
        return "{Peer:\"$Peer\",success:$success,message:$message}"
    }
}

//{"Strings":["connect QmWHp2yAocRdNPU4ZTBgfVSfGGrDLFx5KV5ZLc864GgXUA success"]}

data class SinoVersion(val Version: String, val Patch: Int, val Minor: Int, val Major: Int) {
    override fun equals(other: Any?): Boolean {
        if (other != null && other is SinoVersion) {
            return other.Version == Version && other.Patch == Patch && other.Minor == Minor && other.Major == Major
        }
        return super.equals(other)
    }

    companion object {
        const val unit = 100
        fun of(code: Int): SinoVersion {
            val major: Int = code / unit / unit
            val minor: Int = (code - major * unit * unit) / unit
            val patch: Int = code - major * unit * unit - minor * unit
            val version = "$major.$minor.$patch"
            return SinoVersion(version, patch, minor, major)
        }

        fun of(version: String?): SinoVersion {
            var vCode = 0
            var vUnit = unit * unit
            version?.split(".")?.forEach {
                vCode += runCatching {
                    it.toInt() * vUnit
                }.getOrElse {
                    return@of of(0)
                }
                if (vUnit > 1)
                    vUnit /= unit
                if (vUnit < 1)
                    vUnit = 1
            }
            return of(vCode)
        }
    }
}

operator fun Int.compareTo(currentVersion: SinoVersion?): Int = SinoVersion.of(this).compareTo(currentVersion)
operator fun String.compareTo(currentVersion: SinoVersion?): Int = SinoVersion.of(this).compareTo(currentVersion)
fun SinoVersion.toInt() = Major * SinoVersion.unit * SinoVersion.unit + Minor * SinoVersion.unit + Patch

operator fun SinoVersion.compareTo(other: Any?): Int {
    return when (other) {
        is SinoVersion -> toInt().compareTo(other.toInt())
        is Int -> toInt().compareTo(other)
        is String -> {
            runCatching { other.toInt() }.getOrElse {
                if (Version == other) 0 else toInt()
            }
        }
        else -> toInt()
    }
}

operator fun SinoVersion.compareTo(other: Int?) = toInt().compareTo(other ?: 0)
operator fun SinoVersion.compareTo(other: SinoVersion?) = compareTo(other?.toInt())
operator fun SinoVersion.compareTo(other: String?) = compareTo(SinoVersion.of(other))