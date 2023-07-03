package com.cmoney.kolfanci.ui.screens.search.model

import androidx.compose.ui.text.AnnotatedString
import com.cmoney.fanciapi.fanci.model.ChatMessage

/**
 * @param searchKeyword 搜尋關鍵字
 * @param searchType 搜尋結果類型
 * @param message 搜尋到的訊息
 * @param subTitle 大頭貼底下的 subtitle
 * @param highlightMessage 有highlight關鍵字的內容
 */
data class SearchChatMessage(
    val searchKeyword: String,
    val searchType: SearchType,
    val message: ChatMessage,
    val subTitle: String,
    val highlightMessage: AnnotatedString
)

sealed class SearchType {
    object Chat : SearchType()

    object Post : SearchType()
}
