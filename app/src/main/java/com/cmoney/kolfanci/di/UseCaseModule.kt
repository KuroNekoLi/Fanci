package com.cmoney.kolfanci.di

import com.cmoney.kolfanci.model.usecase.*
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val useCaseModule = module {
    factory { ChatRoomUseCase(get(), get(), get()) }
    factory {
        GroupUseCase(
            androidApplication(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    factory { UserUseCase(get()) }
    factory { ChatRoomPollUseCase(get()) }
    factory { ThemeUseCase(get(), get()) }
    factory { ChannelUseCase(get(), get(), get(), get(), get()) }
    factory { BanUseCase(get()) }
    factory { GroupApplyUseCase(get(), get()) }
    factory { RelationUseCase(get()) }
    factory { PermissionUseCase(get(), get()) }
    factory { OrderUseCase(get()) }
    factory { PostUseCase(get()) }
    factory { DynamicLinkUseCase(androidApplication(), get()) }
    factory { VipManagerUseCase(get(), get(), get(), get()) }
    factory { SearchUseCase(get(), get()) }
    factory { UploadImageUseCase(androidApplication(), get()) }
    factory {
        NotificationUseCase(
            context = androidApplication(),
            network = get(),
            settingsDataStore = get(),
            chatRoomApi = get(),
            bulletinBoardApi = get(),
            pushNotificationApi = get()
        )
    }
    factory { AttachmentUseCase(get()) }
}