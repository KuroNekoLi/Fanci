package com.cmoney.fanci.model.usecase

import com.cmoney.fanci.extension.checkResponseBody
import com.cmoney.fanciapi.fanci.api.ChatRoomApi
import com.cmoney.fanciapi.fanci.model.ChatMessagePaging
import com.cmoney.fanciapi.fanci.model.OrderType
import com.socks.library.KLog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class ChatRoomPollUseCase(
    private val chatRoomApi: ChatRoomApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    Poller {
    private val TAG = ChatRoomPollUseCase::class.java.simpleName
    private var isClose = false

    override fun poll(delay: Long, channelId: String): Flow<ChatMessagePaging> {
        isClose = false
        return channelFlow {
            while (!isClose) {
                kotlin.runCatching {
                    chatRoomApi.apiV1ChatRoomChatRoomChannelIdMessageGet(
                        chatRoomChannelId = channelId
//                        order = OrderType.latest
                    ).checkResponseBody()
                }.fold({
                    send(it)
                }, {
                    KLog.e(TAG, it)
                })
                delay(delay)
            }
        }
    }

    override fun close() {
        isClose = true
    }
}

interface Poller {
    fun poll(delay: Long, channelId: String): Flow<ChatMessagePaging>

    fun close()
}