package com.cmoney.kolfanci.model.usecase

import com.cmoney.fanciapi.fanci.api.BulletinBoardApi
import com.cmoney.fanciapi.fanci.api.MessageApi
import com.cmoney.fanciapi.fanci.model.BulletinboardMessagePaging
import com.cmoney.fanciapi.fanci.model.MessageServiceType
import com.cmoney.fanciapi.fanci.model.OrderType
import com.cmoney.kolfanci.extension.checkResponseBody
import com.cmoney.kolfanci.extension.toBulletinboardMessage
import com.socks.library.KLog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn

class PostPollUseCase(
    private val bulletinBoardApi: BulletinBoardApi,
    private val messageApi: MessageApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val TAG = ChatRoomPollUseCase::class.java.simpleName

    private var isScopeClose = false

    fun pollScope(
        delay: Long,
        channelId: String,
        fromSerialNumber: Long?,
        fetchCount: Int,
        messageId: String,
    ): Flow<BulletinboardMessagePaging> {
        KLog.i(TAG, "pollScope")

        isScopeClose = false

        return channelFlow {
            while (!isScopeClose) {
                kotlin.runCatching {
                    bulletinBoardApi.apiV1BulletinBoardChannelIdMessageGet(
                        channelId = channelId,
                        take = fetchCount,
                        order = OrderType.latest,
                        fromSerialNumber = fromSerialNumber
                    ).checkResponseBody()
                }.onSuccess {
                    send(it)
                }.onFailure {
                    KLog.e(TAG, it)
                }

                //取得單篇文章, 因為取得一系列的資料, 不會包含自己
                kotlin.runCatching {
                    messageApi.apiV2MessageMessageTypeMessageIdGet(
                        messageType = MessageServiceType.bulletinboard,
                        messageId = messageId
                    ).checkResponseBody()
                }.onSuccess { chatMessage ->
                    chatMessage.toBulletinboardMessage()
                    send(BulletinboardMessagePaging(items = listOf(chatMessage.toBulletinboardMessage())))
                }.onFailure {
                    KLog.e(TAG, it)
                }

                delay(delay)
            }
        }.flowOn(dispatcher)
    }

    fun closeScope() {
        isScopeClose = true
    }
}