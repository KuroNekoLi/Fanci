package com.cmoney.fanci.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cmoney.fanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import org.koin.core.component.KoinComponent

class ChatRoomViewModelFactory() : ViewModelProvider.Factory, KoinComponent {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatRoomViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatRoomViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}