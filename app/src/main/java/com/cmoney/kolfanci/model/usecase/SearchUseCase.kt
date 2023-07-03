package com.cmoney.kolfanci.model.usecase

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.screens.search.model.SearchChatMessage
import com.cmoney.kolfanci.ui.screens.search.model.SearchType
import com.cmoney.kolfanci.utils.Utils
import kotlin.random.Random

class SearchUseCase {

    /**
     * 搜尋
     *
     * @param keyword 關鍵字
     */
    suspend fun doSearch(keyword: String): Result<List<SearchChatMessage>> {
        return Result.success(
            MockData.mockListMessage.map {
                val randomType = Random.nextBoolean()
                val searchType = if (randomType) {
                    SearchType.Chat
                } else {
                    SearchType.Post
                }

                SearchChatMessage(
                    searchKeyword = keyword,
                    searchType = searchType,
                    message = it,
                    subTitle = getSearchMessageSubTitle(
                        searchType = searchType,
                        message = it
                    ),
                    highlightMessage = getHighlightTxt(
                        message = it.content?.text.orEmpty(),
                        keyword = keyword
                    )
                )
            }
        )
    }

    /**
     * 取得搜尋結果的 subtitle
     * ex: 聊天・2023.01.13
     */
    private fun getSearchMessageSubTitle(searchType: SearchType, message: ChatMessage): String {
        val typeString = when (searchType) {
            SearchType.Chat -> "聊天"
            SearchType.Post -> "貼文"
        }

        val createTime = message.createUnixTime?.times(1000) ?: 0
        val displayTime = Utils.getDisplayTime(createTime)

        return "%s・%s".format(typeString, displayTime)
    }

    /**
     * 將關鍵字highlight動作
     *
     * @param message 原始訊息
     * @param keyword 要 highlight 的關鍵字
     */
    private fun getHighlightTxt(message: String, keyword: String): AnnotatedString {
        val span = SpanStyle(
            color = Color.Red,
            fontWeight = FontWeight.Bold
        )

        return buildAnnotatedString {
            var start = 0
            while (message.indexOf(
                    keyword,
                    start,
                    ignoreCase = true
                ) != -1 && keyword.isNotBlank()
            ) {
                val firstIndex = message.indexOf(keyword, start, true)
                val end = firstIndex + keyword.length
                append(message.substring(start, firstIndex))
                withStyle(style = span) {
                    append(message.substring(firstIndex, end))
                }
                start = end
            }
            append(message.substring(start, message.length))
            toAnnotatedString()
        }
    }

//    @Deprecated("需求更改, 不需要 顯示了")
//    /**
//     * 取得 原文+連結,圖片 轉為文字訊息
//     */
//    private fun translateLinkMessageToText(message: ChatMessage): String {
//        val linkTextList = message.content?.medias?.joinToString(separator = "\n") {
//            when (it.type) {
//                MediaType.image -> "(圖片)"
//                MediaType.video -> "(影片)"
//                else -> "(圖片)"
//            }
//        } ?: ""
//    }

}