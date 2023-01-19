package com.cmoney.fanci.di

import com.cmoney.fanci.MainViewModel
import com.cmoney.fanci.ui.screens.chat.message.viewmodel.MessageViewModel
import com.cmoney.fanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import com.cmoney.fanci.ui.screens.follow.viewmodel.FollowViewModel
import com.cmoney.fanci.ui.screens.group.create.viewmodel.CreateGroupViewModel
import com.cmoney.fanci.ui.screens.group.search.apply.viewmodel.ApplyForGroupViewModel
import com.cmoney.fanci.ui.screens.group.search.viewmodel.DiscoverViewModel
import com.cmoney.fanci.ui.screens.group.setting.apply.viewmodel.GroupApplyViewModel
import com.cmoney.fanci.ui.screens.group.setting.ban.viewmodel.BanListViewModel
import com.cmoney.fanci.ui.screens.group.setting.group.channel.viewmodel.ChannelSettingViewModel
import com.cmoney.fanci.ui.screens.group.setting.group.openness.viewmodel.GroupOpennessViewModel
import com.cmoney.fanci.ui.screens.group.setting.member.role.viewmodel.RoleManageViewModel
import com.cmoney.fanci.ui.screens.group.setting.report.viewmodel.GroupReportViewModel
import com.cmoney.fanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.fanci.ui.screens.shared.member.viewmodel.MemberViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { FollowViewModel(get()) }
    viewModel { ChatRoomViewModel(get(), get(), get()) }
    viewModel { MessageViewModel(get(), get(), get()) }
    viewModel { DiscoverViewModel(get()) }
    viewModel { GroupSettingViewModel(get(), get(), get()) }
    viewModel { ChannelSettingViewModel(get(), get()) }
    viewModel { RoleManageViewModel(get()) }
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
        CreateGroupViewModel(get())
    }
    viewModel { ApplyForGroupViewModel(get(), get()) }
    viewModel { params ->
        GroupReportViewModel(
            get(),
            reportList = params.get()
        )
    }
}