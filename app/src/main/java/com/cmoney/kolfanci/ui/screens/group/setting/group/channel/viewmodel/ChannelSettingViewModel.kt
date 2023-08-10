package com.cmoney.kolfanci.ui.screens.group.setting.group.channel.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChannelAccessOptionV2
import com.cmoney.fanciapi.fanci.model.ChannelAuthType
import com.cmoney.fanciapi.fanci.model.ChannelPrivacy
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.extension.EmptyBodyException
import com.cmoney.kolfanci.extension.fromJsonTypeToken
import com.cmoney.kolfanci.extension.toVipPlanModel
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
    val categoryName: String = "",                   //分類名稱
    val channelRole: List<FanciRole>? = null,        //目前頻道顯示角色List, 管理員
    val groupRoleList: List<AddChannelRoleModel> = emptyList(),
    val confirmRoleList: String = "",
    val tabSelected: Int = 0,                        //record tab position
    val isOpenSortDialog: Boolean = false,
    val isSoredToServerComplete: Boolean = false,    //完成將排序結果給 server
    val channelSettingTabIndex: Int = 0,             //新增頻道 tab position
    var isNeedApproval: Boolean = false,             //是否公開
    val channelAccessTypeList: List<ChannelAccessOptionV2> = emptyList(), //私密頻道 權限清單
    val clickPermissionMemberModel: Pair<ChannelAccessOptionV2, SelectedModel>? = null, //點擊的 Permission 資料
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
        private set

    var currentSelectedPermission: ChannelAccessOptionV2? = null

    //每個權限所勾選的人員/角色 清單, key = authType
    private val _listPermissionSelected: MutableMap<ChannelAuthType, SelectedModel> = mutableMapOf()
    val listPermissionSelected: Map<ChannelAuthType, SelectedModel> = _listPermissionSelected

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
     * 取得 私密頻道 人員/角色(去除 無權限) 以及 抓取人數
     */
    private fun getPrivateChannelMember(channelId: String) {
        KLog.i(TAG, "getPrivateChannelMember:$channelId")
        viewModelScope.launch {
            channelUseCase.getPrivateChannelWhiteList(
                channelId
            ).fold({
                it.filter { channelWhiteList -> channelWhiteList.authType != ChannelAuthType.noPermission }
                    .map { channelWhiteList ->
                        _listPermissionSelected[channelWhiteList.authType!!] = SelectedModel(
                            selectedMember = channelWhiteList.users.orEmpty(),
                            selectedRole = channelWhiteList.roles.orEmpty(),
                            selectedVipPlans = channelWhiteList.vipRoles.orEmpty()
                                .map { fanciRole ->
                                    fanciRole.toVipPlanModel()
                                }
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
    private fun getChannelRole(channelId: String) {
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
     * 設定 group to ui display
     */
    fun setGroup(group: Group) {
        uiState = uiState.copy(group = group)
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
                } else {
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
            channelUseCase.getChanelAccessTypeWithoutNoPermission().fold({
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
        currentSelectedPermission?.authType?.let { authType ->
            _listPermissionSelected[authType] = selectedModel
            currentSelectedPermission = null
            fetchPrivateChannelUserCount()
        }
    }

    /**
     * 設定目前 所點擊的 權限設定
     */
    fun onPermissionClick(channelPermissionModel: ChannelAccessOptionV2) {
        KLog.i(TAG, "onPermissionClick:$channelPermissionModel")
        currentSelectedPermission = channelPermissionModel

        _listPermissionSelected[channelPermissionModel.authType]?.let {
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
            val userList = _listPermissionSelected.flatMap {
                it.value.selectedMember
            }.map {
                it.id.orEmpty()
            }.distinct()

            val roleList = _listPermissionSelected.flatMap {
                it.value.selectedRole
            }.map {
                it.id.orEmpty()
            }.distinct()

            val vipRoleList = _listPermissionSelected.flatMap {
                it.value.selectedVipPlans
            }.map {
                it.id
            }.distinct()

            val allRoleIds = roleList.union(vipRoleList).toList()

            channelUseCase.getPrivateChannelUserCount(
                roleIds = allRoleIds,
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