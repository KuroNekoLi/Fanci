package com.cmoney.fanci.model.usecase

import android.content.Context
import com.cmoney.fanci.R
import com.cmoney.fanci.model.ChatMessageModel
import com.kedia.ogparser.OpenGraphCacheProvider
import com.kedia.ogparser.OpenGraphCallback
import com.kedia.ogparser.OpenGraphParser
import com.kedia.ogparser.OpenGraphResult
import com.socks.library.KLog
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ChatRoomUseCase {
    private val TAG = ChatRoomUseCase::class.java.simpleName

    /**
     * Get og content
     */
    private suspend fun ogParse(context: Context, url: String): OpenGraphResult? =
        suspendCoroutine { cont ->
            val openGraphParser = OpenGraphParser(
                object : OpenGraphCallback {
                    override fun onError(error: String) {
                        KLog.e(TAG, "onError:$error")
                        cont.resume(null)
                    }

                    override fun onPostResponse(openGraphResult: OpenGraphResult) {
                        KLog.i(TAG, "onPostResponse:$openGraphResult")
                        cont.resume(openGraphResult)
                    }
                },
                showNullOnEmpty = true,
                cacheProvider = OpenGraphCacheProvider(context)
            )
            openGraphParser.parse(url)
        }

    suspend fun getOGMockMessage(context: Context): List<ChatMessageModel> =
        createMockMessage().map { model ->
            model.copy(
                message = model.message.copy(
                    media = model.message.media.map { media ->
                        when (media) {
                            is ChatMessageModel.Media.Article -> {
                                val ogResult = ogParse(context, media.url)
                                val newMedia = ogResult?.let {
                                    val imageUrl = it.image
                                    val title = it.title
                                    ChatMessageModel.Media.Article(
                                        url = media.url,
                                        from = media.from,
                                        title = title ?: media.title,
                                        thumbnail = imageUrl.orEmpty()
                                    )
                                } ?: media
                                newMedia
                            }
                            is ChatMessageModel.Media.Image -> {
                                media
                            }
                            is ChatMessageModel.Media.Instagram -> {
                                media
                            }
                            is ChatMessageModel.Media.Youtube -> {
                                val ogResult = ogParse(context, media.url)
                                val newMedia = ogResult?.let {
                                    val imageUrl = it.image
                                    val title = it.title
                                    ChatMessageModel.Media.Article(
                                        url = media.url,
                                        from = media.from,
                                        title = title ?: media.title,
                                        thumbnail = imageUrl.orEmpty()
                                    )
                                } ?: media
                                newMedia
                            }
                        }
                    }
                )
            )
        }

    fun createMockMessage(): List<ChatMessageModel> {
        return listOf(
            allMessageType,
            ChatMessageModel(
                poster = ChatMessageModel.User(
                    avatar = "https://yt3.ggpht.com/ytc/AMLnZu-9OiE_xfvFOhAqnaTFvN0kdVUfmy4LlEu3guirWQ=s48-c-k-c0x00ffffff-no-rj",
                    nickname = "M2"
                ),
                publishTime = 1666334733000,
                message = ChatMessageModel.Message(
                    reply = null,
                    text = "2023木曜  戀愛老濕機巴士 主題桌曆開賣囉～\n" +
                            "即日起~11/20預購截止\n" +
                            "逛逛麥卡貝：https://shop.camerabay.tv/\n" +
                            "蝦皮賣場：https://shope.ee/8UYTbTMMSX",
                    media = listOf(
                        ChatMessageModel.Media.Youtube(
                            url = "https://www.youtube.com/watch?v=d8pBKXyEt_0",
                            title = "",
                            thumbnail = ""
                        ),
                    ),
                    emoji = listOf(
                        ChatMessageModel.Emoji(
                            resource = R.drawable.emoji_haha,
                            count = 777
                        )
                    )
                )
            ),
            textType,
            imageType,
            ytType,
            igType,
            articleType
        )
    }

    companion object MockData {
        val textType = ChatMessageModel(
            poster = ChatMessageModel.User(
                avatar = "https://picsum.photos/110/110",
                nickname = "TIGER"
            ),
            publishTime = 1666234733000,
            message = ChatMessageModel.Message(
                reply = null,
                text = "純文字"
            )
        )

        val imageType = ChatMessageModel(
            poster = ChatMessageModel.User(
                avatar = "https://picsum.photos/107/107",
                nickname = "FAN"
            ),
            publishTime = 1666234733000,
            message = ChatMessageModel.Message(
                reply = null,
                text = "文字帶圖",
                media = listOf(
                    ChatMessageModel.Media.Image(
                        image = listOf(
                            "https://picsum.photos/${(100..400).random()}/${(100..400).random()}",
                            "https://picsum.photos/${(100..400).random()}/${(100..400).random()}",
                            "https://picsum.photos/${(100..400).random()}/${(100..400).random()}"
                        )
                    )
                ),
                emoji = listOf(
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_cry,
                        count = 111
                    ),
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_like,
                        count = 42
                    )
                )
            )
        )

        val ytType = ChatMessageModel(
            poster = ChatMessageModel.User(
                avatar = "https://picsum.photos/103/103",
                nickname = "FANCY3"
            ),
            publishTime = 1666234733000,
            message = ChatMessageModel.Message(
                reply = null,
                text = "文字YT",
                media = listOf(
                    ChatMessageModel.Media.Youtube(
                        url = "https://www.youtube.com/watch?v=1J3WdNYCm5M&ab_channel=%E9%98%BF%E6%BB%B4%E8%8B%B1%E6%96%87",
                        title = "【奢華挑戰】在紐約一天花10萬台幣! 頂級享受讓阿滴感動到哭\uD83D\uDE2D @黃大謙 @HOOK",
                        thumbnail = "https://img.youtube.com/vi/1J3WdNYCm5M/0.jpg"
                    )
                )
            )
        )

        val igType = ChatMessageModel(
            poster = ChatMessageModel.User(
                avatar = "https://picsum.photos/104/104",
                nickname = "FANCY4"
            ),
            publishTime = 1666234733000,
            message = ChatMessageModel.Message(
                reply = null,
                text = "文字IG",
                media = listOf(
                    ChatMessageModel.Media.Instagram(
                        channel = "滴妹",
                        title = "I will be the crown of all \uD83D\uDC51\n" +
                                "追蹤再睡5分鐘 @napteazzz\n" +
                                "台灣紐約連線\uD83D\uDDFD\uD83E\uDDCBubereats外送三餐給哥哥⬇️\n" +
                                "youtu.be/3dGZS3FYYRY",
                        thumbnail = "https://img.ltn.com.tw/Upload/ent/page/800/2022/03/06/phpqBo3I1.jpg"
                    )
                ),
                emoji = listOf(
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_love,
                        count = 777
                    ),
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_happy,
                        count = 456
                    )
                )
            )
        )

        val articleType = ChatMessageModel(
            poster = ChatMessageModel.User(
                avatar = "https://picsum.photos/105/105",
                nickname = "FANCY5"
            ),
            publishTime = 1666234733000,
            message = ChatMessageModel.Message(
                reply = null,
                text = "文字MEDIUM",
                media = listOf(
                    ChatMessageModel.Media.Article(
                        url = "https://medium.com/@tsif/an-ios-engineer-learns-about-androids-jetpack-compose-and-loves-it-c04fc6a53f10",
                        from = "Medium",
                        title = "An iOS Engineer learns about Android’s Jetpack Compose and loves it.",
                        thumbnail = "https://miro.medium.com/max/1100/1*J-2NDqdY4aWkJFKarhZzJw.jpeg"
                    )
                ),
                emoji = listOf(
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_haha,
                        count = 77
                    ),
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_cry,
                        count = 123
                    ),
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_love,
                        count = 7
                    )
                )
            )
        )

        val allMessageType = ChatMessageModel(
            poster = ChatMessageModel.User(
                avatar = "https://picsum.photos/100/100",
                nickname = "Warren"
            ),
            publishTime = 1666334733000,
            message = ChatMessageModel.Message(
                reply = ChatMessageModel.Reply(
                    replyUser = ChatMessageModel.User(avatar = "", nickname = "阿修羅"),
                    text = "Literally the rookies this year have been killing it"
                ),
                text = "房市利空齊發，不動產大老林正雄提出建言，認為現階段打房政策已奏效，政府不應在此時通過平均地權條例的修法，而內政部長徐國勇則也語帶保留的說，修法時將會考慮各時期的情景轉變。",
                media = listOf(
                    ChatMessageModel.Media.Article(
                        url = "https://medium.com/gitconnected/7-lifelong-learnings-from-my-first-ever-android-app-b6da8749b5cf",
                        from = "Medium",
                        title = "7 Lifelong Learnings From My First Ever Android App",
                        thumbnail = "https://miro.medium.com/max/1100/0*VLSb2Oc_WSxtqN6K"
                    ),
                    ChatMessageModel.Media.Youtube(
                        url = "https://www.youtube.com/watch?v=1p_GLULMNbw&ab_channel=%E8%94%A1%E9%98%BF%E5%98%8ELife",
                        title = "【蔡阿嘎地獄廚房#16】廚佛Fred大戰阿煨師，幹話最多的兩個男人正面對決！",
                        thumbnail = "https://img.youtube.com/vi/1p_GLULMNbw/0.jpg"
                    ),
                    ChatMessageModel.Media.Instagram(
                        channel = "阿滴英文",
                        title = "有妹妹然後教英文的那個\uD83D\uDE4B\uD83C\uDFFB\u200D♂️@raydudaily 比較多日常廢文",
                        thumbnail = "https://okapi.books.com.tw/uploads/image/2018/03/source/22908-1520239183.jpg"
                    ),
                    ChatMessageModel.Media.Image(
                        image = listOf(
                            "https://picsum.photos/500/500",
                            "https://picsum.photos/400/400",
                            "https://picsum.photos/300/300"
                        )
                    )
                ),
                emoji = listOf(
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_haha,
                        count = 777
                    ),
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_cry,
                        count = 123
                    ),
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_like,
                        count = 12355
                    ),
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_happiness,
                        count = 9
                    ),
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_angry,
                        count = 12
                    ),
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_happy,
                        count = 12
                    ),
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_love,
                        count = 55
                    ),
                )
            )
        )
    }

}