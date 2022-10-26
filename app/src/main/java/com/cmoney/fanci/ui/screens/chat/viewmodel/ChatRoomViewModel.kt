package com.cmoney.fanci.ui.screens.chat.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class ChatRoomUiState(
    val imageAttach: List<Uri> = emptyList(),
    val replyMessage: ChatMessageModel.Reply? = null,
    val message: List<ChatMessageModel> = emptyList()
)

class ChatRoomViewModel() : ViewModel() {
    private val TAG = ChatRoomViewModel::class.java.simpleName

    var uiState by mutableStateOf(ChatRoomUiState())
        private set

    init {
        viewModelScope.launch {
            val chatRoomUseCase = ChatRoomUseCase()
            uiState = uiState.copy(message = chatRoomUseCase.createMockMessage())
        }
    }

    /**
     * 點擊 回覆
     */
    fun replyMessage(message: ChatMessageModel) {
        KLog.i(TAG, "replyMessage click:$message")

        uiState = uiState.copy(
            replyMessage = ChatMessageModel.Reply(
                replyUser = message.poster,
                text = message.message.text
            )
        )
    }

    /**
     * 輸入文字
     */
    fun messageInput(text: String) {
        if (text.isNotEmpty()) {
            val orgList = uiState.message.toMutableList()
            orgList.add(
                ChatMessageModel(
                    poster = ChatMessageModel.User(
                        avatar = "https://picsum.photos/102/102",
                        nickname = "TIGER"
                    ),
                    publishTime = System.currentTimeMillis(),
                    message = ChatMessageModel.Message(
                        reply = null,
                        text = text,
                        media = null,
                        emoji = null
                    )
                ),
            )
            uiState = uiState.copy(message = orgList)
        }
    }

    /**
     * 回覆 附加 圖片
     */
    fun attachImage(uri: Uri) {
        KLog.i(TAG, "attachImage:$uri")
        val imageList = uiState.imageAttach.toMutableList()
        imageList.add(uri)

        uiState = uiState.copy(
            imageAttach = imageList
        )
    }

    /**
     * 移除 附加 圖片
     */
    fun removeAttach(uri: Uri) {
        KLog.i(TAG, "removeAttach:$uri")
        val imageList = uiState.imageAttach.toMutableList()

        uiState = uiState.copy(
            imageAttach = imageList.filter {
                it != uri
            }
        )
    }

    /**
     * 取消 回覆
     */
    fun removeReply(reply: ChatMessageModel.Reply) {
        KLog.i(TAG, "removeReply:$reply")
        uiState = uiState.copy(
            replyMessage = null
        )
    }

}