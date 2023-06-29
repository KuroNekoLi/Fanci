package com.cmoney.kolfanci.ui.screens.channel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChannelTabsStatus
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.Color
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.common.CircleDot
import com.cmoney.kolfanci.ui.destinations.AnnouncementScreenDestination
import com.cmoney.kolfanci.ui.destinations.EditPostScreenDestination
import com.cmoney.kolfanci.ui.destinations.PostInfoScreenDestination
import com.cmoney.kolfanci.ui.screens.chat.ChatRoomScreen
import com.cmoney.kolfanci.ui.screens.post.PostScreen
import com.cmoney.kolfanci.ui.screens.post.info.PostInfoScreenResult
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
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
import org.koin.androidx.compose.koinViewModel

/**
 * 頻道主頁面
 */
@Destination
@Composable
fun ChannelScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    channel: Channel,
    viewMode: ChannelViewModel = koinViewModel(),
    announcementResultRecipient: ResultRecipient<AnnouncementScreenDestination, ChatMessage>,
    editPostResultRecipient: ResultRecipient<EditPostScreenDestination, PostViewModel.BulletinboardMessageWrapper>,
    postInfoResultRecipient: ResultRecipient<PostInfoScreenDestination, PostInfoScreenResult>
) {

    LaunchedEffect(Unit) {
        viewMode.fetchChannelTabStatus(channel.id.orEmpty())
    }

    val channelTabStatus by viewMode.channelTabStatus.collectAsState()

    ChannelScreenView(
        modifier = modifier,
        channel = channel,
        navController = navController,
        channelTabStatus = channelTabStatus,
        announcementResultRecipient = announcementResultRecipient,
        editPostResultRecipient = editPostResultRecipient,
        postInfoResultRecipient = postInfoResultRecipient
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun ChannelScreenView(
    modifier: Modifier = Modifier,
    channel: Channel,
    navController: DestinationsNavigator,
    announcementResultRecipient: ResultRecipient<AnnouncementScreenDestination, ChatMessage>,
    channelTabStatus: ChannelTabsStatus,
    editPostResultRecipient: ResultRecipient<EditPostScreenDestination, PostViewModel.BulletinboardMessageWrapper>,
    postInfoResultRecipient: ResultRecipient<PostInfoScreenDestination, PostInfoScreenResult>
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = channel.name.orEmpty(),
                trailingContent = {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .offset(x = (-15).dp)
                            .clickable {
                                //TODO: forward to search page
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(id = R.drawable.message_search),
                            contentDescription = null
                        )
                    }
                },
                backClick = {
                    navController.popBackStack()
                }
            )
        },
        backgroundColor = LocalColor.current.background
    ) { innerPadding ->
        val pages = mutableListOf<String>()

        if (channelTabStatus.chatRoom == true) {
            pages.add("聊天")
        }

        if (channelTabStatus.bulletinboard == true) {
            pages.add("貼文")
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
                        0 -> {
                            //聊天室 Tab
                            ChatRoomScreen(
                                channelId = channel.id.orEmpty(),
                                navController = navController,
                                resultRecipient = announcementResultRecipient
                            )
                        }

                        else -> {
                            //貼文 Tab
                            PostScreen(
                                channel = channel,
                                navController = navController,
                                resultRecipient = editPostResultRecipient,
                                postInfoResultRecipient = postInfoResultRecipient
                            )
                        }
                    }
                }
            }
        } else {
//            Text(text = "No Tab Display")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChannelScreenPreview() {
    FanciTheme {
        ChannelScreenView(
            channel = Channel(
                name = "\uD83D\uDC57｜金針菇穿什麼"
            ),
            navController = EmptyDestinationsNavigator,
            announcementResultRecipient = EmptyResultRecipient(),
            channelTabStatus = ChannelTabsStatus(),
            editPostResultRecipient = EmptyResultRecipient(),
            postInfoResultRecipient = EmptyResultRecipient()
        )
    }
}