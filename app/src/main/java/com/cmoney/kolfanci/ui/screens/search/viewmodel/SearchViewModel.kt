package com.cmoney.kolfanci.ui.screens.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.kolfanci.model.usecase.SearchUseCase
import com.cmoney.kolfanci.ui.screens.search.model.SearchChatMessage
import com.cmoney.kolfanci.ui.screens.search.model.SearchType
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val searchUseCase: SearchUseCase) : ViewModel() {
    val TAG = SearchViewModel::class.java.simpleName

    //搜尋結果
    private val _searchResult = MutableStateFlow<List<SearchChatMessage>>(emptyList())
    val searchResult = _searchResult.asStateFlow()

    /**
     * 進行 搜尋
     * @param keyword 關鍵字
     */
    fun doSearch(keyword: String) {
        KLog.i(TAG, "doSearch:$keyword")
        viewModelScope.launch {
            searchUseCase.doSearch(keyword).onSuccess {
                KLog.i(TAG, "doSearch result:$it")
                _searchResult.value = it
            }.onFailure {
                KLog.e(TAG, it)
            }
        }
    }


}