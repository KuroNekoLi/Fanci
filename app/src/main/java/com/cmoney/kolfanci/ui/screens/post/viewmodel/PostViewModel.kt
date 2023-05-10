package com.cmoney.kolfanci.ui.screens.post.viewmodel

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.IEmojiCount
import com.cmoney.fanciapi.fanci.model.IUserMessageReaction
import com.cmoney.fanciapi.fanci.model.Media
import com.cmoney.fanciapi.fanci.model.MediaIChatContent
import com.cmoney.fanciapi.fanci.model.MediaType
import com.cmoney.fanciapi.fanci.model.ReportReason
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.EmptyBodyException
import com.cmoney.kolfanci.extension.clickCount
import com.cmoney.kolfanci.extension.isMyPost
import com.cmoney.kolfanci.extension.toBulletinboardMessage
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.model.usecase.PostUseCase
import com.cmoney.kolfanci.ui.screens.shared.snackbar.CustomMessage
import com.cmoney.kolfanci.ui.theme.White_494D54
import com.cmoney.kolfanci.ui.theme.White_767A7F
import com.cmoney.kolfanci.utils.Utils
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

class PostViewModel(
    private val postUseCase: PostUseCase,
    val channelId: String,
    private val chatRoomUseCase: ChatRoomUseCase
) : ViewModel() {
    private val TAG = PostViewModel::class.java.simpleName

    /**
     * 顯示貼文model
     */
    @Parcelize
    data class BulletinboardMessageWrapper(
        val message: BulletinboardMessage,
        val isPin: Boolean = false
    ) : Parcelable

    //貼文清單
    private val _post = MutableStateFlow<List<BulletinboardMessageWrapper>>(emptyList())
    val post = _post.asStateFlow()

    //置頂貼文
    private val _pinPost = MutableStateFlow<BulletinboardMessage?>(null)
    val pinPost = _pinPost.asStateFlow()

    //Toast message
    private val _toast = MutableStateFlow<CustomMessage?>(null)
    val toast = _toast.asStateFlow()

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
                postList.addAll(it.items?.map { post ->
                    BulletinboardMessageWrapper(
                        message = post,
                        isPin = false
                    )
                }?.filter { post ->
                    post.message.isDeleted != true
                }.orEmpty())

                _post.value = postList

                fetchPinPost()
            }, {
                KLog.e(TAG, it)
                fetchPinPost()
            })
        }
    }

    /**
     * 拿取 置頂貼文, 並過濾掉原本清單同樣id 的貼文
     */
    private fun fetchPinPost() {
        KLog.i(TAG, "fetchPinPost")
        viewModelScope.launch {
            postUseCase.getPinMessage(
                channelId
            ).fold({
                //有置頂文
                if (it.isAnnounced == true) {
                    KLog.i(TAG, "has pin post.")
                    it.message?.let {
                        val pinPost = it.toBulletinboardMessage()
                        //fix exists post
                        _post.value = _post.value.map { post ->
                            if (post.message.id == pinPost.id) {
                                post.copy(
                                    isPin = true
                                )
                            } else {
                                post.copy(
                                    isPin = false
                                )
                            }
                        }

                        _pinPost.value = pinPost
                    }
                }
                //沒有置頂文
                else {
                    KLog.i(TAG, "no pin post.")
                    _post.value = _post.value.map {
                        it.copy(isPin = false)
                    }
                    _pinPost.value = null
                }
            }, {
                KLog.e(TAG, it)
                it.printStackTrace()
            })
        }
    }

    /**
     * 發送貼文 成功
     */
    fun onPostSuccess(bulletinboardMessage: BulletinboardMessage) {
        KLog.i(TAG, "onPostSuccess:$bulletinboardMessage")
        val postList = _post.value.toMutableList()
        postList.add(0, BulletinboardMessageWrapper(message = bulletinboardMessage))
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
                if (it.message.id == postMessage.id) {
                    BulletinboardMessageWrapper(message = postMessage)
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
            chatRoomUseCase.getSingleMessage(
                messageId = bulletinboardMessage.id.orEmpty()
            ).fold({ result ->
                val updatePost = result.toBulletinboardMessage()
                val editList = _post.value.toMutableList()

                _post.value = editList.map {
                    if (it.message.id == updatePost.id) {
                        BulletinboardMessageWrapper(message = updatePost)
                    } else {
                        it
                    }
                }.filter {
                    it.message.isDeleted != true
                }

                fetchPinPost()
            }, { err ->
                KLog.e(TAG, err)
                err.printStackTrace()
                //local update
                _post.value = _post.value.map {
                    if (it.message.id == bulletinboardMessage.id) {
                        BulletinboardMessageWrapper(message = bulletinboardMessage)
                    } else {
                        it
                    }
                }
                fetchPinPost()
            })
        }
    }

    fun onDeletePostClick(post: BulletinboardMessage) {
        KLog.i(TAG, "deletePost:$post")
        viewModelScope.launch {
            //我發的
            if (post.isMyPost(Constant.MyInfo)) {
                KLog.i(TAG, "delete my comment.")
                chatRoomUseCase.takeBackMyMessage(post.id.orEmpty()).fold({
                }, {
                    if (it is EmptyBodyException) {
                        _post.value = _post.value.filter {
                            it.message.id != post.id
                        }
                    } else {
                        it.printStackTrace()
                    }
                })
            } else {
                KLog.i(TAG, "delete other comment.")
                //他人
                chatRoomUseCase.deleteOtherMessage(post.id.orEmpty()).fold({
                }, {
                    if (it is EmptyBodyException) {
                        _post.value = _post.value.filter {
                            it.message.id != post.id
                        }
                    } else {
                        it.printStackTrace()
                    }
                })
            }

        }
    }

    /**
     * 置頂貼文
     *
     * @param channelId 頻道id
     * @param message 要置頂的文章
     */
    fun pinPost(channelId: String, message: BulletinboardMessage?) {
        KLog.i(TAG, "pinPost:$message")
        viewModelScope.launch {
            postUseCase.pinPost(
                channelId = channelId,
                messageId = message?.id.orEmpty()
            ).fold({
            }, {
                if (it is EmptyBodyException) {
                    KLog.i(TAG, "pinPost success.")
                    fetchPinPost()
                } else {
                    it.printStackTrace()
                    KLog.e(TAG, it)
                }
            })

        }
    }

    /**
     * 取消置頂貼文
     * @param channelId 頻道id
     * @param message 要取消置頂的文章
     */
    fun unPinPost(channelId: String, message: BulletinboardMessage?) {
        KLog.i(TAG, "unPinPost:$message")
        viewModelScope.launch {
            postUseCase.unPinPost(channelId).fold({
            }, {
                if (it is EmptyBodyException) {
                    KLog.i(TAG, "unPinPost success.")
                    fetchPinPost()
                } else {
                    it.printStackTrace()
                    KLog.e(TAG, it)
                }
            })
        }
    }

    /**
     * 檢查該貼文 是否為 pin
     */
    fun isPinPost(post: BulletinboardMessage): Boolean {
        return _pinPost.value?.id == post.id
    }

    /**
     * 檢舉貼文
     */
    fun onReportPost(channelId: String, message: BulletinboardMessage?, reason: ReportReason) {
        KLog.i(TAG, "onReportPost:$reason")
        viewModelScope.launch {
            chatRoomUseCase.reportContent(
                channelId = channelId,
                contentId = message?.id.orEmpty(),
                reason = reason
            ).fold({
                KLog.i(TAG, "onReportUser success:$it")
                _toast.value = CustomMessage(
                    textString = "檢舉成立！",
                    textColor = Color.White,
                    iconRes = R.drawable.report,
                    iconColor = White_767A7F,
                    backgroundColor = White_494D54
                )
            }, {
                KLog.e(TAG, it)
                it.printStackTrace()
            })
        }
    }

    /**
     * 取消 snackBar
     */
    fun dismissSnackBar() {
        KLog.i(TAG, "dismissSnackBar")
        _toast.value = null
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