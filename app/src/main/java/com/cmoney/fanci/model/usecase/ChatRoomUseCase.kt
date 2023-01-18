package com.cmoney.fanci.model.usecase

import com.cmoney.fanci.extension.checkResponseBody
import com.cmoney.fanciapi.fanci.api.ChatRoomApi
import com.cmoney.fanciapi.fanci.api.MessageApi
import com.cmoney.fanciapi.fanci.api.UserReportApi
import com.cmoney.fanciapi.fanci.model.*


class ChatRoomUseCase(
    private val chatRoomApi: ChatRoomApi,
    private val messageApi: MessageApi,
    private val userReport: UserReportApi
) {
    private val TAG = ChatRoomUseCase::class.java.simpleName

    /**
     * 檢舉內容
     * @param channelId 頻道 id
     * @param contentId 哪一篇文章
     * @param reason 原因
     */
    suspend fun reportContent(
        channelId: String,
        contentId: String,
        reason: ReportReason
    ) = kotlin.runCatching {
        userReport.apiV1UserReportChannelChannelIdPost(
            channelId = channelId,
            reportParm = ReportParm(
                contentId = contentId,
                reason = reason
            )
        ).checkResponseBody()
    }


    /**
     * 取得 公告 訊息
     * @param channelId 頻道id
     */
    suspend fun getAnnounceMessage(channelId: String) = kotlin.runCatching {
        chatRoomApi.apiV1ChatRoomChatRoomChannelIdPinnedMessageGet(
            chatRoomChannelId = channelId
        ).checkResponseBody()
    }

    /**
     * 設定 公告 訊息
     * @param chatMessage 訊息
     */
    suspend fun setAnnounceMessage(channelId: String, chatMessage: ChatMessage) =
        kotlin.runCatching {
            chatRoomApi.apiV1ChatRoomChatRoomChannelIdPinnedMessagePut(
                chatRoomChannelId = channelId,
                messageIdParam = MessageIdParam(
                    messageId = chatMessage.id.orEmpty()
                )
            ).checkResponseBody()
        }

    /**
     * 針對指定訊息 發送 Emoji
     * @param messageId 針對的訊息Id
     * @param emoji 要發送的 Emoji
     */
    suspend fun sendEmoji(messageId: String, emoji: Emojis) =
        kotlin.runCatching {
            messageApi.apiV1MessageMessageIdEmojiPut(
                messageId = messageId,
                emojiParam = EmojiParam(
                    emoji = emoji
                )
            ).checkResponseBody()
        }

    /**
     * 收回 Emoji
     * @param messageId 訊息 id
     */
    suspend fun deleteEmoji(messageId: String) = kotlin.runCatching {
        messageApi.apiV1MessageMessageIdEmojiDelete(
            messageId = messageId
        ).checkResponseBody()
    }

    /**
     * 讀取更多 分頁訊息
     * @param chatRoomChannelId 聊天室 id
     * @param fromSerialNumber 從哪一個序列號開始往回找 (若為Null 則從最新開始拿)
     */
    suspend fun fetchMoreMessage(chatRoomChannelId: String, fromSerialNumber: Long?) =
        kotlin.runCatching {
            chatRoomApi.apiV1ChatRoomChatRoomChannelIdMessageGet(
                chatRoomChannelId = chatRoomChannelId,
                fromSerialNumber = fromSerialNumber
            ).checkResponseBody()
        }

    /**
     * 發送訊息
     * @param chatRoomChannelId 聊天室 id
     * @param text 內文
     * @param images 附加圖片
     * @param replyMessageId 回覆訊息的id
     */
    suspend fun sendMessage(
        chatRoomChannelId: String,
        text: String,
        images: List<String> = emptyList(),
        replyMessageId: String = ""
    ) =
        kotlin.runCatching {
            chatRoomApi.apiV1ChatRoomChatRoomChannelIdMessagePost(
                chatRoomChannelId = chatRoomChannelId,
                chatMessageParam = ChatMessageParam(
                    text = text,
                    messageType = MessageType.textMessage,
                    medias = images.map {
                        Media(
                            resourceLink = it,
                            type = MediaType.image
                        )
                    },
                    replyMessageId = replyMessageId
                )
            ).checkResponseBody()
        }

    /**
     * 收回訊息
     * @param messageId 訊息 id
     */
    suspend fun recycleMessage(
        messageId: String
    ) = kotlin.runCatching {
        messageApi.apiV1MessageMeMessageIdDelete(messageId = messageId).checkResponseBody()
    }

    companion object MockData {
        val mockMessage = ChatMessage(
            author = GroupMember(
                name = "Name"
            ),
            content = MediaIChatContent(
                text = "Content"
            )
        )
    }

}