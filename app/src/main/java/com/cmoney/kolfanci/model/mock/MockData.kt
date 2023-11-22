package com.cmoney.kolfanci.model.mock

import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.IEmojiCount
import com.cmoney.fanciapi.fanci.model.IVotingOptionStatistics
import com.cmoney.fanciapi.fanci.model.Media
import com.cmoney.fanciapi.fanci.model.MediaIChatContent
import com.cmoney.fanciapi.fanci.model.PushNotificationSetting
import com.cmoney.fanciapi.fanci.model.PushNotificationSettingType
import com.cmoney.fanciapi.fanci.model.Voting
import com.cmoney.kolfanci.BuildConfig
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.vote.VoteModel
import com.cmoney.kolfanci.ui.screens.group.setting.group.notification.PushNotificationSettingWrap
import com.cmoney.kolfanci.ui.screens.notification.NotificationCenterData
import org.apache.commons.lang3.RandomStringUtils
import kotlin.random.Random

object MockData {

    val mockVote = VoteModel(
        id = System.currentTimeMillis().toString(),
        question = "What is indicated about advertising space on the Mooringtown Library notice board?",
        choice = listOf(
            "(A) complication",
            "(B) complicates",
            "(C) complicate",
            "(D) complicated"
        ),
        isSingleChoice = true
    )

    val mockGroup: Group = Group(
        id = "",
        name = "愛莉莎莎Alisasa",
        description = "大家好，我是愛莉莎莎Alisasa！\n" +
                "\n" +
                "台灣人在韓國留學八個月 \n" +
                "已經在2018 一月\n" +
                "回到台灣當全職Youtuber囉！\n" +
                "\n" +
                "但是我還是每個月會去韓國\n" +
                "更新最新的韓國情報 （流行 美妝 美食等等） \n" +
                "提供給大家不同於一般觀光客\n" +
                "內行的認識韓國新角度\n" +
                "\n" +
                "另外也因為感情經驗豐富（？）\n" +
                "可以提供給大家一些女生的秘密想法～\n" +
                "\n" +
                "希望大家喜歡我的頻道＾＾\n" +
                "\n" +
                "\n" +
                "如果你喜歡我的影片，希望你可以幫我訂閱＋分享\n" +
                "\n" +
                "任何合作邀約請洽Pressplay Email :\n" +
                "alisasa@pressplay.cc\n" +
                "═════════════════════════════════════\n" +
                "\n" +
                "追蹤我 Follow Me \n" +
                "\n" +
                "★Facebook社團『愛莉莎莎敗家基地』: https://www.facebook.com/groups/924974291237889/\n" +
                "★Facebook粉絲專頁: https://www.facebook.com/alisasa11111/\n" +
                "★Instagram: goodalicia",
        coverImageUrl = "https://img.ltn.com.tw/Upload/health/page/800/2021/02/14/phpo5UnZT.png",
        thumbnailImageUrl = "https://picsum.photos/${
            Random.nextInt(
                100,
                300
            )
        }/${Random.nextInt(100, 300)}",
        categories = emptyList()
    )

    val mockNotificationSettingItem: PushNotificationSetting = PushNotificationSetting(
        settingType = PushNotificationSettingType.newPost,
        title = "有任何新動態都提醒我",
        description = "所有新內容，與我的內容有人回饋時請提醒我",
        shortTitle = "新動態都提醒我"
    )

    val mockNotificationSettingItemWrapList: List<PushNotificationSettingWrap> = listOf(
        PushNotificationSettingWrap(
            pushNotificationSetting = mockNotificationSettingItem,
            isChecked = true
        ),
        PushNotificationSettingWrap(
            pushNotificationSetting = mockNotificationSettingItem,
            isChecked = false
        ),
        PushNotificationSettingWrap(
            pushNotificationSetting = mockNotificationSettingItem,
            isChecked = false
        )
    )


    /**
     * 推播中心 假資料
     */
    val mockNotificationCenter: List<NotificationCenterData>
        get() {
            return if (BuildConfig.DEBUG) {
                val kindSize = mockNotificationCenterKind.size
                (1..Random.nextInt(2, 20)).map {
                    mockNotificationCenterKind[Random.nextInt(0, kindSize)]
                }
            } else {
                emptyList()
            }
        }

    /**
     * 所有 推播 種類
     */
    private val mockNotificationCenterKind = listOf<NotificationCenterData>(
        NotificationCenterData(
            notificationId = "",
            image = "https://picsum.photos/${
                Random.nextInt(
                    100,
                    300
                )
            }/${Random.nextInt(100, 300)}",
            title = "聊天室有新訊息",
            description = "[藝術學院小公主的小畫廊\uD83C\uDFA8] 社團有新訊息",
            deepLink = "{\"targetType\": 2, \"serialNumber\" : \"2455\" ,  \"groupId\" : \"27444\",  \"channelId\": \"31913\"}",
            isRead = Random.nextBoolean(),
            displayTime = "剛剛"
        ),
        NotificationCenterData(
            notificationId = "",
            image = "https://picsum.photos/${
                Random.nextInt(
                    100,
                    300
                )
            }/${Random.nextInt(100, 300)}",
            title = "邀請加入社團",
            description = "[藝術學院小公主的小畫廊\uD83C\uDFA8] 社團",
            deepLink = "{\"targetType\": 1, \"groupId\": \"27444\"}",
            isRead = Random.nextBoolean(),
            displayTime = "剛剛"
        ),
        NotificationCenterData(
            notificationId = "",
            image = "https://picsum.photos/${
                Random.nextInt(
                    100,
                    300
                )
            }/${Random.nextInt(100, 300)}",
            title = "有新文章",
            description = "[藝術學院小公主的小畫廊\uD83C\uDFA8] 社團有新文章",
            deepLink = "{\"targetType\": 3, \"messageId\" : \"151547560\" ,  \"groupId\" : \"27444\",  \"channelId\": \"31913\"}",
            isRead = Random.nextBoolean(),
            displayTime = "剛剛"
        ),
        NotificationCenterData(
            notificationId = "",
            image = "https://picsum.photos/${
                Random.nextInt(
                    100,
                    300
                )
            }/${Random.nextInt(100, 300)}",
            title = "社團解散",
            description = " [XLAB-405] 社團解散了",
            deepLink = "{\"targetType\": 4, \"groupId\" : \"28557\"}",
            isRead = Random.nextBoolean(),
            displayTime = "剛剛"
        )
    )


    /**
     * 會員 假資料
     */
    val mockGroupMember: GroupMember
        get() {
            return if (BuildConfig.DEBUG) {
                GroupMember(
                    name = RandomStringUtils.randomAlphabetic(10),
                    thumbNail = "https://picsum.photos/${
                        Random.nextInt(
                            100,
                            300
                        )
                    }/${Random.nextInt(100, 300)}",
                    isGroupVip = Random.nextBoolean(),
                    roleInfos = listOf(
                        FanciRole(
                            name = "Role",
                            color = ""
                        )
                    )
                )
            } else {
                GroupMember()
            }
        }

    val mockListMessage: List<ChatMessage>
        get() {
            return if (BuildConfig.DEBUG) {
                (1..Random.nextInt(2, 10)).map {
                    mockMessage
                }
            } else {
                emptyList()
            }
        }

    val mockMessage: ChatMessage
        get() {
            return if (BuildConfig.DEBUG) {
                ChatMessage(
                    author = mockGroupMember,
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
                                type = AttachmentType.Image.name
                            )
                        }
                    ),
                    createUnixTime = System.currentTimeMillis().div(1000),
                    serialNumber = Random.nextLong(1, 65536),
                    votings = listOf(
                        mockVoting
                    )
                )
            } else {
                ChatMessage()
            }
        }

    val mockVoting: Voting
        get() = Voting(
            id = System.currentTimeMillis(),
            title = RandomStringUtils.randomAlphabetic(10),
            votingOptionStatistics = listOf(
                IVotingOptionStatistics(
                    optionId = System.currentTimeMillis().toInt(),
                    voteCount = 2,
                    text = RandomStringUtils.randomAlphabetic(10)
                ),
                IVotingOptionStatistics(
                    optionId = System.currentTimeMillis().toInt(),
                    voteCount = 3,
                    text = RandomStringUtils.randomAlphabetic(10)
                ),
                IVotingOptionStatistics(
                    optionId = System.currentTimeMillis().toInt(),
                    voteCount = 1,
                    text = RandomStringUtils.randomAlphabetic(10)
                ),
                IVotingOptionStatistics(
                    optionId = System.currentTimeMillis().toInt(),
                    voteCount = 4,
                    text = RandomStringUtils.randomAlphabetic(10)
                )
            ),
            isMultipleChoice = false,
        )

}