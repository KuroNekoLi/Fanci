package com.cmoney.kolfanci.ui.screens.group.setting.group.channel.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.*
import com.cmoney.kolfanci.extension.EmptyBodyException
import com.cmoney.kolfanci.extension.fromJsonTypeToken
import com.cmoney.kolfanci.extension.toGroupMember
import com.cmoney.kolfanci.model.usecase.ChannelUseCase
import com.cmoney.kolfanci.model.usecase.OrderUseCase
import com.cmoney.kolfanci.ui.screens.group.setting.group.channel.sort.MoveItem
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.SelectedModel
import com.google.gson.Gson
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class UiState(
    val isLoading: Boolean = false,
    val group: Group? = null,
    var channelName: String = "",                    //頻道名稱
    var categoryName: String = "",                   //分類名稱
    val channelRole: List<FanciRole>? = null,        //目前頻道顯示角色List, 管理員
    val groupRoleList: List<AddChannelRoleModel> = emptyList(),
    val confirmRoleList: String = "",
    val tabSelected: Int = 0,                        //record tab position
    val isOpenSortDialog: Boolean = false,
    val isSoredToServerComplete: Boolean = false,    //完成將排序結果給 server
    val channelSettingTabIndex: Int = 0,             //新增頻道 tab position
    var isNeedApproval: Boolean = false,             //是否公開
    val channelAccessTypeList: List<ChannelAccessOptionModel> = emptyList(), //私密頻道 權限清單
    val clickPermissionMemberModel: Pair<ChannelAccessOptionModel, SelectedModel>? = null, //點擊的 Permission 資料
    val uniqueUserCount: Int = 0                     //私密頻道成員人數
)

data class AddChannelRoleModel(val role: FanciRole, val isChecked: Boolean = false)

class ChannelSettingViewModel(
    private val channelUseCase: ChannelUseCase,
    private val orderUseCase: OrderUseCase
) : ViewModel() {
    private val TAG = ChannelSettingViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    var orgChannelRoleList = emptyList<FanciRole>() //原本頻道裡的角色清單

    var currentSelectedPermission: ChannelAccessOptionModel? = null

    //每個權限所勾選的人員/角色 清單, key = authType
    private val listPermissionSelected: HashMap<String, SelectedModel> = hashMapOf()

    //預編輯的頻道
    var channel: Channel? = null

    /**
     * 設定 要編輯的 channel
     */
    fun initChannel(channel: Channel) {
        KLog.i(TAG, "initChannel:$channel")
        this.channel = channel

        //頻道名稱
        uiState.channelName = channel.name.orEmpty()

        //取得管理員
        getChannelRole(channel.id.orEmpty())

        //公開/不公開
        uiState.isNeedApproval = channel.privacy == ChannelPrivacy.private

        //取得 私密頻道 人員/角色
        channel.privacy?.let { privacy ->
            if (privacy == ChannelPrivacy.private) {
                getPrivateChannelMember(channel.id.orEmpty())
            }
        }
    }

    /**
     * 取得 私密頻道 人員/角色 以及 抓取人數
     */
    private fun getPrivateChannelMember(channelId: String) {
        KLog.i(TAG, "getPrivateChannelMember:$channelId")
        viewModelScope.launch {
            channelUseCase.getPrivateChannelWhiteList(
                channelId
            ).fold({
                it.map { channelWhiteList ->
                    listPermissionSelected[channelWhiteList.authType.orEmpty()] = SelectedModel(
                        selectedMember = channelWhiteList.users.orEmpty(),
                        selectedRole = channelWhiteList.roles.orEmpty(),
                        // TODO 需要此頻道已設定的VIP方案
                        selectedVipPlans = emptyList()
                    )
                }
                fetchPrivateChannelUserCount()

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
                uiState = uiState.copy(isLoading = false, channelRole = it)
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

        viewModelScope.launch {
            uiState = uiState.copy(
                isLoading = true
            )

            val privacy = if (uiState.isNeedApproval) {
                ChannelPrivacy.private
            } else {
                ChannelPrivacy.public
            }

            channelUseCase.addChannel(
                categoryId = categoryId,
                name = name,
                privacy = privacy
            ).fold({ channel ->
                //新增管理員
                if (uiState.channelRole?.isNotEmpty() == true) {
                    editChannelRole(channel)
                }

                //私密頻道處理
                if (privacy == ChannelPrivacy.private) {
                    setPrivateChannelWhiteList(channelId = channel.id.orEmpty())
                }

                addChannelToGroup(channel, group)
            }, {
                KLog.e(TAG, it)
                uiState = uiState.copy(
                    isLoading = false
                )
            })
        }
    }

    /**
     * 設定 私密頻道 白名單
     */
    private suspend fun setPrivateChannelWhiteList(channelId: String) {
        KLog.i(TAG, "setPrivateChannelWhiteList:$channelId")
        listPermissionSelected.map {
            val authType = it.key
            val selectedModel = it.value
            val job = viewModelScope.launch {
                channelUseCase.putPrivateChannelWhiteList(
                    channelId = channelId,
                    authType = authType,
                    accessorList = selectedModel.toAccessorList()
                ).isSuccess
            }
            job.join()
        }
    }


    /**
     * 將新的 channel append 至 原本 group 做顯示
     */
    private fun addChannelToGroup(channel: Channel, group: Group) {
        channel.category?.let { channelCategory ->
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
     * @param name 要更改的頻道名稱
     */
    fun editChannel(group: Group, name: String) {
        KLog.i(TAG, "editChannel")
        viewModelScope.launch {
            channel?.let { channel ->
                //私密頻道處理
                if (uiState.isNeedApproval) {
                    setPrivateChannelWhiteList(channelId = channel.id.orEmpty())
                }

                editChannelRole(channel)

                editChannelName(group, channel, name)
            }
        }
    }

    /**
     * 編輯 頻道 名稱
     */
    private fun editChannelName(group: Group, channel: Channel, name: String) {
        KLog.i(TAG, "editChanelName")
        viewModelScope.launch {
            channelUseCase.editChannelName(
                channelId = channel.id.orEmpty(),
                name = name,
                privacy = if (uiState.isNeedApproval) {
                    ChannelPrivacy.private
                } else {
                    ChannelPrivacy.public
                }
            ).fold({}, {
                if (it is EmptyBodyException) {
                    val newCategory = group.categories?.map { category ->
                        val newChannel = category.channels?.map { groupChannel ->
                            if (channel.id == groupChannel.id) {
                                channel.copy(
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
    private suspend fun editChannelRole(channel: Channel) {
        KLog.i(TAG, "editChannelRole")
        val job1 = viewModelScope.launch {
            //新增角色至channel
            val addRoleList =
                uiState.channelRole?.filter { !orgChannelRoleList.contains(it) }.orEmpty()
            if (addRoleList.isNotEmpty()) {
                val isSuccess = channelUseCase.addRoleToChannel(
                    channelId = channel.id.orEmpty(),
                    roleIds = addRoleList.map {
                        it.id.orEmpty()
                    }).getOrNull()
                KLog.i(TAG, "editChannelRole addRole: $isSuccess")
            }
        }
        job1.join()

        val job2 = viewModelScope.launch {
            //要移除的角色
            val removeRoleList =
                orgChannelRoleList.filter { !uiState.channelRole.orEmpty().contains(it) }
            if (removeRoleList.isNotEmpty()) {
                val isSuccess =
                    channelUseCase.deleteRoleFromChannel(channelId = channel.id.orEmpty(),
                        roleIds = removeRoleList.map {
                            it.id.orEmpty()
                        }).isSuccess
                KLog.i(TAG, "editChannelRole removeRole: $isSuccess")
            }
        }
        job2.join()
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
        uiState.group?.let { group ->

            val removedCategoryList = group.categories?.map { category ->
                if (category.id == moveItem.fromCategory.id) {
                    category.copy(
                        channels = category.channels?.filter { channel ->
                            channel.id != moveItem.channel.id
                        }
                    )
                } else {
                    category
                }
            }

            val sortedCategory = removedCategoryList?.map { category ->
                if (category.id == moveItem.toCategory?.id) {
                    val channels = category.channels.orEmpty().toMutableList()
                    channels.add(moveItem.channel)
                    category.copy(
                        channels = channels.distinctBy {
                            it.id
                        }
                    )
                } else {
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

    /**
     * 新增頻道 tab selection
     */
    fun onChannelSettingTabSelected(position: Int) {
        KLog.i(TAG, "onChannelSettingTabSelected:$position")
        uiState = uiState.copy(tabSelected = position)
    }

    /**
     * 設定 頻道 公開度
     */
    fun setChannelApproval(isNeedApproval: Boolean) {
        KLog.i(TAG, "setChannelApproval:$isNeedApproval")
        uiState = uiState.copy(
            isNeedApproval = isNeedApproval,
        )

        channel?.apply {
            channel = this.copy(
                privacy = if (isNeedApproval) {
                    ChannelPrivacy.private
                }  else {
                    ChannelPrivacy.public
                }
            )
        }
    }

    /**
     * 抓取 目前私密頻道所有權限
     */
    fun fetchChannelPermissionList() {
        KLog.i(TAG, "fetchChannelPermissionList")
        viewModelScope.launch {
            channelUseCase.getChanelAccessType().fold({
                uiState = uiState.copy(
                    channelAccessTypeList = it
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 設定 所選擇的成員以及角色
     */
    fun setPermissionMemberSelected(selectedModel: SelectedModel) {
        KLog.i(TAG, "setPermissionMemberSelected:$selectedModel")
        currentSelectedPermission?.let {
            listPermissionSelected[it.authType.orEmpty()] = selectedModel
            currentSelectedPermission = null

            fetchPrivateChannelUserCount()
        }
    }

    /**
     * 設定目前 所點擊的 權限設定
     */
    fun onPermissionClick(channelPermissionModel: ChannelAccessOptionModel) {
        KLog.i(TAG, "onPermissionClick:$channelPermissionModel")
        currentSelectedPermission = channelPermissionModel

        listPermissionSelected[channelPermissionModel.authType.orEmpty()]?.let {
            uiState = uiState.copy(
                clickPermissionMemberModel = Pair(channelPermissionModel, it)
            )
        } ?: kotlin.run {
            uiState = uiState.copy(
                clickPermissionMemberModel = Pair(channelPermissionModel, SelectedModel())
            )
        }
    }

    /**
     * 跳轉後關閉參數
     */
    fun dismissPermissionNavigator() {
        uiState = uiState.copy(clickPermissionMemberModel = null)
    }

    /**
     * 設定頻道名稱
     */
    fun setChannelName(name: String) {
        uiState = uiState.copy(channelName = name)
    }

    /**
     * 設定分類名稱
     */
    fun setCategoryName(name: String) {
        uiState = uiState.copy(categoryName = name)
    }

    /**
     * 取得 私密頻道不重複人數
     */
    private fun fetchPrivateChannelUserCount() {
        KLog.i(TAG, "fetchPrivateChannelUserCount")
        viewModelScope.launch {
            val userList = listPermissionSelected.flatMap {
                it.value.selectedMember
            }.map {
                it.id.orEmpty()
            }.distinct()

            val roleList = listPermissionSelected.flatMap {
                it.value.selectedRole
            }.map {
                it.id.orEmpty()
            }.distinct()

            channelUseCase.getPrivateChannelUserCount(
                roleIds = roleList,
                userIds = userList
            ).fold({
                uiState = uiState.copy(
                    uniqueUserCount = it.count ?: 0
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }
}