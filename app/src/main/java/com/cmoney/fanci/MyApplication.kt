package com.cmoney.fanci

import android.util.Log
import com.cmoney.backend2.base.di.BACKEND2_SETTING
import com.cmoney.backend2.base.model.setting.Platform
import com.cmoney.backend2.base.model.setting.Setting
import com.cmoney.data_logdatarecorder.recorder.LogDataRecorder
import com.cmoney.fanci.di.appModule
import com.cmoney.fanci.di.useCaseModule
import com.cmoney.fanci.di.viewModule
import com.cmoney.xlogin.XLoginApplication
import com.flurry.android.FlurryAgent
import com.flurry.android.FlurryPerformance
import com.google.firebase.FirebaseApp
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
                appModule
            )
        )
        // 設定Backend2
        getKoin().get<Setting>(BACKEND2_SETTING).apply { // Setup for apis
            appId = resources.getInteger(R.integer.app_id)
            clientId = getString(R.string.app_client_id)
            platform = Platform.Android
            appVersion = BuildConfig.VERSION_NAME
            domainUrl = getString(R.string.cm_server_url)
        }

        // 根據需求設定是否紀錄API
        LogDataRecorder.initialization(this) {
            isEnable = false
            appId = resources.getInteger(R.integer.app_id)
            platform = com.cmoney.domain_logdatarecorder.data.information.Platform.Android
        }
    }
}