package com.cmoney.kolfanci.ui.screens.post.info.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.IUserMessageReaction
import com.cmoney.imagelibrary.UploadImage
import com.cmoney.kolfanci.BuildConfig
import com.cmoney.kolfanci.extension.clickCount
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.model.usecase.PostUseCase
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.MessageViewModel
import com.cmoney.kolfanci.ui.screens.post.edit.viewmodel.UiState
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

    sealed class UiState {
        object ShowLoading : UiState()
        object DismissLoading : UiState()
    }

    private val TAG = PostInfoViewModel::class.java.simpleName

    private val _post = MutableStateFlow(bulletinboardMessage)
    val post = _post.asStateFlow()

    private val _comment = MutableStateFlow<List<BulletinboardMessage>>(emptyList())
    val comment = _comment.asStateFlow()

    private val _imageAttach = MutableStateFlow<List<Uri>>(emptyList())
    val imageAttach = _imageAttach.asStateFlow()

    private val _uiState: MutableStateFlow<UiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    var haveNextPage: Boolean = false
    var nextWeight: Long? = null    //貼文分頁 索引

    init {
        fetchComment(
            channelId = channel.id.orEmpty(),
            messageId = bulletinboardMessage.id.orEmpty()
        )
    }

    /**
     * 取得 貼文留言
     */
    fun fetchComment(channelId: String, messageId: String) {
        KLog.i(TAG, "fetchComment:$channelId")
        viewModelScope.launch {
            postUseCase.getComments(
                channelId = channelId,
                messageId = messageId,
                fromSerialNumber = nextWeight
            ).fold({
                haveNextPage = it.haveNextPage == true
                nextWeight = it.nextWeight

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
     * 點擊 emoji 處理
     */
    fun onEmojiClick(postMessage: BulletinboardMessage, resourceId: Int) {
        KLog.i(TAG, "onEmojiClick:$resourceId")
        viewModelScope.launch {
            //UI show
            _post.value = emojiHandler(postMessage, resourceId)
        }
    }

    fun onCommentSend(text: String) {
        KLog.i(TAG, "onCommentSend:$text")
        viewModelScope.launch {
            loading()

            //附加圖片, 獲取圖片 Url
            if (_imageAttach.value.isNotEmpty()) {
                uploadImages(_imageAttach.value, object : MessageViewModel.ImageUploadCallback {
                    override fun complete(images: List<String>) {
                        KLog.i(TAG, "all image upload complete:" + images.size)
                        sendComment(text, images)
                    }

                    override fun onFailure(e: Throwable) {
                        KLog.e(TAG, "onFailure:$e")
                        dismissLoading()
                    }
                })
                _imageAttach.value = emptyList()
            } else {
                sendComment(text, emptyList())
            }
        }
    }

    /**
     * 發送留言給後端
     */
    private fun sendComment(text: String, images: List<String>) {
        KLog.i(TAG, "sendComment")
        viewModelScope.launch {
            loading()

            postUseCase.writeComment(
                channelId = channel.id.orEmpty(),
                messageId = bulletinboardMessage.id.orEmpty(),
                text = text,
                images = images
            ).fold({
                dismissLoading()
                KLog.i(TAG, "writeComment success.")

                val comments = _comment.value.toMutableList()
                comments.add(it)

                _comment.value = comments
            }, {
                dismissLoading()
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

}