package com.cmoney.fanci.ui.screens.group.setting.apply.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.model.usecase.GroupApplyUseCase
import com.cmoney.fanciapi.fanci.model.GroupRequirementApply
import com.cmoney.fanciapi.fanci.model.GroupRequirementApplyPaging
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class UiState(
    val applyList: List<GroupRequirementApplySelected>? = null,
)

data class GroupRequirementApplySelected(
    val groupRequirementApply: GroupRequirementApply,
    val isSelected: Boolean
)

class GroupApplyViewModel(private val groupApplyUseCase: GroupApplyUseCase) : ViewModel() {
    val TAG = GroupApplyViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    var groupRequirementApplyPaging: GroupRequirementApplyPaging? = null

    /**
     * 取得 申請清單
     * @param groupId 社團id
     */
    fun fetchApplyQuestion(groupId: String) {
        KLog.i(TAG, "fetchApplyQuestion:$groupId")
        viewModelScope.launch {
            groupApplyUseCase.fetchGroupApplyList(
                groupId = groupId
            ).fold({
                groupRequirementApplyPaging = it

                uiState = uiState.copy(
                    applyList = it.items?.map { groupApply ->
                        GroupRequirementApplySelected(
                            groupRequirementApply = groupApply,
                            isSelected = false
                        )
                    }
                )

            }, {
                KLog.e(TAG, it)
            })
        }
    }

}