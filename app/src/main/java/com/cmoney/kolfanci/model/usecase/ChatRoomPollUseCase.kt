package com.cmoney.kolfanci.model.usecase

import com.cmoney.fanciapi.fanci.api.ChatRoomApi
import com.cmoney.fanciapi.fanci.model.ChatMessagePaging
import com.cmoney.fanciapi.fanci.model.OrderType
import com.cmoney.kolfanci.extension.checkResponseBody
import com.socks.library.KLog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

class ChatRoomPollUseCase(
    private val chatRoomApi: ChatRoomApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    Poller {
    private val TAG = ChatRoomPollUseCase::class.java.simpleName

    private var isClose = false

    private var isScopeClose = false

    private var index: Long? = null

    private val messageTakeSize = 20

    /**
     *  Latest: 往回取
     *  Oldest: 往後取
     */
    override fun poll(delay: Long, channelId: String, fromIndex: Long?): Flow<ChatMessagePaging> {
        isClose = false
        fromIndex?.apply {
            index = this
        }
        return channelFlow {
            while (!isClose) {
                kotlin.runCatching {
                    chatRoomApi.apiV1ChatRoomChatRoomChannelIdMessageGet(
                        chatRoomChannelId = channelId,
                        fromSerialNumber = index,
                        order = OrderType.oldest,
                        take = messageTakeSize
                    ).checkResponseBody()
                }.fold({
                    //當沒有結果, server 會回傳 -1, 確定這次分頁數量拿滿了, 才拿下一頁, 或是 server 跟前端說有下一頁
                    if (it.nextWeight != -1L && it.items?.size?.equals(messageTakeSize) == true || it.haveNextPage == true) {
                        index = it.nextWeight
                    }
                    send(it)
                }, {
                    KLog.e(TAG, it)
                })
                delay(delay)
            }
        }.flowOn(dispatcher)
    }

    override fun pollScope(
        delay: Long,
        channelId: String,
        fromSerialNumber: Long?,
        fetchCount: Int
    ): Flow<ChatMessagePaging> {
        isScopeClose = false

        return channelFlow {
            while (!isScopeClose) {
                kotlin.runCatching {
                    chatRoomApi.apiV1ChatRoomChatRoomChannelIdMessageGet(
                        chatRoomChannelId = channelId,
                        fromSerialNumber = fromSerialNumber,
                        order = OrderType.latest,
                        take = fetchCount
                    ).checkResponseBody()
                }.onSuccess {
                    send(it)
                }.onFailure {
                    KLog.e(TAG, it)
                }
                delay(delay)
            }
        }.flowOn(dispatcher)
    }

    override fun close() {
        isClose = true
        closeScope()
    }

    fun closeScope() {
        isScopeClose = true
    }
}

interface Poller {
    fun poll(delay: Long, channelId: String, fromIndex: Long? = null): Flow<ChatMessagePaging>

    /**
     * 範圍內刷新
     *
     * @param delay 延遲多久 millisecond
     * @param channelId 頻道id
     * @param fromSerialNumber find index
     * @param fetchCount 抓取筆數
     */
    fun pollScope(
        delay: Long,
        channelId: String,
        fromSerialNumber: Long? = null,
        fetchCount: Int
    ): Flow<ChatMessagePaging>

    fun close()
}