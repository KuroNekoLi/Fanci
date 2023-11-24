package com.cmoney.kolfanci.model.usecase

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.cmoney.fanciapi.fanci.api.ChatRoomApi
import com.cmoney.fanciapi.fanci.api.MessageApi
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.MessageServiceType
import com.cmoney.fanciapi.fanci.model.OrderType
import com.cmoney.kolfanci.extension.checkResponseBody
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.screens.search.model.SearchChatMessage
import com.cmoney.kolfanci.ui.screens.search.model.SearchType
import com.cmoney.kolfanci.utils.Utils
import kotlin.random.Random

class SearchUseCase(
    private val messageApi: MessageApi,
    private val chatRoomApi: ChatRoomApi
) {

    /**
     * 搜尋
     *
     * @param keyword 關鍵字
     */
    suspend fun doSearch(keyword: String): Result<List<SearchChatMessage>> {
        return Result.success(
            MockData.mockListMessage.map {
                val randomType = Random.nextBoolean()
                val searchType = if (randomType) {
                    SearchType.Chat
                } else {
                    SearchType.Post
                }

                SearchChatMessage(
                    searchKeyword = keyword,
                    searchType = searchType,
                    message = it,
                    subTitle = getSearchMessageSubTitle(
                        searchType = searchType,
                        message = it
                    ),
                    highlightMessage = getHighlightTxt(
                        message = it.content?.text.orEmpty(),
                        keyword = keyword
                    )
                )
            }
        )
    }

    /**
     * 取得搜尋結果的 subtitle
     * ex: 聊天・2023.01.13
     */
    private fun getSearchMessageSubTitle(searchType: SearchType, message: ChatMessage): String {
        val typeString = when (searchType) {
            SearchType.Chat -> "聊天"
            SearchType.Post -> "貼文"
        }

        val createTime = message.createUnixTime?.div(1000) ?: 0
        val displayTime = Utils.getSearchDisplayTime(createTime)

        return "%s・%s".format(typeString, displayTime)
    }

    /**
     * 將關鍵字highlight動作
     *
     * @param message 原始訊息
     * @param keyword 要 highlight 的關鍵字
     */
    private fun getHighlightTxt(message: String, keyword: String): AnnotatedString {
        val span = SpanStyle(
            color = Color.Red,
            fontWeight = FontWeight.Bold
        )

        return buildAnnotatedString {
            var start = 0
            while (message.indexOf(
                    keyword,
                    start,
                    ignoreCase = true
                ) != -1 && keyword.isNotBlank()
            ) {
                val firstIndex = message.indexOf(keyword, start, true)
                val end = firstIndex + keyword.length
                append(message.substring(start, firstIndex))
                withStyle(style = span) {
                    append(message.substring(firstIndex, end))
                }
                start = end
            }
            append(message.substring(start, message.length))
            toAnnotatedString()
        }
    }

    /**
     * 取得單一貼文訊息
     */
    suspend fun getSinglePostMessage(messageId: String) = kotlin.runCatching {
        messageApi.apiV2MessageMessageTypeMessageIdGet(
            messageType = MessageServiceType.bulletinboard,
            messageId = messageId
        ).checkResponseBody()
    }

    /**
     *  根據原始訊息,抓取上下文各10則訊息串起來
     *
     * @param channelId 頻道id
     * @param message 要查詢的訊息
     */
    suspend fun getChatMessagePreload(channelId: String, message: ChatMessage): List<ChatMessage> {
        val preMessage =
            getPreMessage(
                channelId = channelId,
                serialNumber = message.serialNumber ?: 0L
            ).getOrNull().orEmpty()

        val backMessage =
            getBackMessage(
                channelId = channelId,
                serialNumber = message.serialNumber ?: 0L
            ).getOrNull().orEmpty()

        return buildList {
            addAll(preMessage)
            add(message)
            addAll(backMessage)
        }
    }

    /**
     * 往前抓取10則訊息
     */
    private suspend fun getPreMessage(channelId: String, serialNumber: Long) = kotlin.runCatching {
        //TODO
//        chatRoomApi.apiV1ChatRoomChatRoomChannelIdMessageGet(
//            chatRoomChannelId = channelId,
//            fromSerialNumber = serialNumber,
//            order = OrderType.oldest,
//            take = 10
//        ).checkResponseBody().items.orEmpty()

        MockData.mockListMessage
    }

    /**
     * 往後抓取10則訊息
     */
    private suspend fun getBackMessage(channelId: String, serialNumber: Long) = kotlin.runCatching {
        //TODO
//        chatRoomApi.apiV1ChatRoomChatRoomChannelIdMessageGet(
//            chatRoomChannelId = channelId,
//            fromSerialNumber = serialNumber,
//            order = OrderType.latest,
//            take = 10
//        ).checkResponseBody().items.orEmpty()

        MockData.mockListMessage
    }

}