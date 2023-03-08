package com.cmoney.kolfanci.ui.screens.group.setting.apply.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.ApplyStatus
import com.cmoney.fanciapi.fanci.model.GroupRequirementApply
import com.cmoney.fanciapi.fanci.model.GroupRequirementApplyPaging
import com.cmoney.kolfanci.extension.EmptyBodyException
import com.cmoney.kolfanci.model.usecase.GroupApplyUseCase
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class UiState(
    val applyList: List<GroupRequirementApplySelected>? = null,
    val tips: String? = null,
    val isComplete: Boolean = false
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

    /**
     * 點擊 單條審核項目
     * @param groupRequirementApplySelected 作答&勾選 model
     */
    fun onApplyItemClick(groupRequirementApplySelected: GroupRequirementApplySelected) {
        KLog.i(TAG, "onApplyItemClick:$groupRequirementApplySelected")
        uiState.applyList?.let { applyList ->
            val replaceItem = groupRequirementApplySelected.copy(
                isSelected = !groupRequirementApplySelected.isSelected
            )

            val newList = applyList.map {
                if (it.groupRequirementApply.id == replaceItem.groupRequirementApply.id) {
                    replaceItem
                } else {
                    it
                }
            }
            uiState = uiState.copy(
                applyList = newList
            )
        }
    }

    /**
     * 點擊 全選按鈕
     */
    fun selectAllClick() {
        KLog.i(TAG, "selectAllClick")
        uiState.applyList?.let { applyList ->
            val newList = applyList.map {
                it.copy(
                    isSelected = !it.isSelected
                )
            }
            uiState = uiState.copy(
                applyList = newList
            )
        }
    }

    /**
     * 點擊 允許 or 拒絕
     */
    fun onApplyOrReject(groupId: String, applyStatus: ApplyStatus) {
        KLog.i(TAG, "onReject")
        viewModelScope.launch {
            uiState.applyList?.let { applyList ->
                val selectedApplyId = applyList.filter { it.isSelected }.map {
                    it.groupRequirementApply.id.orEmpty()
                }

                if (selectedApplyId.isNotEmpty()) {
                    groupApplyUseCase.approval(
                        groupId = groupId,
                        applyId = selectedApplyId,
                        applyStatus = applyStatus
                    ).fold({

                    }, {
                        if (it is EmptyBodyException) {
                            removeSelectedItem()
                            showTips("審核完成")
                        } else {
                            KLog.e(TAG, it)
                        }
                    })
                }
            }
        }
    }

    private fun showTips(text: String) {
        uiState = uiState.copy(
            tips = text
        )
    }

    /**
     * 取消 Toast 狀態
     */
    fun dismissTips() {
        uiState = uiState.copy(
            tips = null
        )
    }

    /**
     * 將已選擇項目 移除
     */
    private fun removeSelectedItem() {
        uiState.applyList?.let { applyList ->
            val newList = applyList.filterNot {
                it.isSelected
            }
            uiState = uiState.copy(
                applyList = newList,
                isComplete = true
            )
        }
    }

}