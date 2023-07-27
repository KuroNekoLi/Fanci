package com.cmoney.kolfanci.ui.screens.shared.member.viewmodel

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.AccessorParam
import com.cmoney.fanciapi.fanci.model.AccessorTypes
import com.cmoney.fanciapi.fanci.model.BanPeriodOption
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.kolfanci.extension.EmptyBodyException
import com.cmoney.kolfanci.extension.fromJsonTypeToken
import com.cmoney.kolfanci.model.usecase.BanUseCase
import com.cmoney.kolfanci.model.usecase.DynamicLinkUseCase
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.cmoney.kolfanci.ui.screens.group.setting.ban.viewmodel.BanUiModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.socks.library.KLog
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit

data class UiState(
    val groupMember: List<GroupMemberSelect>? = null,  //社團會員
    val kickMember: GroupMember? = null,     //踢除會員
    val showAddSuccessTip: Boolean = false,  //show 新增成功 toast
    val loading: Boolean = false,
    val selectedRole: List<FanciRole> = emptyList(), //選中的角色
    val selectedVipPlanModels: List<VipPlanModel> = emptyList(), // 選中的VIP方案
    val tabIndex: Int = 0
)

/**
 * 包裝會員,標註哪一個選中
 */
data class GroupMemberSelect(val groupMember: GroupMember, val isSelected: Boolean = false)

/**
 * 選擇的成員/角色 清單
 */
@Parcelize
data class SelectedModel(
    val selectedMember: List<GroupMember> = emptyList(),
    val selectedRole: List<FanciRole> = emptyList(),
    val selectedVipPlans: List<VipPlanModel> = emptyList()
) : Parcelable {

    fun toAccessorList(): List<AccessorParam> {
        val memberAccessor = AccessorParam(
            type = AccessorTypes.users,
            ids = selectedMember.map {
                it.id.orEmpty()
            }.filter { it.isNotEmpty() }
        )

        val roleAccessor = AccessorParam(
            type = AccessorTypes.role,
            ids = selectedRole.map {
                it.id.orEmpty()
            }.filter { it.isNotEmpty() }
        )

        val vipAccessor = AccessorParam(
            type = AccessorTypes.vipRole,
            ids = selectedVipPlans.map {
                it.id
            }.filter { it.isNotEmpty() }
        )

        return listOf(memberAccessor, roleAccessor, vipAccessor)
    }
}

class MemberViewModel(
    private val groupUseCase: GroupUseCase,
    private val banUseCase: BanUseCase,
    private val dynamicLinkUseCase: DynamicLinkUseCase
) : ViewModel() {

    private val TAG = MemberViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    //禁言中 info
    private val _banUiModel = MutableStateFlow<BanUiModel?>(null)
    val banUiModel = _banUiModel.asStateFlow()

    //選中的會員
    private val _selectedMember = MutableStateFlow(emptyList<GroupMember>())
    val selectedMember = _selectedMember.asStateFlow()

    //群組會員清單
    val orgGroupMemberList = mutableListOf<GroupMemberSelect>()

    //新增的會員
    private val addGroupMemberQueue = mutableListOf<GroupMember>()

    //排除顯示的會員清單
    private val excludeMember: MutableList<GroupMember> = mutableListOf()

    //目前輸入的 keyword
    private var currentKeyword: String = ""

    //分享文案
    private val _shareText = MutableStateFlow("")
    val shareText = _shareText.asStateFlow()

    //管理人員
    private val _managerMember = MutableStateFlow<GroupMember?>(null)
    val managerMember = _managerMember.asStateFlow()

    private fun showLoading() {
        uiState = uiState.copy(
            loading = true
        )
    }

    private fun dismissLoading() {
        uiState = uiState.copy(
            loading = false
        )
    }

    /**
     * 解除 禁言
     */
    fun liftBanUser(userId: String, groupId: String) {
        KLog.i(TAG, "liftBanUser:$userId")
        viewModelScope.launch {
            banUseCase.liftBanUser(
                groupId = groupId,
                userIds = listOf(userId)
            ).fold({
                _banUiModel.value = null
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 禁言 使用者, 成功之後再次抓取 該 user 禁言 info
     * @param groupId 社團id
     * @param userId 要ban 的 user
     * @param banPeriodOption 天數
     */
    fun banUser(groupId: String, userId: String, banPeriodOption: BanPeriodOption) {
        viewModelScope.launch {
            banUseCase.banUser(
                userId = userId,
                groupId = groupId,
                banPeriodOption = banPeriodOption
            ).fold({
            }, {
                if (it is EmptyBodyException) {
                    fetchUserBanInfo(
                        groupId = groupId,
                        userId = userId
                    )
                } else {
                    KLog.e(TAG, it)
                }
            })
        }
    }

    /**
     * 取得 使用者 禁言 info
     */
    fun fetchUserBanInfo(groupId: String, userId: String) {
        KLog.i(TAG, "fetchUserBanInfo:$groupId, $userId")
        viewModelScope.launch {
            banUseCase.fetchBanInfo(
                groupId = groupId,
                userId = userId
            ).fold({ userBanInformation ->
                val date = Date(
                    userBanInformation.startDateTime?.times(1000) ?: System.currentTimeMillis()
                )
                val startDay = SimpleDateFormat("yyyy/MM/dd").format(date)

                val oneDaySecond = TimeUnit.DAYS.toSeconds(1)
                var duration = 0
                userBanInformation.panaltySeconds?.let { second ->
                    duration = (second / oneDaySecond).toInt()
                }

                val durationStr = if (duration > 365) {
                    "永久"
                } else {
                    "%d日".format(duration)
                }

                _banUiModel.value = BanUiModel(
                    user = userBanInformation.user,
                    startDay = startDay,
                    duration = durationStr
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }


    /**
     * 編輯 會員之後 刷新
     */
    fun editGroupMember(groupMember: GroupMember) {
        val memberList = uiState.groupMember.orEmpty().toMutableList()
        val newList = memberList.map {
            if (it.groupMember.id == groupMember.id) {
                GroupMemberSelect(
                    groupMember = groupMember
                )
            } else {
                it
            }
        }
        uiState = uiState.copy(
            groupMember = newList
        )
    }

    /**
     * 獲取群組會員清單
     */
    fun fetchGroupMember(
        groupId: String,
        skip: Int = 0,
        excludeMember: List<GroupMember> = emptyList(),
        searchKeyword: String? = null
    ) {
        KLog.i(TAG, "fetchGroupMember:$groupId")

        this.excludeMember.addAll(excludeMember)

        viewModelScope.launch {
            showLoading()
            groupUseCase.getGroupMember(groupId = groupId, skipCount = skip, search = searchKeyword?.trim())
                .fold({
                    val responseMembers = it.items.orEmpty()
                    if (responseMembers.isNotEmpty()) {
                        val wrapMember = responseMembers.map { member ->
                            GroupMemberSelect(
                                groupMember = member
                            )
                        }

                        val orgMemberList = uiState.groupMember.orEmpty().toMutableList()
                        orgMemberList.addAll(wrapMember)

                        val filterMember = orgMemberList.filter {
                            excludeMember.find { exclude ->
                                exclude.id == it.groupMember.id
                            } == null
                        }

                        orgGroupMemberList.addAll(filterMember)
                        val distinct = orgGroupMemberList.distinct()
                        orgGroupMemberList.clear()
                        orgGroupMemberList.addAll(distinct)

                        uiState = uiState.copy(
                            groupMember = orgGroupMemberList
                        )
                    } else {
                        uiState = uiState.copy(
                            groupMember = emptyList()
                        )
                    }
                    dismissLoading()
                }, {
                    dismissLoading()
                    KLog.e(TAG, it)
                })
        }
    }

    /**
     * 讀取下一頁 會員資料
     */
    fun onLoadMoreGroupMember(groupId: String) {
        KLog.i(TAG, "onLoadMoreGroupMember")
        val skip = uiState.groupMember.orEmpty().size
        if (skip != 0) {
            fetchGroupMember(groupId, skip, searchKeyword = currentKeyword)
        }
    }

    /**
     * 點擊會員
     */
    fun onMemberClick(groupMemberSelect: GroupMemberSelect) {
        KLog.i(TAG, "onMemberClick:$groupMemberSelect")
        val memberList = uiState.groupMember?.map {
            if (it.groupMember.id == groupMemberSelect.groupMember.id) {
                groupMemberSelect.copy(
                    isSelected = !groupMemberSelect.isSelected
                )
            } else {
                it
            }
        }

        uiState = uiState.copy(
            groupMember = memberList
        )
    }

    /**
     * 取得 已選擇的會員清單
     */
    fun fetchSelectedMember(): String {
        val gson = Gson()
        return gson.toJson(addGroupMemberQueue)
    }

    /**
     * 給予成員新的角色清單
     *
     * @param groupId 社團 id
     * @param userId 使用者 id
     * @param oldFanciRole 原本擁有的角色清單
     * @param newFanciRole 新的角色清單
     */
    fun assignMemberRole(
        groupId: String,
        userId: String,
        oldFanciRole: List<FanciRole>,
        newFanciRole: List<FanciRole>,
        onDone: () -> Unit
    ) {
        KLog.i(TAG, "assignMemberRole")
        viewModelScope.launch {
            val task = listOf(
                async {
                    val addRole = newFanciRole.filter { !oldFanciRole.contains(it) }
                    if (addRole.isNotEmpty()) {
                        groupUseCase.addRoleToMember(
                            groupId = groupId,
                            userId = userId,
                            roleIds = addRole.map { it.id.orEmpty() }
                        ).getOrNull()
                    }
                },
                async {
                    val deleteRole = oldFanciRole.filter { !newFanciRole.contains(it) }
                    if (deleteRole.isNotEmpty()) {
                        groupUseCase.removeRoleFromUser(
                            groupId = groupId,
                            userId = userId,
                            roleIds = deleteRole.map { it.id.orEmpty() }
                        ).getOrNull()
                    }
                }
            )

            task.awaitAll()
            onDone.invoke()
        }
    }

    /**
     * 剔除人員
     * @param groupId 社團id
     * @param groupMember 會員
     */
    fun kickOutMember(groupId: String, groupMember: GroupMember) {
        KLog.i(TAG, "kickOutMember:$groupMember")
        viewModelScope.launch {
            groupUseCase.kickOutMember(
                groupId = groupId,
                userId = groupMember.id.orEmpty()
            ).fold({
            }, {
                if (it is EmptyBodyException) {
                    uiState = uiState.copy(
                        kickMember = groupMember
                    )

                } else {
                    KLog.e(TAG, it)
                }
            })
        }
    }

    /**
     * 踢除 成員
     */
    fun removeMember(groupMember: GroupMember) {
        KLog.i(TAG, "removeMember$groupMember")
        viewModelScope.launch {
            val newGroupList = uiState.groupMember?.filter {
                it.groupMember.id != groupMember.id
            }
            uiState = uiState.copy(
                groupMember = newGroupList
            )
        }
    }

    private var timer: Timer? = null

    /**
     * 搜尋成員
     */
    fun onSearchMember(
        groupId: String,
        keyword: String
    ) {
        KLog.i(TAG, "onSearchMember:$keyword")

        currentKeyword = keyword

        timer?.cancel()

        timer = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    //searching
                    KLog.i(TAG, "searching:$keyword")
                    if (keyword.isEmpty()) {
                        uiState = uiState.copy(
                            groupMember = orgGroupMemberList
                        )
                    } else {
                        fetchGroupMember(
                            groupId = groupId,
                            excludeMember = excludeMember,
                            searchKeyword = keyword
                        )
                    }

                    //Local Searching, server api not ready use.
//                    //return all
//                    uiState = if (keyword.isEmpty()) {
//                        uiState.copy(
//                            groupMember = orgGroupMemberList
//                        )
//                    } else {
//                        uiState.copy(
//                            groupMember = orgGroupMemberList.filter {
//                                it.groupMember.name?.startsWith(keyword) == true
//                            }
//                        )
//                    }
                }
            }, 400)
        }
    }

    /**
     * 點擊 新增 會員, 將選中的會員暫存, 並從畫面上移除
     */
    fun onAddSelectedMember() {
        KLog.i(TAG, "onAddSelectedMember")
        val selectedMember = uiState.groupMember?.filter {
            it.isSelected
        }?.map {
            it.groupMember
        }.orEmpty()

        if (selectedMember.isNotEmpty()) {
            addGroupMemberQueue.addAll(selectedMember)
            val distinctList = addGroupMemberQueue.distinct()
            addGroupMemberQueue.clear()
            addGroupMemberQueue.addAll(distinctList)
        }
    }

    /**
     * 取消 toast
     */
    fun dismissAddSuccessTip() {
        uiState = uiState.copy(
            showAddSuccessTip = false
        )
    }

    /**
     * 將選中的 會員加入清單
     */
    fun addSelectedMember(member: String) {
        val gson = Gson()
        val listType: Type =
            object : TypeToken<List<GroupMember>>() {}.type
        val responseMemberList = gson.fromJson(member, listType) as List<GroupMember>

        if (responseMemberList.isNotEmpty()) {
            val newList = _selectedMember.value.toMutableList()
            newList.addAll(responseMemberList)
            _selectedMember.value = newList.distinct()
        }
    }

    /**
     * 增加 角色
     */
    fun addSelectedRole(roleListStr: String) {
        KLog.i(TAG, "addSelectedRole:$roleListStr")
        val gson = Gson()
        val roleList = gson.fromJsonTypeToken<List<FanciRole>>(roleListStr)
        val orgRoleList = uiState.selectedRole
        val unionList = roleList.union(orgRoleList).toMutableList()
        uiState = uiState.copy(
            selectedRole = unionList
        )
    }

    fun initialUiStateFromModel(selectedModel: SelectedModel) {
        KLog.i(TAG, "initialUiStateFromModel")
        uiState = uiState.copy(
            selectedRole = selectedModel.selectedRole.ifEmpty {
                uiState.selectedRole
            },
            selectedVipPlanModels = selectedModel.selectedVipPlans.ifEmpty {
                uiState.selectedVipPlanModels
            }
        )
        if (selectedModel.selectedMember.isNotEmpty()) {
            _selectedMember.value = selectedModel.selectedMember
        }
    }

    /**
     * 點擊 tab
     */
    fun onTabClick(pos: Int) {
        KLog.i(TAG, "onTabClick:$pos")
        uiState = uiState.copy(
            tabIndex = pos
        )
    }

    /**
     * 將選擇的 member remove
     */
    fun removeSelectedMember(groupMember: GroupMember) {
        KLog.i(TAG, "removeSelectedMember:$groupMember")
        val filterList = _selectedMember.value.filter {
            it.id != groupMember.id
        }
        _selectedMember.value = filterList
    }

    /**
     * 將選擇的 role remove
     */
    fun removeSelectedRole(fanciRole: FanciRole) {
        KLog.i(TAG, "removeSelectedRole:$fanciRole")
        val filterList = uiState.selectedRole.filter {
            it.id != fanciRole.id
        }
        uiState = uiState.copy(
            selectedRole = filterList
        )
    }

    /**
     * 抓取 所選擇的成員以及角色
     */
    fun fetchSelected(): SelectedModel {
        return SelectedModel(
            selectedMember = _selectedMember.value,
            selectedRole = uiState.selectedRole,
            selectedVipPlans = uiState.selectedVipPlanModels
        )
    }

    /**
     * 點擊 邀請按鈕
     */
    fun onInviteClick(group: Group) {
        KLog.i(TAG, "onInviteClick:$group")
        viewModelScope.launch {
            showLoading()
            dynamicLinkUseCase.createInviteGroupLink(
                groupId = group.id.orEmpty()
            )?.let { link ->
                KLog.i(TAG, "created link:$link")
                _shareText.value = "您已被邀請加入 「${group.name}」!\n請點選以下連結加入社群!\n$link"
            }
            dismissLoading()
        }
    }

    fun resetShareText() {
        _shareText.value = ""
    }

    /**
     * 設置要編輯的會員info
     */
    fun setManageGroupMember(groupMember: GroupMember) {
        KLog.i(TAG, "setManageGroupMember:$groupMember")
        _managerMember.value = groupMember
    }

    /**
     * 增加角色 - 編輯人員角色
     */
    fun onAddRole(
        groupId: String,
        userId: String,
        addRole: List<FanciRole>
    ) {
        KLog.i(TAG, "onAddRole")

        viewModelScope.launch {
            if (addRole.isNotEmpty()) {
                groupUseCase.addRoleToMember(
                    groupId = groupId,
                    userId = userId,
                    roleIds = addRole.map { it.id.orEmpty() }
                ).getOrNull()
            }

            _managerMember.value?.let { member ->
                val newMemberList = member.roleInfos?.toMutableList()
                newMemberList?.addAll(addRole)

                _managerMember.value = member.copy(
                    roleInfos = newMemberList?.distinctBy {
                        it.id
                    }
                )
            }
        }
    }

    /**
     * 移除 - 編輯人員角色
     */
    fun onRemoveRole(
        groupId: String,
        userId: String,
        fanciRole: FanciRole
    ) {
        KLog.i(TAG, "onRemoveRole:$fanciRole")

        viewModelScope.launch {
            groupUseCase.removeRoleFromUser(
                groupId = groupId,
                userId = userId,
                roleIds = listOf(fanciRole.id.orEmpty())
            ).getOrNull()

            _managerMember.value?.let { member ->
                _managerMember.value = member.copy(
                    roleInfos = member.roleInfos?.filter { role ->
                        role != fanciRole
                    }
                )
            }
        }
    }

    fun removeSelectedVipPlan(model: VipPlanModel) {
        uiState = uiState.copy(
            selectedVipPlanModels = uiState.selectedVipPlanModels.filterNot { vipPlanModel ->
                vipPlanModel.name == model.name
            }
        )
    }

    fun addSelectedVipPlanModel(model: VipPlanModel) {
        KLog.i(TAG, "addSelectedVipPlanModel:$model")
        uiState = uiState.copy(
            selectedVipPlanModels = uiState.selectedVipPlanModels.plus(model)
        )
    }
}