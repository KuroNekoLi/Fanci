package com.cmoney.fanci.model

import com.cmoney.fanci.ui.screens.chat.viewmodel.ChatRoomUiState
import com.cmoney.fanciapi.fanci.model.ChatMessage

data class ChatMessageWrapper(
    val message: ChatMessage = ChatMessage(),
    val haveNextPage: Boolean = false,
    val uploadAttachPreview: List<ChatRoomUiState.ImageAttachState> = emptyList()
)
