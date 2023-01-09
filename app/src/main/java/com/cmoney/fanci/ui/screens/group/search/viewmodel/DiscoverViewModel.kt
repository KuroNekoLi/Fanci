package com.cmoney.fanci.ui.screens.group.search.viewmodel

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

data class UiState(
    val groupList: List<Group> = emptyList(),
    val searchGroupClick: Group? = null,
    val joinSuccess: Boolean = false,
    val tabIndex: Int = 0
)

class DiscoverViewModel(private val groupUseCase: GroupUseCase) : ViewModel() {
    val TAG = DiscoverViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    init {
        fetchPopularGroup()
    }

    /**
     * 抓取 熱門社團
     */
    private fun fetchPopularGroup() {
        uiState = uiState.copy(groupList = emptyList())
        viewModelScope.launch {
            groupUseCase.getPopularGroup().fold(
                {
                    uiState = uiState.copy(groupList = it.items.orEmpty())
                },
                {
                    KLog.e(TAG, it)
                }
            )
        }
    }

    /**
     * 抓取 最新社團
     */
    private fun fetchLatestGroup() {
        uiState = uiState.copy(groupList = emptyList())
        viewModelScope.launch {
            groupUseCase.getNewestGroup().fold(
                {
                    uiState = uiState.copy(groupList = it.items.orEmpty())
                },
                {
                    KLog.e(TAG, it)
                }
            )
        }
    }

    fun openGroupItemDialog(groupModel: Group) {
        KLog.i(TAG, "openGroupItemDialog:$groupModel")
        uiState = uiState.copy(searchGroupClick = groupModel)
    }

    fun closeGroupItemDialog() {
        KLog.i(TAG, "closeGroupItemDialog")
        uiState = uiState.copy(searchGroupClick = null)
    }

    fun joinGroup(group: Group) {
        KLog.i(TAG, "joinGroup:$group")
        viewModelScope.launch {
            groupUseCase.joinGroup(group).fold({
                uiState = uiState.copy(joinSuccess = true)
            }, {
                if (it is EmptyBodyException) {
                    uiState = uiState.copy(joinSuccess = true)
                }
            })
        }
    }

    /**
     * 點擊 Tab
     */
    fun onTabClick(index: Int) {
        KLog.i(TAG, "onTabClick:$index")
        uiState = uiState.copy(
            tabIndex = index
        )
        when (index) {
            0 -> {
                fetchPopularGroup()
            }
            else -> {
                fetchLatestGroup()
            }
        }
    }
}