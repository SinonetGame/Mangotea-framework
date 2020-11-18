package com.mangotea.http

import com.mangotea.cage.core.pileCage
import com.mangotea.rely.d
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.io.Serializable

class CookieLoader : CookieJar {
    private val TAG = "COOKIE"
    private val sessionId = "session-1"
    private val localCookies by pileCage<LocalCookie>(sessionId)

    private var cookies: MutableList<Cookie> = arrayListOf()
        get() {
            if (field.isEmpty()) {
                field.addAll(localCookies.toCookieList())
            }
            return field
        }
        set(value) {
            field = value
            localCookies.clear()
            localCookies.addAll(value.toLocalCookies())
        }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookies.let {
            if (it.isNotEmpty() && cookies != this.cookies) {
                this.cookies = it.toMutableList()
            }
        }
    }


    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies=ArrayList(this.cookies)
        "used current cookie $cookies".d(TAG)
        return cookies
    }


    private fun List<LocalCookie>.toCookieList(): MutableList<Cookie> {
        val cookies = arrayListOf<Cookie>()
        forEach {
            cookies.add(it.cookie)
        }
        "load local cookie $cookies".d(TAG)
        return cookies
    }

    private fun List<Cookie>.toLocalCookies(): List<LocalCookie> {
        val localCookies = arrayListOf<LocalCookie>()
        forEach {
            localCookies.add(LocalCookie(it))
        }
        "save new cookie $this".d(TAG)
        return localCookies
    }

    private data class LocalCookie(val name: String,
                                   val value: String,
                                   val expiresAt: Long,
                                   val domain: String,
                                   val path: String,
                                   val secure: Boolean,
                                   val httpOnly: Boolean,
                                   val hostOnly: Boolean,
                                   val persistent: Boolean) : Serializable {


        constructor(cookie: Cookie) : this(cookie.name, cookie.value, cookie.expiresAt, cookie.domain, cookie.path, cookie.secure, cookie.httpOnly, cookie.hostOnly, cookie.persistent)

        val cookie
            get() = Cookie.Builder().name(name).value(value).expiresAt(expiresAt).domain(domain)
                    .path(path).apply {
                        if (secure) secure()
                        if (httpOnly) httpOnly()
                    }.build()

        companion object {
            private const val serialVersionUID = 7352486L
        }

    }
}