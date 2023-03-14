package com.cmoney.kolfanci.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GroupViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return GroupViewModel(GroupUseCase()) as T
//        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}