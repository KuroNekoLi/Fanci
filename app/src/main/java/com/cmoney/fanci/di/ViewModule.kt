package com.cmoney.fanci.di

import com.cmoney.fanci.MainViewModel
import com.cmoney.fanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import com.cmoney.fanci.ui.screens.follow.viewmodel.FollowViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { FollowViewModel(get()) }
    viewModel { ChatRoomViewModel(get(), get()) }
}