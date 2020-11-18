package com.mangotea.ipfs

import com.mangotea.http.addGsonConverter
import com.mangotea.rely.Sub
import com.mangotea.rely.d
import okhttp3.OkHttpClient
import retrofit2.Retrofit

var apiPort: Int = 5001
var gatewayPort: Int = 8080
var swarmPort: Int = 4001
val host = "http://127.0.0.1"

val ROOT
    get() = "$host:$gatewayPort/ipfs/"

val String.ofIpfs
    get() = "$ROOT$this"

val String.ofIpfsVideo
    get() = "$ROOT$this?type=video"

private var _ipfsHttpBuilder: Sub<OkHttpClient.Builder>? = null

fun ipfsHttp(blo: Sub<OkHttpClient.Builder>) {
    _ipfsHttpBuilder = blo
}

val ipfs: Ipfs by lazy {
    Retrofit.Builder().apply {
        client(OkHttpClient.Builder().apply {
            _ipfsHttpBuilder?.let { it() }
        }.build())
        baseUrl("http://127.0.0.1:$apiPort/api/v0/")
        addGsonConverter()
    }.build().create(Ipfs::class.java)
}

