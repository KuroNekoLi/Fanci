package com.cmoney.fanci.ui.screens.group.setting.group.channel.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.extension.EmptyBodyException
import com.cmoney.fanci.extension.fromJsonTypeToken
import com.cmoney.fanci.model.usecase.ChannelUseCase
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanci.model.usecase.OrderUseCase
import com.cmoney.fanci.ui.screens.group.setting.group.channel.sort.MoveItem
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.google.gson.Gson
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class UiState(
    val isLoading: Boolean = false,
    val group: Group? = null,
    val channelRole: List<FanciRole>? = null,   //目前頻道顯示角色List
    val groupRoleList: List<AddChannelRoleModel> = emptyList(),
    val confirmRoleList: String = "",
    val tabSelected: Int = 0,    //record tab position
    val isOpenSortDialog: Boolean = false,
    val isSoredToServerComplete: Boolean = false    //完成將排序結果給 server
)

data class AddChannelRoleModel(val role: FanciRole, val isChecked: Boolean = false)

class ChannelSettingViewModel(
    private val channelUseCase: ChannelUseCase,
    private val groupUseCase: GroupUseCase,
    private val orderUseCase: OrderUseCase
) : ViewModel() {
    private val TAG = ChannelSettingViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    var orgChannelRoleList = emptyList<FanciRole>() //原本頻道裡的角色清單

    /**
     * 取得 社團下角色清單
     * @param groupId 社團 id
     * @param exclusiveRole 排除的 Role
     */
    fun getGroupRoleList(groupId: String, exclusiveRole: Array<FanciRole>) {
        KLog.i(TAG, "getGroupRole:$groupId")
        viewModelScope.launch {
            groupUseCase.fetchGroupRole(
                groupId = groupId
            ).fold({
                uiState = uiState.copy(
                    groupRoleList = it.filter {
                        !exclusiveRole.contains(it)
                    }.map {
                        AddChannelRoleModel(role = it)
                    }
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }


    /**
     * 取得 頻道角色清單
     */
    fun getChannelRole(channelId: String) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            channelUseCase.getChannelRole(channelId).fold({
                orgChannelRoleList = it
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
     * 編輯 頻道
     * @param group 社團 model
     * @param channel 該社團底下的頻道
     * @param name 要更改的頻道名稱
     */
    fun editChannel(group: Group, channel: Channel, name: String) {
        KLog.i(TAG, "editChannel")
        editChannelName(group, channel, name)
        editChannelRole(channel)
    }

    /**
     * 編輯 頻道 名稱
     */
    private fun editChannelName(group: Group, channel: Channel, name: String) {
        KLog.i(TAG, "editChanelName")
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
     * 編輯 頻道 管理員
     */
    private fun editChannelRole(channel: Channel) {
        KLog.i(TAG, "editChannelRole")
        viewModelScope.launch {
            //新增角色至channel
            val addRoleList =
                uiState.channelRole?.filter { !orgChannelRoleList.contains(it) }.orEmpty()
            if (addRoleList.isNotEmpty()) {
                channelUseCase.addRoleToChannel(
                    channelId = channel.id.orEmpty(),
                    roleIds = addRoleList.map {
                        it.id.orEmpty()
                    }).fold({
                }, {
                    KLog.e(TAG, it)
                })
            }


            //要移除的角色
            val removeRoleList =
                orgChannelRoleList.filter { !uiState.channelRole.orEmpty().contains(it) }
            if (removeRoleList.isNotEmpty()) {
                channelUseCase.deleteRoleFromChannel(channelId = channel.id.orEmpty(),
                    roleIds = removeRoleList.map {
                        it.id.orEmpty()
                    }).fold({

                }, {
                    KLog.e(TAG, it)
                })
            }
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
     * 刪除 分類, 並將該分類下的頻道分配至 預設
     */
    fun deleteCategory(group: Group, category: Category) {
        KLog.i(TAG, "deleteCategory")
        viewModelScope.launch {
            channelUseCase.deleteCategory(categoryId = category.id.orEmpty()).fold({
            }, {
                if (it is EmptyBodyException) {

                    var newCategory = group.categories?.filter { groupCategory ->
                        groupCategory.id != category.id
                    }

                    //將刪除分類下的頻道移至預設分類下
                    val channels = category.channels ?: emptyList()
                    newCategory = newCategory?.map { category ->
                        if (category.isDefault == true) {
                            val newChannel = category.channels?.toMutableList() ?: mutableListOf()
                            newChannel.addAll(channels)
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
                } else {
                    KLog.e(TAG, it)
                }
            })
        }
    }

    /**
     * 點擊 新增角色
     */
    fun onRoleClick(addChannelRoleModel: AddChannelRoleModel) {
        val role = addChannelRoleModel.copy(
            isChecked = !addChannelRoleModel.isChecked
        )

        val newList = uiState.groupRoleList.map {
            if (it.role.id == addChannelRoleModel.role.id) {
                role
            } else {
                it
            }
        }

        uiState = uiState.copy(
            groupRoleList = newList
        )
    }

    /**
     * 確定新增 角色
     */
    fun onAddRoleConfirm() {
        KLog.i(TAG, "onAddRoleConfirm.")
        val confirmRole = uiState.groupRoleList.filter {
            it.isChecked
        }
        val finalRoleList = confirmRole.map {
            it.role
        }

        val gson = Gson()
        uiState = uiState.copy(
            confirmRoleList = gson.toJson(finalRoleList)
        )
    }

    /**
     * 增加 角色
     */
    fun addChannelRole(roleListStr: String) {
        KLog.i(TAG, "addChannelRole:$roleListStr")
        val gson = Gson()
        val roleList = gson.fromJsonTypeToken<List<FanciRole>>(roleListStr)
        val orgRoleList = uiState.channelRole.orEmpty()
        val unionList = roleList.union(orgRoleList).toMutableList()
        uiState = uiState.copy(
            channelRole = unionList
        )
    }

    /**
     * 紀錄 Tab
     */
    fun onTabSelected(position: Int) {
        uiState = uiState.copy(tabSelected = position)
    }

    /**
     * 點擊 移除角色
     */
    fun onRemoveRole(fanciRole: FanciRole) {
        KLog.i(TAG, "onRemoveRole:$fanciRole")
        val filterRoleList = uiState.channelRole?.filterNot {
            it.id == fanciRole.id
        }.orEmpty()

        uiState = uiState.copy(
            channelRole = filterRoleList
        )
    }

    /**
     * 點擊重新排序
     */
    fun onSortClick() {
        KLog.i(TAG, "onSortClick.")
        uiState = uiState.copy(
            isOpenSortDialog = true
        )
    }

    fun closeSortDialog() {
        uiState = uiState.copy(
            isOpenSortDialog = false
        )
    }

    /**
     * 儲存 分類排序
     */
    fun onSortCategoryOrChannel(group: Group, categories: List<Category>) {
        KLog.i(TAG, "onSortCategory:$categories")
        viewModelScope.launch {
            orderUseCase.orderCategoryOrChannel(
                groupId = group.id.orEmpty(),
                category = categories
            ).fold({
            }, {
                if (it is EmptyBodyException) {
                    uiState = uiState.copy(
                        group = group.copy(
                            categories = categories
                        ),
                        isSoredToServerComplete = true
                    )
                } else {
                    KLog.e(TAG, it)
                }
            })
        }
    }

    /**
     * 排序 頻道
     */
    fun sortChannel(moveItem: MoveItem) {
        KLog.i(TAG, "sortChannel:$moveItem")
        uiState.group?.let {group ->

            val removedCategoryList = group.categories?.map {category ->
                if (category.id == moveItem.fromCategory.id) {
                    category.copy(
                        channels = category.channels?.filter {channel ->
                            channel.id != moveItem.channel.id
                        }
                    )
                }
                else {
                    category
                }
            }

            val sortedCategory = removedCategoryList?.map {category ->
                if (category.id == moveItem.toCategory?.id) {
                    val channels = category.channels?.toMutableList()
                    channels?.add(moveItem.channel)
                    category.copy(
                        channels = channels?.distinctBy {
                            it.id
                        }
                    )
                }
                else {
                    category
                }
            }

            uiState = uiState.copy(
                group = group.copy(
                    categories = sortedCategory
                )
            )
        }
    }
}