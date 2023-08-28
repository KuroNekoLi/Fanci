package com.cmoney.kolfanci.ui.screens.chat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.User
import com.cmoney.kolfanci.extension.EmptyBodyException
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.model.usecase.PermissionUseCase
import com.cmoney.kolfanci.model.usecase.RelationUseCase
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 聊天室設定相關, 公告, 用戶權限, 封鎖名單
 */
class ChatRoomViewModel(
    val context: Application,
    private val chatRoomUseCase: ChatRoomUseCase,
    private val relationUseCase: RelationUseCase,
    private val permissionUseCase: PermissionUseCase
) : AndroidViewModel(context) {

    private val TAG = ChatRoomViewModel::class.java.simpleName

    //封鎖我的用戶
    private val _blockerList = MutableStateFlow<List<User>>(emptyList())
    val blockerList = _blockerList.asStateFlow()

    //我封鎖的用戶
    private val _blockingList = MutableStateFlow<List<User>>(emptyList())
    val blockingList = _blockingList.asStateFlow()

    //是否更新完權限
    private val _updatePermissionDone = MutableStateFlow<Channel?>(null)
    val updatePermissionDone = _updatePermissionDone.asStateFlow()

    //公告訊息,顯示用
    private val _announceMessage = MutableStateFlow<ChatMessage?>(null)
    val announceMessage = _announceMessage.asStateFlow()

    //錯誤訊息, toast
    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    //取得封鎖清單
    init {
        viewModelScope.launch {
            try {
                val blockList = relationUseCase.getMyRelation()
                _blockingList.value = blockList.first
                _blockerList.value = blockList.second
            } catch (e: Exception) {
                e.printStackTrace()
                KLog.e(TAG, e)
            }
        }
    }

    /**
     * 抓取 公告 訊息
     */
    fun fetchAnnounceMessage(channelId: String) {
        KLog.i(TAG, "fetchAnnounceMessage:$channelId")
        viewModelScope.launch {
            chatRoomUseCase.getAnnounceMessage(channelId).fold({
                if (it.isAnnounced == true) {
                    _announceMessage.value = it.message
                }
            }, {
                KLog.e(TAG, it)
            })
        }
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
                val newList = _blockingList.value.toMutableList()
                newList.add(it)
                _blockingList.value = newList.distinct()
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
                    val newList = _blockingList.value.filter { user ->
                        user.id != chatMessage.author?.id.orEmpty()
                    }

                    _blockingList.value = newList.distinct()

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
            }, {
                if (it is EmptyBodyException) {
                    _announceMessage.value = chatMessage
                } else {
                    _errorMessage.emit(it.toString())
                }
            })
        }
    }

    /**
     *  抓取頻道權限
     */
    fun fetchChannelPermission(channel: Channel) {
        KLog.i(TAG, "fetchChannelPermission:" + channel.id)
        viewModelScope.launch {
            permissionUseCase.updateChannelPermissionAndBuff(channelId = channel.id.orEmpty())
                .fold({
                    _updatePermissionDone.value = channel
                }, {
                    KLog.e(TAG, it)
                })
        }
    }

    fun afterUpdatePermissionDone() {
        _updatePermissionDone.value = null
    }
}