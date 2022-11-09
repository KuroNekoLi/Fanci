package com.cmoney.fanci.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanci.ui.screens.follow.viewmodel.FollowViewModel
import com.cmoney.fanci.ui.screens.group.viewmodel.GroupViewModel

class GroupViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroupViewModel(GroupUseCase()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}