package com.cmoney.fanci.ui.screens.group.setting.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.extension.EmptyBodyException
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanciapi.fanci.model.Group
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class GroupSettingUiState(
    val changeGroupName: Group? = null
)

class GroupSettingViewModel(private val groupUseCase: GroupUseCase) : ViewModel() {
    private val TAG = GroupSettingViewModel::class.java.simpleName

    var uiState by mutableStateOf(GroupSettingUiState())
        private set

    /**
     * 更換 社團名字
     * @param name 更換的名字
     * @param group 社團 model
     */
    fun changeGroupName(name: String, group: Group) {
        KLog.i(TAG, "changeGroupNameL$name")
        viewModelScope.launch {
            groupUseCase.changeGroupName(name, group).fold({
            }, {
                KLog.e(TAG, it)
                if (it is EmptyBodyException) {
                    uiState = uiState.copy(
                        changeGroupName = group.copy(
                            name = name
                        )
                    )
                }
            }
            )
        }
    }
}