package com.cmoney.fanci.ui.screens.chat.message.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.model.ChatMessageWrapper
import com.cmoney.fanci.model.usecase.ChatRoomPollUseCase
import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class MessageUiState(
    val message: List<ChatMessageWrapper> = emptyList(),
)

class MessageViewModel(
    val chatRoomUseCase: ChatRoomUseCase,
    private val chatRoomPollUseCase: ChatRoomPollUseCase
) : ViewModel() {
    private val TAG = MessageViewModel::class.java.simpleName

    var uiState by mutableStateOf(MessageUiState())
        private set

    /**
     * 獲取 聊天室 訊息
     */
    fun startPolling(channelId: String?) {
        KLog.i(TAG, "startPolling:$channelId")
        viewModelScope.launch {
            if (channelId?.isNotEmpty() == true) {
                chatRoomPollUseCase.poll(3000, channelId).collect {
                    KLog.i(TAG, it)
                    uiState = uiState.copy(
                        message = it.items?.map { message ->
                            ChatMessageWrapper(
                                message = message,
                                haveNextPage = it.haveNextPage == true
                            )
                        }.orEmpty().reversed()
                    )
                }
            }
        }
    }

    /**
     * 停止 聊天室 訊息
     */
    fun stopPolling() {
        KLog.i(TAG, "stopPolling")
        chatRoomPollUseCase.close()
    }

    /**
     * 讀取更多 訊息
     * @param channelId 頻道id
     */
    fun onLoadMore(channelId: String) {
        KLog.i(TAG, "onLoadMore.")
        viewModelScope.launch {
            //訊息不為空,才抓取分頁,因為預設會有Polling訊息, 超過才需讀取分頁
            if (uiState.message.isNotEmpty()) {
                val lastMessage = uiState.message.last()

                val serialNumber = lastMessage.message.serialNumber
                chatRoomUseCase.fetchMoreMessage(
                    chatRoomChannelId = channelId,
                    fromSerialNumber = serialNumber,
                ).fold({
                    it.items?.also { message ->
                        //combine old message
                        val oldMessage = uiState.message.toMutableList()
                        val newMessage = message.map {
                            ChatMessageWrapper(message = it)
                        }.reversed()

                        oldMessage.addAll(newMessage)

                        val distinctMessage = oldMessage.distinctBy { combineMessage ->
                            combineMessage.message.id
                        }

                        uiState = uiState.copy(
                            message = distinctMessage,
                        )
                    }
                }, {
                    KLog.e(TAG, it)
                })
            }
        }
    }
}