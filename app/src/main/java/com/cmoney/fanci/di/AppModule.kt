package com.cmoney.fanci.di

import com.cmoney.fanci.model.persistence.SettingsDataStore
import com.cmoney.fanci.model.persistence.dataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    single {
        SettingsDataStore(
            androidContext().dataStore
        )
    }

}