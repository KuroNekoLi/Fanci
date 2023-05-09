package com.cmoney.kolfanci.model.usecase

import com.cmoney.fanciapi.fanci.api.ChatRoomApi
import com.cmoney.fanciapi.fanci.api.MessageApi
import com.cmoney.fanciapi.fanci.api.UserReportApi
import com.cmoney.fanciapi.fanci.model.*
import com.cmoney.kolfanci.extension.checkResponseBody
import com.socks.library.KLog
import kotlin.random.Random


class ChatRoomUseCase(
    private val chatRoomApi: ChatRoomApi,
    private val messageApi: MessageApi,
    private val userReport: UserReportApi
) {
    private val TAG = ChatRoomUseCase::class.java.simpleName

    /**
     * 取得單一訊息
     */
    suspend fun getSingleMessage(messageId: String) = kotlin.runCatching {
        messageApi.apiV1MessageMessageIdGet(
            messageId = messageId
        ).checkResponseBody()
    }

    /**
     * 更新訊息
     */
    suspend fun updateMessage(
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
        messageApi.apiV1MessageMessageIdPut(
            messageId = messageId,
            chatMessageParam = chatMessageParam
        ).checkResponseBody()
    }

    /**
     * 刪除他人訊息
     */
    suspend fun deleteOtherMessage(messageId: String) = kotlin.runCatching {
        messageApi.apiV1MessageRoleMessageIdDelete(messageId = messageId).checkResponseBody()
    }

    /**
     * 刪除自己發送的訊息
     */
    suspend fun takeBackMyMessage(messageId: String) = kotlin.runCatching {
        messageApi.apiV1MessageMeMessageIdDelete(messageId = messageId).checkResponseBody()
    }

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
     * 點擊 emoji
     */
    suspend fun clickEmoji(messageId: String, emojiCount: Int, clickEmoji: Emojis) =
        kotlin.runCatching {
            if (emojiCount == -1) {
                //收回
                deleteEmoji(messageId).fold({
                    KLog.e(TAG, "delete emoji success.")
                }, {
                    KLog.e(TAG, it)
                })
            } else {
                //增加
                sendEmoji(messageId, clickEmoji).fold({
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
        val mockListMessage: List<ChatMessage>
            get() {
                return (1..Random.nextInt(2, 10)).map {
                    mockMessage
                }
            }
        val mockMessage: ChatMessage
            get() {
                return ChatMessage(
                    author = GroupMember(
                        name = "Groudon",
                        thumbNail = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSqi_eE11nxjmd9wkk0Q7IR_0anrm8Uf9DaQA&usqp=CAU"
                    ),
                    emojiCount = IEmojiCount(
                        like = Random.nextInt(1, 999),
                        laugh = Random.nextInt(1, 999),
                        money = Random.nextInt(1, 999),
                        shock = Random.nextInt(1, 999),
                        cry = Random.nextInt(1, 999),
                        think = Random.nextInt(1, 999),
                        angry = Random.nextInt(1, 999)
                    ),
                    content = MediaIChatContent(
                        text = "大學時期時想像的出社會的我\n" +
                                "就是這個樣子吧！！\n" +
                                "穿著西裝匆忙地走在大樓間\n" +
                                "再來有一個幻想是：（這是真的哈哈哈）\n" +
                                "因為我發現很多台灣人都有自己的水壺 （韓國以前沒有這個文化）\n" +
                                "心裡想…我以後也要有一個哈哈哈哈在辦公室喝嘻嘻\n" +
                                "最近水壺越來越厲害了也\n" +
                                "WOKY的水壺也太好看了吧！！！\n" +
                                "不僅有9個顏色 選項超多\n" +
                                "它是770ML大大的容量\n" +
                                "超適合外帶手搖飲在辦公喝哈哈\n" +
                                "再來是我最重視的！\n" +
                                "它的口很大\n" +
                                "而且是鈦陶瓷的關係容易清潔\n" +
                                "裝咖啡、果汁都不沾色不卡味\n" +
                                "我命名為～Fancy Cutie 一波呦 渾圓杯\n" +
                                "太好看了 我不會忘記帶它出門的^^\n" +
                                "最近還有在持續執行多喝水計畫\n" +
                                "大家如果也剛好有需要水壺\n" +
                                "可以參考看看一起多喝水",
                        medias = (1..Random.nextInt(2, 10)).map {
                            Media(
                                resourceLink = "https://picsum.photos/${
                                    Random.nextInt(
                                        100,
                                        300
                                    )
                                }/${Random.nextInt(100, 300)}",
                                type = MediaType.image
                            )
                        }
                    )
                )
            }
    }
}