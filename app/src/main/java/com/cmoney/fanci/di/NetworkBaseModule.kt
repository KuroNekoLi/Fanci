package com.cmoney.fanci.di

import android.app.Application
import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.cmoney.fanci.BuildConfig
import com.cmoney.fanci.repository.Network
import com.cmoney.fanci.repository.NetworkImpl
import com.cmoney.fanciapi.fanci.api.GroupApi
import com.cmoney.fanciapi.fanci.api.GroupMemberApi
import com.cmoney.fanciapi.fanci.api.UserApi
import com.cmoney.fanciapi.infrastructure.ApiClient
import com.cmoney.xlogin.XLoginHelper
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val APP_GSON = named("app_gson")
val networkBaseModule = module {

    single(APP_GSON) {
        GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .setLenient()
            .create()
    }

//    single<Retrofit> {
//        Retrofit.Builder()
//            .baseUrl(
//                getDomain(androidApplication())
//            )
//            .client(createOkHttpClient(androidApplication()))
//            .addConverterFactory(GsonConverterFactory.create(get(APP_GSON)))
//            .build()
//    }

//    single<FanciService> {
//        get<Retrofit>().create(FanciService::class.java)
//    }

    single<Network> {
        NetworkImpl(
            groupApi = get()
        )
    }

    single {
        ApiClient(
            baseUrl = getDomain(androidApplication()),
            okHttpClientBuilder = createOkHttpClient(androidApplication()).newBuilder(),
            authNames = arrayOf("Bearer"),
        ).apply {
            setBearerToken(XLoginHelper.accessToken)
        }
    }

    single {
        get<ApiClient>().createService(GroupApi::class.java)
    }

    single {
        get<ApiClient>().createService(GroupMemberApi::class.java)
    }

    single {
        get<ApiClient>().createService(UserApi::class.java)
    }
}

private fun getDomain(context: Context): String {
//    return context.getString(R.string.server_url)
    return "http://34.80.212.140/fanci/"
}

private fun createOkHttpClient(
    context: Application,
): OkHttpClient {
    val cacheSize = 10 * 1024 * 1024L
    val cache = Cache(context.cacheDir, cacheSize)
    return OkHttpClient.Builder()
        .connectionSpecs(listOf(ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
        .apply {
            if (BuildConfig.DEBUG) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(interceptor)
                val chuckerInterceptor = ChuckerInterceptor.Builder(context)
                    .collector(ChuckerCollector(context))
                    .maxContentLength(250000L)
                    .redactHeaders(emptySet())
                    .alwaysReadResponseBody(false)
                    .build()
                addInterceptor(chuckerInterceptor)
            }
            connectTimeout(30L, TimeUnit.SECONDS)
            callTimeout(30L, TimeUnit.SECONDS)
            readTimeout(30L, TimeUnit.SECONDS)
            writeTimeout(30L, TimeUnit.SECONDS)
            cache(cache)
        }
        .build()
}