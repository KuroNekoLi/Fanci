package com.cmoney.fanci.ui.screens.group.setting.channel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.model.usecase.ChannelUseCase
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.Group
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class UiState(
    val isLoading: Boolean = false,
    val channel: Channel? = null,
    val group: Group? = null
)

class ChannelSettingViewModel(
    private val channelUseCase: ChannelUseCase
) : ViewModel() {
    private val TAG = ChannelSettingViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    /**
     * 新增 頻道
     * @param categoryId 分類id, 在此分類下建立
     * @param name 頻道名稱
     */
    fun addChannel(categoryId: String, name: String) {
        KLog.i(TAG, "addChannel: $categoryId, $name")
        uiState = uiState.copy(
            isLoading = true
        )
        viewModelScope.launch {
            channelUseCase.addChannel(
                categoryId = categoryId,
                name = name
            ).fold({
                uiState = uiState.copy(
                    isLoading = true,
                    channel = it
                )
            }, {
                KLog.e(TAG, it)
                uiState = uiState.copy(
                    isLoading = false
                )
            })
        }
    }

    /**
     * 將新的 channel append 至 原本 group 顯示
     */
    fun addChannelToGroup(channel: Channel, group: Group) {
        val channelCategory = channel.category
        channelCategory?.let { channelCategory ->
            val newCategory = group.categories?.map { category ->
                if (category.id == channelCategory.id) {
                    val newChannel = category.channels.orEmpty().toMutableList()
                    newChannel.add(channel)
                    category.copy(
                        channels = newChannel
                    )
                } else {
                    category
                }
            }

            uiState = uiState.copy(
                group = group.copy(
                    categories = newCategory
                )
            )
        }
    }
}