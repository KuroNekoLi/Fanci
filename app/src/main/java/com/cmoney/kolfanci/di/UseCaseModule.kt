package com.cmoney.kolfanci.di

import com.cmoney.kolfanci.model.usecase.*
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val useCaseModule = module {
    factory { ChatRoomUseCase(get(), get(), get()) }
    factory { GroupUseCase(androidApplication(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { UserUseCase(get()) }
    factory { ChatRoomPollUseCase(get()) }
    factory { ThemeUseCase(get(), get()) }
    factory { ChannelUseCase(get(), get(), get(), get(), get()) }
    factory { BanUseCase(get()) }
    factory { GroupApplyUseCase(get()) }
    factory { RelationUseCase(get()) }
    factory { PermissionUseCase(get(), get()) }
    factory { OrderUseCase(get()) }
    factory { PostUseCase(get()) }
    factory { DynamicLinkUseCase(get()) }
    factory { VipManagerUseCase(get(), get(), get(), get()) }
    factory { SearchUseCase(get(), get()) }
    factory { UploadImageUseCase(androidApplication(), get()) }
}