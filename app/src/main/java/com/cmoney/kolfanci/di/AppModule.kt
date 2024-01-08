package com.cmoney.kolfanci.di

import android.content.ComponentName
import com.cmoney.kolfanci.BuildConfig
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.notification.NotificationHelper
import com.cmoney.kolfanci.model.persistence.SettingsDataStore
import com.cmoney.kolfanci.model.persistence.dataStore
import com.cmoney.kolfanci.service.media.MusicMediaService
import com.cmoney.kolfanci.service.media.MusicServiceConnection
import com.cmoney.kolfanci.service.media.RecorderAndPlayer
import com.cmoney.kolfanci.service.media.RecorderAndPlayerImpl
import com.cmoney.remoteconfig_library.IRemoteConfig
import com.cmoney.remoteconfig_library.RemoteConfigImpl
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
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
        NotificationHelper(androidApplication(), get(APP_GSON))
    }

    single {
        MixpanelAPI.getInstance(
            androidContext(),
            if (BuildConfig.DEBUG) "fakeMixpanelToken" else androidContext().getString(R.string.mixpanel_token)
        )
    }

    single<IRemoteConfig> {
        RemoteConfigImpl(
            context = androidApplication(),
            firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        )
    }

    single {
        MusicServiceConnection.getInstance(
            androidContext(),
            ComponentName(androidContext(), MusicMediaService::class.java)
        )
    }
    single<RecorderAndPlayer> {
        RecorderAndPlayerImpl(
            context = androidApplication()
        )
    }
}