package com.cmoney.kolfanci.ui.screens.channel

import android.net.Uri
import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChannelTabType
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.globalGroupViewModel
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.vote.VoteModel
import com.cmoney.kolfanci.ui.destinations.AnnouncementScreenDestination
import com.cmoney.kolfanci.ui.destinations.CreateChoiceQuestionScreenDestination
import com.cmoney.kolfanci.ui.destinations.EditPostScreenDestination
import com.cmoney.kolfanci.ui.destinations.PostInfoScreenDestination
import com.cmoney.kolfanci.ui.screens.chat.ChatRoomScreen
import com.cmoney.kolfanci.ui.screens.media.audio.AudioViewModel
import com.cmoney.kolfanci.ui.screens.post.PostScreen
import com.cmoney.kolfanci.ui.screens.post.info.data.PostInfoScreenResult
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
import com.cmoney.kolfanci.ui.screens.shared.toolbar.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.audio.AudioMiniPlayIconScreen
import com.cmoney.kolfanci.ui.screens.shared.bottomSheet.audio.AudioBottomPlayerScreen
import com.cmoney.kolfanci.ui.screens.shared.item.RedDotItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.EmptyResultRecipient
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * 是否 reset 相關 紅點資訊
 */
@Parcelize
data class ResetRedDot(
    val channelId: String,
    val isResetPost: Boolean,
    val isResetChat: Boolean
) : Parcelable

/**
 * 頻道主頁面
 *
 * @param channel 點擊頻道
 * @param jumpChatMessage 打開直接前往的聊天訊息
 */
@OptIn(ExperimentalPagerApi::class)
@Destination
@Composable
fun ChannelScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    channel: Channel,
    viewMode: ChannelViewModel = koinViewModel(),
    jumpChatMessage: ChatMessage? = null,
    audioViewModel: AudioViewModel = koinViewModel(
        parameters = {
            parametersOf(Uri.EMPTY)
        }
    ),
    announcementResultRecipient: ResultRecipient<AnnouncementScreenDestination, ChatMessage>,
    editPostResultRecipient: ResultRecipient<EditPostScreenDestination, PostViewModel.BulletinboardMessageWrapper>,
    postInfoResultRecipient: ResultRecipient<PostInfoScreenDestination, PostInfoScreenResult>,
    choiceRecipient: ResultRecipient<CreateChoiceQuestionScreenDestination, VoteModel>,
    redDotResultBackNavigator: ResultBackNavigator<ResetRedDot>
) {
    val group by globalGroupViewModel().currentGroup.collectAsState()

    LaunchedEffect(Unit) {
        audioViewModel.fetchIsShowMiniIcon()
        viewMode.fetchAllUnreadCount(channel)
    }

    val unreadCount by viewMode.unreadCount.collectAsState()

    val pagerState = rememberPagerState()

    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current
        ?.onBackPressedDispatcher

    val isShowAudioMiniIcon by audioViewModel.isShowMiniIcon.collectAsState()

    val isOpenBottomAudioPlayer by audioViewModel.isShowBottomPlayer.collectAsState()

    val isAudioPlaying by audioViewModel.isPlaying.collectAsState()

    ChannelScreenView(
        modifier = modifier,
        pagerState = pagerState,
        channel = channel,
        jumpChatMessage = jumpChatMessage,
        unreadCount = unreadCount,
        navController = navController,
        isShowAudioMiniIcon = isShowAudioMiniIcon,
        isAudioPlaying = isAudioPlaying,
        announcementResultRecipient = announcementResultRecipient,
        editPostResultRecipient = editPostResultRecipient,
        postInfoResultRecipient = postInfoResultRecipient,
        choiceRecipient = choiceRecipient,
        onChatPageSelected = {
            viewMode.onChatRedDotClick(
                channelId = channel.id.orEmpty()
            )
        },
        onPostPageSelected = {
            viewMode.onPostRedDotClick(
                channelId = channel.id.orEmpty()
            )
        },
        onBackClick = {
            onBackPressedDispatcher?.onBackPressed()
        },
        isOpenBottomAudioPlayer = isOpenBottomAudioPlayer
    )

    BackHandler {
        val currentPage = pagerState.currentPage
        redDotResultBackNavigator.navigateBack(
            ResetRedDot(
                channelId = channel.id.orEmpty(),
                isResetPost = (currentPage == 0),
                isResetChat = (currentPage == 1)
            )
        )
    }

}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
private fun ChannelScreenView(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    channel: Channel,
    jumpChatMessage: ChatMessage? = null,
    unreadCount: Pair<Long, Long>?,
    navController: DestinationsNavigator,
    isShowAudioMiniIcon: Boolean,
    announcementResultRecipient: ResultRecipient<AnnouncementScreenDestination, ChatMessage>,
    editPostResultRecipient: ResultRecipient<EditPostScreenDestination, PostViewModel.BulletinboardMessageWrapper>,
    postInfoResultRecipient: ResultRecipient<PostInfoScreenDestination, PostInfoScreenResult>,
    choiceRecipient: ResultRecipient<CreateChoiceQuestionScreenDestination, VoteModel>,
    onChatPageSelected: () -> Unit,
    onPostPageSelected: () -> Unit,
    onBackClick: () -> Unit,
    isOpenBottomAudioPlayer: Boolean,
    isAudioPlaying: Boolean
) {
    //控制 audio BottomSheet
    val audioPlayerState = rememberModalBottomSheetState(
        if (isOpenBottomAudioPlayer) {
            ModalBottomSheetValue.Expanded
        } else {
            ModalBottomSheetValue.Hidden
        }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = channel.name.orEmpty(),
                backClick = {
                    onBackClick.invoke()
                }
            )
        },
        backgroundColor = LocalColor.current.background
    ) { innerPadding ->
        val pages = mutableListOf<String>()

        channel.tabs?.map { it.type }?.forEach { type ->
            when (type) {
                ChannelTabType.chatRoom -> {
                    pages.add(stringResource(id = R.string.chat))
                }

                ChannelTabType.bulletinboard -> {
                    pages.add(stringResource(id = R.string.post))
                }

                null -> {}
            }
        }

        val tabIndex = pagerState.currentPage

        val coroutineScope = rememberCoroutineScope()

        if (pages.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            ) {
                //Content
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
                        //聊天 Tab
                        if (pages[page] == stringResource(id = R.string.chat)) {
                            ChatRoomScreen(
                                channelId = channel.id.orEmpty(),
                                navController = navController,
                                resultRecipient = announcementResultRecipient,
                                jumpChatMessage = jumpChatMessage,
                                choiceRecipient = choiceRecipient
                            )
                            LaunchedEffect(key1 = Unit) {
                                onChatPageSelected.invoke()
                            }
                        }
                        //貼文 Tab
                        else {
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
                    }
                }

                if (isShowAudioMiniIcon) {
                    AudioMiniPlayIconScreen(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 120.dp),
                        isPlaying = isAudioPlaying,
                        onClick = {
                            coroutineScope.launch {
                                audioPlayerState.show()
                            }
                        }
                    )

                    //mini player
                    AudioBottomPlayerScreen(
                        state = audioPlayerState
                    )
                }
            }
        } else {
//            Text(text = "No Tab Display")
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
fun ChannelScreenPreview() {
    FanciTheme {
        ChannelScreenView(
            pagerState = rememberPagerState(),
            unreadCount = Pair(10, 20),
            channel = Channel(),
            navController = EmptyDestinationsNavigator,
            isShowAudioMiniIcon = true,
            announcementResultRecipient = EmptyResultRecipient(),
            editPostResultRecipient = EmptyResultRecipient(),
            postInfoResultRecipient = EmptyResultRecipient(),
            choiceRecipient = EmptyResultRecipient(),
            onChatPageSelected = {

            },
            onPostPageSelected = {

            },
            onBackClick = {},
            isOpenBottomAudioPlayer = false,
            isAudioPlaying = false
        )
    }
}