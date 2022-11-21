package com.cmoney.fanci.model.usecase

import com.cmoney.fanci.extension.checkResponseBody
import com.cmoney.fanciapi.fanci.api.ChatRoomApi
import com.cmoney.fanciapi.fanci.model.*


class ChatRoomUseCase(private val chatRoomApi: ChatRoomApi) {
    private val TAG = ChatRoomUseCase::class.java.simpleName

    suspend fun sendMessage(chatRoomChannelId: String, text: String, images: List<String>) =
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
                    }
                )
            ).checkResponseBody()
        }


//    fun createMockMessage(): List<ChatMessageModel> {
//        return listOf(
//            allMessageType,
//            ChatMessageModel(
//                poster = ChatMessageModel.User(
//                    avatar = "https://yt3.ggpht.com/ytc/AMLnZu-9OiE_xfvFOhAqnaTFvN0kdVUfmy4LlEu3guirWQ=s48-c-k-c0x00ffffff-no-rj",
//                    nickname = "M2"
//                ),
//                publishTime = 1666334733000,
//                message = ChatMessageModel.Message(
//                    reply = null,
//                    text = "https://www.youtube.com/watch?v=d8pBKXyEt_0 \n" +
//                            "2023木曜  戀愛老濕機巴士 主題桌曆開賣囉～\n" +
//                            "即日起~11/20預購截止\n" +
//                            "逛逛麥卡貝：https://shop.camerabay.tv/\n" +
//                            "蝦皮賣場：https://shope.ee/8UYTbTMMSX",
//                    emoji = listOf(
//                        ChatMessageModel.Emoji(
//                            resource = R.drawable.emoji_haha,
//                            count = 777
//                        )
//                    )
//                )
//            ),
//            textType,
//            imageType,
//            ytType,
//            igType,
//            articleType
//        )
//    }

    companion object MockData {
        val mockMessage = ChatMessage(
            author = GroupMember(
                name = "Name"
            ),
            content = MediaIChatContent(
                text = "Content"
            )
        )

//        val textType = ChatMessageModel(
//            poster = ChatMessageModel.User(
//                avatar = "https://picsum.photos/110/110",
//                nickname = "TIGER"
//            ),
//            publishTime = 1666234733000,
//            message = ChatMessageModel.Message(
//                reply = null,
//                text = "純文字"
//            )
//        )
//
//        val imageType = ChatMessageModel(
//            poster = ChatMessageModel.User(
//                avatar = "https://picsum.photos/107/107",
//                nickname = "FAN"
//            ),
//            publishTime = 1666234733000,
//            message = ChatMessageModel.Message(
//                reply = null,
//                text = "文字帶圖",
//                media = listOf(
//                    ChatMessageModel.Media.Image(
//                        image = listOf(
//                            "https://picsum.photos/${(100..400).random()}/${(100..400).random()}",
//                            "https://picsum.photos/${(100..400).random()}/${(100..400).random()}",
//                            "https://picsum.photos/${(100..400).random()}/${(100..400).random()}"
//                        )
//                    )
//                ),
//                emoji = listOf(
//                    ChatMessageModel.Emoji(
//                        resource = R.drawable.emoji_cry,
//                        count = 111
//                    ),
//                    ChatMessageModel.Emoji(
//                        resource = R.drawable.emoji_like,
//                        count = 42
//                    )
//                )
//            )
//        )
//
//        val ytType = ChatMessageModel(
//            poster = ChatMessageModel.User(
//                avatar = "https://picsum.photos/103/103",
//                nickname = "FANCY3"
//            ),
//            publishTime = 1666234733000,
//            message = ChatMessageModel.Message(
//                reply = null,
//                text = "文字YT https://www.youtube.com/watch?v=1J3WdNYCm5M&ab_channel=%E9%98%BF%E6%BB%B4%E8%8B%B1%E6%96%87",
//            )
//        )
//
//        val igType = ChatMessageModel(
//            poster = ChatMessageModel.User(
//                avatar = "https://picsum.photos/104/104",
//                nickname = "FANCY4"
//            ),
//            publishTime = 1666234733000,
//            message = ChatMessageModel.Message(
//                reply = null,
//                text = "文字IG https://www.instagram.com/p/Ckf9wV7jkPu/",
//                emoji = listOf(
//                    ChatMessageModel.Emoji(
//                        resource = R.drawable.emoji_love,
//                        count = 777
//                    ),
//                    ChatMessageModel.Emoji(
//                        resource = R.drawable.emoji_happy,
//                        count = 456
//                    )
//                )
//            )
//        )
//
//        val articleType = ChatMessageModel(
//            poster = ChatMessageModel.User(
//                avatar = "https://picsum.photos/105/105",
//                nickname = "FANCY5"
//            ),
//            publishTime = 1666234733000,
//            message = ChatMessageModel.Message(
//                reply = null,
//                text = "文字MEDIUM \n https://medium.com/@tsif/an-ios-engineer-learns-about-androids-jetpack-compose-and-loves-it-c04fc6a53f10",
//                emoji = listOf(
//                    ChatMessageModel.Emoji(
//                        resource = R.drawable.emoji_haha,
//                        count = 77
//                    ),
//                    ChatMessageModel.Emoji(
//                        resource = R.drawable.emoji_cry,
//                        count = 123
//                    ),
//                    ChatMessageModel.Emoji(
//                        resource = R.drawable.emoji_love,
//                        count = 7
//                    )
//                )
//            )
//        )
//
//        val allMessageType = ChatMessageModel(
//            poster = ChatMessageModel.User(
//                avatar = "https://picsum.photos/100/100",
//                nickname = "Warren"
//            ),
//            publishTime = 1666334733000,
//            message = ChatMessageModel.Message(
//                reply = ChatMessageModel.Reply(
//                    replyUser = ChatMessageModel.User(avatar = "", nickname = "阿修羅"),
//                    text = "Literally the rookies this year have been killing it"
//                ),
//                text = "房市利空齊發，不動產大老林正雄提出建言，認為現階段打房政策已奏效，政府不應在此時通過平均地權條例的修法，而內政部長徐國勇則也語帶保留的說，修法時將會考慮各時期的情景轉變。",
//                media = listOf(
//                    ChatMessageModel.Media.Image(
//                        image = listOf(
//                            "https://picsum.photos/500/500",
//                            "https://picsum.photos/400/400",
//                            "https://picsum.photos/300/300"
//                        )
//                    )
//                ),
//                emoji = listOf(
//                    ChatMessageModel.Emoji(
//                        resource = R.drawable.emoji_haha,
//                        count = 777
//                    ),
//                    ChatMessageModel.Emoji(
//                        resource = R.drawable.emoji_cry,
//                        count = 123
//                    ),
//                    ChatMessageModel.Emoji(
//                        resource = R.drawable.emoji_like,
//                        count = 12355
//                    ),
//                    ChatMessageModel.Emoji(
//                        resource = R.drawable.emoji_happiness,
//                        count = 9
//                    ),
//                    ChatMessageModel.Emoji(
//                        resource = R.drawable.emoji_angry,
//                        count = 12
//                    ),
//                    ChatMessageModel.Emoji(
//                        resource = R.drawable.emoji_happy,
//                        count = 12
//                    ),
//                    ChatMessageModel.Emoji(
//                        resource = R.drawable.emoji_love,
//                        count = 55
//                    ),
//                )
//            )
//        )
    }

}