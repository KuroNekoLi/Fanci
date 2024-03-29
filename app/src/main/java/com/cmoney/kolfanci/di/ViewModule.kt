package com.cmoney.kolfanci.di

import com.cmoney.kolfanci.model.viewmodel.AttachmentViewModel
import com.cmoney.kolfanci.model.viewmodel.GroupViewModel
import com.cmoney.kolfanci.model.viewmodel.NotificationViewModel
import com.cmoney.kolfanci.model.viewmodel.UserViewModel
import com.cmoney.kolfanci.ui.SplashViewModel
import com.cmoney.kolfanci.ui.main.MainViewModel
import com.cmoney.kolfanci.ui.screens.channel.ChannelViewModel
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.MessageViewModel
import com.cmoney.kolfanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import com.cmoney.kolfanci.ui.screens.follow.viewmodel.FollowViewModel
import com.cmoney.kolfanci.ui.screens.group.create.viewmodel.CreateGroupViewModel
import com.cmoney.kolfanci.ui.screens.group.search.apply.viewmodel.ApplyForGroupViewModel
import com.cmoney.kolfanci.ui.screens.group.search.viewmodel.DiscoverViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.apply.viewmodel.GroupApplyViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.ban.viewmodel.BanListViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.group.channel.viewmodel.ChannelSettingViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.avatar.GroupSettingImageViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.group.notification.NotificationSettingViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.group.openness.viewmodel.GroupOpennessViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.member.role.viewmodel.RoleManageViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.report.viewmodel.GroupReportViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.viewmodel.VipManagerViewModel
import com.cmoney.kolfanci.ui.screens.media.audio.AudioViewModel
import com.cmoney.kolfanci.ui.screens.media.audio.RecordingViewModel
import com.cmoney.kolfanci.ui.screens.media.txt.TextPreviewViewModel
import com.cmoney.kolfanci.ui.screens.my.MyScreenViewModel
import com.cmoney.kolfanci.ui.screens.notification.NotificationCenterViewModel
import com.cmoney.kolfanci.ui.screens.post.edit.viewmodel.EditPostViewModel
import com.cmoney.kolfanci.ui.screens.post.info.viewmodel.PostInfoViewModel
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
import com.cmoney.kolfanci.ui.screens.search.viewmodel.SearchViewModel
import com.cmoney.kolfanci.ui.screens.shared.bottomSheet.mediaPicker.MediaPickerBottomSheetViewModel
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.MemberViewModel
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.RoleViewModel
import com.cmoney.kolfanci.ui.screens.shared.vip.viewmodel.VipPlanViewModel
import com.cmoney.kolfanci.ui.screens.vote.viewmodel.VoteViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModule = module {
    viewModel { SplashViewModel(get(), get()) }
    viewModel { MainViewModel(get(), get(), get(), get()) }
    viewModel {
        FollowViewModel(
            groupUseCase = get(),
            notificationUseCase = get(),
            dataStore = get(),
            groupApplyUseCase = get()
        )
    }
    viewModel { ChatRoomViewModel(get(), get(), get(), get()) }
    viewModel {
        MessageViewModel(
            androidApplication(),
            get(),
            get(),
            get()
        )
    }
    viewModel { DiscoverViewModel(get()) }
    viewModel {
        GroupSettingViewModel(
            groupUseCase = get(),
            themeUseCase = get(),
            groupApplyUseCase = get(),
            notificationUseCase = get()
        )
    }

    viewModel {
        ChannelSettingViewModel(get(), get())
    }

    viewModel { RoleManageViewModel(get(), get()) }
    viewModel { MemberViewModel(
        context = androidApplication(),
        get(), get(), get()) }
    viewModel { BanListViewModel(get()) }
    viewModel { GroupApplyViewModel(get()) }
    viewModel { params ->
        GroupOpennessViewModel(
            group = params.get(),
            get()
        )
    }
    viewModel {
        CreateGroupViewModel(androidApplication(), get(), get(), get(), settingsDataStore = get())
    }
    viewModel { ApplyForGroupViewModel(get(), get()) }
    viewModel { params ->
        GroupReportViewModel(
            get(),
            reportList = params.get(),
            group = params.get(),
            banUseCase = get(),
            chatRoomUseCase = get()
        )
    }
    viewModel {
        GroupSettingImageViewModel()
    }
    viewModel {
        RoleViewModel(get(), get(), get())
    }
    viewModel {
        UserViewModel(androidApplication())
    }
    viewModel { params ->
        PostViewModel(get(), params.get(), chatRoomUseCase = get(), postPollUseCase = get())
    }
    viewModel { params ->
        EditPostViewModel(
            androidApplication(),
            get(),
            get(),
            params.get(),
            get()
        )
    }
    viewModel {
        ChannelViewModel(
            notificationUseCase = get()
        )
    }
    viewModel { params ->
        PostInfoViewModel(
            androidApplication(),
            get(),
            get(),
            params.get(),
            params.get(),
            postPollUseCase = get()
        )
    }
    viewModel {
        GroupViewModel(
            themeUseCase = get(),
            groupUseCase = get(),
            channelUseCase = get(),
            permissionUseCase = get(),
            orderUseCase = get(),
            groupApplyUseCase = get(),
            notificationUseCase = get()
        )
    }
    viewModel { params ->
        VipManagerViewModel(
            group = params.get(),
            vipManagerUseCase = get()
        )
    }
    viewModel {
        VipPlanViewModel(
            vipManagerUseCase = get()
        )
    }
    viewModel {
        MyScreenViewModel(androidApplication(), get())
    }
    viewModel {
        SearchViewModel(get())
    }
    viewModel {
        NotificationCenterViewModel(get(), get())
    }
    viewModel {
        NotificationSettingViewModel(androidApplication(), get())
    }
    viewModel {
        NotificationViewModel(get(), get(), get(), get())
    }
    viewModel {
        MediaPickerBottomSheetViewModel(
            context = androidApplication()
        )
    }
    viewModel { params ->
        AudioViewModel(
            context = androidApplication(),
            musicServiceConnection = get(),
            uri = params.get()
        )
    }
    viewModel {
        AttachmentViewModel(
            context = androidApplication(),
            attachmentUseCase = get(),
            uploadImageUseCase = get(),
            voteUseCase = get()
        )
    }
    viewModel {
        TextPreviewViewModel(
            attachmentUseCase = get()
        )
    }
    viewModel {
        VoteViewModel(
            context = androidApplication(),
            voteUseCase = get(),
            groupUseCase = get()
        )
    }
    viewModel {
        RecordingViewModel(get())
    }
}