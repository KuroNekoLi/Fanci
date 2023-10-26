package com.cmoney.kolfanci.ui.screens.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChannelTabType
import com.cmoney.kolfanci.model.usecase.ChannelUseCase
import com.cmoney.kolfanci.model.usecase.NotificationUseCase
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChannelViewModel(
    private val channelUseCase: ChannelUseCase,
    private val notificationUseCase: NotificationUseCase
) : ViewModel() {
    private val TAG = ChannelViewModel::class.java.simpleName

    //未讀數量 (1.聊天室 2.文章)
    private val _unreadCount: MutableStateFlow<Pair<Long, Long>?> = MutableStateFlow(null)
    val unreadCount = _unreadCount.asStateFlow()

    /**
     * 抓取未讀取數量 (聊天室/貼文)
     */
    fun fetchAllUnreadCount(channel: Channel) {
        viewModelScope.launch {
            var chatRedDotCount = 0L
            var postRedDotCount = 0L

            channel.tabs?.forEach { tabType ->
                when (tabType.type) {
                    //貼文
                    ChannelTabType.bulletinboard -> {
                        tabType.userContext?.unreadCount?.let { unreadCount ->
                            postRedDotCount = unreadCount
                        }
                    }
                    //聊天
                    ChannelTabType.chatRoom -> {
                        tabType.userContext?.unreadCount?.let { unreadCount ->
                            chatRedDotCount = unreadCount
                        }
                    }

                    else -> {}
                }
            }

            _unreadCount.value = Pair(chatRedDotCount, postRedDotCount)
        }
    }

    /**
     * 聊天未讀 清空
     */
    fun onChatRedDotClick(channelId: String) {
        KLog.i(TAG, "onChatRedDotClick")
        viewModelScope.launch {
            _unreadCount.update {
                it?.copy(first = 0)
            }

            notificationUseCase.clearChatUnReadCount(channelId = channelId)
        }
    }

    /**
     * 貼文未讀 清空
     */
    fun onPostRedDotClick(channelId: String) {
        KLog.i(TAG, "onPostRedDotClick")
        viewModelScope.launch {
            _unreadCount.update {
                it?.copy(second = 0)
            }

            notificationUseCase.clearPostUnReadCount(channelId = channelId)
        }
    }

}