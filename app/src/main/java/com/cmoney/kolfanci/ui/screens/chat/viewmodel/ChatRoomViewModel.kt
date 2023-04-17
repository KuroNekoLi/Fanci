package com.cmoney.kolfanci.ui.screens.chat.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.User
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.EmptyBodyException
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.model.usecase.PermissionUseCase
import com.cmoney.kolfanci.model.usecase.RelationUseCase
import com.cmoney.kolfanci.ui.screens.shared.snackbar.CustomMessage
import com.cmoney.kolfanci.ui.theme.White_494D54
import com.cmoney.kolfanci.ui.theme.White_767A7F
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class ChatRoomUiState(
    val snackBarMessage: CustomMessage? = null,
    val hideUserMessage: ChatMessage? = null,
    val deleteMessage: ChatMessage? = null,
    val emojiMessage: Pair<ChatMessage, Int>? = null,
    val announceMessage: ChatMessage? = null,          //公告訊息,顯示用
    val errorMessage: String? = null,
    var blockingList: List<User> = emptyList(), //封鎖用戶
    var blockerList: List<User> = emptyList(),  //封鎖我的用戶
    val startPolling: Boolean = false,
    val enterChannel: Channel? = null
) {
    data class ImageAttachState(
        val uri: Uri,
        val isUploadComplete: Boolean = false,
        val serverUrl: String = ""
    )
}

class ChatRoomViewModel(
    val context: Application,
    private val chatRoomUseCase: ChatRoomUseCase,
    private val relationUseCase: RelationUseCase,
    private val permissionUseCase: PermissionUseCase
) : AndroidViewModel(context) {

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
                uiState = if (it is EmptyBodyException) {
                    uiState.copy(
                        announceMessage = chatMessage
                    )
                } else {
                    uiState.copy(
                        errorMessage = it.toString()
                    )
                }

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
    fun fetchChannelPermission(channel: Channel) {
        KLog.i(TAG, "fetchChannelPermission:" + channel.id)
        viewModelScope.launch {
            permissionUseCase.getPermissionByChannel(channelId = channel.id.orEmpty()).fold({
                Constant.MyChannelPermission = it
                uiState = uiState.copy(
                    enterChannel = channel
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    fun resetChannel() {
        uiState = uiState.copy(
            enterChannel = null
        )
    }
}