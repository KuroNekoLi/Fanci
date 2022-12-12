package com.cmoney.fanci.di

import com.cmoney.fanci.MainViewModel
import com.cmoney.fanci.ui.screens.chat.message.viewmodel.MessageViewModel
import com.cmoney.fanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import com.cmoney.fanci.ui.screens.follow.viewmodel.FollowViewModel
import com.cmoney.fanci.ui.screens.group.setting.channel.viewmodel.ChannelSettingViewModel
import com.cmoney.fanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.fanci.ui.screens.group.viewmodel.GroupViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModule = module {
    viewModel { MainViewModel(get(), get()) }
    viewModel { FollowViewModel(get()) }
    viewModel { ChatRoomViewModel(get(), get()) }
    viewModel { MessageViewModel(get(), get(), get()) }
    viewModel { GroupViewModel(get()) }
    viewModel { GroupSettingViewModel(get(), get()) }
    viewModel { ChannelSettingViewModel(get()) }
}