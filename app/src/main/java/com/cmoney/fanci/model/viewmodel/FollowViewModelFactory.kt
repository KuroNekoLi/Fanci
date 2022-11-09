package com.cmoney.fanci.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanci.ui.screens.follow.viewmodel.FollowViewModel

class FollowViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FollowViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FollowViewModel(GroupUseCase()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}