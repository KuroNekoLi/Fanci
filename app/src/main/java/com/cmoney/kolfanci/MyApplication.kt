package com.cmoney.kolfanci

import android.app.Application
import com.cmoney.application_user_behavior.AnalyticsAgent
import com.cmoney.application_user_behavior.di.analyticsModule
import com.cmoney.backend2.base.model.manager.GlobalBackend2Manager
import com.cmoney.backend2.base.model.setting.Platform
import com.cmoney.backend2.di.backendServicesModule
import com.cmoney.crypto.di.CryptoHelper
import com.cmoney.data_logdatarecorder.recorder.LogDataRecorder
import com.cmoney.kolfanci.di.appModule
import com.cmoney.kolfanci.di.networkBaseModule
import com.cmoney.kolfanci.di.useCaseModule
import com.cmoney.kolfanci.di.viewModule
import com.cmoney.loginlibrary.di.loginModule
import com.cmoney.loginlibrary.di.loginServiceModule
import com.cmoney.loginlibrary.di.loginSharedPreferencesModule
import com.cmoney.loginlibrary.di.memberProfileCacheModule
import com.cmoney.loginlibrary.di.visitBindRepositoryModule
import com.cmoney.loginlibrary.di.visitBindViewModelModule
import com.cmoney.member.application.di.CMoneyMemberServiceLocator
import com.cmoney.remoteconfig_library.IRemoteConfig
import com.google.firebase.FirebaseApp
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

class MyApplication : Application() {
    companion object {
        lateinit var instance: MyApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CryptoHelper.registerAead()
        startKoin {
            androidContext(this@MyApplication)
            loadKoinModules(
                listOf(
                    // 此為登入模組需要的網路或快取定義
                    memberProfileCacheModule,
                    // 登入模組內的DI
                    loginModule,
                    // 華為/Google的定義
                    loginServiceModule,
                    // 訪客使用的API定義
                    visitBindRepositoryModule,
                    visitBindViewModelModule,
                    // 登入提供的SharedPreference定義，需要位於 backendServicesModule 下
                    loginSharedPreferencesModule,
                    // backend2
                    backendServicesModule,
                    viewModule,
                    useCaseModule,
                    appModule,
                    networkBaseModule,
                    analyticsModule
                )
            )
        }

        FirebaseApp.initializeApp(this)

        // 設定Backend2
        val remoteConfig = getKoin().get<IRemoteConfig>()
        val cmServer = remoteConfig.getApiConfig().serverUrl.ifBlank {
            BuildConfig.CM_SERVER_URL
        }

        val globalBackend2Manager = getKoin().get<GlobalBackend2Manager>().apply {
            setGlobalDomainUrl(cmServer)
            setPlatform(Platform.Android)
            setClientId(getString(R.string.app_client_id))
            setAppId(resources.getInteger(R.integer.app_id))
        }

        // 根據需求設定是否紀錄API
        LogDataRecorder.initialization(this) {
            isEnable = false
            appId = resources.getInteger(R.integer.app_id)
            platform = com.cmoney.domain_logdatarecorder.data.information.Platform.Android
        }

        CMoneyMemberServiceLocator.initialGoogle(
            context = this,
            identityWeb = get(),
            profileWeb = get(),
            commonWeb = get(),
            notificationWeb = get(),
            globalBackend2Manager = globalBackend2Manager
        )

        AnalyticsAgent.initialization(context = this) {
            platform = com.cmoney.domain_user_behavior.data.information.Platform.Android
            appId = resources.getInteger(R.integer.app_id)
        }
    }
}