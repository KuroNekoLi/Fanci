package com.cmoney.kolfanci.ui.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.destinations.PostInfoScreenDestination
import com.cmoney.kolfanci.ui.destinations.SearchChatInfoScreenDestination
import com.cmoney.kolfanci.ui.screens.search.model.SearchChatMessage
import com.cmoney.kolfanci.ui.screens.search.model.SearchType
import com.cmoney.kolfanci.ui.screens.search.viewmodel.SearchViewModel
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.socks.library.KLog
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun SearchMainScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    channel: Channel,
    viewModel: SearchViewModel = koinViewModel()
) {
    val TAG = "SearchMainScreen"
    val searchAllResult by viewModel.searchResult.collectAsState()
    val searchChatResult by viewModel.searchChatResult.collectAsState()
    val searchPostResult by viewModel.searchPostResult.collectAsState()

    //前往 貼文 info page
    LaunchedEffect(Unit) {
        viewModel.bulletinboardMessage.collect { clickPostItem ->
            navController.navigate(
                PostInfoScreenDestination(
                    post = clickPostItem,
                    channel = channel
                )
            )
        }
    }

    SearchMainScreenView(
        modifier = modifier,
        searchAllResult = searchAllResult,
        searchChatResult = searchChatResult,
        searchPostResult = searchPostResult,
        onClose = {
            navController.popBackStack()
        },
        onSearch = {
            viewModel.doSearch(it)
        },
        onPostItemClick = {
            viewModel.onPostItemClick(it)
        },
        onChatItemClick = {
            KLog.i(TAG, "onChatItemClick:$it")
            navController.navigate(SearchChatInfoScreenDestination(
                group = group,
                channel = channel,
                message = it.message
            ))
        }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun SearchMainScreenView(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onSearch: (String) -> Unit,
    searchAllResult: List<SearchChatMessage>,
    searchChatResult: List<SearchChatMessage>,
    searchPostResult: List<SearchChatMessage>,
    onPostItemClick: (SearchChatMessage) -> Unit,
    onChatItemClick: (SearchChatMessage) -> Unit
) {
    val pages = mutableListOf(
        stringResource(id = R.string.all),
        stringResource(id = R.string.chat),
        stringResource(id = R.string.post)
    )

    val pagerState = rememberPagerState()
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        backgroundColor = LocalColor.current.env_100,
        topBar = {
            SearchToolBar(
                onClose = onClose,
                onSearch = onSearch
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = tabIndex,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                        color = LocalColor.current.primary
                    )
                }
            ) {
                pages.forEachIndexed { index, title ->
                    Tab(
                        text = {
                            Text(
                                title,
                                fontSize = 14.sp,
                                color = LocalColor.current.text.default_80
                            )
                        },
                        selected = tabIndex == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }

            HorizontalPager(
                count = pages.size,
                state = pagerState,
            ) { page ->
                when (page) {
                    //全部
                    0 -> {
                        if (searchAllResult.isEmpty()) {
                            SearchEmptyScreen(modifier = modifier.fillMaxSize())
                        } else {
                            SearchResultScreen(searchResult = searchAllResult, onSearchItemClick = {
                                when (it.searchType) {
                                    SearchType.Chat -> onChatItemClick.invoke(it)
                                    SearchType.Post -> onPostItemClick.invoke(it)
                                }
                            })
                        }
                    }
                    //聊天
                    1 -> {
                        if (searchAllResult.isEmpty()) {
                            SearchEmptyScreen(modifier = modifier.fillMaxSize())
                        } else {
                            SearchResultScreen(
                                searchResult = searchChatResult,
                                onSearchItemClick = {
                                    onChatItemClick.invoke(it)
                                })
                        }
                    }
                    //貼文
                    else -> {
                        if (searchAllResult.isEmpty()) {
                            SearchEmptyScreen(modifier = modifier.fillMaxSize())
                        } else {
                            SearchResultScreen(
                                searchResult = searchPostResult,
                                onSearchItemClick = {
                                    onPostItemClick.invoke(it)
                                })
                        }
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchToolBar(
    onClose: () -> Unit,
    onSearch: (String) -> Unit
) {
    val TAG = "SearchToolBar"
    var textState by remember { mutableStateOf("") }
    val keyboard = LocalSoftwareKeyboardController.current
    Row(
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 15.dp, bottom = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Search Field
        BasicTextField(
            modifier = Modifier
                .height(45.dp)
                .weight(1f)
                .background(
                    color = LocalColor.current.background,
                    shape = RoundedCornerShape(5.dp)
                ),
            singleLine = true,
            cursorBrush = SolidColor(LocalColor.current.primary),
            textStyle = TextStyle(
                color = LocalColor.current.text.default_100,
                fontSize = 16.sp
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    KLog.i(TAG, "onSearch:$textState")
                    keyboard?.hide()
                    onSearch.invoke(textState)
                }
            ),
            value = textState,
            onValueChange = {
                textState = it
            },
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                        painter = painterResource(id = R.drawable.member_search),
                        contentDescription = null
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        if (textState.isEmpty()) {
                            Text(
                                text = "輸入關鍵字",
                                fontSize = 16.sp,
                                color = LocalColor.current.text.default_30
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )

        Spacer(modifier = Modifier.width(10.dp))

        //Close Field
        Box(
            modifier = Modifier
                .size(35.dp)
                .clickable {
                    onClose.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.white_close),
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
fun SearchToolBarPreview() {
    FanciTheme {
        SearchToolBar(
            onSearch = {},
            onClose = {}
        )
    }
}

@Preview
@Composable
fun SearchMainScreenPreview() {
    FanciTheme {
        SearchMainScreenView(
            onClose = {},
            onSearch = {},
            searchAllResult = emptyList(),
            searchChatResult = emptyList(),
            searchPostResult = emptyList(),
            onPostItemClick = {},
            onChatItemClick = {}
        )
    }
}