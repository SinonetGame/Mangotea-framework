package com.mangotea.ipfs

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.Path

interface Ipfs {

    @POST("swarm/peers")
    suspend fun peers(): SwarmPeers

    @POST("config")
    suspend fun config(@Query("arg") key: String, @Query("arg") value: String): ResponseBody

    @POST("cat/{hash}")
    @Streaming
    fun cat(@Path("hash") hash: String): Call<ResponseBody>

    @POST("get/{hash}")
    suspend fun get(@Path("hash") hash: String): ResponseBody

    @POST("version")
    suspend fun version(): Version

    fun versionCall(): Call<Version>

    @POST("id")
    suspend fun id(): PeerID

    @POST("ls/{hash}")
    suspend fun ls(@Path("hash") hash: String): LinkObjects

    @POST("pin/ls")
    suspend fun pinLs(@Query("type") type: String = "recursive"): Map<String, Map<String, Any>>

    @POST("pin/add/{hash}")
    suspend fun addPin(@Path("hash") hash: String): ResponseBody

    @POST("pin/rm/{hash}")
    suspend fun removePin(@Path("hash") hash: String): PinRm

    @POST("pubsub/pub")
    suspend fun pub(@Query("arg") theme: String, @Query("arg") content: String): ResponseBody

    @POST("pubsub/sub/{theme}")
    suspend fun sub(@Path("theme") theme: String): ResponseBody

    @POST("stats/bw")
    suspend fun statsBw(@Query("peer") hash: String? = null, @Query("proto") proto: String? = null): BandWidthInfo

    @POST("stats/bitswap")
    suspend fun statBitswap(@Query("verbose") verbose: Boolean = false, @Query("human") human: Boolean = false): BitswapStats

    @POST("repo/stat")
    suspend fun statStat(): Stat

    @POST("repo/gc")
    suspend fun statGc(): ResponseBody

    @POST("diag/cmds")
    suspend fun diagCmds(): ResponseBody

    @POST("name/publish/{hash}")
    suspend fun namePublish(@Path("hash") hash: String): NameValue

    @POST("name/resolve/{hash}")
    suspend fun nameResolve(@Path("hash") hash: String): com.mangotea.ipfs.Path

    @POST("object/stat/{hash}")
    suspend fun objectStat(@Path("hash") hash: String): ObjectStat

    @Streaming
    @POST("{hash}")
    suspend fun resource(@Header("Range") range: String, @Path("hash") hash: String): ResponseBody

    @Streaming
    @POST("progress/{hash}")
    fun progress(@Path("hash") hash: String): Call<ResponseBody>

    @POST("iorate/{hash}")
    suspend fun ioRate(@Path("hash") hash: String): IORate

    @POST("share/on")
    suspend fun shareOn(): ResponseBody

    @POST("share/off")
    suspend fun shareOff(): ResponseBody

    @POST("swarm/connect")
    suspend fun connect(): ResponseBody

    @POST("swarm/connect")
    suspend fun connect(@Query("arg", encoded = true) address: String): ConnectResult

    @POST("swarm/connect")
    suspend fun connect(@Query("arg", encoded = true) address: Array<String>): ConnectResult

    @POST("swarm/disconnect")
    suspend fun disconnect(@Query("arg", encoded = true) address: String): ConnectResult

    @POST("swarm/disconnect")
    suspend fun disconnect(@Query("arg", encoded = true) address: Array<String>): ConnectResult

    @POST("remove/{hash}")
    suspend fun remove(@Path("hash") hash: String): ResponseBody

}