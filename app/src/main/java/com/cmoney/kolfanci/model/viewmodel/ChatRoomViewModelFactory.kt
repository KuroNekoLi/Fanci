package com.cmoney.kolfanci.model.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatRoomViewModelFactory(val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(ChatRoomViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return ChatRoomViewModel(context, ChatRoomUseCase()) as T
//        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}