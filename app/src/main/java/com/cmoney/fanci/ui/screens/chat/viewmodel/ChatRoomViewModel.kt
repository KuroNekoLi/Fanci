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
import com.cmoney.fanci.extension.EmptyBodyException
import com.cmoney.fanci.model.Constant
import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import com.cmoney.fanci.model.usecase.PermissionUseCase
import com.cmoney.fanci.model.usecase.RelationUseCase
import com.cmoney.fanci.ui.screens.shared.snackbar.CustomMessage
import com.cmoney.fanci.ui.theme.White_494D54
import com.cmoney.fanci.ui.theme.White_767A7F
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.User
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class ChatRoomUiState(
//    val replyMessage: ChatMessage? = null,
    val snackBarMessage: CustomMessage? = null,
    val hideUserMessage: ChatMessage? = null,
    val deleteMessage: ChatMessage? = null,
//    val reportUser: ChatMessage? = null,
    val emojiMessage: Pair<ChatMessage, Int>? = null,
    val announceMessage: ChatMessage? = null,          //公告訊息,顯示用
    val errorMessage: String? = null,
    var blockingList: List<User> = emptyList(), //封鎖用戶
    var blockerList: List<User> = emptyList()   //封鎖我的用戶
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
    val relationUseCase: RelationUseCase,
    val permissionUseCase: PermissionUseCase
) : ViewModel() {

    private val TAG = ChatRoomViewModel::class.java.simpleName

    var uiState by mutableStateOf(ChatRoomUiState())
        private set

    private val preSendChatId = "Preview"       //發送前預覽的訊息id, 用來跟其他訊息區分

    //取得封鎖清單
    init {
        viewModelScope.launch {
            try {
                val blockList = relationUseCase.getMyRelation()
                uiState = uiState.copy(
                    blockingList = blockList.first,
                    blockerList = blockList.second
                )
            } catch (e: Exception) {
                e.printStackTrace()
                KLog.e(TAG, e)
            }
        }
    }

//    init {
//        viewModelScope.launch {
////            val chatRoomUseCase = ChatRoomUseCase()
////            uiState = uiState.copy(message = chatRoomUseCase.createMockMessage().reversed())
//        }
//    }

    /**
     * 抓取 公告 訊息
     */
    fun fetchAnnounceMessage(channelId: String) {
        KLog.i(TAG, "fetchAnnounceMessage:$channelId")
        viewModelScope.launch {
            chatRoomUseCase.getAnnounceMessage(channelId).fold({
                if (it.isAnnounced == true) {
                    uiState = uiState.copy(
                        announceMessage = it.message
                    )
                }
            }, {
                KLog.e(TAG, it)
            })
        }
    }


//    /**
//     * 檢舉 用戶
//     */
//    private fun reportUser(message: ChatMessage) {
//        KLog.i(TAG, "reportUser:$message")
//        uiState = uiState.copy(
//            reportUser = message
//        )
//    }

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
     * 複製訊息
     */
    fun copyMessage(message: ChatMessage) {
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
//        val orgMessage = uiState.message.toMutableList()
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

//    /**
//     * 關閉 檢舉用戶 彈窗
//     */
//    fun onReportUserDialogDismiss() {
//        uiState = uiState.copy(
//            reportUser = null
//        )
//    }

    // TODO:
//    /**
//     * 檢舉 用戶
//     */
//    fun onReportUser(reason: String) {
//        KLog.i(TAG, "onReportUser:$reason")
//        uiState = uiState.copy(
//            reportUser = null,
//            snackBarMessage = CustomMessage(
//                textString = "檢舉成立！",
//                textColor = Color.White,
//                iconRes = R.drawable.report,
//                iconColor = White_767A7F,
//                backgroundColor = White_494D54
//            )
//        )
//    }

    /**
     * 確定 封鎖 用戶
     */
    fun onBlockingUserConfirm(user: GroupMember) {
        KLog.i(TAG, "onHideUserConfirm:$user")
        viewModelScope.launch {
            relationUseCase.blocking(
                userId = user.id.orEmpty()
            ).fold({
                val newList = uiState.blockingList.toMutableList()
                newList.add(it)
                uiState = uiState.copy(
                    blockingList = newList.distinct(),
                    hideUserMessage = null
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 解除 封鎖 用戶
     */
    fun onMsgDismissHide(chatMessage: ChatMessage) {
        KLog.i(TAG, "onMsgDismissHide")
        viewModelScope.launch {
            relationUseCase.disBlocking(
                userId = chatMessage.author?.id.orEmpty()
            ).fold({
            }, {
                if (it is EmptyBodyException) {
                    KLog.i(TAG, "onMsgDismissHide success")
                    val newList = uiState.blockingList.filter { user ->
                        user.id != chatMessage.author?.id.orEmpty()
                    }

                    uiState = uiState.copy(
                        blockingList = newList.distinct(),
                        hideUserMessage = null
                    )

                } else {
                    KLog.e(TAG, it)
                }
            })
        }
    }

    /**
     * 將訊息設定成公告
     * @param channelId 頻道 ID
     * @param chatMessage 訊息
     */
    fun announceMessageToServer(channelId: String, chatMessage: ChatMessage) {
        KLog.i(TAG, "announceMessageToServer:$chatMessage")
        viewModelScope.launch {
            chatRoomUseCase.setAnnounceMessage(channelId, chatMessage).fold({
                uiState = uiState.copy(
                    announceMessage = chatMessage
                )
            }, {
                uiState = uiState.copy(
                    errorMessage = it.toString()
                )
            })
        }
    }

    /**
     * Finish error message.
     */
    fun errorMessageDone() {
        uiState = uiState.copy(
            errorMessage = null
        )
    }

    /**
     *  抓取頻道權限
     */
    fun fetchChannelPermission(channelId: String) {
        KLog.i(TAG, "fetchChannelPermission:$channelId")
        viewModelScope.launch {
            permissionUseCase.getPermissionByChannel(channelId = channelId).fold({
                Constant.MyChannelPermission = it
            }, {
                KLog.e(TAG, it)
            })
        }
    }
}