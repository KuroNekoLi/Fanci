package com.cmoney.fanci.ui.screens.group.setting.role.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanciapi.fanci.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.socks.library.KLog
import kotlinx.coroutines.launch
import java.lang.reflect.Type

data class UiState(
    val fanciRole: List<FanciRole>? = null,  //角色清單
    val permissionList: List<PermissionCategory>? = null,    //權限清單
    val permissionSelected: Map<String, Boolean> = emptyMap(),   //勾選權限
    val tabSelected: Int = 0,
    val memberList: List<GroupMember> = emptyList(), //assign 成員
    val addRoleComplete: Boolean = false,   //新增角色 完成
    val addRoleError: String = "",          //新增角色 錯誤
    val roleName: String = "",              //角色名稱
    val roleColor: Color = Color(),         //角色顏色
    val addFanciRole: FanciRole? = null     //新增角色
)

class RoleManageViewModel(
    private val groupUseCase: GroupUseCase
) : ViewModel() {
    private val TAG = RoleManageViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    /**
     * 取得 角色清單
     */
    fun fetchRoleList(groupId: String) {
        viewModelScope.launch {
            groupUseCase.fetchGroupRole(groupId).fold({
                uiState = uiState.copy(
                    fanciRole = it
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 取得 權限清單
     */
    fun fetchPermissionList() {
        KLog.i(TAG, "fetchPermissionList")
        viewModelScope.launch {
            groupUseCase.fetchPermissionList().fold({ permissionList ->
                val flatMapPermission = permissionList.flatMap {
                    it.permissions.orEmpty()
                }
                val permissionCheckedMap = flatMapPermission.associate {
                    Pair(it.id.orEmpty(), false)
                }

                uiState = uiState.copy(
                    permissionList = permissionList,
                    permissionSelected = permissionCheckedMap
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 勾選 權限
     */
    fun onPermissionSelected(permissionId: String, isSelected: Boolean) {
        if (uiState.permissionSelected.containsKey(permissionId)) {
            val newMap = uiState.permissionSelected.toMutableMap()
            newMap[permissionId] = isSelected
            uiState = uiState.copy(
                permissionSelected = newMap
            )
        }
    }

    fun onTabSelected(position: Int) {
        uiState = uiState.copy(tabSelected = position)
    }

    /**
     * 選擇assign的 成員
     */
    fun addMember(memberStr: String) {
        val gson = Gson()
        val listType: Type =
            object : TypeToken<List<GroupMember>>() {}.type
        val responseMemberList = gson.fromJson(memberStr, listType) as List<GroupMember>
        uiState = uiState.copy(
            memberList = responseMemberList
        )
    }

    /**
     * 移除成員
     */
    fun onMemberRemove(groupMember: GroupMember) {
        val memberList = uiState.memberList.toMutableList()
        memberList.remove(groupMember)
        uiState = uiState.copy(
            memberList = memberList
        )
    }

    /**
     * 設定角色樣式
     */
    fun setRoleStyle(name: String, color: Color) {
        KLog.i(TAG, "setRoleStyle:$name, $color")
        uiState = uiState.copy(
            roleName = name,
            roleColor = color
        )
    }

    /**
     * 確定 新增角色
     */
    fun onConfirmAddRole(group: Group) {
        KLog.i(TAG, "onConfirmAddRole")
        viewModelScope.launch {
            val name = uiState.roleName
            if (name.isEmpty()) {
                uiState = uiState.copy(
                    addRoleError = "請輸入名稱"
                )
                return@launch
            }
            val permissionIds = uiState.permissionSelected.toList().filter {
                it.second
            }.map {
                it.first
            }

            groupUseCase.addGroupRole(
                groupId = group.id.orEmpty(),
                name = name,
                permissionIds = permissionIds,
                colorCode = uiState.roleColor
            ).fold({
                KLog.i(TAG, it)
                uiState = uiState.copy(
                    addFanciRole = it,
                    addRoleError = "",
                    addRoleComplete = true
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 呈現錯誤 訊息完畢後
     */
    fun errorShowDone() {
        uiState = uiState.copy(
            addRoleError = ""
        )
    }

    /**
     * 增加會員至清單
     */
    fun addMemberRole(role: FanciRole) {
        KLog.i(TAG, "addMemberRole:$role")
        val roleList = uiState.fanciRole?.toMutableList()
        roleList?.add(role)
        uiState = uiState.copy(
            fanciRole = roleList
        )
    }
}