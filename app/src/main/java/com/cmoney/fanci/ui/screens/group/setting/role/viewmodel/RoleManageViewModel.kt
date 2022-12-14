package com.cmoney.fanci.ui.screens.group.setting.role.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class UiState(
    val fanciRole: List<FanciRole>? = null  //角色清單
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

}