package com.cmoney.fanci.ui.screens.group.setting.member.role.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.extension.EmptyBodyException
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

    private var isEdited = false    //是否已經初始化編輯資料
    private var editFanciRole: FanciRole? = null  //要編輯的角色
    private var editMemberList: List<GroupMember> = emptyList() //編輯模式下原本的成員清單

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

        val newList = uiState.memberList.toMutableList()
        newList.addAll(responseMemberList)
        newList.distinctBy {
            it.id
        }

        uiState = uiState.copy(
            memberList = newList
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
     * 確定 新增角色 or 編輯角色
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

            //編輯
            if (isEdited && editFanciRole != null) {
                //re-assign data
                editFanciRole = editFanciRole?.copy(
                    name = name,
                    color = uiState.roleColor.name,
                    permissionIds = permissionIds,
                    userCount = uiState.memberList.size.toLong()
                )

                groupUseCase.editGroupRole(
                    groupId = group.id.orEmpty(),
                    roleId = editFanciRole!!.id.orEmpty(),
                    name = name,
                    permissionIds = permissionIds,
                    colorCode = uiState.roleColor
                ).fold({
                }, {
                    KLog.e(TAG, it)
                    if (it is EmptyBodyException) {
                        assignMemberRole(group.id.orEmpty(), editFanciRole!!)

                    } else {
                        // TODO: 判斷server error type
                        uiState = uiState.copy(
                            addRoleError = "已有相同名稱的腳色"
                        )
                    }
                })
            }
            //新增
            else {
                groupUseCase.addGroupRole(
                    groupId = group.id.orEmpty(),
                    name = name,
                    permissionIds = permissionIds,
                    colorCode = uiState.roleColor
                ).fold({
                    KLog.i(TAG, it)
                    assignMemberRole(group.id.orEmpty(), it)
                }, {
                    KLog.e(TAG, it)
                })
            }
        }
    }

    /**
     * 將角色分配給選擇的人員, 或是將人員移除
     * @param groupId 群組Id
     * @param fanciRole 角色 model
     */
    private fun assignMemberRole(groupId: String, fanciRole: FanciRole) {
        KLog.i(TAG, "assignMemberRole:$groupId , $fanciRole")
        viewModelScope.launch {
            if (uiState.memberList.isNotEmpty()) {

                //新增人員至角色
                val addMemberList = uiState.memberList.filter { !editMemberList.contains(it) }
                if (addMemberList.isNotEmpty()) {
                    groupUseCase.addMemberToRole(
                        groupId = groupId,
                        roleId = fanciRole.id.orEmpty(),
                        memberList = addMemberList.map {
                            it.id.orEmpty()
                        }
                    ).fold({
                        KLog.i(TAG, "assignMemberRole complete")
                        uiState = uiState.copy(
                            addFanciRole = fanciRole.copy(userCount = uiState.memberList.size.toLong()),
                            addRoleError = "",
                            addRoleComplete = true
                        )
                    }, {
                        KLog.e(TAG, it)
                    })
                }

                //要移除的人員
                val removeMemberList = editMemberList.filter { !uiState.memberList.contains(it) }
                if (removeMemberList.isNotEmpty()) {
                    groupUseCase.removeUserRole(
                        groupId = groupId,
                        roleId = fanciRole.id.orEmpty(),
                        userId = removeMemberList.map {
                            it.id.orEmpty()
                        }
                    ).fold({

                    }, {
                        KLog.e(TAG, it)
                        if (it is EmptyBodyException) {
                            uiState = uiState.copy(
                                addFanciRole = fanciRole.copy(userCount = uiState.memberList.size.toLong()),
                                addRoleError = "",
                                addRoleComplete = true
                            )
                        }
                    })
                }
            } else {
                //將原本清單的人員都移除
                if (editMemberList.isNotEmpty()) {
                    groupUseCase.removeUserRole(
                        groupId = groupId,
                        roleId = fanciRole.id.orEmpty(),
                        userId = editMemberList.map {
                            it.id.orEmpty()
                        }
                    ).fold({

                    }, {
                        KLog.e(TAG, it)
                    })
                }

                uiState = uiState.copy(
                    addFanciRole = fanciRole,
                    addRoleError = "",
                    addRoleComplete = true
                )
            }
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
    fun addMemberRole(fanciRole: FanciRole) {
        KLog.i(TAG, "addMemberRole:$fanciRole")
        val roleList = uiState.fanciRole?.toMutableList()
        roleList?.let {
            //find out exists or not
            val existsPos = it.indexOfFirst { role ->
                role.id == fanciRole.id
            }
            if (existsPos == -1) {
                roleList.add(fanciRole)
            } else {
                roleList.set(existsPos, fanciRole)
            }
        }

        uiState = uiState.copy(
            fanciRole = roleList
        )
    }

    /**
     *  編輯模式 設定
     *  @param fanciRole 要編輯的角色
     *  @param roleColors 角色色卡清單
     */
    fun setRoleEdit(groupId: String, fanciRole: FanciRole, roleColors: List<Color>) {
        KLog.i(TAG, "setRoleEdit:$fanciRole, isEdited:$isEdited")
        if (!isEdited) {
            isEdited = true
            editFanciRole = fanciRole

            viewModelScope.launch {
                //Color
                val roleColor = roleColors.first { color ->
                    color.name == fanciRole.color
                }

                //Permission
                val checkedPermission = fanciRole.permissionIds.orEmpty()
                val permissionList = groupUseCase.fetchPermissionList().getOrNull()
                val flatMapPermission = permissionList?.flatMap {
                    it.permissions.orEmpty()
                }
                val permissionCheckedMap = flatMapPermission?.associate {
                    if (checkedPermission.contains(it.id)) {
                        Pair(it.id.orEmpty(), true)
                    } else {
                        Pair(it.id.orEmpty(), false)
                    }
                }

                //Member List
                editMemberList = groupUseCase.fetchRoleMemberList(
                    groupId = groupId,
                    roleId = fanciRole.id.orEmpty()
                ).getOrNull().orEmpty().map { user ->
                    GroupMember(
                        id = user.id,
                        name = user.name,
                        thumbNail = user.thumbNail,
                        serialNumber = user.serialNumber
                    )
                }

                uiState = uiState.copy(
                    roleName = fanciRole.name.orEmpty(),
                    roleColor = roleColor,
                    permissionList = permissionList,
                    permissionSelected = permissionCheckedMap.orEmpty(),
                    memberList = editMemberList
                )
            }
        }
    }
}