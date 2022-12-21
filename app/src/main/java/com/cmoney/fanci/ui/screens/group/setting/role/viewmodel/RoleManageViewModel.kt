package com.cmoney.fanci.ui.screens.group.setting.role.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.PermissionCategory
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class UiState(
    val fanciRole: List<FanciRole>? = null,  //角色清單
    val permissionList: List<PermissionCategory>? = null,    //權限清單
    val permissionSelected: Map<String, Boolean> = emptyMap(),   //勾選權限
    val tabSelected: Int = 0
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

}