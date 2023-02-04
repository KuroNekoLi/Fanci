package com.cmoney.kolfanci.model

import com.cmoney.kolfanci.ui.screens.chat.viewmodel.ChatRoomUiState
import com.cmoney.fanciapi.fanci.model.ChatMessage

data class ChatMessageWrapper(
    val message: ChatMessage = ChatMessage(),
    val haveNextPage: Boolean = false,
    val uploadAttachPreview: List<ChatRoomUiState.ImageAttachState> = emptyList(),
    val isBlocking: Boolean = false,    //我封鎖
    val isBlocker: Boolean = false,    //我被封鎖
)
