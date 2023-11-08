package com.cmoney.kolfanci.ui.screens.post.info.viewmodel

import android.app.Application
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChannelTabType
import com.cmoney.fanciapi.fanci.model.IUserMessageReaction
import com.cmoney.fanciapi.fanci.model.MessageServiceType
import com.cmoney.fanciapi.fanci.model.ReportReason
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.clickCount
import com.cmoney.kolfanci.extension.isMyPost
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.model.usecase.PostUseCase
import com.cmoney.kolfanci.model.usecase.UploadImageUseCase
import com.cmoney.kolfanci.ui.screens.post.info.PostInfoScreenResult
import com.cmoney.kolfanci.ui.screens.post.info.model.ReplyData
import com.cmoney.kolfanci.ui.screens.post.info.model.UiState
import com.cmoney.kolfanci.ui.screens.shared.snackbar.CustomMessage
import com.cmoney.kolfanci.ui.theme.White_494D54
import com.cmoney.kolfanci.ui.theme.White_767A7F
import com.cmoney.kolfanci.utils.Utils
import com.socks.library.KLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PostInfoViewModel(
    private val context: Application,
    private val postUseCase: PostUseCase,
    private val chatRoomUseCase: ChatRoomUseCase,
    private val bulletinboardMessage: BulletinboardMessage,
    private val channel: Channel,
    private val uploadImageUseCase: UploadImageUseCase
) : AndroidViewModel(context) {

    private val TAG = PostInfoViewModel::class.java.simpleName

    private val _post = MutableStateFlow(bulletinboardMessage)
    val post = _post.asStateFlow()

    //留言
    private val _comment = MutableStateFlow<List<BulletinboardMessage>>(emptyList())
    val comment = _comment.asStateFlow()

    private val _uiState: MutableStateFlow<UiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    //點擊留言回覆要顯示的資料
    private val _commentReply = MutableStateFlow<BulletinboardMessage?>(null)
    val commentReply = _commentReply.asStateFlow()

    //回覆有展開的資料
    private val _replyMap =
        MutableStateFlow<SnapshotStateMap<String, ReplyData>>(SnapshotStateMap())
    val replyMap = _replyMap.asStateFlow()

    //輸入匡 文字
    private val _inputText: MutableStateFlow<String> = MutableStateFlow("")
    val inputText = _inputText.asStateFlow()

    //更新貼文
    private val _updatePost = MutableStateFlow<PostInfoScreenResult?>(null)
    val updatePost = _updatePost.asStateFlow()

    //Toast message
    private val _toast = MutableStateFlow<CustomMessage?>(null)
    val toast = _toast.asStateFlow()

    //訊息是否發送完成
    private val _isSendComplete = MutableStateFlow<Boolean>(false)
    val isSendComplete = _isSendComplete.asStateFlow()

    //紀錄是否有分頁
    private val haveNextPageMap = hashMapOf<String, Boolean>()

    //紀錄 分頁索引值
    private val nextWeightMap = hashMapOf<String, Long?>()

    //紀錄展開過的回覆資料
    private val cacheReplyData = hashMapOf<String, ReplyData>()

    //紀錄目前展開狀態
    private val replyExpandState = hashMapOf<String, Boolean>()

    init {
        fetchComment(
            channelId = channel.id.orEmpty(),
            messageId = bulletinboardMessage.id.orEmpty()
        )
    }

    /**
     * 取得 貼文留言
     */
    private fun fetchComment(channelId: String, messageId: String) {
        KLog.i(TAG, "fetchComment:$channelId")
        viewModelScope.launch {
            postUseCase.getComments(
                channelId = channelId,
                messageId = messageId,
                fromSerialNumber = nextWeightMap[bulletinboardMessage.id] ?: 0
            ).fold({
                haveNextPageMap[bulletinboardMessage.id.orEmpty()] = (it.haveNextPage == true)
                nextWeightMap[bulletinboardMessage.id.orEmpty()] = it.nextWeight

                val commentList = _comment.value.toMutableList()

                commentList.addAll(it.items.orEmpty())

                _comment.value = commentList
            }, {
                it.printStackTrace()
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 展開 or 縮合 該留言回覆
     */
    fun onExpandOrCollapseClick(channelId: String, commentId: String) {
        KLog.i(TAG, "fetchCommentReply")
        viewModelScope.launch {
            //目前為展開, 要隱藏起來
            if (isReplyExpand(commentId)) {
                setReplyCollapse(commentId)
                //將顯示資料移除
                _replyMap.value.remove(commentId)
            }
            //展開
            else {
                setReplyExpand(commentId)
                //search cache
                val replyCache = cacheReplyData[commentId]

                if (replyCache == null || replyCache.replyList.isEmpty()) {
                    fetchCommentReply(channelId, commentId)
                } else {
                    _replyMap.value[commentId] = replyCache
                }
            }
        }
    }

    /**
     * Request reply data
     */
    private fun fetchCommentReply(channelId: String, commentId: String) {
        KLog.i(TAG, "fetchCommentReply:$channelId, $commentId")
        viewModelScope.launch {
            postUseCase.getCommentReply(
                channelId = channelId,
                commentId = commentId,
                fromSerialNumber = nextWeightMap[commentId]
            ).fold({
                haveNextPageMap[commentId] = (it.haveNextPage == true)
                nextWeightMap[commentId] = it.nextWeight

                val oldReplyList = _replyMap.value[commentId]?.replyList.orEmpty().toMutableList()
                oldReplyList.addAll(it.items.orEmpty())

                cacheReplyData[commentId] = ReplyData(
                    replyList = oldReplyList,
                    haveMore = (it.haveNextPage == true)
                )

                _replyMap.value[commentId] = ReplyData(
                    replyList = oldReplyList,
                    haveMore = (it.haveNextPage == true)
                )
            }, {
                it.printStackTrace()
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 設定該留言回覆為展開狀態
     */
    private fun setReplyExpand(commentId: String) {
        replyExpandState[commentId] = true
    }

    /**
     * 設定該留言回覆為縮起來狀態
     */
    private fun setReplyCollapse(commentId: String) {
        replyExpandState[commentId] = false
    }

    /**
     * 檢查該留言,是否為展開狀態
     */
    private fun isReplyExpand(commentId: String): Boolean = (replyExpandState[commentId] == true)

    /**
     * 點擊 emoji 處理
     */
    fun onEmojiClick(postMessage: BulletinboardMessage, resourceId: Int) {
        KLog.i(TAG, "onEmojiClick:$resourceId")
        viewModelScope.launch {
            //UI show
            _post.value = emojiHandler(postMessage, resourceId)
        }
    }

    fun onCommentReplySend(
        text: String,
        attachment: List<Pair<AttachmentType, AttachmentInfoItem>>
    ) {
        KLog.i(TAG, "onCommentSend:$text")
        viewModelScope.launch {
            loading()

            //看是要發送回覆 or 留言
            val message = _commentReply.value ?: bulletinboardMessage
            //是否為回覆
            val isReply = _commentReply.value != null

            postUseCase.writeComment(
                channelId = channel.id.orEmpty(),
                messageId = message.id.orEmpty(),
                text = text,
                attachment = attachment
            ).fold({
                _isSendComplete.update { true }

                dismissLoading()
                onCommentReplyClose()

                KLog.i(TAG, "writeComment success.")

                //如果是回覆, 將回覆資料寫回
                if (isReply) {
                    val replyData = _replyMap.value[message.id.orEmpty()]
                    replyData?.let { replyNotNullData ->
                        val replyList = replyNotNullData.replyList.toMutableList()
                        replyList.add(0, it)
                        _replyMap.value[message.id.orEmpty()] = replyNotNullData.copy(
                            replyList = replyList
                        )

                        //刷新上一層 留言裡面的回覆數量
                        refreshCommentCount(message, replyList)

                    } ?: kotlin.run {
                        //empty case
                        _replyMap.value[message.id.orEmpty()] = ReplyData(
                            replyList = listOf(it),
                            haveMore = false
                        )

                        //設定 expand status
                        setReplyExpand(message.id.orEmpty())

                        //刷新上一層 留言裡面的回覆數量
                        refreshCommentCount(message, listOf(it))
                    }
                } else {
                    val comments = _comment.value.toMutableList()
                    comments.add(0, it)
                    _comment.value = comments
                }

                delay(500)
                _isSendComplete.update { false }

            }, {
                dismissLoading()
                onCommentReplyClose()

                it.printStackTrace()
                KLog.e(TAG, it)
            })
        }
    }

    private fun loading() {
        _uiState.value = UiState.ShowLoading
    }

    private fun dismissLoading() {
        _uiState.value = UiState.DismissLoading
    }

    /**
     * 點擊 留言的 Emoji
     */
    fun onCommentEmojiClick(commentMessage: BulletinboardMessage, resourceId: Int) {
        KLog.i(TAG, "onCommentEmojiClick:$comment")
        viewModelScope.launch {
            //回填資料
            val postMessage = emojiHandler(commentMessage, resourceId)

            _comment.value = _comment.value.map {
                if (it.id == postMessage.id) {
                    postMessage
                } else {
                    it
                }
            }
        }
    }

    /**
     * 處理 Emoji增刪後的資料
     */
    private suspend fun emojiHandler(
        message: BulletinboardMessage,
        resourceId: Int
    ): BulletinboardMessage {

        val clickEmoji = Utils.emojiResourceToServerKey(resourceId)
        //判斷是否為收回Emoji
        var emojiCount = 1
        message.messageReaction?.let {
            emojiCount = if (it.emoji.orEmpty().lowercase() == clickEmoji.value.lowercase()) {
                //收回
                -1
            } else {
                //增加
                1
            }
        }

        val orgEmoji = message.emojiCount
        val newEmoji = clickEmoji.clickCount(emojiCount, orgEmoji)

        val postMessage = message.copy(
            emojiCount = newEmoji,
            messageReaction = if (emojiCount == -1) null else {
                IUserMessageReaction(
                    emoji = clickEmoji.value
                )
            }
        )

        //Call Emoji api
        chatRoomUseCase.clickEmoji(
            messageServiceType = MessageServiceType.bulletinboard,
            messageId = postMessage.id.orEmpty(),
            emojiCount = emojiCount,
            clickEmoji = clickEmoji
        )

        return postMessage
    }

    /**
     * 點擊留言-回覆
     */
    fun onCommentReplyClick(bulletinboardMessage: BulletinboardMessage) {
        KLog.i(TAG, "onCommentReplyClick:$bulletinboardMessage")
        _commentReply.value = bulletinboardMessage
    }

    /**
     * 關閉留言-回覆
     */
    fun onCommentReplyClose() {
        KLog.i(TAG, "onCommentReplyClose")
        _commentReply.value = null
        _inputText.value = ""
    }

    /**
     * 留言 讀取更多
     */
    fun onCommentLoadMore() {
        KLog.i(TAG, "onCommentLoadMore:" + haveNextPageMap[bulletinboardMessage.id.orEmpty()])
        viewModelScope.launch {
            //check has next page
            if (haveNextPageMap[bulletinboardMessage.id.orEmpty()] == true) {
                fetchComment(
                    channelId = channel.id.orEmpty(),
                    messageId = bulletinboardMessage.id.orEmpty()
                )
            }
        }
    }

    /**
     * 讀取 更多回覆資料
     */
    fun onLoadMoreReply(comment: BulletinboardMessage) {
        KLog.i(TAG, "onLoadMoreReply:" + haveNextPageMap[bulletinboardMessage.id.orEmpty()])
        viewModelScope.launch {
            //check has next page
            if (haveNextPageMap[bulletinboardMessage.id.orEmpty()] == true) {
                fetchCommentReply(
                    channelId = channel.id.orEmpty(),
                    commentId = comment.id.orEmpty()
                )
            }
        }
    }

    /**
     * 點擊 回覆的 Emoji
     */
    fun onReplyEmojiClick(
        comment: BulletinboardMessage,
        reply: BulletinboardMessage,
        resourceId: Int
    ) {
        KLog.i(TAG, "onReplyEmojiClick.")
        viewModelScope.launch {
            //回填資料
            val replyMessage = emojiHandler(reply, resourceId)
            _replyMap.value[comment.id.orEmpty()]?.let { replyData ->
                val replyList = replyData.replyList.map { replyItem ->
                    if (replyItem.id == reply.id) {
                        replyMessage
                    } else {
                        replyItem
                    }
                }

                _replyMap.value[comment.id.orEmpty()] = replyData.copy(
                    replyList = replyList
                )

                //Update cache
                cacheReplyData[comment.id.orEmpty()] = replyData.copy(
                    replyList = replyList
                )
            }
        }
    }


    /**
     * 刪除留言 or 回覆
     *
     * @param comment 要刪除資料
     * @param isComment 是否為留言否則為回覆
     */
    fun onDeleteCommentOrReply(comment: Any?, reply: Any?, isComment: Boolean) {
        KLog.i(TAG, "onDeleteCommentOrReply:$isComment,  $comment")
        comment?.let {
            if (comment is BulletinboardMessage) {
                viewModelScope.launch {
                    val deleteId = if (isComment) {
                        comment.id
                    } else {
                        if (reply is BulletinboardMessage) {
                            reply.id
                        } else {
                            ""
                        }
                    }

                    //我發的
                    if (comment.isMyPost()) {
                        KLog.i(TAG, "delete my comment.")
                        chatRoomUseCase.takeBackMyMessage(
                            messageServiceType = MessageServiceType.bulletinboard,
                            deleteId.orEmpty()
                        ).fold({
                            if (isComment) {
                                deleteComment(comment)
                            } else {
                                deleteReply(comment, reply)
                            }
                        }, {
                            KLog.e(TAG, it)
                        })
                    } else {
                        KLog.i(TAG, "delete other comment.")
                        //他人
                        chatRoomUseCase.deleteOtherMessage(
                            messageServiceType = MessageServiceType.bulletinboard,
                            deleteId.orEmpty()
                        ).fold({
                            if (isComment) {
                                deleteComment(comment)
                            } else {
                                deleteReply(comment, reply)
                            }
                        }, {
                            KLog.e(TAG, it)
                        })
                    }
                }
            }
        }
    }

    /**
     * 刪除 留言
     */
    private fun deleteComment(comment: BulletinboardMessage) {
        KLog.i(TAG, "deleteComment:$comment")
        _comment.value = _comment.value.filter {
            it.id != comment.id
        }

        _toast.value = CustomMessage(
            textString = "留言已刪除！",
            textColor = Color.White,
            iconRes = R.drawable.delete,
            iconColor = White_767A7F,
            backgroundColor = White_494D54
        )
    }

    /**
     * 刪除 回覆
     */
    private fun deleteReply(comment: BulletinboardMessage, reply: Any?) {
        KLog.i(TAG, "deleteReply:$reply")
        reply?.let {
            if (reply is BulletinboardMessage) {
                val replyData = _replyMap.value[comment.id]
                replyData?.let { replyNotNullData ->
                    val filterReplyList = replyNotNullData.replyList.filter {
                        it.id != reply.id
                    }
                    _replyMap.value[comment.id.orEmpty()] = replyNotNullData.copy(
                        replyList = filterReplyList
                    )

                    _toast.value = CustomMessage(
                        textString = "回覆已刪除！",
                        textColor = Color.White,
                        iconRes = R.drawable.delete,
                        iconColor = White_767A7F,
                        backgroundColor = White_494D54
                    )

                    //刷新上一層 留言裡面的回覆數量
                    refreshCommentCount(comment, filterReplyList)
                }
            }
        }
    }

    /**
     * 刷新 留言數量
     */
    private fun refreshCommentCount(
        comment: BulletinboardMessage,
        replyList: List<BulletinboardMessage>
    ) {
        KLog.i(TAG, "refreshCommentCount")
        _comment.value = _comment.value.map {
            if (comment.id == it.id) {
                it.copy(
                    commentCount = replyList.size
                )
            } else {
                it
            }
        }
    }


    /**
     * 刪除貼文
     */
    fun onDeletePostClick(post: BulletinboardMessage) {
        KLog.i(TAG, "onDeletePostClick:$post")
        viewModelScope.launch {
            //我發的
            if (post.isMyPost()) {
                KLog.i(TAG, "delete my comment.")
                chatRoomUseCase.takeBackMyMessage(
                    messageServiceType = MessageServiceType.bulletinboard,
                    post.id.orEmpty()
                ).fold({
                    _updatePost.value = PostInfoScreenResult(
                        post = post,
                        action = PostInfoScreenResult.PostInfoAction.Delete
                    )
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
                    _updatePost.value = PostInfoScreenResult(
                        post = post,
                        action = PostInfoScreenResult.PostInfoAction.Delete
                    )
                }, {
                    KLog.e(TAG, it)
                })
            }
        }
    }

    /**
     * 更新貼文
     */
    fun onUpdatePost(post: BulletinboardMessage) {
        _post.value = post
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
                _updatePost.value = PostInfoScreenResult(
                    post = message!!,
                    action = PostInfoScreenResult.PostInfoAction.Pin
                )
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
                _updatePost.value = PostInfoScreenResult(
                    post = message!!,
                    action = PostInfoScreenResult.PostInfoAction.Default
                )
            }, {
                KLog.e(TAG, it)
            })
        }
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
     * 留言編輯完後, 更新留言資料
     */
    fun onUpdateComment(editMessage: BulletinboardMessage) {
        KLog.i(TAG, "onUpdateComment:$editMessage")
        viewModelScope.launch {
            _comment.value = _comment.value.map {
                if (it.id == editMessage.id) {
                    editMessage
                } else {
                    it
                }
            }

        }
    }

    /**
     * 回覆編輯完後, 更新回覆資料
     */
    fun onUpdateReply(editMessage: BulletinboardMessage, commentId: String) {
        KLog.i(TAG, "onUpdateReply:$editMessage")
        viewModelScope.launch {
            val replyData = _replyMap.value[commentId] ?: ReplyData(
                replyList = emptyList(),
                haveMore = false
            )

            val replyList = replyData.replyList.map {
                if (it.id == editMessage.id) {
                    editMessage
                } else {
                    it
                }
            }
            _replyMap.value[commentId] = replyData.copy(
                replyList = replyList
            )
        }
    }
}