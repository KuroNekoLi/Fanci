package com.cmoney.kolfanci

import android.util.Log
import com.cmoney.application_user_behavior.AnalyticsAgent
import com.cmoney.application_user_behavior.di.analyticsModule
import com.cmoney.backend2.base.di.BACKEND2_SETTING
import com.cmoney.backend2.base.model.setting.Platform
import com.cmoney.backend2.base.model.setting.Setting
import com.cmoney.backend2.di.backendServicesModule
import com.cmoney.data_logdatarecorder.recorder.LogDataRecorder
import com.cmoney.kolfanci.di.appModule
import com.cmoney.kolfanci.di.networkBaseModule
import com.cmoney.kolfanci.di.useCaseModule
import com.cmoney.kolfanci.di.viewModule
import com.cmoney.member.application.di.CMoneyMemberServiceLocator
import com.cmoney.xlogin.XLoginApplication
import com.flurry.android.FlurryAgent
import com.flurry.android.FlurryPerformance
import com.google.firebase.FirebaseApp
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.core.context.loadKoinModules

class MyApplication : XLoginApplication() {
    companion object {
        lateinit var instance: MyApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)

        if (!BuildConfig.DEBUG) {
            FlurryAgent.Builder()
                .withLogEnabled(BuildConfig.DEBUG)
                .withDataSaleOptOut(false)
                .withCaptureUncaughtExceptions(true)
                .withIncludeBackgroundSessionsInMetrics(true)
                .withLogLevel(Log.VERBOSE)
                .withPerformanceMetrics(FlurryPerformance.ALL)
                .build(this, getString(R.string.flurry_api_key))
        }
        loadKoinModules(
            listOf(
                viewModule,
                useCaseModule,
                appModule,
                networkBaseModule,
                backendServicesModule,
                analyticsModule
            )
        )
        // 設定Backend2
        getKoin().get<Setting>(BACKEND2_SETTING).apply { // Setup for apis
            appId = resources.getInteger(R.integer.app_id)
            clientId = getString(R.string.app_client_id)
            platform = Platform.Android
            domainUrl = BuildConfig.CM_SERVER_URL
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
            notificationWeb = get()
        ) { backend2Setting ->
            backend2Setting.appId = resources.getInteger(R.integer.app_id)
            backend2Setting.clientId = getString(R.string.app_client_id)
            backend2Setting.domainUrl = BuildConfig.CM_SERVER_URL
            backend2Setting.platform = Platform.Android
        }

        AnalyticsAgent.initialization(context = this) {
            platform = com.cmoney.domain_user_behavior.data.information.Platform.Android
            appId = resources.getInteger(R.integer.app_id)
        }
    }
}