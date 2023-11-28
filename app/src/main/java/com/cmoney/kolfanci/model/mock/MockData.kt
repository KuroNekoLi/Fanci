package com.cmoney.kolfanci.model.mock

import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChannelPrivacy
import com.cmoney.fanciapi.fanci.model.ChannelTabType
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.DeleteStatus
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.IChannelTab
import com.cmoney.fanciapi.fanci.model.IEmojiCount
import com.cmoney.fanciapi.fanci.model.IReplyMessage
import com.cmoney.fanciapi.fanci.model.IReplyVoting
import com.cmoney.fanciapi.fanci.model.IUserVoteInfo
import com.cmoney.fanciapi.fanci.model.IVotingOptionStatistics
import com.cmoney.fanciapi.fanci.model.IVotingOptionStatisticsWithVoter
import com.cmoney.fanciapi.fanci.model.ImageContent
import com.cmoney.fanciapi.fanci.model.Media
import com.cmoney.fanciapi.fanci.model.MediaIChatContent
import com.cmoney.fanciapi.fanci.model.MessageServiceType
import com.cmoney.fanciapi.fanci.model.PushNotificationSetting
import com.cmoney.fanciapi.fanci.model.PushNotificationSettingType
import com.cmoney.fanciapi.fanci.model.Voting
import com.cmoney.kolfanci.BuildConfig
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.vote.VoteModel
import com.cmoney.kolfanci.ui.screens.group.setting.group.notification.PushNotificationSettingWrap
import com.cmoney.kolfanci.ui.screens.notification.NotificationCenterData
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
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
        categories = listOf(
            Category(
                name = "一般分類",
                channels = listOf(
                    mockChannelPublic,
                    mockChannelPrivate
                )
            )
        )
    )

    val mockChannelPublic: Channel
        get() {
            val id = Random.nextInt(0, 20).toString()
            return Channel(
                id = id,
                name = "測試頻道 $id",
                privacy = ChannelPrivacy.public,
                tabs = listOf(
                    IChannelTab(
                        type = ChannelTabType.bulletinboard
                    ),
                    IChannelTab(
                        type = ChannelTabType.chatRoom
                    )
                ),
                channelType = ChannelTabType.bulletinboard
            )
        }

    val mockChannelPrivate: Channel
        get() = mockChannelPublic.copy(
            privacy = ChannelPrivacy.private
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
                    mockReplyMessage
                }.apply {
//                    val mutableList = this.toMutableList()
//                    mutableList.add(mockReplyMessage)
                }
            } else {
                emptyList()
            }
        }

    //聊天室訊息
    val mockMessage: ChatMessage
        get() {
            return if (BuildConfig.DEBUG) {
                ChatMessage(
                    id = Random.nextInt(1, 999).toString(),
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
                        mockSingleVoting
                    )
                )
            } else {
                ChatMessage()
            }
        }

    val mockReplyMessage: ChatMessage
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
                        text = "回覆訊息",
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
                        mockSingleVoting
                    ),
                    replyMessage = IReplyMessage(
                        author = mockGroupMember,
                        content = MediaIChatContent(
                            text = "訊息內文"
                        ),
                        replyVotings = listOf(
                            IReplyVoting(
                                title = "✈️ 投票決定我去哪裡玩！史丹利這"
                            )
                        )
                    )
                )
            } else {
                ChatMessage()
            }
        }

    //貼文
    val mockBulletinboardMessage: BulletinboardMessage
        get() = BulletinboardMessage(
            replyMessage = null,
            votings = listOf(
                mockSingleVoting,
                mockMultiVoting
            ),
            messageFromType = MessageServiceType.bulletinboard,
            author = GroupMember(
                id = "637447b722171bd081b1521b",
                name = "Boring12",
                thumbNail = "https://cm-176-test.s3-ap-northeast-1.amazonaws.com/images/d230fca7-d202-4208-8547-2c20016ee99d.jpeg",
                serialNumber = 23122246, roleInfos = emptyList(), isGroupVip = false
            ),
            content = MediaIChatContent(
                text = "「 小説を音楽にするユニット 」 であるYOASOBIの結成以降初となるCD作品であり [12]、2019 年以降にリリースされた 「 夜に駆ける 」 から 「 群青 」 までのシングル5曲 [注1]と新曲が収録される[13]。7thシングル「怪物」と同時リリースされたが[12]、同曲は収録されていない。",
                medias = listOf(
                    Media(
                        resourceLink = "https://image.cmoney.tw/attachment/blog/1700150400/0d9e81d5-4870-4af9-be5a-8f6e6dd600b6.jpg",
                        type = "Image",
                        isNeedAuthenticate = false,
                        image = ImageContent(width = 0, height = 0),
                    )
                )
            ),
            emojiCount = IEmojiCount(
                like = 0,
                dislike = 0,
                laugh = 0,
                money = 0,
                shock = 0,
                cry = 0,
                think = 0,
                angry = 0
            ),
            id = "151628405",
            isDeleted = false,
            createUnixTime = 1700201671,
            updateUnixTime = 1700201671,
            serialNumber = 1300,
            deleteStatus = DeleteStatus.none,
            commentCount = 0
        )

    val mockListBulletinboardMessage: List<BulletinboardMessage>
        get() {
            return (1..Random.nextInt(2, 10)).map {
                mockBulletinboardMessage
            }
        }

    val mockBulletinboardMessageWrapper: PostViewModel.BulletinboardMessageWrapper
        get() = PostViewModel.BulletinboardMessageWrapper(
            message = mockBulletinboardMessage, isPin = false
        )

    //單選題
    val mockSingleVoting: Voting
        get() = Voting(
            id = System.currentTimeMillis(),
            title = RandomStringUtils.randomAlphabetic(10),
            votingOptionStatistics = listOf(
                IVotingOptionStatistics(
                    optionId = 1,
                    voteCount = 2,
                    text = RandomStringUtils.randomAlphabetic(10)
                ),
                IVotingOptionStatistics(
                    optionId = 2,
                    voteCount = 3,
                    text = RandomStringUtils.randomAlphabetic(10)
                ),
                IVotingOptionStatistics(
                    optionId = 3,
                    voteCount = 1,
                    text = RandomStringUtils.randomAlphabetic(10)
                ),
                IVotingOptionStatistics(
                    optionId = 4,
                    voteCount = 4,
                    text = RandomStringUtils.randomAlphabetic(10)
                )
            ),
            isMultipleChoice = false,
            userVoteInfo = IUserVoteInfo()
        )

    //多選題
    val mockMultiVoting: Voting
        get() = mockSingleVoting.copy(
            id = 123,
            isMultipleChoice = true
        )

    val mockIVotingOptionStatisticsWithVoterList: List<IVotingOptionStatisticsWithVoter>
        get() = listOf(
            IVotingOptionStatisticsWithVoter(
                voterIds = listOf("1", "2", "3"),
                optionId = 1,
                text = RandomStringUtils.randomAlphabetic(10)
            ),
            IVotingOptionStatisticsWithVoter(
                voterIds = listOf("1", "2", "3"),
                optionId = 2,
                text = RandomStringUtils.randomAlphabetic(10)
            ),
            IVotingOptionStatisticsWithVoter(
                voterIds = listOf("1", "2", "3"),
                optionId = 3,
                text = RandomStringUtils.randomAlphabetic(10)
            ),
            IVotingOptionStatisticsWithVoter(
                voterIds = listOf("1", "2", "3"),
                optionId = 4,
                text = RandomStringUtils.randomAlphabetic(10)
            )
        )
}