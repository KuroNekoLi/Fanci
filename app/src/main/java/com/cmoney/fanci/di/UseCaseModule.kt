package com.cmoney.fanci.di

import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import com.cmoney.fanci.model.usecase.GroupUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { ChatRoomUseCase() }
    factory { GroupUseCase() }
}