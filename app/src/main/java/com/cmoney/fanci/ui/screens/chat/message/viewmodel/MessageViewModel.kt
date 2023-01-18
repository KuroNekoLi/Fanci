package com.cmoney.fanci.ui.screens.chat.message.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.BuildConfig
import com.cmoney.fanci.R
import com.cmoney.fanci.extension.EmptyBodyException
import com.cmoney.fanci.model.ChatMessageWrapper
import com.cmoney.fanci.model.usecase.ChatRoomPollUseCase
import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import com.cmoney.fanci.ui.screens.chat.viewmodel.ChatRoomUiState
import com.cmoney.fanci.ui.screens.shared.bottomSheet.MessageInteract
import com.cmoney.fanci.ui.screens.shared.snackbar.CustomMessage
import com.cmoney.fanci.ui.theme.White_494D54
import com.cmoney.fanci.ui.theme.White_767A7F
import com.cmoney.fanci.utils.Utils
import com.cmoney.fanciapi.fanci.model.*
import com.cmoney.imagelibrary.UploadImage
import com.cmoney.xlogin.XLoginHelper
import com.socks.library.KLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MessageUiState(
    val snackBarMessage: CustomMessage? = null,
    val message: List<ChatMessageWrapper> = emptyList(),    //訊息
    val imageAttach: List<Uri> = emptyList(),   //附加圖片
    val isSendComplete: Boolean = false,        //訊息是否發送完成
    val replyMessage: ChatMessage? = null,      //回覆訊息用
    val routeAnnounceMessage: ChatMessage? = null,    //設定公告訊息,跳轉設定頁面
    val copyMessage: ChatMessage? = null,    //複製訊息
    val hideUserMessage: ChatMessage? = null,    //封鎖用戶
    val reportMessage: ChatMessage? = null
)

/**
 * 處理聊天室 相關訊息
 */
class MessageViewModel(
    val context: Context,
    val chatRoomUseCase: ChatRoomUseCase,
    private val chatRoomPollUseCase: ChatRoomPollUseCase
) : ViewModel() {
    private val TAG = MessageViewModel::class.java.simpleName

    var uiState by mutableStateOf(MessageUiState())
        private set

    private val preSendChatId = "Preview"       //發送前預覽的訊息id, 用來跟其他訊息區分

    private val pollingInterval = 30000L

    /**
     * 圖片上傳 callback
     */
    interface ImageUploadCallback {
        fun complete(images: List<String>)

        fun onFailure(e: Throwable)
    }

    /**
     * 開始 Polling 聊天室 訊息
     * @param channelId 聊天室id
     */
    fun startPolling(channelId: String?) {
        KLog.i(TAG, "startPolling:$channelId")
        viewModelScope.launch {
            if (channelId?.isNotEmpty() == true) {
                chatRoomPollUseCase.poll(pollingInterval, channelId).collect {
                    KLog.i(TAG, it)
                    val newMessage = it.items?.map { chatMessage ->
                        ChatMessageWrapper(message = chatMessage)
                    }.orEmpty().reversed()
                    processMessageCombine(newMessage, true)
                }
            }
        }
    }

    /**
     * 停止 Polling 聊天室 訊息
     */
    fun stopPolling() {
        KLog.i(TAG, "stopPolling")
        chatRoomPollUseCase.close()
    }

    /**
     *  將新的訊息 跟舊的合併
     *  @param newChatMessage 新訊息
     *  @param isLatest 是否為新訊息,要加在頭
     */
    private fun processMessageCombine(
        newChatMessage: List<ChatMessageWrapper>,
        isLatest: Boolean = false
    ) {
        //combine old message
        val oldMessage = uiState.message.toMutableList()
        if (isLatest) {
            oldMessage.addAll(0, newChatMessage)
        } else {
            oldMessage.addAll(newChatMessage)
        }

        val distinctMessage = oldMessage.distinctBy { combineMessage ->
            combineMessage.message.id
        }
        uiState = uiState.copy(
            message = distinctMessage,
        )
    }

    /**
     * 讀取更多 訊息
     * @param channelId 頻道id
     */
    fun onLoadMore(channelId: String) {
        KLog.i(TAG, "onLoadMore.")
        viewModelScope.launch {
            //訊息不為空,才抓取分頁,因為預設會有Polling訊息, 超過才需讀取分頁
            if (uiState.message.isNotEmpty()) {
                val lastMessage = uiState.message.last()

                val serialNumber = lastMessage.message.serialNumber
                chatRoomUseCase.fetchMoreMessage(
                    chatRoomChannelId = channelId,
                    fromSerialNumber = serialNumber,
                ).fold({
                    it.items?.also { message ->
                        val newMessage = message.map {
                            ChatMessageWrapper(message = it)
                        }.reversed()
                        processMessageCombine(newMessage)
                    }
                }, {
                    KLog.e(TAG, it)
                })
            }
        }
    }

    /**
     * 附加 圖片
     * @param uri 圖片 uri.
     */
    fun attachImage(uri: Uri) {
        KLog.i(TAG, "attachImage:$uri")
        val imageList = uiState.imageAttach.toMutableList()
        imageList.add(uri)
        uiState = uiState.copy(
            imageAttach = imageList
        )
    }

    /**
     * 移除 附加 圖片
     * @param uri 圖片 uri.
     */
    fun removeAttach(uri: Uri) {
        KLog.i(TAG, "removeAttach:$uri")
        val imageList = uiState.imageAttach.toMutableList()

        uiState = uiState.copy(
            imageAttach = imageList.filter {
                it != uri
            }
        )
    }

    /**
     * 對外 發送訊息 接口
     * @param channelId 頻道id
     * @param text 內文
     */
    fun messageSend(channelId: String, text: String) {
        KLog.i(TAG, "messageSend:$text")
        viewModelScope.launch {
            generatePreviewBeforeSend(text)

            //附加圖片, 獲取圖片 Url
            if (uiState.imageAttach.isNotEmpty()) {
                uploadImages(uiState.imageAttach, object : ImageUploadCallback {
                    override fun complete(images: List<String>) {
                        KLog.i(TAG, "all image upload complete:" + images.size)
                        send(channelId, text, images)
                    }

                    override fun onFailure(e: Throwable) {
                        KLog.e(TAG, "onFailure:$e")
                    }
                })
                uiState = uiState.copy(imageAttach = emptyList())
            }
            //Only text
            else {
                send(channelId, text)
            }
        }
    }

    /**
     * 發送前 直接先貼上畫面 Preview
     * @param text 發送文字
     */
    private fun generatePreviewBeforeSend(text: String) {
        uiState = uiState.copy(
            message = uiState.message.toMutableList().apply {
                add(0,
                    ChatMessageWrapper(
                        message = ChatMessage(
                            id = preSendChatId,
                            author = GroupMember(
                                name = XLoginHelper.nickName,
                                thumbNail = XLoginHelper.headImagePath
                            ),
                            content = MediaIChatContent(
                                text = text
                            ),
                            createUnixTime = System.currentTimeMillis() / 1000,
                            replyMessage = uiState.replyMessage
                        ),
                        uploadAttachPreview = uiState.imageAttach.map { uri ->
                            ChatRoomUiState.ImageAttachState(
                                uri = uri
                            )
                        }
                    )
                )
            }
        )
    }

    /**
     * 上傳所有 附加圖片
     * @param uriLis 圖片清單
     * @param imageUploadCallback 圖片上傳 callback
     */
    private suspend fun uploadImages(uriLis: List<Uri>, imageUploadCallback: ImageUploadCallback) {
        KLog.i(TAG, "uploadImages:" + uriLis.size)

        uiState = uiState.copy(
            imageAttach = emptyList()
        )

        val uploadImage = UploadImage(
            context,
            uriLis,
            XLoginHelper.accessToken,
            isStaging = BuildConfig.DEBUG
        )

        withContext(Dispatchers.IO) {
            uploadImage.upload().catch { e ->
                KLog.e(TAG, e)
                imageUploadCallback.onFailure(e)

            }.collect {
                KLog.i(TAG, "uploadImage:$it")
                val uri = it.first
                val imageUrl = it.second
                //將 上傳成功的圖片 message overwrite 更改狀態
                uiState = uiState.copy(
                    message = uiState.message.map { chatMessageWrapper ->
                        if (chatMessageWrapper.message.id == preSendChatId) {
                            chatMessageWrapper.copy(
                                uploadAttachPreview = chatMessageWrapper.uploadAttachPreview.map { attachImage ->
                                    if (attachImage.uri == uri) {
                                        ChatRoomUiState.ImageAttachState(
                                            uri = uri,
                                            serverUrl = imageUrl,
                                            isUploadComplete = true
                                        )
                                    } else {
                                        attachImage
                                    }
                                }
                            )
                        } else {
                            chatMessageWrapper
                        }
                    }
                )

                //check is all image upload complete
                val preSendAttach = uiState.message.find {
                    it.message.id == preSendChatId
                }?.uploadAttachPreview

                val isComplete = preSendAttach?.none { imageAttach ->
                    !imageAttach.isUploadComplete
                }

                if (isComplete == true) {
                    imageUploadCallback.complete(preSendAttach.map { attach ->
                        attach.serverUrl
                    })
                }
            }
        }
    }

    /**
     * 對後端 Server 發送訊息
     * @param channelId 頻道id
     * @param text 發送內文
     * @param images 圖片清單
     */
    private fun send(channelId: String, text: String, images: List<String> = emptyList()) {
        KLog.i(TAG, "send:" + text + " , media:" + images.size)
        viewModelScope.launch {
            chatRoomUseCase.sendMessage(
                chatRoomChannelId = channelId,
                text = text,
                images = images,
                replyMessageId = uiState.replyMessage?.id.orEmpty()
            ).fold({ chatMessage ->
                //發送成功
                KLog.i(TAG, "send success:$chatMessage")
                uiState = uiState.copy(
                    message = uiState.message.map {
                        if (it.message.id == preSendChatId) {
                            ChatMessageWrapper(message = chatMessage)
                        } else {
                            it
                        }
                    },
                    isSendComplete = true,
                    replyMessage = null
                )

                //恢復聊天室內訊息,不會自動捲到最下面
                delay(800)
                uiState = uiState.copy(isSendComplete = false)

            }, {
                KLog.e(TAG, it)
                // TODO: send error
            })
        }
    }

    /**
     * 點擊 Emoji, 判斷是否增加or收回
     *
     * @param chatMessage 訊息 model
     * @param resourceId 點擊的Emoji resourceId
     */
    private fun onEmojiClick(chatMessage: ChatMessage, resourceId: Int) {
        KLog.i(TAG, "onEmojiClick:$chatMessage")
        viewModelScope.launch {
            val clickEmoji = Utils.emojiResourceToServerKey(resourceId)

            //判斷是否為收回Emoji
            var emojiCount = 1
            chatMessage.messageReaction?.let {
                emojiCount = if (it.emoji.orEmpty().lowercase() == clickEmoji.value.lowercase()) {
                    //收回
                    -1
                } else {
                    //增加
                    1
                }
            }

            //先將 Emoji append to ui.
            uiState = uiState.copy(
                message = uiState.message.map { chatMessageWrapper ->
                    if (chatMessageWrapper.message.id == chatMessage.id) {
                        val orgEmoji = chatMessageWrapper.message.emojiCount
                        val newEmoji = when (clickEmoji) {
                            Emojis.like -> orgEmoji?.copy(like = orgEmoji.like?.plus(emojiCount))
                            Emojis.dislike -> orgEmoji?.copy(
                                dislike = orgEmoji.dislike?.plus(
                                    emojiCount
                                )
                            )
                            Emojis.laugh -> orgEmoji?.copy(laugh = orgEmoji.laugh?.plus(emojiCount))
                            Emojis.money -> orgEmoji?.copy(money = orgEmoji.money?.plus(emojiCount))
                            Emojis.shock -> orgEmoji?.copy(shock = orgEmoji.shock?.plus(emojiCount))
                            Emojis.cry -> orgEmoji?.copy(cry = orgEmoji.cry?.plus(emojiCount))
                            Emojis.think -> orgEmoji?.copy(think = orgEmoji.think?.plus(emojiCount))
                            Emojis.angry -> orgEmoji?.copy(angry = orgEmoji.angry?.plus(emojiCount))
                        }

                        //回填資料
                        chatMessageWrapper.copy(
                            message = chatMessageWrapper.message.copy(
                                emojiCount = newEmoji,
                                messageReaction = if (emojiCount == -1) null else {
                                    IUserMessageReaction(
                                        emoji = clickEmoji.value
                                    )
                                }
                            )
                        )
                    } else {
                        chatMessageWrapper
                    }
                }
            )

            //Call Emoji api
            if (emojiCount == -1) {
                //收回
                chatRoomUseCase.deleteEmoji(chatMessage.id.orEmpty()).fold({
                    KLog.e(TAG, "delete emoji success.")
                }, {
                    KLog.e(TAG, it)
                })
            } else {
                //增加
                chatRoomUseCase.sendEmoji(chatMessage.id.orEmpty(), clickEmoji).fold({
                    KLog.i(TAG, "sendEmoji success.")
                }, {
                    KLog.e(TAG, it)
                })
            }
        }
    }

    /**
     * 互動彈窗
     * @param messageInteract 互動 model
     */
    fun onInteractClick(messageInteract: MessageInteract) {
        KLog.i(TAG, "onInteractClick:$messageInteract")
        when (messageInteract) {
            is MessageInteract.Announcement -> announceMessage(messageInteract.message)
            is MessageInteract.Copy -> {
                uiState = uiState.copy(
                    copyMessage = messageInteract.message
                )
            }
//            is MessageInteract.Delete -> deleteMessage(messageInteract.message)
            is MessageInteract.HideUser -> {
                uiState = uiState.copy(
                    hideUserMessage = messageInteract.message
                )
            }
            is MessageInteract.Recycle -> {
                recycleMessage(messageInteract.message)
            }
            is MessageInteract.Reply -> replyMessage(messageInteract.message)
            is MessageInteract.Report -> {
                uiState = uiState.copy(
                    reportMessage = messageInteract.message
                )
            }
            is MessageInteract.EmojiClick -> onEmojiClick(
                messageInteract.message,
                messageInteract.emojiResId
            )
            else -> {}
        }
    }

    /**
     * 收回 訊息
     */
    private fun recycleMessage(message: ChatMessage) {
        KLog.i(TAG, "recycleMessage:$message")
        viewModelScope.launch {
            chatRoomUseCase.recycleMessage(
                messageId = message.id.orEmpty()
            ).fold({
            }, {
                KLog.e(TAG, it)
                if (it is EmptyBodyException) {
                    uiState = uiState.copy(
                        message = uiState.message.map { chatMessageWrapper ->
                            if (chatMessageWrapper.message.id == message.id) {
                                chatMessageWrapper.copy(
                                    message = chatMessageWrapper.message.copy(
                                        isDeleted = true
                                    )
                                )
                            } else {
                                chatMessageWrapper
                            }
                        }
                    )
                } else {
                    it.printStackTrace()
                }
            })
        }
    }

    /**
     * 點擊 回覆訊息
     * @param message
     */
    private fun replyMessage(message: ChatMessage) {
        KLog.i(TAG, "replyMessage click:$message")
        uiState = uiState.copy(
            replyMessage = message
        )
    }

    /**
     * 取消 回覆訊息
     * @param reply
     */
    fun removeReply(reply: ChatMessage) {
        KLog.i(TAG, "removeReply:$reply")
        uiState = uiState.copy(
            replyMessage = null
        )
    }

    /**
     * 訊息 設定 公告
     * @param messageModel 訊息
     */
    private fun announceMessage(messageModel: ChatMessage) {
        KLog.i(TAG, "announceMessage:$messageModel")
        val noEmojiMessage = messageModel.copy(
            emojiCount = null
        )
        uiState = uiState.copy(
            routeAnnounceMessage = noEmojiMessage
        )
    }

    /**
     * 設置公告 跳轉完畢
     */
    fun announceRouteDone() {
        uiState = uiState.copy(
            routeAnnounceMessage = null
        )
    }

    /**
     * 執行完複製
     */
    fun copyDone() {
        uiState = uiState.copy(
            copyMessage = null
        )
    }

    /**
     * 關閉 隱藏用戶彈窗
     */
    fun onHideUserDialogDismiss() {
        uiState = uiState.copy(
            hideUserMessage = null
        )
    }


    /**
     * 檢舉 用戶
     */
    fun onReportUser(reason: ReportReason, channelId: String, contentId: String) {
        KLog.i(TAG, "onReportUser:$reason")
        viewModelScope.launch {
            chatRoomUseCase.reportContent(
                channelId = channelId,
                contentId = contentId,
                reason = reason
            ).fold({
                KLog.i(TAG, "onReportUser success:$it")
                uiState = uiState.copy(
                    reportMessage = null,
                    snackBarMessage = CustomMessage(
                        textString = "檢舉成立！",
                        textColor = Color.White,
                        iconRes = R.drawable.report,
                        iconColor = White_767A7F,
                        backgroundColor = White_494D54
                    )
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 關閉 檢舉用戶 彈窗
     */
    fun onReportUserDialogDismiss() {
        uiState = uiState.copy(
            reportMessage = null
        )
    }

    /**
     * 隱藏 SnackBar
     */
    fun snackBarDismiss() {
        uiState = uiState.copy(
            snackBarMessage = null
        )
    }
}