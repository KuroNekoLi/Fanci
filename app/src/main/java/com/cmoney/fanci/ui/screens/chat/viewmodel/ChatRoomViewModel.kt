package com.cmoney.fanci.ui.screens.chat.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
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
import com.cmoney.fanci.model.ChatMessageWrapper
import com.cmoney.fanci.model.usecase.ChatRoomPollUseCase
import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import com.cmoney.fanci.ui.screens.shared.bottomSheet.MessageInteract
import com.cmoney.fanci.ui.screens.shared.snackbar.CustomMessage
import com.cmoney.fanci.ui.theme.White_494D54
import com.cmoney.fanci.ui.theme.White_767A7F
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.MediaIChatContent
import com.cmoney.imagelibrary.UploadImage
import com.cmoney.xlogin.XLoginHelper
import com.socks.library.KLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatRoomUiState(
    val imageAttach: List<Uri> = emptyList(),
    val replyMessage: ChatMessage? = null,
    val message: List<ChatMessageWrapper> = emptyList(),
    val snackBarMessage: CustomMessage? = null,
    val announceMessage: ChatMessage? = null,
    val hideUserMessage: ChatMessage? = null,
    val deleteMessage: ChatMessage? = null,
    val reportUser: ChatMessage? = null,
    val isSendComplete: Boolean = false
) {
    data class ImageAttachState(
        val uri: Uri,
        val isUploadComplete: Boolean = false,
        val serverUrl: String = ""
    )
}

class ChatRoomViewModel(
    val context: Context,
    val chatRoomUseCase: ChatRoomUseCase,
    val chatRoomPollUseCase: ChatRoomPollUseCase
) : ViewModel() {
    private val TAG = ChatRoomViewModel::class.java.simpleName

    var uiState by mutableStateOf(ChatRoomUiState())
        private set

    private val preSendChatId = "Preview"       //發送前預覽的訊息id, 用來跟其他訊息區分

    init {
        viewModelScope.launch {
//            val chatRoomUseCase = ChatRoomUseCase()
//            uiState = uiState.copy(message = chatRoomUseCase.createMockMessage().reversed())
        }
    }

    /**
     * 點擊 回覆
     */
    private fun replyMessage(message: ChatMessage) {
        KLog.i(TAG, "replyMessage click:$message")
        uiState = uiState.copy(
            replyMessage = message
        )
    }

    /**
     * 圖片上傳
     */
    interface ImageUploadCallback {
        fun complete(images: List<String>)

        fun onFailure(e: Throwable)
    }

    /**
     * 對外 發送訊息 接口
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

//        if (text.isNotEmpty()) {
//            val orgList = uiState.message.toMutableList()
//
////            //如果是回覆 訊息
////            uiState.replyMessage?.apply {
////                orgList.add(
////                    0,
////                    ChatMessage(
////                        author = GroupMember(
////                            thumbNail = "https://picsum.photos/110/110",
////                            name = "TIGER"
////                        ),
////                        createUnixTime = System.currentTimeMillis(),
////                        content = IChatContent(
////                            text = text
////                        ),
////                        replyMessage = this
////                    )
////                )
////            } ?: kotlin.run {
////                orgList.add(
////                    0,
////                    ChatMessage(
////                        author = GroupMember(
////                            thumbNail = "https://picsum.photos/110/110",
////                            name = "TIGER"
////                        ),
////                        createUnixTime = System.currentTimeMillis(),
////                        content = IChatContent(
////                            text = text
////                        )
////                    )
////                )
////            }
//
//            uiState = uiState.copy(message = orgList, replyMessage = null)
//        }
    }

    /**
     * 上傳所有 附加圖片
     */
    private suspend fun uploadImages(uriLis: List<Uri>, imageUploadCallback: ImageUploadCallback) {
        KLog.i(TAG, "uploadImages:" + uriLis.size)

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
                //將 上傳成功的圖片 message overwrite
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
     */
    private fun send(channelId: String, text: String, images: List<String> = emptyList()) {
        KLog.i(TAG, "send:" + text + " , media:" + images.size)
        viewModelScope.launch {
            chatRoomUseCase.sendMessage(
                chatRoomChannelId = channelId,
                text = text,
                images = images
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
                    isSendComplete = true
                )

                //恢復聊天室內訊息,不會自動捲到最下面
                delay(800)
                uiState = uiState.copy(isSendComplete = false)

            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 發送前 直接先貼上畫面 Preview
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
                            createUnixTime = System.currentTimeMillis() / 1000
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
     * 附加 圖片
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
     * 取消 回覆
     */
    fun removeReply(reply: ChatMessage) {
        KLog.i(TAG, "removeReply:$reply")
        uiState = uiState.copy(
            replyMessage = null
        )
    }

    /**
     * 點擊 互動彈窗
     */
    fun onInteractClick(messageInteract: MessageInteract) {
        KLog.i(TAG, "onInteractClick:$messageInteract")
        when (messageInteract) {
            is MessageInteract.Announcement -> announceMessage(messageInteract.message)
            is MessageInteract.Copy -> {
                copyMessage(messageInteract.message)
            }
            is MessageInteract.Delete -> deleteMessage(messageInteract.message)
            is MessageInteract.HideUser -> hideUserMessage(messageInteract.message)
            is MessageInteract.Recycle -> {
                recycleMessage(messageInteract.message)
            }
            is MessageInteract.Reply -> replyMessage(messageInteract.message)
            is MessageInteract.Report -> reportUser(messageInteract.message)
            is MessageInteract.EmojiClick -> onEmojiClick(
                messageInteract.message,
                messageInteract.emojiResId
            )
        }
    }

    /**
     * 檢舉 用戶
     */
    private fun reportUser(message: ChatMessage) {
        KLog.i(TAG, "reportUser:$message")
        uiState = uiState.copy(
            reportUser = message
        )
    }

    /**
     * 刪除 訊息
     */
    private fun deleteMessage(message: ChatMessage) {
        KLog.i(TAG, "deleteMessage:$message")
        uiState = uiState.copy(
            deleteMessage = message
        )
    }

    /**
     * 隱藏 用戶
     */
    private fun hideUserMessage(message: ChatMessage) {
        KLog.i(TAG, "hideUserMessage:$message")
        uiState = uiState.copy(
            hideUserMessage = message
        )
    }

    /**
     * 訊息 設定 公告
     */
    private fun announceMessage(messageModel: ChatMessage) {
        KLog.i(TAG, "announceMessage:$messageModel")
        val noEmojiMessage = messageModel.copy(
            emojiCount = null
        )
        uiState = uiState.copy(
            announceMessage = noEmojiMessage
        )
    }

    /**
     * 複製訊息
     */
    private fun copyMessage(message: ChatMessage) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("", message.content?.text)
        clipboard.setPrimaryClip(clip)

        uiState = uiState.copy(
            snackBarMessage = CustomMessage(
                textString = "訊息複製成功！",
                textColor = Color.White,
                iconRes = R.drawable.copy,
                iconColor = White_767A7F,
                backgroundColor = White_494D54
            )
        )
    }

    /**
     *  回收 訊息 並 show 回收成功 snackBar
     */
    private fun recycleMessage(message: ChatMessage) {
        KLog.i(TAG, "recycleMessage:$message")
        val orgMessage = uiState.message.toMutableList()
//        uiState = uiState.copy(
//            snackBarMessage = CustomMessage(
//                textString = "訊息收回成功！",
//                textColor = Color.White,
//                iconRes = R.drawable.recycle,
//                iconColor = White_767A7F,
//                backgroundColor = White_494D54
//            ),
//            message = orgMessage.map { chatModel ->
//                if (chatModel.message == message) {
//                    chatModel.copy(
//                        isDeleted = true
//                    )
//                } else {
//                    chatModel
//                }
//            }
//        )
    }

    /**
     * 隱藏 SnackBar
     */
    fun snackBarDismiss() {
        uiState = uiState.copy(
            snackBarMessage = null
        )
    }

    fun routeDone() {
        uiState = uiState.copy(
            announceMessage = null
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
     * 關閉 刪除訊息 彈窗
     */
    fun onDeleteMessageDialogDismiss() {
        uiState = uiState.copy(
            deleteMessage = null
        )
    }

    /**
     * 確定 刪除 訊息
     */
    fun onDeleteClick(chatMessageModel: ChatMessage) {
        KLog.i(TAG, "onDeleteClick:$chatMessageModel")
//        uiState = uiState.copy(
//            message = uiState.message.filter {
//                it != chatMessageModel
//            },
//            deleteMessage = null,
//            snackBarMessage = CustomMessage(
//                textString = "成功刪除訊息！",
//                textColor = Color.White,
//                iconRes = R.drawable.delete,
//                iconColor = White_767A7F,
//                backgroundColor = White_494D54
//            )
//        )
    }

    /**
     * 關閉 檢舉用戶 彈窗
     */
    fun onReportUserDialogDismiss() {
        uiState = uiState.copy(
            reportUser = null
        )
    }

    // TODO:
    /**
     * 檢舉 用戶
     */
    fun onReportUser(reason: String) {
        KLog.i(TAG, "onReportUser:$reason")
        uiState = uiState.copy(
            reportUser = null,
            snackBarMessage = CustomMessage(
                textString = "檢舉成立！",
                textColor = Color.White,
                iconRes = R.drawable.report,
                iconColor = White_767A7F,
                backgroundColor = White_494D54
            )
        )
    }

    // TODO:
    /**
     * 確定 隱藏 用戶
     */
    fun onHideUserConfirm(user: GroupMember) {
//        val message = uiState.message.toMutableList()
//        val newMessage = message.map { chatMessageModel ->
//            if (chatMessageModel.poster == user) {
//                val fixMessage = chatMessageModel.message.copy(
//                    isHideUser = true
//                )
//
//                chatMessageModel.copy(
//                    message = fixMessage,
//                )
//            } else {
//                chatMessageModel
//            }
//        }
//
//        uiState = uiState.copy(
//            message = newMessage,
//            hideUserMessage = null
//        )
    }

    /**
     * 解除 隱藏 用戶
     */
    fun onMsgDismissHide(userModel: ChatMessage) {
        // TODO:
//        val message = uiState.message.toMutableList()
//        val newMessage = message.map { chatMessageModel ->
//            if (chatMessageModel.poster == userModel.poster) {
//                val fixMessage = chatMessageModel.message.copy(
//                    isHideUser = false
//                )
//
//                chatMessageModel.copy(
//                    message = fixMessage,
//                )
//            } else {
//                chatMessageModel
//            }
//        }
//
//        uiState = uiState.copy(
//            message = newMessage,
//            hideUserMessage = null
//        )
    }

    /**
     * 點擊 Emoji
     */
    fun onEmojiClick(model: ChatMessage, resourceId: Int) {
        KLog.i(TAG, "onEmojiClick.")
        // TODO:
//        val orgEmojiList = model.message.emoji.toMutableList()
//
//        model.message.emoji.find {
//            it.resource == resourceId
//        }.apply {
//            this?.apply {
//                val countEmoji = copy(
//                    count = count.plus(1)
//                )
//                orgEmojiList[orgEmojiList.indexOf(this)] = countEmoji
//            } ?: kotlin.run {
//                orgEmojiList.add(
//                    ChatMessageModel.Emoji(
//                        resource = resourceId,
//                        count = 1
//                    )
//                )
//            }
//        }
//
//        val message = uiState.message.toMutableList()
//        val newMessage = message.map { chatMessageModel ->
//            if (chatMessageModel.message == model.message) {
//                val fixMessage = chatMessageModel.message.copy(
//                    emoji = orgEmojiList
//                )
//                chatMessageModel.copy(
//                    message = fixMessage,
//                )
//            } else {
//                chatMessageModel
//            }
//        }
//
//        uiState = uiState.copy(
//            message = newMessage,
//            hideUserMessage = null
//        )
    }
}