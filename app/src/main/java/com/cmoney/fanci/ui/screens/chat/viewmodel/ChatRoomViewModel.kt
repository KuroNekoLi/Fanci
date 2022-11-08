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
import com.cmoney.fanci.R
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import com.cmoney.fanci.ui.screens.shared.bottomSheet.MessageInteract
import com.cmoney.fanci.ui.screens.shared.snackbar.CustomMessage
import com.cmoney.fanci.ui.theme.White_494D54
import com.cmoney.fanci.ui.theme.White_767A7F
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class ChatRoomUiState(
    val imageAttach: List<Uri> = emptyList(),
    val replyMessage: ChatMessageModel.Reply? = null,
    val message: List<ChatMessageModel> = emptyList(),
    val snackBarMessage: CustomMessage? = null,
    val announceMessage: ChatMessageModel? = null,
    val hideUserMessage: ChatMessageModel? = null,
    val deleteMessage: ChatMessageModel? = null,
    val reportUser: ChatMessageModel? = null
)

class ChatRoomViewModel(val context: Context, val chatRoomUseCase: ChatRoomUseCase) : ViewModel() {
    private val TAG = ChatRoomViewModel::class.java.simpleName

    var uiState by mutableStateOf(ChatRoomUiState())
        private set

    init {
        viewModelScope.launch {
//            val chatRoomUseCase = ChatRoomUseCase()
            uiState = uiState.copy(message = chatRoomUseCase.createMockMessage().reversed())
        }
    }

    /**
     * 點擊 回覆
     */
    private fun replyMessage(message: ChatMessageModel) {
        KLog.i(TAG, "replyMessage click:$message")

        uiState = uiState.copy(
            replyMessage = ChatMessageModel.Reply(
                replyUser = message.poster,
                text = message.message.text
            )
        )
    }

    /**
     * 輸入文字
     */
    fun messageInput(text: String) {
        if (text.isNotEmpty()) {
            val orgList = uiState.message.toMutableList()

            //如果是回覆 訊息
            uiState.replyMessage?.apply {
                orgList.add(
                    0,
                    ChatMessageModel(
                        poster = ChatMessageModel.User(
                            avatar = "https://picsum.photos/110/110",
                            nickname = "TIGER"
                        ),
                        publishTime = System.currentTimeMillis(),
                        message = ChatMessageModel.Message(
                            reply = ChatMessageModel.Reply(
                                replyUser = this.replyUser,
                                text = this.text
                            ),
                            text = text
                        )
                    ),
                )
            } ?: kotlin.run {
                orgList.add(
                    0,
                    ChatMessageModel(
                        poster = ChatMessageModel.User(
                            avatar = "https://picsum.photos/110/110",
                            nickname = "TIGER"
                        ),
                        publishTime = System.currentTimeMillis(),
                        message = ChatMessageModel.Message(
                            reply = null,
                            text = text
                        )
                    ),
                )
            }

            uiState = uiState.copy(message = orgList, replyMessage = null)
        }
    }

    /**
     * 回覆 附加 圖片
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
    fun removeReply(reply: ChatMessageModel.Reply) {
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
    private fun reportUser(message: ChatMessageModel) {
        KLog.i(TAG, "reportUser:$message")
        uiState = uiState.copy(
            reportUser = message
        )
    }

    /**
     * 刪除 訊息
     */
    private fun deleteMessage(message: ChatMessageModel) {
        KLog.i(TAG, "deleteMessage:$message")
        uiState = uiState.copy(
            deleteMessage = message
        )
    }

    /**
     * 隱藏 用戶
     */
    private fun hideUserMessage(message: ChatMessageModel) {
        KLog.i(TAG, "hideUserMessage:$message")
        uiState = uiState.copy(
            hideUserMessage = message
        )
    }

    /**
     * 訊息 設定 公告
     */
    private fun announceMessage(messageModel: ChatMessageModel) {
        KLog.i(TAG, "announceMessage:$messageModel")
        val noEmojiMessage = messageModel.copy(
            message = messageModel.message.copy(
                emoji = emptyList()
            )
        )
        uiState = uiState.copy(
            announceMessage = noEmojiMessage
        )
    }

    /**
     * 複製訊息
     */
    private fun copyMessage(message: ChatMessageModel) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("", message.message.text)
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
    private fun recycleMessage(message: ChatMessageModel) {
        KLog.i(TAG, "recycleMessage:$message")
        val orgMessage = uiState.message.toMutableList()
        uiState = uiState.copy(
            snackBarMessage = CustomMessage(
                textString = "訊息收回成功！",
                textColor = Color.White,
                iconRes = R.drawable.recycle,
                iconColor = White_767A7F,
                backgroundColor = White_494D54
            ),
            message = orgMessage.map { chatModel ->
                if (chatModel == message) {
                    chatModel.copy(
                        message = chatModel.message.copy(
                            isRecycle = true
                        )
                    )
                } else {
                    chatModel
                }
            }
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
    fun onDeleteClick(chatMessageModel: ChatMessageModel) {
        KLog.i(TAG, "onDeleteClick:$chatMessageModel")
        uiState = uiState.copy(
            message = uiState.message.filter {
                it != chatMessageModel
            },
            deleteMessage = null,
            snackBarMessage = CustomMessage(
                textString = "成功刪除訊息！",
                textColor = Color.White,
                iconRes = R.drawable.delete,
                iconColor = White_767A7F,
                backgroundColor = White_494D54
            )
        )
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
    fun onHideUserConfirm(user: ChatMessageModel.User) {
        val message = uiState.message.toMutableList()
        val newMessage = message.map { chatMessageModel ->
            if (chatMessageModel.poster == user) {
                val fixMessage = chatMessageModel.message.copy(
                    isHideUser = true
                )

                chatMessageModel.copy(
                    message = fixMessage,
                )
            } else {
                chatMessageModel
            }
        }

        uiState = uiState.copy(
            message = newMessage,
            hideUserMessage = null
        )
    }

    /**
     * 解除 隱藏 用戶
     */
    fun onMsgDismissHide(userModel: ChatMessageModel) {
        val message = uiState.message.toMutableList()
        val newMessage = message.map { chatMessageModel ->
            if (chatMessageModel.poster == userModel.poster) {
                val fixMessage = chatMessageModel.message.copy(
                    isHideUser = false
                )

                chatMessageModel.copy(
                    message = fixMessage,
                )
            } else {
                chatMessageModel
            }
        }

        uiState = uiState.copy(
            message = newMessage,
            hideUserMessage = null
        )
    }

    /**
     * 點擊 Emoji
     */
    fun onEmojiClick(model: ChatMessageModel, resourceId: Int) {
        KLog.i(TAG, "onEmojiClick.")

        val orgEmojiList = model.message.emoji.toMutableList()

        model.message.emoji.find {
            it.resource == resourceId
        }.apply {
            this?.apply {
                val countEmoji = copy(
                    count = count.plus(1)
                )
                orgEmojiList[orgEmojiList.indexOf(this)] = countEmoji
            } ?: kotlin.run {
                orgEmojiList.add(
                    ChatMessageModel.Emoji(
                        resource = resourceId,
                        count = 1
                    )
                )
            }
        }

        val message = uiState.message.toMutableList()
        val newMessage = message.map { chatMessageModel ->
            if (chatMessageModel.message == model.message) {
                val fixMessage = chatMessageModel.message.copy(
                    emoji = orgEmojiList
                )
                chatMessageModel.copy(
                    message = fixMessage,
                )
            } else {
                chatMessageModel
            }
        }

        uiState = uiState.copy(
            message = newMessage,
            hideUserMessage = null
        )
    }
}