package com.cmoney.fanci.ui.screens.shared.member.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.google.gson.Gson
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class UiState(
    val groupMember: List<GroupMemberSelect>? = null,  //社團會員
)

data class GroupMemberSelect(val groupMember: GroupMember, val isSelected: Boolean = false)

class AddMemberViewModel(private val groupUseCase: GroupUseCase) : ViewModel() {
    private val TAG = AddMemberViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    /**
     * 獲取群組會員清單
     */
    fun fetchGroupMember(groupId: String, skip: Int = 0) {
        KLog.i(TAG, "fetchGroupMember:$groupId")
        viewModelScope.launch {
            groupUseCase.getGroupMember(groupId = groupId, skipCount = skip).fold({
                val responseMembers = it.items.orEmpty()
                if (responseMembers.isNotEmpty()) {
                    val wrapMember = responseMembers.map { member ->
                        GroupMemberSelect(
                            groupMember = member
                        )
                    }

                    val orgMemberList = uiState.groupMember.orEmpty().toMutableList()
                    orgMemberList.addAll(wrapMember)

                    uiState = uiState.copy(
                        groupMember = orgMemberList
                    )
                }
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 讀取下一頁 會員資料
     */
    fun onLoadMoreGroupMember(groupId: String) {
        KLog.i(TAG, "onLoadMoreGroupMember")
        val skip = uiState.groupMember.orEmpty().size
        if (skip != 0) {
            fetchGroupMember(groupId, skip)
        }
    }

    /**
     * 點擊會員
     */
    fun onMemberClick(groupMemberSelect: GroupMemberSelect) {
        KLog.i(TAG, "onMemberClick:$groupMemberSelect")
        val memberList = uiState.groupMember?.map {
            if (it.groupMember.id == groupMemberSelect.groupMember.id) {
                groupMemberSelect.copy(
                    isSelected = !groupMemberSelect.isSelected
                )
            } else {
                it
            }
        }

        uiState = uiState.copy(
            groupMember = memberList
        )
    }

    /**
     * 取得 已選擇的會員清單
     */
    fun fetchSelectedMember(): String {
        val gson = Gson()
        val list = uiState.groupMember?.filter {
            it.isSelected
        }?.map {
            it.groupMember
        }.orEmpty()
        return gson.toJson(list)
    }

}