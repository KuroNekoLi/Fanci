package com.cmoney.kolfanci.ui.screens.group.setting.member.role.viewmodel

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.Color
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.PermissionCategory
import com.cmoney.kolfanci.extension.EmptyBodyException
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.cmoney.kolfanci.model.usecase.OrderUseCase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import retrofit2.HttpException
import java.lang.reflect.Type

data class UiState(
    val fanciRole: List<FanciRole>? = null,  //角色清單
    val permissionList: List<PermissionCategory>? = null,    //權限清單
    val permissionSelected: Map<String, Boolean> = emptyMap(),   //勾選權限
    val tabSelected: Int = 0,
    val memberList: List<GroupMember> = emptyList(), //assign 成員
    val addRoleComplete: Boolean = false,   //新增角色 完成
    val addRoleError: Pair<String, String>? = null,          //新增角色 錯誤
    val roleName: String = "",              //角色名稱
    val roleColor: Color = Color(),         //角色顏色
    val fanciRoleCallback: FanciRoleCallback? = null, // 新增 or 刪除 角色
    val loading: Boolean = true
)

@Parcelize
data class FanciRoleCallback(
    val isAdd: Boolean = true,
    val fanciRole: FanciRole
) : Parcelable

class RoleManageViewModel(
    private val groupUseCase: GroupUseCase,
    private val orderUseCase: OrderUseCase
) : ViewModel() {
    private val TAG = RoleManageViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private var isEdited = false    //是否已經初始化編輯資料
    private var editFanciRole: FanciRole? = null  //要編輯的角色
    private var editMemberList: List<GroupMember> = emptyList() //編輯模式下原本的成員清單

    private fun showLoading() {
        _loading.value = true

        uiState = uiState.copy(
            loading = true
        )
    }

    private fun dismissLoading() {
        _loading.value = false

        uiState = uiState.copy(
            loading = false
        )
    }


    /**
     * 取得 角色清單
     */
    fun fetchRoleList(groupId: String) {
        viewModelScope.launch {
            showLoading()
            groupUseCase.fetchGroupRole(groupId).fold({
                uiState = uiState.copy(
                    fanciRole = it
                )
                dismissLoading()
            }, {
                KLog.e(TAG, it)
                dismissLoading()
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
        KLog.i(TAG, "addMember:$memberStr")
        val gson = Gson()
        val listType: Type =
            object : TypeToken<List<GroupMember>>() {}.type
        val responseMemberList = gson.fromJson(memberStr, listType) as List<GroupMember>

        val newList = uiState.memberList.toMutableList()
        newList.addAll(responseMemberList)

        uiState = uiState.copy(
            memberList = newList.distinctBy {
                it.id
            }
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
     *  設定角色 名稱
     */
    fun setRoleName(name: String) {
        KLog.i(TAG, "setRoleName: $name")
        uiState = uiState.copy(
            roleName = name
        )
    }

    /**
     * 確定 新增角色 or 編輯角色
     */
    fun onConfirmAddRole(group: Group) {
        KLog.i(TAG, "onConfirmAddRole")
        viewModelScope.launch {
            showLoading()
            val name = uiState.roleName
            if (name.isEmpty()) {
                uiState = uiState.copy(
                    addRoleError = Pair("角色名稱空白", "角色名稱不可以是空白的唷！")
                )
                dismissLoading()
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
                    dismissLoading()
                    KLog.e(TAG, it)
                    if (it is EmptyBodyException) {
                        assignMemberRole(group.id.orEmpty(), editFanciRole!!)
                    } else if (it is HttpException) {
                        //Conflict error
                        if (it.code() == 409) {
                            uiState = uiState.copy(
                                addRoleError = Pair(
                                    "角色名稱重複", "名稱「%s」與現有角色重複\n".format(name) +
                                            "請修改後再次儲存！"
                                )
                            )
                        }
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
                    dismissLoading()
                    assignMemberRole(group.id.orEmpty(), it)
                }, {
                    KLog.e(TAG, it)
                    dismissLoading()
                    //Conflict error
                    if ((it as HttpException).code() == 409) {
                        uiState = uiState.copy(
                            addRoleError = Pair(
                                "角色名稱重複", "名稱「%s」與現有角色重複\n".format(name) +
                                        "請修改後再次儲存！"
                            )
                        )
                    }
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
        var editFanciRole = fanciRole.copy()
        viewModelScope.launch {
            if (uiState.memberList.isNotEmpty()) {

                //新增人員至角色
                val addMemberList = uiState.memberList.filter { !editMemberList.contains(it) }
                if (addMemberList.isNotEmpty()) {
                    groupUseCase.addMemberToRole(
                        groupId = groupId,
                        roleId = editFanciRole.id.orEmpty(),
                        memberList = addMemberList.map {
                            it.id.orEmpty()
                        }
                    ).fold({
                        KLog.i(TAG, "assignMemberRole complete")
                        editFanciRole = fanciRole.copy(userCount = uiState.memberList.size.toLong())
                        uiState = uiState.copy(
                            fanciRoleCallback = FanciRoleCallback(
                                fanciRole = editFanciRole
                            ),
                            addRoleError = null,
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
                        roleId = editFanciRole.id.orEmpty(),
                        userId = removeMemberList.map {
                            it.id.orEmpty()
                        }
                    ).fold({
                    }, {
                        KLog.e(TAG, it)
                        if (it is EmptyBodyException) {
                            editFanciRole = fanciRole.copy(userCount = uiState.memberList.size.toLong())
                            uiState = uiState.copy(
                                fanciRoleCallback = FanciRoleCallback(
                                    fanciRole = editFanciRole
                                ),
                                addRoleError = null,
                                addRoleComplete = true
                            )
                        }
                    })
                } else {
                    uiState = uiState.copy(
                        fanciRoleCallback = FanciRoleCallback(
                            fanciRole = editFanciRole
                        ),
                        addRoleError = null,
                        addRoleComplete = true
                    )
                }
            } else {
                //將原本清單的人員都移除
                if (editMemberList.isNotEmpty()) {
                    groupUseCase.removeUserRole(
                        groupId = groupId,
                        roleId = editFanciRole.id.orEmpty(),
                        userId = editMemberList.map {
                            it.id.orEmpty()
                        }
                    ).fold({

                    }, {
                        KLog.e(TAG, it)
                    })
                }

                uiState = uiState.copy(
                    fanciRoleCallback = FanciRoleCallback(
                        fanciRole = editFanciRole
                    ),
                    addRoleError = null,
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
            addRoleError = null,
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
                val roleColor = roleColors.firstOrNull { color ->
                    color.name == fanciRole.color
                } ?: Color()

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

    /**
     * 刪除 角色
     */
    fun onDelete(fanciRole: FanciRole, group: Group) {
        KLog.i(TAG, "onDelete:$fanciRole")
        viewModelScope.launch {
            groupUseCase.deleteRole(
                groupId = group.id.orEmpty(),
                roleId = fanciRole.id.orEmpty()
            ).fold({
            }, {
                if (it is EmptyBodyException) {
                    uiState = uiState.copy(
                        fanciRoleCallback = FanciRoleCallback(
                            isAdd = false,
                            fanciRole = fanciRole
                        )
                    )
                } else {
                    KLog.e(TAG, it)
                }
            })
        }
    }

    /**
     * 移除 角色
     */
    fun removeRole(fanciRole: FanciRole) {
        KLog.i(TAG, "removeRole:$fanciRole")
        val roleList = uiState.fanciRole?.filter {
            it.id != fanciRole.id
        }
        uiState = uiState.copy(
            fanciRole = roleList
        )
    }

    /**
     * 重新排序 角色清單
     */
    fun sortRole(
        groupId: String,
        roleList: List<FanciRole>
    ) {
        KLog.i(TAG, "sortRole:$roleList")
        viewModelScope.launch {
            orderUseCase.orderRole(
                groupId = groupId,
                roleIds = roleList.map { it.id.orEmpty() }
            ).fold({
            }, {
                if (it is EmptyBodyException) {
                    KLog.i(TAG, "sortRole complete.")
                    uiState = uiState.copy(
                        fanciRole = roleList
                    )
                } else {
                    KLog.e(TAG, it)
                }
            })
        }
    }

    /**
     * re-assign data to screen
     */
    fun setSortResult(fanciRoleList: List<FanciRole>) {
        uiState = uiState.copy(
            fanciRole = fanciRoleList
        )
    }

    /**
     * 設定 初始化角色顏色
     */
    fun setDefaultRoleColor(color: Color) {
        uiState = uiState.copy(
            roleColor = color
        )
    }
}