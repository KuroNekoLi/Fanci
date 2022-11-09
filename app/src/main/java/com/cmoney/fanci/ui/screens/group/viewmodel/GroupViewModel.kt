package com.cmoney.fanci.ui.screens.group.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.model.GroupModel
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class GroupUiState(
    val groupList: List<GroupModel> = emptyList(),
    val searchGroupClick: GroupModel? = null
)

class GroupViewModel(private val groupUseCase: GroupUseCase) : ViewModel() {
    val TAG = GroupViewModel::class.java.simpleName

    var uiState by mutableStateOf(GroupUiState())
        private set

    init {
        viewModelScope.launch {
            uiState = uiState.copy(groupList = groupUseCase.getGroupMockData())
        }
    }

    fun openGroupItemDialog(groupModel: GroupModel) {
        KLog.i(TAG, "openGroupItemDialog:$groupModel")
        uiState = uiState.copy(searchGroupClick = groupModel)
    }

    fun closeGroupItemDialog() {
        KLog.i(TAG, "closeGroupItemDialog")
        uiState = uiState.copy(searchGroupClick = null)
    }
}