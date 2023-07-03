package com.cmoney.kolfanci.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.screens.search.model.SearchChatMessage
import com.cmoney.kolfanci.ui.screens.search.model.SearchType
import com.cmoney.kolfanci.ui.screens.shared.member.SearchMemberItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.socks.library.KLog

@Composable
fun SearchAllScreen(
    modifier: Modifier = Modifier,
    searchResult: List<SearchChatMessage>,
    onSearchItemClick: (SearchChatMessage) -> Unit
) {
    val TAG = "SearchAllScreen"
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(searchResult) { item ->
            SearchResultItem(
                searchChatMessage = item,
                onClick = {
                    KLog.i(TAG, "search item click:$item")
                    onSearchItemClick.invoke(item)
                }
            )
        }
    }
}

@Composable
fun SearchResultItem(searchChatMessage: SearchChatMessage, onClick: (SearchChatMessage) -> Unit) {
    Column(modifier = Modifier
        .background(LocalColor.current.background)
        .clickable {
            onClick.invoke(searchChatMessage)
        }) {
        SearchMemberItemScreen(
            groupMember = searchChatMessage.message.author ?: GroupMember(),
            subTitle = searchChatMessage.subTitle
        )

        Text(
            modifier = Modifier.padding(start = 30.dp, end = 24.dp, bottom = 10.dp),
            text = searchChatMessage.highlightMessage,
            fontSize = 17.sp,
            color = LocalColor.current.text.default_100,
            maxLines = 10,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Preview
@Composable
fun SearchResultItemPreview() {
    SearchResultItem(
        searchChatMessage = SearchChatMessage(
            searchKeyword = "水",
            searchType = SearchType.Chat,
            message = MockData.mockMessage,
            subTitle = "聊天・2023.01.13",
            highlightMessage = AnnotatedString(
                text = ""
            )
        ),
        onClick = {}
    )
}

@Preview
@Composable
fun SearchAllScreenPreview() {
    FanciTheme {
        SearchAllScreen(
            searchResult = listOf(
                SearchChatMessage(
                    searchKeyword = "水",
                    searchType = SearchType.Chat,
                    message = MockData.mockMessage,
                    subTitle = "聊天・2023.01.13",
                    highlightMessage = AnnotatedString(
                        text = ""
                    )
                )
            ),
            onSearchItemClick = {}
        )
    }
}