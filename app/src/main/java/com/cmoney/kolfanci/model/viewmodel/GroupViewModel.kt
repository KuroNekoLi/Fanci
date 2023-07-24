package com.cmoney.kolfanci.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.ColorTheme
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.extension.EmptyBodyException
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.cmoney.kolfanci.model.usecase.PermissionUseCase
import com.cmoney.kolfanci.model.usecase.ThemeUseCase
import com.cmoney.kolfanci.ui.screens.follow.model.GroupItem
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.avatar.ImageChangeData
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.theme.model.GroupTheme
import com.cmoney.kolfanci.ui.theme.DefaultThemeColor
import com.cmoney.xlogin.XLoginHelper
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ThemeSetting {
    object Default : ThemeSetting()
    object Coffee : ThemeSetting()
}

/**
 * 社團相關設定
 */
class GroupViewModel(
    private val themeUseCase: ThemeUseCase,
    private val groupUseCase: GroupUseCase,
    private val permissionUseCase: PermissionUseCase
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
                            fetchAllGroupList()
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
}