package com.cmoney.kolfanci.repository.interceptor

import android.os.Build
import com.cmoney.kolfanci.BuildConfig
import com.cmoney.xlogin.XLoginHelper
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response

class AddBearerTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val host = chain.request().url.host
        val bearerToken = XLoginHelper.accessToken
        val headers = Headers.Builder()
            .addUnsafeNonAscii("Authorization", "Bearer $bearerToken")
            .addUnsafeNonAscii("os", "android")
            .addUnsafeNonAscii("os-version", Build.VERSION.SDK_INT.toString())
            .addUnsafeNonAscii("app-version", BuildConfig.VERSION_NAME)

        builder.headers(headers.build())

        return chain.proceed(builder.build())
    }
}