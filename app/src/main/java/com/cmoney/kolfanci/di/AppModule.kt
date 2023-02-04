package com.cmoney.kolfanci.di

import com.cmoney.kolfanci.model.persistence.SettingsDataStore
import com.cmoney.kolfanci.model.persistence.dataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    single {
        SettingsDataStore(
            androidContext().dataStore
        )
    }

}