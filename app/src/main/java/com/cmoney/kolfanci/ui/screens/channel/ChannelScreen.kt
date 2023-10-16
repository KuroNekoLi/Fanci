package com.cmoney.kolfanci.ui.screens.channel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChannelTabsStatus
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.globalGroupViewModel
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.destinations.AnnouncementScreenDestination
import com.cmoney.kolfanci.ui.destinations.EditPostScreenDestination
import com.cmoney.kolfanci.ui.destinations.PostInfoScreenDestination
import com.cmoney.kolfanci.ui.screens.chat.ChatRoomScreen
import com.cmoney.kolfanci.ui.screens.post.PostScreen
import com.cmoney.kolfanci.ui.screens.post.info.PostInfoScreenResult
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.item.RedDotItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.EmptyResultRecipient
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * 頻道主頁面
 *
 * @param channel 點擊頻道
 * @param jumpChatMessage 打開直接前往的聊天訊息
 */
@Destination
@Composable
fun ChannelScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    channel: Channel,
    viewMode: ChannelViewModel = koinViewModel(),
    jumpChatMessage: ChatMessage? = null,
    announcementResultRecipient: ResultRecipient<AnnouncementScreenDestination, ChatMessage>,
    editPostResultRecipient: ResultRecipient<EditPostScreenDestination, PostViewModel.BulletinboardMessageWrapper>,
    postInfoResultRecipient: ResultRecipient<PostInfoScreenDestination, PostInfoScreenResult>
) {
    val group by globalGroupViewModel().currentGroup.collectAsState()

    LaunchedEffect(Unit) {
        viewMode.fetchChannelTabStatus(channel.id.orEmpty())
    }

    val channelTabStatus by viewMode.channelTabStatus.collectAsState()

    LaunchedEffect(Unit) {
        viewMode.fetchAllUnreadCount(channel)
    }

    val unreadCount by viewMode.unreadCount.collectAsState()

    ChannelScreenView(
        modifier = modifier,
        group = group,
        channel = channel,
        unreadCount = unreadCount,
        navController = navController,
        jumpChatMessage = jumpChatMessage,
        channelTabStatus = channelTabStatus,
        announcementResultRecipient = announcementResultRecipient,
        editPostResultRecipient = editPostResultRecipient,
        postInfoResultRecipient = postInfoResultRecipient,
        onChatPageSelected = {
            viewMode.onChatRedDotClick(
                channelId = channel.id.orEmpty()
            )
        },
        onPostPageSelected = {
            viewMode.onPostRedDotClick(
                channelId = channel.id.orEmpty()
            )
        }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun ChannelScreenView(
    modifier: Modifier = Modifier,
    group: Group?,
    channel: Channel,
    jumpChatMessage: ChatMessage? = null,
    channelTabStatus: ChannelTabsStatus,
    unreadCount: Pair<Long, Long>?,
    navController: DestinationsNavigator,
    announcementResultRecipient: ResultRecipient<AnnouncementScreenDestination, ChatMessage>,
    editPostResultRecipient: ResultRecipient<EditPostScreenDestination, PostViewModel.BulletinboardMessageWrapper>,
    postInfoResultRecipient: ResultRecipient<PostInfoScreenDestination, PostInfoScreenResult>,
    onChatPageSelected: () -> Unit,
    onPostPageSelected: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = channel.name.orEmpty(),
                backClick = {
                    navController.popBackStack()
                }
            )
        },
        backgroundColor = LocalColor.current.background
    ) { innerPadding ->
        val pages = mutableListOf<String>()

        if (channelTabStatus.bulletinboard == true) {
            pages.add(stringResource(id = R.string.post))
        }

        if (channelTabStatus.chatRoom == true) {
            pages.add(stringResource(id = R.string.chat))
        }

        val pagerState = rememberPagerState()

        val tabIndex = pagerState.currentPage

        val coroutineScope = rememberCoroutineScope()

        if (pages.isNotEmpty()) {
            Column(modifier = Modifier.padding(innerPadding)) {
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
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {

                                    Text(
                                        title,
                                        fontSize = 14.sp,
                                        color = LocalColor.current.text.default_80
                                    )

                                    //紅點
                                    unreadCount?.let { unreadCount ->
                                        val readCount =
                                            if (title == stringResource(id = R.string.chat)) {
                                                unreadCount.first
                                            } else {
                                                unreadCount.second
                                            }

                                        if (readCount > 0) {
                                            RedDotItemScreen(
                                                modifier = Modifier.align(Alignment.CenterEnd),
                                                unreadCount = readCount
                                            )
                                        }
                                    }
                                }
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
                        0 -> {
                            //貼文 Tab
                            PostScreen(
                                channel = channel,
                                navController = navController,
                                resultRecipient = editPostResultRecipient,
                                postInfoResultRecipient = postInfoResultRecipient
                            )

                            LaunchedEffect(key1 = Unit) {
                                onPostPageSelected.invoke()
                            }

                            LaunchedEffect(key1 = page) {
                                AppUserLogger.getInstance().log(Page.PostWall)
                            }
                        }

                        else -> {
                            //聊天室 Tab
                            ChatRoomScreen(
                                channelId = channel.id.orEmpty(),
                                navController = navController,
                                resultRecipient = announcementResultRecipient,
                                jumpChatMessage = jumpChatMessage
                            )
                            LaunchedEffect(key1 = Unit) {
                                onChatPageSelected.invoke()
                            }
                        }
                    }
                }
            }
        } else {
//            Text(text = "No Tab Display")
        }
    }
}

@Preview
@Composable
fun ChannelScreenPreview() {
    FanciTheme {
        ChannelScreenView(
            group = Group(),
            channel = Channel(
                name = "\uD83D\uDC57｜金針菇穿什麼"
            ),
            navController = EmptyDestinationsNavigator,
            announcementResultRecipient = EmptyResultRecipient(),
            channelTabStatus = ChannelTabsStatus(),
            editPostResultRecipient = EmptyResultRecipient(),
            postInfoResultRecipient = EmptyResultRecipient(),
            unreadCount = Pair(10, 20),
            onChatPageSelected = {

            },
            onPostPageSelected = {

            }
        )
    }
}