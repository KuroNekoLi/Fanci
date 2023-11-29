package com.cmoney.kolfanci.ui.screens.post.viewmodel

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.ChannelTabType
import com.cmoney.fanciapi.fanci.model.Emojis
import com.cmoney.fanciapi.fanci.model.IUserMessageReaction
import com.cmoney.fanciapi.fanci.model.MessageServiceType
import com.cmoney.fanciapi.fanci.model.ReportReason
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.clickCount
import com.cmoney.kolfanci.extension.isMyPost
import com.cmoney.kolfanci.extension.toBulletinboardMessage
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.model.usecase.PostPollUseCase
import com.cmoney.kolfanci.model.usecase.PostUseCase
import com.cmoney.kolfanci.ui.screens.post.info.data.PostInfoScreenResult
import com.cmoney.kolfanci.ui.screens.shared.snackbar.CustomMessage
import com.cmoney.kolfanci.ui.theme.White_494D54
import com.cmoney.kolfanci.ui.theme.White_767A7F
import com.cmoney.kolfanci.utils.Utils
import com.socks.library.KLog
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class PostViewModel(
    private val postUseCase: PostUseCase,
    val channelId: String,
    private val chatRoomUseCase: ChatRoomUseCase,
    private val postPollUseCase: PostPollUseCase
) : ViewModel() {
    private val TAG = PostViewModel::class.java.simpleName

    //區塊刷新 間隔
    private val scopePollingInterval: Long = 5000

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
    private val _pinPost = MutableStateFlow<BulletinboardMessageWrapper?>(null)
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

                pollingScopePost(
                    channelId = channelId,
                    startItemIndex = 0,
                    lastIndex = 0
                )
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
                    it.message?.let { message ->
                        val pinPost = message.toBulletinboardMessage()
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

                        _pinPost.value =
                            BulletinboardMessageWrapper(message = pinPost, isPin = true)
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

    // TODO: 一個人只能按一個 emoji 處理
    fun onEmojiClick(postMessage: BulletinboardMessageWrapper, resourceId: Int) {
        KLog.i(TAG, "onEmojiClick:$resourceId")
        viewModelScope.launch {
            val clickEmoji = Utils.emojiResourceToServerKey(resourceId)

            var orgEmojiCount = postMessage.message.emojiCount
            val beforeClickEmojiStr = postMessage.message.messageReaction?.emoji
            //之前所點擊的 Emoji
            val beforeClickEmoji = Emojis.decode(beforeClickEmojiStr)

            //將之前所點擊的 Emoji reset
            orgEmojiCount = beforeClickEmoji?.clickCount(-1, orgEmojiCount)

            //判斷是否為收回Emoji
            var emojiCount = 1
            postMessage.message.messageReaction?.let {
                emojiCount = if (it.emoji.orEmpty().lowercase() == clickEmoji.value.lowercase()) {
                    //收回
                    -1
                } else {
                    //增加
                    1
                }
            }

            val newEmoji = clickEmoji.clickCount(emojiCount, orgEmojiCount)

            //回填資料
            val newPostMessage = postMessage.message.copy(
                emojiCount = newEmoji,
                messageReaction = if (emojiCount == -1) null else {
                    IUserMessageReaction(
                        emoji = clickEmoji.value
                    )
                }
            )

            //UI show
            if (postMessage.isPin) {
                _pinPost.value = BulletinboardMessageWrapper(message = newPostMessage, isPin = true)
            } else {
                _post.value = _post.value.map {
                    if (it.message.id == newPostMessage.id) {
                        BulletinboardMessageWrapper(message = newPostMessage)
                    } else {
                        it
                    }
                }
            }


            //Call Emoji api
            chatRoomUseCase.clickEmoji(
                messageServiceType = MessageServiceType.bulletinboard,
                messageId = postMessage.message.id.orEmpty(),
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
                messageId = bulletinboardMessage.id.orEmpty(),
                messageServiceType = MessageServiceType.bulletinboard
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
            if (post.isMyPost()) {
                KLog.i(TAG, "delete my comment.")
                chatRoomUseCase.takeBackMyMessage(
                    messageServiceType = MessageServiceType.bulletinboard,
                    post.id.orEmpty()
                ).fold({
                    _post.value = _post.value.filter {
                        it.message.id != post.id
                    }

                    showPostInfoToast(PostInfoScreenResult.PostInfoAction.Delete)
                }, {
                    KLog.e(TAG, it)
                })
            } else {
                KLog.i(TAG, "delete other comment.")
                //他人
                chatRoomUseCase.deleteOtherMessage(
                    messageServiceType = MessageServiceType.bulletinboard,
                    post.id.orEmpty()
                ).fold({
                    _post.value = _post.value.filter {
                        it.message.id != post.id
                    }

                    showPostInfoToast(PostInfoScreenResult.PostInfoAction.Delete)
                }, {
                    KLog.e(TAG, it)
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
                KLog.i(TAG, "pinPost success.")
                fetchPinPost()
            }, {
                KLog.e(TAG, it)
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
                KLog.i(TAG, "unPinPost success.")
                fetchPinPost()
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 檢查該貼文 是否為 pin
     */
    fun isPinPost(post: BulletinboardMessage): Boolean {
        return _pinPost.value?.message?.id == post.id
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
                reason = reason,
                tabType = ChannelTabType.bulletinboard
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

    /**
     * PostInfo 頁面,操作完成後回來刷新通知
     */
    fun showPostInfoToast(action: PostInfoScreenResult.PostInfoAction) {
        KLog.i(TAG, "showPostInfoToast")
        when (action) {
            PostInfoScreenResult.PostInfoAction.Default -> {}
            PostInfoScreenResult.PostInfoAction.Delete -> {
                _toast.value = CustomMessage(
                    textString = "貼文已刪除！",
                    textColor = Color.White,
                    iconRes = R.drawable.delete,
                    iconColor = White_767A7F,
                    backgroundColor = White_494D54
                )
            }

            PostInfoScreenResult.PostInfoAction.Pin -> {
                _toast.value = CustomMessage(
                    textString = "貼文已置頂！",
                    textColor = Color.White,
                    iconRes = R.drawable.pin,
                    iconColor = White_767A7F,
                    backgroundColor = White_494D54
                )
            }
        }
    }

    private var pollingJob: Job? = null

    /**
     * Polling 範圍內的貼文
     *
     * @param channelId 頻道 id
     * @param startItemIndex 畫面第一個 item position
     * @param lastIndex 畫面最後一個 item position
     */
    fun pollingScopePost(channelId: String,
                         startItemIndex: Int,
                         lastIndex: Int) {
        KLog.i(TAG, "pollingScopePost:$channelId, startItemIndex:$startItemIndex, lastIndex:$lastIndex")

        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            if (_post.value.size > startItemIndex) {
                val item = _post.value[startItemIndex]
                val message = item.message
                val serialNumber = message.serialNumber
                val scopeFetchCount = (lastIndex - startItemIndex).plus(1)

                postPollUseCase.pollScope(
                    delay = scopePollingInterval,
                    channelId = channelId,
                    fromSerialNumber = serialNumber,
                    fetchCount = scopeFetchCount,
                    messageId = message.id.orEmpty()
                ).collect { emitPostPaging ->
                    if (emitPostPaging.items?.isEmpty() == true) {
                        return@collect
                    }

                    //Update data
                    val updateMessageList = _post.value.map { post ->
                        val filterMessage = emitPostPaging.items?.firstOrNull {
                            it.id == post.message.id
                        }

                        if (filterMessage == null) {
                            post
                        } else {
                            post.copy(message = filterMessage)
                        }
                    }

                    _post.update {
                        updateMessageList
                    }
                }
            }
        }
    }
}