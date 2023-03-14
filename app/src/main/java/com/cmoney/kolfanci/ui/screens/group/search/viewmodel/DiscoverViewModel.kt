package com.cmoney.kolfanci.ui.screens.group.search.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupPaging
import com.cmoney.kolfanci.extension.EmptyBodyException
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class UiState(
    val groupList: List<Group> = emptyList(),
    val searchGroupClick: Group? = null,
    val joinSuccess: Group? = null,
    val tabIndex: Int = 0,
    val isLoading: Boolean = false
)

class DiscoverViewModel(private val groupUseCase: GroupUseCase) : ViewModel() {
    val TAG = DiscoverViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    var haveNextPage: Boolean = false       //拿取所有群組時 是否還有分頁
    var nextWeight: Long? = null            //下一分頁權重

    init {
        fetchPopularGroup()
    }

    private fun loading() {
        uiState = uiState.copy(
            isLoading = true
        )
    }

    private fun dismissLoading() {
        uiState = uiState.copy(
            isLoading = false,
        )
    }

    /**
     * 抓取 熱門社團
     */
    private fun fetchPopularGroup() {
        viewModelScope.launch {
            loading()
            groupUseCase.getPopularGroup(
                pageSize = 10,
                startWeight = nextWeight ?: Long.MAX_VALUE
            ).fold(
                {
                    dismissLoading()
                    handleApiResult(it)
                },
                {
                    dismissLoading()
                    KLog.e(TAG, it)
                }
            )
        }
    }

    /**
     * 抓取 最新社團
     */
    private fun fetchLatestGroup() {
        viewModelScope.launch {
            loading()
            groupUseCase.getNewestGroup(
                pageSize = 10,
                startWeight = nextWeight ?: Long.MAX_VALUE
            ).fold(
                {
                    dismissLoading()
                    handleApiResult(it)
                },
                {
                    dismissLoading()
                    KLog.e(TAG, it)
                }
            )
        }
    }

    /**
     * 處理 熱門/最新 社團 response
     */
    private fun handleApiResult(groupPaging: GroupPaging) {
        haveNextPage = groupPaging.haveNextPage == true
        nextWeight = groupPaging.nextWeight
        val orgGroupList = uiState.groupList.toMutableList()
        orgGroupList.addAll(groupPaging.items.orEmpty())
        uiState = uiState.copy(groupList = orgGroupList.distinctBy { group ->
            group.id
        })
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
                uiState = uiState.copy(joinSuccess = group)
            }, {
                if (it is EmptyBodyException) {
                    uiState = uiState.copy(joinSuccess = group)
                }
            })
        }
    }

    /**
     * 點擊 Tab
     */
    fun onTabClick(index: Int) {
        KLog.i(TAG, "onTabClick:$index")
        reset()
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

    /**
     * Reset 分頁基準點以及顯示資料
     */
    private fun reset() {
        uiState = uiState.copy(groupList = emptyList())
        haveNextPage = false
        nextWeight = null
    }

    /**
     * 讀取社團 下一分頁
     */
    fun onLoadMore() {
        KLog.i(TAG, "onLoadMore: haveNextPage:$haveNextPage nextWeight:$nextWeight")
        if (haveNextPage && nextWeight != null && nextWeight!! > 0) {
            when (uiState.tabIndex) {
                0 -> {
                    fetchPopularGroup()
                }
                else -> {
                    fetchLatestGroup()
                }
            }
        }
    }
}