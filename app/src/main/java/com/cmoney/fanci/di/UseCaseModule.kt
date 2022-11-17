package com.cmoney.fanci.di

import com.cmoney.fanci.model.usecase.ChatRoomPollUseCase
import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanci.model.usecase.UserUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { ChatRoomUseCase() }
    factory { GroupUseCase(get(), get()) }
    factory { UserUseCase(get()) }
    factory { ChatRoomPollUseCase(get()) }
}