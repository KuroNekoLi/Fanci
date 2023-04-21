package com.cmoney.kolfanci.ui.screens.post.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.api.ChatRoomApi
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.kolfanci.extension.checkResponseBody
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostViewModel(
    private val chatRoomApi: ChatRoomApi
): ViewModel() {
    private val TAG = PostViewModel::class.java.simpleName

    private val _post = MutableStateFlow<List<ChatMessage>>(emptyList())
    val post = _post.asStateFlow()

    fun fetchPost(channelId: String) {
        KLog.i(TAG, "fetchPost")
        viewModelScope.launch {
//            _post.value = chatRoomApi.apiV1ChatRoomChatRoomChannelIdMessageGet(
//                chatRoomChannelId = channelId,
//            ).checkResponseBody().items.orEmpty()

            _post.value = ChatRoomUseCase.mockListMessage

        }
    }


}