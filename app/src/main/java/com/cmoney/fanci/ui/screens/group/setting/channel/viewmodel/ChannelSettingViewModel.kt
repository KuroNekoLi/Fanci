package com.cmoney.fanci.ui.screens.group.setting.channel.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.extension.EmptyBodyException
import com.cmoney.fanci.model.usecase.ChannelUseCase
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class UiState(
    val isLoading: Boolean = false,
    val group: Group? = null,
    val channelRole: List<FanciRole>? = null
)

class ChannelSettingViewModel(
    private val channelUseCase: ChannelUseCase
) : ViewModel() {
    private val TAG = ChannelSettingViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    /**
     * 取得 頻道角色清單
     */
    fun getChannelRole(channelId: String) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            channelUseCase.getChannelRole(channelId).fold({
                uiState = uiState.copy(isLoading = true, channelRole = it)
            }, {
                KLog.e(TAG, it)
                uiState = uiState.copy(isLoading = false)
            })
        }
    }


    /**
     * 新增 分類
     * @param group 群組 要在此群組下建立
     * @param name 分類名稱
     */
    fun addCategory(group: Group, name: String) {
        KLog.i(TAG, "addCategory: $group, $name")
        uiState = uiState.copy(
            isLoading = true
        )
        viewModelScope.launch {
            channelUseCase.addCategory(groupId = group.id.orEmpty(), name = name).fold({
                val newCategoryList = group.categories.orEmpty().toMutableList()
                newCategoryList.add(it)

                val newGroup = group.copy(
                    categories = newCategoryList
                )

                uiState = uiState.copy(
                    isLoading = false, group = newGroup
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
     * 新增 頻道
     * @param group
     * @param categoryId 分類id, 在此分類下建立
     * @param name 頻道名稱
     */
    fun addChannel(group: Group, categoryId: String, name: String) {
        KLog.i(TAG, "addChannel: $categoryId, $name")
        uiState = uiState.copy(
            isLoading = true
        )
        viewModelScope.launch {
            channelUseCase.addChannel(
                categoryId = categoryId, name = name
            ).fold({
                addChannelToGroup(it, group)
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
    private fun addChannelToGroup(channel: Channel, group: Group) {
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
                ), isLoading = false
            )
        }
    }

    /**
     * 設定 group to ui display
     */
    fun setGroup(group: Group) {
        uiState = uiState.copy(group = group)
    }

    /**
     * 編輯 頻道名稱
     */
    fun editChannel(group: Group, channel: Channel, name: String) {
        KLog.i(TAG, "editChannel")
        viewModelScope.launch {
            channelUseCase.editChannelName(channelId = channel.id.orEmpty(), name = name).fold({}, {
                if (it is EmptyBodyException) {
                    val newCategory = group.categories?.map { category ->
                        val newChannel = category.channels?.map { groupChannel ->
                            if (channel.id == groupChannel.id) {
                                groupChannel.copy(
                                    name = name
                                )
                            } else {
                                groupChannel
                            }
                        }
                        category.copy(
                            channels = newChannel
                        )
                    }

                    uiState = uiState.copy(
                        group = group.copy(
                            categories = newCategory
                        )
                    )
                } else {
                    KLog.e(TAG, it)
                }
            })

        }
    }

    /**
     * 刪除 頻道
     */
    fun deleteChannel(group: Group, channel: Channel) {
        KLog.i(TAG, "deleteChannel:$channel")
        viewModelScope.launch {
            channelUseCase.deleteChannel(channel.id.orEmpty()).fold({
            }, {
                if (it is EmptyBodyException) {
                    val newCategory = group.categories?.map { category ->
                        category.copy(
                            channels = category.channels?.filter { groupChannel ->
                                groupChannel.id != channel.id
                            }
                        )
                    }
                    uiState = uiState.copy(
                        group = group.copy(
                            categories = newCategory
                        )
                    )
                }
            })
        }
    }

    /**
     * 編輯 類別名稱
     */
    fun editCategory(group: Group, category: Category, name: String) {
        KLog.i(TAG, "editCategory")
        viewModelScope.launch {
            channelUseCase.editCategoryName(categoryId = category.id.orEmpty(), name = name).fold({
            }, {
                if (it is EmptyBodyException) {
                    val newCategory = group.categories?.map { groupCategory ->
                        if (groupCategory.id == category.id) {
                            groupCategory.copy(name = name)
                        } else {
                            groupCategory
                        }
                    }

                    uiState = uiState.copy(
                        group = group.copy(
                            categories = newCategory
                        )
                    )
                } else {
                    KLog.e(TAG, it)
                }
            })

        }
    }

    /**
     * 刪除 分類
     */
    fun deleteCategory(group: Group, category: Category) {
        KLog.i(TAG, "deleteCategory")
        viewModelScope.launch {
            channelUseCase.deleteCategory(categoryId = category.id.orEmpty()).fold({
            }, {
                if (it is EmptyBodyException) {
                    val newCategory = group.categories?.filter { groupCategory ->
                        groupCategory.id != category.id
                    }
                    uiState = uiState.copy(
                        group = group.copy(
                            categories = newCategory
                        )
                    )
                } else {
                    KLog.e(TAG, it)
                }
            })
        }
    }
}