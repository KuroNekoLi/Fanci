package com.cmoney.fanci.ui.screens.group.setting.role.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanci.model.usecase.ThemeUseCase
import com.cmoney.fanciapi.fanci.model.Color
import com.cmoney.fanciapi.fanci.model.ColorTheme
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.PermissionCategory
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class UiState(
    val fanciRole: List<FanciRole>? = null,  //角色清單
    val permissionList: List<PermissionCategory>? = null,    //權限清單
)

class RoleManageViewModel(
    private val groupUseCase: GroupUseCase
) : ViewModel() {
    private val TAG = RoleManageViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

//    /**
//     * 取得新增角色需要色碼
//     */
//    fun fetchRoleColor(colorTheme: ColorTheme) {
//        KLog.i(TAG, "fetchRoleColor.")
//        viewModelScope.launch {
//            themeUseCase.fetchRoleColor(colorTheme).fold({
//                uiState = uiState.copy(
//                    roleColor = it.colors
//                )
//
//            },{
//                KLog.e(TAG, it)
//            })
//        }
//    }

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
            groupUseCase.fetchPermissionList().fold({
                uiState = uiState.copy(
                    permissionList = it
                )
            }, {
                KLog.e(TAG, it)
            })
        }
    }

}