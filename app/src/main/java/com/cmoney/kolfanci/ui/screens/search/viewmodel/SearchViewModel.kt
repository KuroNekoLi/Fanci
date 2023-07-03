package com.cmoney.kolfanci.ui.screens.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.kolfanci.extension.toBulletinboardMessage
import com.cmoney.kolfanci.model.usecase.SearchUseCase
import com.cmoney.kolfanci.ui.screens.search.model.SearchChatMessage
import com.cmoney.kolfanci.ui.screens.search.model.SearchType
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val searchUseCase: SearchUseCase) : ViewModel() {
    val TAG = SearchViewModel::class.java.simpleName

    //全部搜尋結果
    private val _searchResult = MutableStateFlow<List<SearchChatMessage>>(emptyList())
    val searchResult = _searchResult.asStateFlow()

    //聊天搜尋結果
    private val _searchChatResult = MutableStateFlow<List<SearchChatMessage>>(emptyList())
    val searchChatResult = _searchChatResult.asStateFlow()

    //貼文搜尋結果
    private val _searchPostResult = MutableStateFlow<List<SearchChatMessage>>(emptyList())
    val searchPostResult = _searchPostResult.asStateFlow()

    //Open Post info page
    private val _bulletinboardMessage = MutableSharedFlow<BulletinboardMessage>()
    val bulletinboardMessage = _bulletinboardMessage.asSharedFlow()

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

                _searchChatResult.value = it.filter { result ->
                    result.searchType == SearchType.Chat
                }

                _searchPostResult.value = it.filter { result ->
                    result.searchType == SearchType.Post
                }
            }.onFailure {
                KLog.e(TAG, it)
            }
        }
    }

    /**
     * 點擊貼文 item
     *
     * @param searchChatMessage 點擊item
     */
    fun onPostItemClick(searchChatMessage: SearchChatMessage) {
        KLog.i(TAG, "onPostItemClick:$searchChatMessage")
        viewModelScope.launch {
            searchUseCase.getSinglePostMessage(messageId = searchChatMessage.message.id.orEmpty())
                .fold({
                    _bulletinboardMessage.emit(it.toBulletinboardMessage())
                }, {
                    KLog.e(TAG, it)
                })
        }
    }


}