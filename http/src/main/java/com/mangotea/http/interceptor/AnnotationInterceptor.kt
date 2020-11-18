package com.mangotea.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation

class AnnotationInterceptor(val customAnnotation: (Interceptor.Chain, Array<Annotation>) -> Response) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val retrofitMethod = request.tag(Invocation::class.java)?.method() ?: return chain.proceed(request)
        return customAnnotation(chain, retrofitMethod.annotations)
    }
}