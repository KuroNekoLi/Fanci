package com.cmoney.kolfanci.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChannelAuthType
import com.cmoney.fanciapi.fanci.model.ChannelPrivacy
import com.cmoney.fanciapi.fanci.model.ColorTheme
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.MessageServiceType
import com.cmoney.kolfanci.extension.EmptyBodyException
import com.cmoney.kolfanci.extension.toBulletinboardMessage
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.notification.TargetType
import com.cmoney.kolfanci.model.usecase.ChannelUseCase
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.cmoney.kolfanci.model.usecase.OrderUseCase
import com.cmoney.kolfanci.model.usecase.PermissionUseCase
import com.cmoney.kolfanci.model.usecase.ThemeUseCase
import com.cmoney.kolfanci.ui.screens.follow.model.GroupItem
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.avatar.ImageChangeData
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.theme.model.GroupTheme
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.SelectedModel
import com.cmoney.kolfanci.ui.theme.DefaultThemeColor
import com.cmoney.xlogin.XLoginHelper
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class ThemeSetting {
    object Default : ThemeSetting()
    object Coffee : ThemeSetting()
}

/**
 * 推播 跳轉頁面所需資料
 */
sealed class PushDataWrapper {
    /**
     * 前往頻道聊天
     */
    data class ChannelMessage(
        val channel: Channel,
        val serialNumber: String
    ) : PushDataWrapper()

    /**
     * 前往頻道貼文
     */
    data class ChannelPost(
        val channel: Channel,
        val bulletinboardMessage: BulletinboardMessage
    ) : PushDataWrapper()

}

/**
 * 社團相關設定
 */
class GroupViewModel(
    private val themeUseCase: ThemeUseCase,
    private val groupUseCase: GroupUseCase,
    private val channelUseCase: ChannelUseCase,
    private val permissionUseCase: PermissionUseCase,
    private val orderUseCase: OrderUseCase,
    private val chatRoomUseCase: ChatRoomUseCase
) : ViewModel() {
    private val TAG = GroupViewModel::class.java.simpleName

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    //目前選中的社團
    private val _currentGroup: MutableStateFlow<Group?> = MutableStateFlow(null)
    val currentGroup = _currentGroup.asStateFlow()

    //我目前加入的社團
    private val _myGroupList: MutableStateFlow<List<GroupItem>> = MutableStateFlow(emptyList())
    val myGroupList = _myGroupList.asStateFlow()

    //目前server有的社團清單 (沒加入社團時顯示用)
    private val _groupList: MutableStateFlow<List<Group>> = MutableStateFlow(emptyList())
    val groupList = _groupList.asStateFlow()

    //主題設定檔
    private val _theme = MutableStateFlow(DefaultThemeColor)
    val theme = _theme.asStateFlow()

    //前往指定頻道, 指定貼文/指定訊息...
    private val _jumpToChannelDest = MutableStateFlow<PushDataWrapper?>(null)
    val jumpToChannelDest = _jumpToChannelDest.asStateFlow()

    var haveNextPage: Boolean = false       //拿取所有群組時 是否還有分頁
    var nextWeight: Long? = null            //下一分頁權重

    /**
     * 有登入狀態, 取得 我的群組, 並設定該主題
     * 未登入, 取得 server 社團清單
     */
    fun fetchMyGroup() {
        KLog.i(TAG, "fetchMyGroup")
        if (XLoginHelper.isLogin) {
            viewModelScope.launch {
                loading()
                groupUseCase.groupToSelectGroupItem().fold({
                    if (it.isNotEmpty()) {
                        var currentSelectedPos = _myGroupList.value.indexOfFirst { groupItem ->
                            groupItem.isSelected
                        }

                        //not found selected group, reset to first
                        if (currentSelectedPos < 0) {
                            currentSelectedPos = 0
                        }

                        //我的所有群組
                        _myGroupList.value = it.mapIndexed { index, groupItem ->
                            if (index == currentSelectedPos) {
                                groupItem.copy(
                                    isSelected = true
                                )
                            } else {
                                groupItem.copy(
                                    isSelected = false
                                )
                            }
                        }

                        val selectedGroup = _myGroupList.value[currentSelectedPos].groupModel

                        //設定目前的社團
                        _currentGroup.value = selectedGroup

                        //抓取選中社團的主題
                        setAppTheme(selectedGroup)

                        //抓取選中社團的權限
                        fetchGroupPermission(selectedGroup)
                    } else {
                        resetToDefault()
                        fetchAllGroupList()
                    }
                    dismissLoading()
                }, {
                    dismissLoading()
                    KLog.e(TAG, it)
                })
            }
        } else {
            fetchAllGroupList()
        }
    }

    /**
     * 將原本設定過的值, 都恢復至 default value
     */
    private fun resetToDefault() {
        _currentGroup.value = null
        _myGroupList.value = emptyList()
        _theme.value = DefaultThemeColor
    }

    /**
     * 當沒有 加入社團的時候, 取得目前server有的社團
     */
    fun fetchAllGroupList() {
        KLog.i(TAG, "fetchAllGroupList")
        viewModelScope.launch {
            loading()
            groupUseCase.getPopularGroup(
                pageSize = 10,
                startWeight = nextWeight ?: Long.MAX_VALUE
            ).fold({
                haveNextPage = it.haveNextPage == true
                nextWeight = it.nextWeight
                val orgGroupList = _groupList.value.toMutableList()
                orgGroupList.addAll(it.items.orEmpty())
                _groupList.value = orgGroupList.distinctBy { group ->
                    group.id
                }
                dismissLoading()
            }, {
                dismissLoading()
                KLog.e(TAG, it)
            })
        }
    }

    private fun loading() {
        KLog.i(TAG, "loading")
        _loading.value = true
    }

    private fun dismissLoading() {
        KLog.i(TAG, "dismissLoading")
        _loading.value = false
    }

    /**
     * 讀取server 社團 下一分頁
     */
    fun onLoadMore() {
        KLog.i(TAG, "onLoadMore: haveNextPage:$haveNextPage nextWeight:$nextWeight")
        if (haveNextPage && nextWeight != null && nextWeight!! > 0) {
            fetchAllGroupList()
        }
    }

    /**
     * 設定 目前所選的社團, 並設定Theme
     */
    fun setCurrentGroup(group: Group) {
        KLog.i(TAG, "setCurrentGroup")
        if (group != _currentGroup.value && group.id != null) {
            KLog.i(TAG, "setCurrentGroup diff:$group")
            setAppTheme(group)
            fetchGroupPermission(group)
            _currentGroup.value = group
            setupMenuSelectedStatus(group)
        }
    }

    /**
     * 解散社團
     *
     * @param id
     */
    fun leaveGroup(id: String) {
        viewModelScope.launch {
            loading()
            val result = groupUseCase.leaveGroup(id = id)
            result.onSuccess {
                // TODO 目前不會成功，因為回傳 204 會被轉為 EmptyBodyException
            }
                .onFailure { t ->
                    if (t is EmptyBodyException) {
                        val myGroup = _myGroupList.value
                        val newGroups = myGroup.filterNot { groupItem ->
                            groupItem.groupModel.id == id
                        }.mapIndexed { index, groupItem ->
                            if (index == 0) {
                                groupItem.copy(isSelected = true)
                            } else {
                                groupItem
                            }
                        }
                        if (newGroups.isNotEmpty()) {
                            val selectGroup = newGroups.first()
                            _myGroupList.value = newGroups
                            setCurrentGroup(group = selectGroup.groupModel)
                        } else {
//                            fetchAllGroupList()
                            fetchMyGroup()
                        }
                    } else {
                        KLog.e(TAG, t)
                    }
                }
            dismissLoading()
        }
    }

    /**
     * 設定 側邊欄目前選擇狀態, 如果該 Group 不存在就新增
     */
    private fun setupMenuSelectedStatus(group: Group) {
        val groupList = _myGroupList.value.toMutableList()
        val isExists = groupList.any {
            it.groupModel.id == group.id
        }

        if (!isExists) {
            groupList.add(
                0, GroupItem(
                    groupModel = group,
                    isSelected = true
                )
            )
        }

        _myGroupList.value = groupList.map {
            if (it.groupModel.id == group.id) {
                it.copy(
                    isSelected = true
                )
            } else {
                it.copy(
                    isSelected = false
                )
            }
        }
    }

    /**
     * 根據選擇的社團 設定 theme
     */
    private fun setAppTheme(group: Group) {
        KLog.i(TAG, "setAppTheme.")
        viewModelScope.launch {
            group.colorSchemeGroupKey?.apply {
                themeUseCase.fetchThemeConfig(this).fold({
                    _theme.value = it.theme
                }, {
                    KLog.e(TAG, it)
                })
            }
        }
    }

    /**
     * 抓取在該社團的權限
     */
    private fun fetchGroupPermission(group: Group) {
        KLog.i(TAG, "fetchGroupPermission:$group")
        viewModelScope.launch {
            permissionUseCase.getPermissionByGroup(groupId = group.id.orEmpty()).fold(
                {
                    KLog.i(TAG, it)
                    Constant.MyGroupPermission = it
                }, {
                    KLog.e(TAG, it)
                }
            )
        }
    }

    /**
     * 更換 社團 簡介
     * @param desc 簡介
     */
    fun changeGroupDesc(desc: String) {
        val group = _currentGroup.value ?: return
        viewModelScope.launch {
            groupUseCase.changeGroupDesc(desc, group).fold({
            }, {
                KLog.e(TAG, it)
                if (it is EmptyBodyException) {
                    _currentGroup.value = group.copy(description = desc)
                }
            })
        }
    }

    /**
     * 更換 社團名字
     * @param name 更換的名字
     */
    fun changeGroupName(name: String) {
        KLog.i(TAG, "changeGroupNameL$name")
        val group = _currentGroup.value ?: return
        viewModelScope.launch {
            groupUseCase.changeGroupName(name, group).fold({
            }, {
                KLog.e(TAG, it)
                if (it is EmptyBodyException) {
                    _currentGroup.value = group.copy(name = name)
                }
            })
        }
    }

    /**
     * 更換社團 頭貼
     */
    fun changeGroupAvatar(data: ImageChangeData) {
        KLog.i(TAG, "changeGroupAvatar")
        val group = _currentGroup.value ?: return
        viewModelScope.launch {
            var uri: Any? = data.uri
            if (uri == null) {
                uri = data.url
            }
            uri?.let {
                groupUseCase.changeGroupAvatar(uri, group).collect {
                    _currentGroup.value = group.copy(
                        thumbnailImageUrl = it
                    )

                    //refresh group list
                    _myGroupList.value = _myGroupList.value.map { groupItem ->
                        if (groupItem.groupModel.id == group.id) {
                            groupItem.copy(
                                groupModel = groupItem.groupModel.copy(
                                    thumbnailImageUrl = it
                                )
                            )
                        } else {
                            groupItem
                        }
                    }
                }
            }
        }
    }

    /**
     * 更換 社團 背景圖
     */
    fun changeGroupCover(data: ImageChangeData) {
        KLog.i(TAG, "changeGroupCover")
        val group = _currentGroup.value ?: return
        viewModelScope.launch {
            var uri: Any? = data.uri
            if (uri == null) {
                uri = data.url
            }
            uri?.let {
                groupUseCase.changeGroupBackground(uri, group).collect {
                    _currentGroup.value = group.copy(
                        coverImageUrl = it
                    )
                }
            }
        }
    }

    /**
     * Usr 選擇的 預設 大頭貼
     */
    fun onGroupAvatarSelect(url: String) {
        KLog.i(TAG, "onGroupAvatarSelect:$url")
        val group = _currentGroup.value ?: return
        viewModelScope.launch {
            _currentGroup.value = group.copy(
                thumbnailImageUrl = url
            )
        }
    }

    /**
     * Usr 選擇的 預設 背景
     */
    fun onGroupCoverSelect(url: String) {
        KLog.i(TAG, "onGroupCoverSelect:$url")
        val group = _currentGroup.value ?: return
        viewModelScope.launch {
            _currentGroup.value = group.copy(
                coverImageUrl = url
            )
        }
    }

    /**
     * 更換 主題
     * @param groupTheme 要更換的主題
     */
    fun changeTheme(groupTheme: GroupTheme) {
        KLog.i(TAG, "changeTheme: $groupTheme")
        val group = _currentGroup.value ?: return
        viewModelScope.launch {
            if (group.id != null) {
                themeUseCase.changeGroupTheme(group, groupTheme)
                    .onSuccess {
                    }
                    .onFailure {
                        KLog.e(TAG, it)
                        if (it is EmptyBodyException) {
                            ColorTheme.decode(groupTheme.id)?.let { colorTheme ->
                                themeUseCase.fetchThemeConfig(colorTheme).fold({ localGroupTheme ->
                                    _theme.value = localGroupTheme.theme
                                }, { t ->
                                    KLog.e(TAG, t)
                                })
                                setSelectedTheme(group, colorTheme)
                            }
                        }
                    }
            }
        }
    }

    /**
     * 設定 選中的 Theme
     */
    private fun setSelectedTheme(group: Group, colorTheme: ColorTheme) {
        _currentGroup.value = group.copy(
            colorSchemeGroupKey = colorTheme
        )
    }

    /**
     * 設定公開度
     *
     * @param openness 公開度，true 公開，false 不公開
     */
    fun changeOpenness(openness: Boolean) {
        _currentGroup.update { group ->
            group?.copy(
                isNeedApproval = !openness
            )
        }
    }

    /**
     * 新增 分類
     *
     * @param name 分類名稱
     */
    fun addCategory(name: String) {
        val group = _currentGroup.value ?: return
        KLog.i(TAG, "addCategory: $group, $name")
        viewModelScope.launch {
            channelUseCase.addCategory(groupId = group.id.orEmpty(), name = name).fold({
                val newCategoryList = group.categories.orEmpty().toMutableList()
                newCategoryList.add(it)
                _currentGroup.update { oldGroup ->
                    oldGroup?.copy(
                        categories = newCategoryList
                    )
                }
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 新增 頻道
     *
     * @param categoryId 分類id, 在此分類下建立
     * @param name 頻道名稱
     */
    fun addChannel(
        categoryId: String,
        name: String,
        isNeedApproval: Boolean,
        listPermissionSelected: Map<ChannelAuthType, SelectedModel>,
        orgChannelRoleList: List<FanciRole>,
        channelRole: List<FanciRole>? = null
    ) {
        val group = _currentGroup.value ?: return
        KLog.i(TAG, "addChannel: $categoryId, $name")

        viewModelScope.launch {
            val privacy = if (isNeedApproval) {
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
                if (channelRole?.isNotEmpty() == true) {
                    editChannelRole(
                        channel = channel,
                        orgChannelRoleList = orgChannelRoleList,
                        channelRole = channelRole
                    )
                }
                //私密頻道處理
                if (privacy == ChannelPrivacy.private) {
                    setPrivateChannelWhiteList(
                        channelId = channel.id.orEmpty(),
                        listPermissionSelected = listPermissionSelected
                    )
                }
                // 設定類別到畫面上
                addChannelToGroup(channel, group)
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 編輯 頻道
     *
     * @param name 要更改的頻道名稱
     */
    fun editChannel(
        channel: Channel?,
        name: String,
        isNeedApproval: Boolean,
        listPermissionSelected: Map<ChannelAuthType, SelectedModel>,
        orgChannelRoleList: List<FanciRole>,
        channelRole: List<FanciRole>? = null
    ) {
        val group = _currentGroup.value ?: return
        KLog.i(TAG, "editChannel")
        viewModelScope.launch {
            channel?.let { channel ->
                // 私密頻道處理
                if (isNeedApproval) {
                    setPrivateChannelWhiteList(
                        channelId = channel.id.orEmpty(),
                        listPermissionSelected = listPermissionSelected
                    )
                }
                // 編輯管理員
                editChannelRole(
                    channel = channel,
                    orgChannelRoleList = orgChannelRoleList,
                    channelRole = channelRole
                )
                // 編輯名稱
                editChannelName(
                    group = group,
                    channel = channel,
                    name = name,
                    isNeedApproval = isNeedApproval
                )
            }
        }
    }

    /**
     * 刪除 頻道
     */
    fun deleteChannel(channel: Channel) {
        val group = _currentGroup.value ?: return
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
                    _currentGroup.update { oldGroup ->
                        oldGroup?.copy(categories = newCategory)
                    }
                }
            })
        }
    }

    /**
     * 編輯 頻道 管理員
     */
    private suspend fun editChannelRole(
        channel: Channel,
        orgChannelRoleList: List<FanciRole>,
        channelRole: List<FanciRole>? = null
    ) {
        KLog.i(TAG, "editChannelRole")
        //新增角色至channel
        val addRoleList =
            channelRole?.filter { !orgChannelRoleList.contains(it) }.orEmpty()
        if (addRoleList.isNotEmpty()) {
            val isSuccess = channelUseCase.addRoleToChannel(
                channelId = channel.id.orEmpty(),
                roleIds = addRoleList.map {
                    it.id.orEmpty()
                }).getOrNull()
            KLog.i(TAG, "editChannelRole addRole: $isSuccess")
        }
        //要移除的角色
        val removeRoleList =
            orgChannelRoleList.filter { !channelRole.orEmpty().contains(it) }
        if (removeRoleList.isNotEmpty()) {
            val isSuccess =
                channelUseCase.deleteRoleFromChannel(channelId = channel.id.orEmpty(),
                    roleIds = removeRoleList.map {
                        it.id.orEmpty()
                    }).isSuccess
            KLog.i(TAG, "editChannelRole removeRole: $isSuccess")
        }
    }

    /**
     * 設定 私密頻道 白名單
     */
    private suspend fun setPrivateChannelWhiteList(
        channelId: String,
        listPermissionSelected: Map<ChannelAuthType, SelectedModel>
    ) {
        KLog.i(TAG, "setPrivateChannelWhiteList:$channelId")
        listPermissionSelected.forEach { (authType, selectedModel) ->
            channelUseCase.putPrivateChannelWhiteList(
                channelId = channelId,
                authType = authType,
                accessorList = selectedModel.toAccessorList()
            )
        }
    }

    /**
     * 將新的 channel append 至 原本 group 做顯示
     */
    private fun addChannelToGroup(channel: Channel, group: Group) {
        channel.category?.let { channelCategory ->
            val newCategories = group.categories?.map { category ->
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
            _currentGroup.update {
                it?.copy(
                    categories = newCategories
                )
            }
        }
    }

    /**
     * 編輯 頻道 名稱
     */
    private fun editChannelName(
        group: Group,
        channel: Channel,
        name: String,
        isNeedApproval: Boolean
    ) {
        KLog.i(TAG, "editChanelName")
        viewModelScope.launch {
            channelUseCase.editChannelName(
                channelId = channel.id.orEmpty(),
                name = name,
                privacy = if (isNeedApproval) {
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
                    _currentGroup.update { oldGroup ->
                        oldGroup?.copy(categories = newCategory)
                    }
                } else {
                    KLog.e(TAG, it)
                }
            })
        }
    }

    /**
     * 編輯 類別名稱
     */
    fun editCategory(category: Category, name: String) {
        KLog.i(TAG, "editCategory")
        val group = _currentGroup.value ?: return
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
                    _currentGroup.update { oldGroup ->
                        oldGroup?.copy(
                            categories = newCategory
                        )
                    }
                } else {
                    KLog.e(TAG, it)
                }
            })
        }
    }

    /**
     * 刪除 分類, 並將該分類下的頻道分配至 預設
     */
    fun deleteCategory(category: Category) {
        KLog.i(TAG, "deleteCategory")
        val group = _currentGroup.value ?: return
        viewModelScope.launch {
            channelUseCase.deleteCategory(categoryId = category.id.orEmpty()).fold({
            }, {
                if (it is EmptyBodyException) {
                    val targetChannels = category.channels ?: emptyList()
                    // 刪除分類
                    val newCategories = group.categories?.filter { groupCategory ->
                        groupCategory.id != category.id
                    }
                        // 將刪除分類下的頻道移至預設分類下
                        ?.map { groupCategory ->
                            if (groupCategory.isDefault == true) {
                                val currentChannels =
                                    groupCategory.channels?.toMutableList() ?: mutableListOf()
                                currentChannels.addAll(targetChannels)
                                groupCategory.copy(
                                    channels = currentChannels
                                )
                            } else {
                                groupCategory
                            }
                        }
                    _currentGroup.update { oldGroup ->
                        oldGroup?.copy(categories = newCategories)
                    }
                } else {
                    KLog.e(TAG, it)
                }
            })
        }
    }

    /**
     * 儲存 分類排序
     */
    fun updateCategories(categories: List<Category>) {
        val group = _currentGroup.value ?: return
        KLog.i(TAG, "updateCategories: $categories")
        viewModelScope.launch {
            orderUseCase.orderCategoryOrChannel(
                groupId = group.id.orEmpty(),
                category = categories
            ).fold({
            }, {
                if (it is EmptyBodyException) {
                    _currentGroup.update { oldGroup ->
                        oldGroup?.copy(categories = categories)
                    }
                } else {
                    KLog.e(TAG, it)
                }
            })
        }
    }

    /**
     * 收到新訊息 推播
     *
     * 檢查是否已經加入該社團並打開該社團
     * 確認該社團有此頻道, 並前往該頻道
     */
    fun receiveNewMessage(receiveNewMessage: TargetType.ReceiveMessage?) {
        KLog.i(TAG, "receiveNewMessage:$receiveNewMessage")
        receiveNewMessage?.let {
            viewModelScope.launch {
                val groupId = receiveNewMessage.groupId
                val channelId = receiveNewMessage.channelId
                val serialNumber = receiveNewMessage.serialNumber

                _myGroupList.value.firstOrNull { groupItem ->
                    groupItem.groupModel.id == groupId
                }?.also {
                    setCurrentGroup(it.groupModel)

                    it.groupModel.categories?.flatMap { category ->
                        category.channels.orEmpty()
                    }?.firstOrNull { channel ->
                        channel.id == channelId
                    }?.also { channel ->
                        _jumpToChannelDest.value = PushDataWrapper.ChannelMessage(
                            channel = channel,
                            serialNumber = serialNumber
                        )
                    }
                }
            }
        }
    }

    /**
     * 收到 新貼文 推播
     *
     * 檢查是否已經加入該社團並打開該社團
     * 取得指定文章資訊
     */
    fun receiveNewPost(receivePostMessage: TargetType.ReceivePostMessage?) {
        KLog.i(TAG, "receivePostMessage:$receivePostMessage")
        receivePostMessage?.let {
            viewModelScope.launch {
                val groupId = receivePostMessage.groupId
                val channelId = receivePostMessage.channelId
                val messageId = receivePostMessage.messageId

                _myGroupList.value.firstOrNull { groupItem ->
                    groupItem.groupModel.id == groupId
                }?.also {
                    setCurrentGroup(it.groupModel)

                    it.groupModel.categories?.flatMap { category ->
                        category.channels.orEmpty()
                    }?.firstOrNull { channel ->
                        channel.id == channelId
                    }?.also { channel ->

                        //取得指定文章資訊
                        chatRoomUseCase.getSingleMessage(
                            messageId = messageId,
                            messageServiceType = MessageServiceType.bulletinboard
                        ).onSuccess { chatMessage ->
                            _jumpToChannelDest.value = PushDataWrapper.ChannelPost(
                                channel = channel,
                                bulletinboardMessage = chatMessage.toBulletinboardMessage()
                            )
                        }.onFailure { err ->
                            KLog.e(TAG, err)
                        }
                    }
                }
            }
        }
    }

    fun finishJumpToChannelDest() {
        _jumpToChannelDest.value = null
    }
}