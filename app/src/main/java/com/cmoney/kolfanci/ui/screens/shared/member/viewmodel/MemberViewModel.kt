package com.cmoney.kolfanci.ui.screens.shared.member.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.kolfanci.extension.EmptyBodyException
import com.cmoney.kolfanci.model.usecase.BanUseCase
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.cmoney.kolfanci.ui.screens.group.setting.ban.viewmodel.BanUiModel
import com.cmoney.fanciapi.fanci.model.BanPeriodOption
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.google.gson.Gson
import com.socks.library.KLog
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class UiState(
    val groupMember: List<GroupMemberSelect>? = null,  //社團會員
    val kickMember: GroupMember? = null,     //踢除會員
    val banUiModel: BanUiModel? = null      //禁言中 info
)

data class GroupMemberSelect(val groupMember: GroupMember, val isSelected: Boolean = false)

class MemberViewModel(private val groupUseCase: GroupUseCase, private val banUseCase: BanUseCase) :
    ViewModel() {
    private val TAG = MemberViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

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

                uiState = uiState.copy(
                    banUiModel = BanUiModel(
                        user = userBanInformation.user,
                        startDay = startDay,
                        duration = "%d日".format(duration)
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
    fun fetchGroupMember(groupId: String, skip: Int = 0) {
        KLog.i(TAG, "fetchGroupMember:$groupId")
        viewModelScope.launch {
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

                    uiState = uiState.copy(
                        groupMember = orgMemberList
                    )
                }
            }, {
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
        val list = uiState.groupMember?.filter {
            it.isSelected
        }?.map {
            it.groupMember
        }.orEmpty()
        return gson.toJson(list)
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
}