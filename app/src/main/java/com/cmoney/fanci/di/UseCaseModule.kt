package com.cmoney.fanci.di

import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { ChatRoomUseCase() }
}