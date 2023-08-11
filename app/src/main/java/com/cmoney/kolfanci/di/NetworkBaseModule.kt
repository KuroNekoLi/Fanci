package com.cmoney.kolfanci.di

import android.app.Application
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.cmoney.kolfanci.BuildConfig
import com.cmoney.kolfanci.repository.interceptor.AddBearerTokenInterceptor
import com.cmoney.fanciapi.fanci.api.*
import com.cmoney.fanciapi.infrastructure.ApiClient
import com.cmoney.kolfanci.model.remoteconfig.BaseDomainKey
import com.cmoney.remoteconfig_library.extension.getKeyValue
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
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

    single {
        ApiClient(
            baseUrl = getDomain(),
            okHttpClientBuilder = createOkHttpClient(androidApplication()).newBuilder(),
        ).apply {
            addAuthorization("Bearer", AddBearerTokenInterceptor())
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

    single {
        get<ApiClient>().createService(ChatRoomApi::class.java)
    }

    single {
        get<ApiClient>().createService(MessageApi::class.java)
    }

    single {
        get<ApiClient>().createService(DefaultImageApi::class.java)
    }

    single {
        get<ApiClient>().createService(ThemeColorApi::class.java)
    }

    single {
        get<ApiClient>().createService(CategoryApi::class.java)
    }

    single {
        get<ApiClient>().createService(ChannelApi::class.java)
    }

    single {
        get<ApiClient>().createService(PermissionApi::class.java)
    }

    single {
        get<ApiClient>().createService(RoleUserApi::class.java)
    }

    single {
        get<ApiClient>().createService(BanApi::class.java)
    }

    single {
        get<ApiClient>().createService(GroupApplyApi::class.java)
    }

    single {
        get<ApiClient>().createService(GroupRequirementApi::class.java)
    }

    single {
        get<ApiClient>().createService(RelationApi::class.java)
    }

    single {
        get<ApiClient>().createService(UserReportApi::class.java)
    }

    single {
        get<ApiClient>().createService(OrderApi::class.java)
    }

    single {
        get<ApiClient>().createService(BuffInformationApi::class.java)
    }

    single {
        get<ApiClient>().createService(ChannelTabApi::class.java)
    }

    single {
        get<ApiClient>().createService(BulletinBoardApi::class.java)
    }

    single {
        get<ApiClient>().createService(VipApi::class.java)
    }
}

private fun getDomain(): String {
    val baseDomain = FirebaseRemoteConfig.getInstance().getKeyValue(BaseDomainKey).ifBlank {
        BuildConfig.FANCI_SERVER_URL
    }
    return baseDomain
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