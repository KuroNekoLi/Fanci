package com.cmoney.kolfanci.di

import com.cmoney.kolfanci.model.usecase.AttachmentUseCase
import com.cmoney.kolfanci.model.usecase.BanUseCase
import com.cmoney.kolfanci.model.usecase.ChannelUseCase
import com.cmoney.kolfanci.model.usecase.ChatRoomPollUseCase
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.model.usecase.DynamicLinkUseCase
import com.cmoney.kolfanci.model.usecase.GroupApplyUseCase
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.cmoney.kolfanci.model.usecase.NotificationUseCase
import com.cmoney.kolfanci.model.usecase.OrderUseCase
import com.cmoney.kolfanci.model.usecase.PermissionUseCase
import com.cmoney.kolfanci.model.usecase.PostUseCase
import com.cmoney.kolfanci.model.usecase.RelationUseCase
import com.cmoney.kolfanci.model.usecase.SearchUseCase
import com.cmoney.kolfanci.model.usecase.ThemeUseCase
import com.cmoney.kolfanci.model.usecase.UploadImageUseCase
import com.cmoney.kolfanci.model.usecase.UserUseCase
import com.cmoney.kolfanci.model.usecase.VipManagerUseCase
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
    factory {
        PostUseCase(
            context = androidApplication(),
            get()
        )
    }
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
    factory {
        AttachmentUseCase(
            context = androidApplication(),
            network = get()
        )
    }
}