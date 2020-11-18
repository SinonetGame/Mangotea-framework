@file:Suppress("ObjectPropertyName")

package com.mangotea.http

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mangotea.http.interceptor.AnnotationInterceptor
import com.mangotea.http.interceptor.TimeoutInterceptor
import com.mangotea.rely.Sub
import com.mangotea.rely.TIME_OF_FULL_DEFAULT
import com.mangotea.rely.d
import com.mangotea.rely.e
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URLEncoder
import kotlin.reflect.KClass


object Http {
    internal lateinit var _client: OkHttpClient
    internal lateinit var _api: Retrofit
    internal lateinit var _gson: Gson
    private var inited = false
    fun init(
            gsonBuilder: Sub<GsonBuilder>? = null,
            httpBuilder: Sub<OkHttpClient.Builder>? = null,
            apiBuilder: Sub<Retrofit.Builder>? = null
    ) {
        if (inited)
            throw RuntimeException("Http can not be init again!")
        _gson = GsonBuilder().apply {
            setDateFormat(TIME_OF_FULL_DEFAULT)
            gsonBuilder?.let { it() }
        }.create()

        _client = OkHttpClient.Builder().apply {
            addInterceptor(TimeoutInterceptor())
            httpBuilder?.let {
                it()
            }
        }.build()

        _api = Retrofit.Builder().apply {
            client(_client)
            apiBuilder?.let { it() }
        }.build()
        inited = true
    }


}

inline fun OkHttpClient.Builder.interceptor(crossinline block: (chain: Interceptor.Chain) -> okhttp3.Response) =
        addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response = block(chain)
        })

inline fun OkHttpClient.Builder.annotationInterceptor(crossinline block: (chain: Interceptor.Chain, Array<Annotation>) -> okhttp3.Response) =
        addInterceptor(AnnotationInterceptor { chain, annotations ->
            block(chain, annotations)
        })


fun Retrofit.Builder.addGsonConverter() {
    this.addConverterFactory(GsonConverterFactory.create(Http._gson))
}

fun Request.Builder.encodedHeader(name: String, value: String) = header(name, encoded(value))

fun Request.Builder.addEncodedHeader(name: String, value: String) = addHeader(name, encoded(value))
private fun encoded(value: String?): String {
    if (value == null) return "null"
    var i = 0
    val sb = java.lang.StringBuilder(value.replace("\n", ""))
    var length = sb.length
    while (i < length) {
        val c = sb[i]
        if (c <= '\u001f' || c >= '\u007f') {
            val nc = URLEncoder.encode(c.toString(), "UTF-8")
            sb.replace(i, i + 1, nc)
            i += nc.length
            length = sb.length
            continue
        }
        i++
    }
    return sb.toString()
}

@Suppress("UPPER_BOUND_VIOLATED")
val <T> KClass<T>.api: T
    @JvmName("getJavaClass")
    get() = com.mangotea.http.api.create(this.java)

val client by lazy { Http._client }

val api by lazy { Http._api }

val gson by lazy { Http._gson }

fun body(vararg params: Pair<String, Any?>, tag: String = "requestBody"): RequestBody {
    val jsonContentBuilder = StringBuilder().append("{")
    params.forEachIndexed { index, pair ->
        jsonContentBuilder.append("\"${pair.first}\":")
                .append(gson.toJson(pair.second))
        if (index != params.lastIndex) jsonContentBuilder.append(",")
    }
    jsonContentBuilder.append("}")
    val jsonContent = jsonContentBuilder.toString()
    d(jsonContent, tag)
    return jsonContent.toRequestBody("application/json".toMediaTypeOrNull())
}

private val PING_TAG = "HTTP-PING"

fun ping(host: String, pingCount: Int = 1, str: String? = "ping"): Boolean {
    val stringBuffer = StringBuffer(str)
    var line: String
    var process: Process? = null
    var successReader: BufferedReader? = null
    //        String command = "ping -c " + pingCount + " -w 5 " + host;
    val command = "ping -c $pingCount $host"
    var isSuccess = false
    try {
        process = Runtime.getRuntime().exec(command)
        if (process == null) {
            e("ping fail:process is null.", PING_TAG)
            stringBuffer.appendLine("ping fail:process is null.")
            return false
        }
        successReader = BufferedReader(InputStreamReader(process.inputStream))
        while (successReader.readLine().also { line = it } != null) {
            line.d(PING_TAG)
            stringBuffer.appendLine(line)
        }
        val status = process.waitFor()
        isSuccess = if (status == 0) {
            d("exec cmd success:$command", PING_TAG)
            stringBuffer.appendLine("exec cmd success:$command")
            true
        } else {
            e("exec cmd fail.", PING_TAG)
            stringBuffer.appendLine("exec cmd fail.")
            false
        }
        d("exec finished.", PING_TAG)
        stringBuffer.appendLine("exec finished.")
    } catch (e: Throwable) {
        e(e, PING_TAG)
    } catch (e: InterruptedException) {
        e(e, PING_TAG)
    } finally {
        d("ping exit.", PING_TAG)
        process?.destroy()
        if (successReader != null) {
            try {
                successReader.close()
            } catch (e: Throwable) {
                e(e, PING_TAG)
            }
        }
    }
    return isSuccess
}

private fun StringBuffer.appendLine(text: String) {
    append(text).append("\n")
}