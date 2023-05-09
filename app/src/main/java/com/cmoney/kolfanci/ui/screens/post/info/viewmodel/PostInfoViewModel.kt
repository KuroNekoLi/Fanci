package com.cmoney.kolfanci.ui.screens.post.info.viewmodel

import android.app.Application
import android.net.Uri
import android.webkit.URLUtil
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.IUserMessageReaction
import com.cmoney.fanciapi.fanci.model.Media
import com.cmoney.fanciapi.fanci.model.MediaIChatContent
import com.cmoney.fanciapi.fanci.model.MediaType
import com.cmoney.imagelibrary.UploadImage
import com.cmoney.kolfanci.BuildConfig
import com.cmoney.kolfanci.extension.EmptyBodyException
import com.cmoney.kolfanci.extension.clickCount
import com.cmoney.kolfanci.extension.isMyPost
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.model.usecase.PostUseCase
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.MessageViewModel
import com.cmoney.kolfanci.ui.screens.post.info.model.ReplyData
import com.cmoney.kolfanci.ui.screens.post.info.model.UiState
import com.cmoney.kolfanci.utils.Utils
import com.cmoney.xlogin.XLoginHelper
import com.socks.library.KLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostInfoViewModel(
    private val context: Application,
    private val postUseCase: PostUseCase,
    private val chatRoomUseCase: ChatRoomUseCase,
    private val bulletinboardMessage: BulletinboardMessage,
    private val channel: Channel
) : AndroidViewModel(context) {

    private val TAG = PostInfoViewModel::class.java.simpleName

    private val _post = MutableStateFlow(bulletinboardMessage)
    val post = _post.asStateFlow()

    //留言
    private val _comment = MutableStateFlow<List<BulletinboardMessage>>(emptyList())
    val comment = _comment.asStateFlow()

    private val _imageAttach = MutableStateFlow<List<Uri>>(emptyList())
    val imageAttach = _imageAttach.asStateFlow()

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

    //刪除貼文
    private val _deletePost = MutableStateFlow<BulletinboardMessage?>(null)
    val deletePost = _deletePost.asStateFlow()

    private val haveNextPageMap = hashMapOf<String, Boolean>() //紀錄是否有分頁
    private val nextWeightMap = hashMapOf<String, Long?>() //紀錄 分頁索引值

    //紀錄展開過的回覆資料
    private val cacheReplyData = hashMapOf<String, ReplyData>()

    //紀錄目前展開狀態
    private val replyExpandState = hashMapOf<String, Boolean>()

    //目前正在編輯的訊息
    private var currentEditMessage: BulletinboardMessage? = null

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
                fromSerialNumber = nextWeightMap[bulletinboardMessage.id]
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

                if (replyCache == null || replyCache.replyList.isNullOrEmpty()) {
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

    fun onCommentReplySend(text: String) {
        KLog.i(TAG, "onCommentSend:$text")
        viewModelScope.launch {
            loading()

            //附加圖片, 獲取圖片 Url
            val httpUrls = _imageAttach.value.filter {
                it.toString().startsWith("https")
            }.map {
                it.toString()
            }.toMutableList()

            val filesUri = _imageAttach.value.filter {
                !it.toString().startsWith("https")
            }

            if (filesUri.isNotEmpty()) {
                uploadImages(filesUri, object : MessageViewModel.ImageUploadCallback {
                    override fun complete(images: List<String>) {
                        KLog.i(TAG, "all image upload complete:" + images.size)
                        httpUrls.addAll(images)
                        sendCommentOrReply(text, httpUrls)
                    }

                    override fun onFailure(e: Throwable) {
                        KLog.e(TAG, "onFailure:$e")
                        dismissLoading()
                    }
                })
                _imageAttach.value = emptyList()
            } else {
                sendCommentOrReply(text, emptyList())
            }
        }
    }

    /**
     * 發送留言/回覆給後端
     */
    private fun sendCommentOrReply(text: String, images: List<String>) {
        KLog.i(TAG, "sendComment")
        viewModelScope.launch {
            loading()

            //看是要發送回覆 or 留言
            val message = _commentReply.value ?: bulletinboardMessage
            //是否為回覆
            val isReply = _commentReply.value != null

            //是否為編輯
            if (currentEditMessage != null) {
                updateEditMessage(
                    text, images, isReply
                )
            } else {
                postUseCase.writeComment(
                    channelId = channel.id.orEmpty(),
                    messageId = message.id.orEmpty(),
                    text = text,
                    images = images
                ).fold({
                    dismissLoading()
                    onCommentReplyClose()

                    KLog.i(TAG, "writeComment success.")

                    //如果是回覆, 將回覆資料寫回
                    if (isReply) {
                        val replyData = _replyMap.value[message.id.orEmpty()]
                        replyData?.let { replyData ->
                            val replyList = replyData.replyList.toMutableList()
                            replyList.add(it)
                            _replyMap.value[message.id.orEmpty()] = replyData.copy(
                                replyList = replyList
                            )
                        } ?: kotlin.run {
                            //empty case
                            _replyMap.value[message.id.orEmpty()] = ReplyData(
                                replyList = listOf(it),
                                haveMore = false
                            )
                        }
                    } else {
                        val comments = _comment.value.toMutableList()
                        comments.add(it)
                        _comment.value = comments
                    }

                }, {
                    dismissLoading()
                    onCommentReplyClose()

                    it.printStackTrace()
                    KLog.e(TAG, it)
                })
            }
        }
    }

    /**
     * 編輯 更新資料
     */
    private fun updateEditMessage(text: String, images: List<String>, isReply: Boolean) {
        KLog.i(TAG, "updateMessage")
        viewModelScope.launch {
            //看是 回覆 or 留言
            val message = _commentReply.value ?: bulletinboardMessage

            currentEditMessage?.let { currentEditMessage ->
                this@PostInfoViewModel.currentEditMessage = null

                chatRoomUseCase.updateMessage(
                    messageId = currentEditMessage.id.orEmpty(),
                    text = text,
                    images = images
                ).fold({
                }, {
                    if (it is EmptyBodyException) {
                        //如果是回覆, 將回覆資料寫回
                        if (isReply) {
                            val replyData = _replyMap.value[message.id] ?: ReplyData(
                                replyList = emptyList(),
                                haveMore = false
                            )
                            val replyList = replyData.replyList.map {
                                if (it.id == currentEditMessage.id) {
                                    currentEditMessage.copy(
                                        content = MediaIChatContent(
                                            text = text,
                                            medias = images.map { image ->
                                                Media(
                                                    resourceLink = image,
                                                    type = MediaType.image
                                                )
                                            }
                                        )
                                    )
                                } else {
                                    it
                                }
                            }

                            _replyMap.value[message.id.orEmpty()] = replyData.copy(
                                replyList = replyList
                            )
                        } else {
                            //更新留言
                            _comment.value = _comment.value.map {
                                if (it.id == currentEditMessage.id) {
                                    currentEditMessage.copy(
                                        content = MediaIChatContent(
                                            text = text,
                                            medias = images.map { image ->
                                                Media(
                                                    resourceLink = image,
                                                    type = MediaType.image
                                                )
                                            }
                                        )
                                    )
                                }
                                else {
                                    it
                                }
                            }
                        }
                    }
                    dismissLoading()
                    onCommentReplyClose()
                    it.printStackTrace()
                    KLog.e(TAG, it)
                })
            }
        }
    }

    private fun loading() {
        _uiState.value = UiState.ShowLoading
    }

    private fun dismissLoading() {
        _uiState.value = UiState.DismissLoading
    }

    private suspend fun uploadImages(
        uriLis: List<Uri>,
        imageUploadCallback: MessageViewModel.ImageUploadCallback
    ) {
        KLog.i(TAG, "uploadImages:" + uriLis.size)
        val uploadImage = UploadImage(
            context,
            uriLis,
            XLoginHelper.accessToken,
            isStaging = BuildConfig.DEBUG
        )

        val completeImageUrl = mutableListOf<String>()

        withContext(Dispatchers.IO) {
            uploadImage.upload().catch { e ->
                KLog.e(TAG, e)
                imageUploadCallback.onFailure(e)
            }.collect {
                KLog.i(TAG, "uploadImage:$it")
                val uri = it.first
                val imageUrl = it.second
                completeImageUrl.add(imageUrl)
                if (completeImageUrl.size == uriLis.size) {
                    imageUploadCallback.complete(completeImageUrl)
                }
            }
        }
    }

    /**
     * 附加圖片
     */
    fun attachImage(uri: Uri) {
        KLog.i(TAG, "attachImage:$uri")
        val imageList = _imageAttach.value.toMutableList()
        imageList.add(uri)
        _imageAttach.value = imageList
    }

    /**
     * 刪除選擇的 附加圖片
     */
    fun onDeleteAttach(uri: Uri) {
        KLog.i(TAG, "onDeleteAttach:$uri")
        _imageAttach.value = _imageAttach.value.filter {
            it != uri
        }
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
     * 點擊 編輯留言
     */
    fun onEditCommentClick(comment: BulletinboardMessage) {
        KLog.i(TAG, "onEditCommentClick:$comment")
        currentEditMessage = comment
        _inputText.value = comment.content?.text.orEmpty()
        _imageAttach.value = comment.content?.medias?.map {
            Uri.parse(it.resourceLink)
        }.orEmpty()
    }

    /**
     * 點擊 編輯回覆
     */
    fun onEditReplyClick(comment: BulletinboardMessage, reply: BulletinboardMessage) {
        KLog.i(TAG, "onEditReplyClick.")
        currentEditMessage = reply
        onCommentReplyClick(comment)
        _inputText.value = reply.content?.text.orEmpty()
        _imageAttach.value = reply.content?.medias?.map {
            Uri.parse(it.resourceLink)
        }.orEmpty()
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
        comment?.let { comment ->
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
                    if (comment.isMyPost(Constant.MyInfo)) {
                        KLog.i(TAG, "delete my comment.")
                        chatRoomUseCase.takeBackMyMessage(deleteId.orEmpty()).fold({
                        }, {
                            if (it is EmptyBodyException) {
                                if (isComment) {
                                    deleteComment(comment)
                                } else {
                                    deleteReply(comment, reply)
                                }
                            } else {
                                it.printStackTrace()
                            }
                        })
                    } else {
                        KLog.i(TAG, "delete other comment.")
                        //他人
                        chatRoomUseCase.deleteOtherMessage(deleteId.orEmpty()).fold({
                        }, {
                            if (it is EmptyBodyException) {
                                if (isComment) {
                                    deleteComment(comment)
                                } else {
                                    deleteReply(comment, reply)
                                }
                            } else {
                                it.printStackTrace()
                            }
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
    }

    /**
     * 刪除 回覆
     */
    private fun deleteReply(comment: BulletinboardMessage, reply: Any?) {
        KLog.i(TAG, "deleteReply:$reply")
        reply?.let {
            if (reply is BulletinboardMessage) {
                val replyData = _replyMap.value[comment.id]
                replyData?.let { replyData ->
                    val filterReplyList = replyData.replyList.filter {
                        it.id != reply.id
                    }
                    _replyMap.value[comment.id.orEmpty()] = replyData.copy(
                        replyList = filterReplyList
                    )
                }
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
            if (post.isMyPost(Constant.MyInfo)) {
                KLog.i(TAG, "delete my comment.")
                chatRoomUseCase.takeBackMyMessage(post.id.orEmpty()).fold({
                }, {
                    if (it is EmptyBodyException) {
                        _deletePost.value = post
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
                        _deletePost.value = post
                    } else {
                        it.printStackTrace()
                    }
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

}