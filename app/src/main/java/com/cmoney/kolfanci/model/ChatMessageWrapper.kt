package com.cmoney.kolfanci.model

import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.ImageAttachState

data class ChatMessageWrapper(
    val message: ChatMessage = ChatMessage(),
    val haveNextPage: Boolean = false,
    val uploadAttachPreview: List<ImageAttachState> = emptyList(),
    val isBlocking: Boolean = false,    //我封鎖
    val isBlocker: Boolean = false,     //我被封鎖
    val isPendingSendMessage: Boolean = false,   //未送出的訊息
    val messageType: MessageType = MessageType.Default
) {
    /**
     * 聊天室 呈現內容 類型
     */
    sealed class MessageType {
        object TimeBar : MessageType()   //時間 Bar
        object Blocking : MessageType()  //用戶已被你封鎖
        object Blocker : MessageType()   //用戶已將您封鎖
        object Delete : MessageType()    //被刪除
        object RecycleMessage : MessageType()    //收回訊息
        object Default : MessageType()   //聊天室內文
    }

}
