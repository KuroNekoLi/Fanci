package com.cmoney.fanci.model.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cmoney.fanci.ui.screens.chat.viewmodel.ChatRoomViewModel

class ChatRoomViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatRoomViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatRoomViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}