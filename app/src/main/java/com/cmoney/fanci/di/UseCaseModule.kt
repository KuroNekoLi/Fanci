package com.cmoney.fanci.di

import com.cmoney.fanci.model.usecase.*
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val useCaseModule = module {
    factory { ChatRoomUseCase(get(), get()) }
    factory { GroupUseCase(androidApplication(), get(), get(), get()) }
    factory { UserUseCase(get()) }
    factory { ChatRoomPollUseCase(get()) }
    factory { ThemeUseCase(get(), get()) }
    factory { ChannelUseCase(get(), get()) }
}