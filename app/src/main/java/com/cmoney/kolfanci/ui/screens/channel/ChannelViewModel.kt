package com.cmoney.kolfanci.ui.screens.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.ChannelTabsStatus
import com.cmoney.kolfanci.model.usecase.ChannelUseCase
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChannelViewModel(private val channelUseCase: ChannelUseCase) : ViewModel() {
    private val TAG = ChannelViewModel::class.java.simpleName

    private val _channelTabStatus: MutableStateFlow<ChannelTabsStatus> = MutableStateFlow(
        ChannelTabsStatus(
            chatRoom = false,
            bulletinboard = false
        )
    )
    val channelTabStatus = _channelTabStatus.asStateFlow()

    fun fetchChannelTabStatus(channelId: String) {
        KLog.i(TAG, "fetchChannelTabStatus:$channelId")
        viewModelScope.launch {
            channelUseCase.fetchChannelTabStatus(channelId).fold({
                _channelTabStatus.value = it
            }, {
                KLog.e(TAG, it)
            })
        }
    }

}