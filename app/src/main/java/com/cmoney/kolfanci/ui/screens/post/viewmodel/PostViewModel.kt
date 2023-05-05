package com.cmoney.kolfanci.ui.screens.post.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.IEmojiCount
import com.cmoney.fanciapi.fanci.model.IUserMessageReaction
import com.cmoney.fanciapi.fanci.model.Media
import com.cmoney.fanciapi.fanci.model.MediaIChatContent
import com.cmoney.fanciapi.fanci.model.MediaType
import com.cmoney.kolfanci.extension.clickCount
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.model.usecase.PostUseCase
import com.cmoney.kolfanci.utils.Utils
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class PostViewModel(
    private val postUseCase: PostUseCase,
    val channelId: String,
    private val chatRoomUseCase: ChatRoomUseCase
) : ViewModel() {
    private val TAG = PostViewModel::class.java.simpleName

    private val _post = MutableStateFlow<List<BulletinboardMessage>>(emptyList())
    val post = _post.asStateFlow()

    var haveNextPage: Boolean = false
    var nextWeight: Long? = null    //貼文分頁 索引

    /**
     * 拿取 貼文清單
     */
    fun fetchPost() {
        KLog.i(TAG, "fetchPost")
        viewModelScope.launch {
            postUseCase.getPost(
                channelId = channelId,
                fromSerialNumber = nextWeight
            ).fold({
                haveNextPage = it.haveNextPage == true
                nextWeight = it.nextWeight

                val postList = _post.value.toMutableList()
                postList.addAll(it.items?.filter { post ->
                    post.isDeleted != true
                }.orEmpty())

                _post.value = postList
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    fun onPostSuccess(bulletinboardMessage: BulletinboardMessage) {
        KLog.i(TAG, "onPostSuccess:$bulletinboardMessage")
        val postList = _post.value.toMutableList()
        postList.add(0, bulletinboardMessage)
        _post.value = postList
    }

    fun onLoadMore() {
        KLog.i(TAG, "onLoadMore:$haveNextPage")
        if (haveNextPage) {
            fetchPost()
        }
    }

    fun onEmojiClick(postMessage: BulletinboardMessage, resourceId: Int) {
        KLog.i(TAG, "onEmojiClick:$resourceId")
        viewModelScope.launch {
            val clickEmoji = Utils.emojiResourceToServerKey(resourceId)
            //判斷是否為收回Emoji
            var emojiCount = 1
            postMessage.messageReaction?.let {
                emojiCount = if (it.emoji.orEmpty().lowercase() == clickEmoji.value.lowercase()) {
                    //收回
                    -1
                } else {
                    //增加
                    1
                }
            }

            val orgEmoji = postMessage.emojiCount
            val newEmoji = clickEmoji.clickCount(emojiCount, orgEmoji)

            //回填資料
            val postMessage = postMessage.copy(
                emojiCount = newEmoji,
                messageReaction = if (emojiCount == -1) null else {
                    IUserMessageReaction(
                        emoji = clickEmoji.value
                    )
                }
            )

            //UI show
            _post.value = _post.value.map {
                if (it.id == postMessage.id) {
                    postMessage
                } else {
                    it
                }
            }

            //Call Emoji api
            chatRoomUseCase.clickEmoji(
                messageId = postMessage.id.orEmpty(),
                emojiCount = emojiCount,
                clickEmoji = clickEmoji
            )
        }
    }

    /**
     * 更新 資料
     */
    fun onUpdate(bulletinboardMessage: BulletinboardMessage) {
        KLog.i(TAG, "onUpdate:$bulletinboardMessage")
        viewModelScope.launch {
            _post.value = _post.value.map {
                if (it.id == bulletinboardMessage.id) {
                    bulletinboardMessage
                } else {
                    it
                }
            }
        }
    }

    companion object {
        val mockListMessage: List<BulletinboardMessage>
            get() {
                return (1..Random.nextInt(2, 10)).map {
                    mockPost
                }
            }

        val mockPost = BulletinboardMessage(
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