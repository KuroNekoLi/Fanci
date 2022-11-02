package com.cmoney.fanci.model.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cmoney.fanci.ui.screens.chat.viewmodel.ChatRoomViewModel

class ChatRoomViewModelFactory(val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatRoomViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatRoomViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}