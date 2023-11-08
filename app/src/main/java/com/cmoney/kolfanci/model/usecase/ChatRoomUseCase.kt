package com.cmoney.kolfanci.model.usecase

import android.content.Context
import com.cmoney.fanciapi.fanci.api.ChatRoomApi
import com.cmoney.fanciapi.fanci.api.MessageApi
import com.cmoney.fanciapi.fanci.api.UserReportApi
import com.cmoney.fanciapi.fanci.model.ChannelTabType
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.ChatMessageParam
import com.cmoney.fanciapi.fanci.model.EmojiParam
import com.cmoney.fanciapi.fanci.model.Emojis
import com.cmoney.fanciapi.fanci.model.Media
import com.cmoney.fanciapi.fanci.model.MediaType
import com.cmoney.fanciapi.fanci.model.MessageIdParam
import com.cmoney.fanciapi.fanci.model.MessageServiceType
import com.cmoney.fanciapi.fanci.model.MessageType
import com.cmoney.fanciapi.fanci.model.OrderType
import com.cmoney.fanciapi.fanci.model.ReportParm
import com.cmoney.fanciapi.fanci.model.ReportReason
import com.cmoney.kolfanci.extension.checkResponseBody
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.model.attachment.toUploadMedia
import com.socks.library.KLog


class ChatRoomUseCase(
    val context: Context,
    private val chatRoomApi: ChatRoomApi,
    private val messageApi: MessageApi,
    private val userReport: UserReportApi
) {
    private val TAG = ChatRoomUseCase::class.java.simpleName

    /**
     * 取得單一訊息
     */
    suspend fun getSingleMessage(messageId: String, messageServiceType: MessageServiceType) =
        kotlin.runCatching {
            messageApi.apiV2MessageMessageTypeMessageIdGet(
                messageType = messageServiceType,
                messageId = messageId
            ).checkResponseBody()
        }

    /**
     * 更新訊息
     */
    suspend fun updateMessage(
        messageServiceType: MessageServiceType,
        messageId: String,
        text: String,
        images: List<String> = emptyList()
    ) = kotlin.runCatching {

        val chatMessageParam = ChatMessageParam(
            text = text,
            messageType = MessageType.textMessage,
            medias = images.map {
                Media(
                    resourceLink = it,
                    type = MediaType.image
                )
            }
        )
        messageApi.apiV2MessageMessageTypeMessageIdPut(
            messageType = messageServiceType,
            messageId = messageId,
            chatMessageParam = chatMessageParam
        ).checkResponseBody()
    }

    /**
     * 刪除他人訊息
     */
    suspend fun deleteOtherMessage(messageServiceType: MessageServiceType, messageId: String) =
        kotlin.runCatching {
            messageApi.apiV2MessageRoleMessageTypeMessageIdDelete(
                messageType = messageServiceType,
                messageId = messageId
            ).checkResponseBody()
        }

    /**
     * 刪除自己發送的訊息
     */
    suspend fun takeBackMyMessage(messageServiceType: MessageServiceType, messageId: String) =
        kotlin.runCatching {
            messageApi.apiV2MessageMeMessageTypeMessageIdDelete(
                messageType = messageServiceType,
                messageId = messageId
            ).checkResponseBody()
        }

    /**
     * 檢舉內容
     * @param channelId 頻道 id
     * @param contentId 哪一篇文章
     * @param reason 原因
     * @param tabType 聊天室 or 貼文
     */
    suspend fun reportContent(
        channelId: String,
        contentId: String,
        reason: ReportReason,
        tabType: ChannelTabType = ChannelTabType.chatRoom
    ) = kotlin.runCatching {
        userReport.apiV1UserReportChannelChannelIdPost(
            channelId = channelId,
            reportParm = ReportParm(
                contentId = contentId,
                reason = reason,
                tabType = tabType
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
    suspend fun sendEmoji(
        messageServiceType: MessageServiceType,
        messageId: String, emoji: Emojis
    ) =
        kotlin.runCatching {
            messageApi.apiV2MessageMessageTypeMessageIdEmojiPut(
                messageType = messageServiceType,
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
    suspend fun deleteEmoji(messageServiceType: MessageServiceType, messageId: String) =
        kotlin.runCatching {
            messageApi.apiV2MessageMessageTypeMessageIdEmojiDelete(
                messageType = messageServiceType,
                messageId = messageId
            ).checkResponseBody()
        }

    /**
     * 點擊 emoji
     */
    suspend fun clickEmoji(
        messageServiceType: MessageServiceType,
        messageId: String,
        emojiCount: Int,
        clickEmoji: Emojis
    ) =
        kotlin.runCatching {
            if (emojiCount == -1) {
                //收回
                deleteEmoji(messageServiceType, messageId).fold({
                    KLog.e(TAG, "delete emoji success.")
                }, {
                    KLog.e(TAG, it)
                })
            } else {
                //增加
                sendEmoji(messageServiceType, messageId, clickEmoji).fold({
                    KLog.i(TAG, "sendEmoji success.")
                }, {
                    KLog.e(TAG, it)
                })
            }
        }


    /**
     * 讀取更多 分頁訊息
     * @param chatRoomChannelId 聊天室 id
     * @param fromSerialNumber 從哪一個序列號開始往回找 (若為Null 則從最新開始拿)
     */
    suspend fun fetchMoreMessage(
        chatRoomChannelId: String,
        fromSerialNumber: Long?,
        order: OrderType = OrderType.latest
    ) =
        kotlin.runCatching {
            chatRoomApi.apiV1ChatRoomChatRoomChannelIdMessageGet(
                chatRoomChannelId = chatRoomChannelId,
                fromSerialNumber = fromSerialNumber,
                order = order
            ).checkResponseBody()
        }

    /**
     * 發送訊息
     *
     * @param chatRoomChannelId 聊天室 id
     * @param text 內文
     * @param attachment 附加檔案
     * @param replyMessageId 回覆訊息的id
     */
    suspend fun sendMessage(
        chatRoomChannelId: String,
        text: String,
        attachment: List<Pair<AttachmentType, AttachmentInfoItem>>,
        replyMessageId: String = ""
    ) =
        kotlin.runCatching {
            chatRoomApi.apiV1ChatRoomChatRoomChannelIdMessagePost(
                chatRoomChannelId = chatRoomChannelId,
                chatMessageParam = ChatMessageParam(
                    text = text,
                    messageType = MessageType.textMessage,
                    medias = attachment.toUploadMedia(context),
                    replyMessageId = replyMessageId
                )
            ).checkResponseBody()
        }

    /**
     * 收回訊息
     * @param messageId 訊息 id
     */
    suspend fun recycleMessage(
        messageServiceType: MessageServiceType,
        messageId: String
    ) = takeBackMyMessage(
        messageServiceType,
        messageId
    )
}