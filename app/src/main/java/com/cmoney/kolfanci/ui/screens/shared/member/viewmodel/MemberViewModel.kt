package com.cmoney.kolfanci.ui.screens.shared.member.viewmodel

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.*
import com.cmoney.kolfanci.extension.EmptyBodyException
import com.cmoney.kolfanci.extension.fromJsonTypeToken
import com.cmoney.kolfanci.model.usecase.BanUseCase
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.cmoney.kolfanci.ui.screens.group.setting.ban.viewmodel.BanUiModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.socks.library.KLog
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class UiState(
    val groupMember: List<GroupMemberSelect>? = null,  //社團會員
    val kickMember: GroupMember? = null,     //踢除會員
    val banUiModel: BanUiModel? = null,      //禁言中 info
    val showAddSuccessTip: Boolean = false,  //show 新增成功 toast
    val loading: Boolean = true,
    var selectedMember: List<GroupMember> = emptyList(), //選中的會員
    var selectedRole: List<FanciRole> = emptyList(), //選中的角色
    val tabIndex: Int = 0
)

data class GroupMemberSelect(val groupMember: GroupMember, val isSelected: Boolean = false)

/**
 * 選擇的成員/角色 清單
 */
@Parcelize
data class SelectedModel(
    val selectedMember: List<GroupMember> = emptyList(),
    val selectedRole: List<FanciRole> = emptyList()
) : Parcelable {

    fun toAccessorList(): List<AccessorParam> {
        val memberAccessor = AccessorParam(
            type = AccessorTypes.users,
            ids = selectedMember.map {
                it.id.orEmpty()
            }
        )

        val roleAccessor = AccessorParam(
            type = AccessorTypes.role,
            ids = selectedRole.map {
                it.id.orEmpty()
            }
        )

        return listOf(memberAccessor, roleAccessor)
    }
}

class MemberViewModel(private val groupUseCase: GroupUseCase, private val banUseCase: BanUseCase) :
    ViewModel() {
    private val TAG = MemberViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    //群組會員清單
    val orgGroupMemberList = mutableListOf<GroupMemberSelect>()

    //新增的會員
    private val addGroupMemberQueue = mutableListOf<GroupMember>()

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
                uiState = uiState.copy(
                    banUiModel = null
                )
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

                uiState = uiState.copy(
                    banUiModel = BanUiModel(
                        user = userBanInformation.user,
                        startDay = startDay,
                        duration = durationStr
                    )
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
        excludeMember: List<GroupMember> = emptyList()
    ) {
        KLog.i(TAG, "fetchGroupMember:$groupId")
        viewModelScope.launch {
            showLoading()
            groupUseCase.getGroupMember(groupId = groupId, skipCount = skip).fold({
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

                    orgGroupMemberList.clear()
                    orgGroupMemberList.addAll(filterMember)

                    uiState = uiState.copy(
                        groupMember = filterMember
                    )
                }
                dismissLoading()
            }, {
                dismissLoading()
                KLog.e(TAG, it)
                uiState = uiState.copy(
                    groupMember = emptyList()
                )
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
            fetchGroupMember(groupId, skip)
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
        oldFanciRole: List<FanciRole>, newFanciRole: List<FanciRole>
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
    fun onSearchMember(keyword: String) {
        KLog.i(TAG, "onSearchMember:$keyword")
        timer?.cancel()

        timer = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    //searching
                    KLog.i(TAG, "searching:$keyword")
                    //return all
                    uiState = if (keyword.isEmpty()) {
                        uiState.copy(
                            groupMember = orgGroupMemberList
                        )
                    } else {
                        uiState.copy(
                            groupMember = orgGroupMemberList.filter {
                                it.groupMember.name?.startsWith(keyword) == true
                            }
                        )
                    }
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

            val orgMemberList = uiState.groupMember.orEmpty()
            val filterMember = orgMemberList.filter {
                addGroupMemberQueue.find { exclude ->
                    exclude.id == it.groupMember.id
                } == null
            }

            uiState = uiState.copy(
                groupMember = filterMember,
                showAddSuccessTip = true
            )
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
            val newList = uiState.selectedMember.toMutableList()
            newList.addAll(responseMemberList)
            uiState = uiState.copy(
                selectedMember = newList.distinct()
            )
        }
    }

    fun addSelectedMember(selectedMember: List<GroupMember>) {
        if (selectedMember.isNotEmpty()) {
            uiState.selectedMember = selectedMember
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

    fun addSelectedRole(selectedRole: List<FanciRole>) {
        if (selectedRole.isNotEmpty()) {
            uiState.selectedRole = selectedRole
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
        val filterList = uiState.selectedMember.filter {
            it.id != groupMember.id
        }
        uiState = uiState.copy(
            selectedMember = filterList
        )
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
            selectedMember = uiState.selectedMember,
            selectedRole = uiState.selectedRole
        )
    }
}