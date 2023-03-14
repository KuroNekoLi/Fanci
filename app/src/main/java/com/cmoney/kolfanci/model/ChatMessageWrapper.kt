package com.cmoney.kolfanci.model

import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.kolfanci.ui.screens.chat.viewmodel.ChatRoomUiState

data class ChatMessageWrapper(
    val message: ChatMessage = ChatMessage(),
    val haveNextPage: Boolean = false,
    val uploadAttachPreview: List<ChatRoomUiState.ImageAttachState> = emptyList(),
    val isBlocking: Boolean = false,    //我封鎖
    val isBlocker: Boolean = false,     //我被封鎖
    val isPendingSendMessage: Boolean = false   //未送出的訊息
//    val pendingSendMessage: List<PendingSendMessage> = emptyList()  //未送出的訊息
)
