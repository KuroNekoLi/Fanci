package com.cmoney.kolfanci.di

import com.cmoney.kolfanci.model.viewmodel.UserViewModel
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
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.avatar.GroupSettingAvatarViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.group.openness.viewmodel.GroupOpennessViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.member.role.viewmodel.RoleManageViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.report.viewmodel.GroupReportViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.kolfanci.ui.screens.post.edit.viewmodel.EditPostViewModel
import com.cmoney.kolfanci.ui.screens.post.info.viewmodel.PostInfoViewModel
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.MemberViewModel
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.RoleViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModule = module {
    viewModel { MainViewModel(get(), get(), get(), get()) }
    viewModel { FollowViewModel(get()) }
    viewModel { ChatRoomViewModel(get(), get(), get(), get(), get()) }
    viewModel { MessageViewModel(androidApplication(), get(), get(), get()) }
    viewModel { DiscoverViewModel(get()) }
    viewModel { GroupSettingViewModel(get(), get(), get()) }

    viewModel {
        ChannelSettingViewModel(get(), get())
    }

    viewModel { RoleManageViewModel(get(), get()) }
    viewModel { MemberViewModel(get(), get()) }
    viewModel { BanListViewModel(get()) }
    viewModel { GroupApplyViewModel(get()) }
    viewModel { params ->
        GroupOpennessViewModel(
            group = params.get(),
            get()
        )
    }
    viewModel {
        CreateGroupViewModel(androidApplication(), get())
    }
    viewModel { ApplyForGroupViewModel(get(), get()) }
    viewModel { params ->
        GroupReportViewModel(
            get(),
            reportList = params.get(),
            group = params.get(),
            banUseCase = get()
        )
    }
    viewModel {
        GroupSettingAvatarViewModel()
    }
    viewModel {
        RoleViewModel(get(), get(), get())
    }
    viewModel {
        UserViewModel(androidApplication())
    }
    viewModel { params ->
        PostViewModel(get(), params.get(), get())
    }
    viewModel { params ->
        EditPostViewModel(
            androidApplication(),
            get(),
            params.get()
        )
    }
    viewModel {
        ChannelViewModel(get())
    }
    viewModel { params ->
        PostInfoViewModel(get(), params.get())
    }
}