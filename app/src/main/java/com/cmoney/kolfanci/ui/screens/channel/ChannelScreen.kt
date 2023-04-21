package com.cmoney.kolfanci.ui.screens.channel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.kolfanci.ui.destinations.AnnouncementScreenDestination
import com.cmoney.kolfanci.ui.screens.chat.ChatRoomScreen
import com.cmoney.kolfanci.ui.screens.post.PostScreen
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
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

@OptIn(ExperimentalPagerApi::class)
@Destination
@Composable
fun ChannelScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    channel: Channel,
    announcementResultRecipient: ResultRecipient<AnnouncementScreenDestination, ChatMessage>
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = channel.name.orEmpty(),
                moreEnable = false,
                backClick = {
                    navController.popBackStack()
                }
            )
        },
        backgroundColor = LocalColor.current.background
    ) { innerPadding ->
        val pages = listOf("聊天", "貼文")

        val pagerState = rememberPagerState()

        val tabIndex = pagerState.currentPage

        val coroutineScope = rememberCoroutineScope()

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
                        },
                    )
                }
            }

            HorizontalPager(
                count = pages.size,
                state = pagerState,
            ) { page ->
                when (page) {
                    0 -> {
                        ChatRoomScreen(
                            channelId = channel.id.orEmpty(),
                            channelTitle = channel.name.orEmpty(),
                            navController = navController,
                            resultRecipient = announcementResultRecipient
                        )
                    }

                    else -> {
                        PostScreen(
                            channel = channel,
                            navController = navController,
                        )
                    }
                }
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun ChannelScreenPreview() {
    FanciTheme {
        ChannelScreen(
            navController = EmptyDestinationsNavigator,
            channel = Channel(
                name = "\uD83D\uDC57｜金針菇穿什麼"
            ),
            announcementResultRecipient = EmptyResultRecipient()
        )
    }
}