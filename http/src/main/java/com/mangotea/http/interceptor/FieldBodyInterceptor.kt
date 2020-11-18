package com.mangotea.http.interceptor

import com.mangotea.http.annotation.FiledBody
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import java.lang.reflect.*

class FieldBodyInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val retrofitMethod = request.tag(Invocation::class.java)?.method() ?: return chain.proceed(request)
        retrofitMethod.annotations.forEach { annotation ->
            if (annotation is FiledBody) {
//                validateResolvableType(p, type)
//                val query = annotation as Query
//                val name: String = query.value()
//                val encoded: Boolean = query.encoded()
//
//                val rawParameterType = getRawType(type)
//                gotQuery = true
//                return if (Iterable::class.java.isAssignableFrom(rawParameterType)) {
//                    if (type !is ParameterizedType) {
//                        throw parameterError(
//                            method, p, rawParameterType.simpleName
//                                    + " must include generic type (e.g., "
//                                    + rawParameterType.simpleName
//                                    + "<String>)"
//                        )
//                    }
//                    val parameterizedType = type as ParameterizedType
//                    val iterableType = getParameterUpperBound(0, parameterizedType)
//                    val converter: Converter<*, String> = retrofit.stringConverter(iterableType, annotations)
//                    ParameterHandler.Query<Any>(name, converter, encoded).iterable()
//                } else if (rawParameterType.isArray) {
//                    val arrayComponentType = RequestFactory.Builder.boxIfPrimitive(rawParameterType.componentType)
//                    val converter: Converter<*, String> = retrofit.stringConverter(arrayComponentType, annotations)
//                    ParameterHandler.Query<Any>(name, converter, encoded).array()
//                } else {
//                    val converter: Converter<*, String> = retrofit.stringConverter(type, annotations)
//                    ParameterHandler.Query<Any>(name, converter, encoded)
//                }
            }
        }

        return chain.proceed(request)
    }

    private fun validateResolvableType(p: Int, type: Type, method: Method) {
        if (hasUnresolvableType(type)) {
            throw parameterError(method, p, "Parameter type must not include a type variable or wildcard: %s", type)
        }
    }

    private fun methodError(method: Method?, message: String?, vararg args: Any?): Throwable {
        return methodError(method, null, message, *args)
    }

    private fun parameterError(method: Method?, p: Int, message: String, vararg args: Any?): Throwable {
        return methodError(method, message + " (parameter #" + (p + 1) + ")", *args)
    }

    private fun hasUnresolvableType(type: Type?): Boolean {
        if (type is Class<*>) {
            return false
        }
        if (type is ParameterizedType) {
            for (typeArgument in type.actualTypeArguments) {
                if (hasUnresolvableType(typeArgument)) {
                    return true
                }
            }
            return false
        }
        if (type is GenericArrayType) {
            return hasUnresolvableType(type.genericComponentType)
        }
        if (type is TypeVariable<*>) {
            return true
        }
        if (type is WildcardType) {
            return true
        }
        val className = if (type == null) "null" else type.javaClass.name
        throw IllegalArgumentException(
            "Expected a Class, ParameterizedType, or "
                    + "GenericArrayType, but <" + type + "> is of type " + className
        )
    }
}