package com.cmoney.kolfanci.di

import com.cmoney.kolfanci.BuildConfig
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.notification.NotificationHelper
import com.cmoney.kolfanci.model.persistence.SettingsDataStore
import com.cmoney.kolfanci.model.persistence.dataStore
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    single {
        SettingsDataStore(
            androidContext().dataStore
        )
    }

    single {
        NotificationHelper(get(APP_GSON))
    }

    single {
        MixpanelAPI.getInstance(
            androidContext(),
            if (BuildConfig.DEBUG) "fakeMixpanelToken" else androidContext().getString(R.string.mixpanel_token)
        )
    }
}