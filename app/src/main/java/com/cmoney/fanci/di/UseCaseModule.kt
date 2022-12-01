package com.cmoney.fanci.di

import com.cmoney.fanci.model.usecase.ChatRoomPollUseCase
import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanci.model.usecase.UserUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val useCaseModule = module {
    factory { ChatRoomUseCase(get(), get()) }
    factory { GroupUseCase(androidApplication(), get(), get(), get()) }
    factory { UserUseCase(get()) }
    factory { ChatRoomPollUseCase(get()) }
}