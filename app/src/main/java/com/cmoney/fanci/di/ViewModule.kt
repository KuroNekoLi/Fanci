package com.cmoney.fanci.di

import com.cmoney.fanci.ui.screens.follow.FollowViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModule = module {
    viewModel { FollowViewModel() }
}