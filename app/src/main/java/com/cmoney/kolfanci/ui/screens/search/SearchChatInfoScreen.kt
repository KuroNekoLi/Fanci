package com.cmoney.kolfanci.ui.screens.search

import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.toColor
import com.cmoney.kolfanci.model.ChatMessageWrapper
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.common.AutoLinkText
import com.cmoney.kolfanci.ui.common.ChatTimeText
import com.cmoney.kolfanci.ui.destinations.ChannelScreenDestination
import com.cmoney.kolfanci.ui.screens.chat.message.MediaContent
import com.cmoney.kolfanci.ui.screens.chat.message.MessageOGScreen
import com.cmoney.kolfanci.ui.screens.chat.message.MessageReplayScreen
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.ImageAttachState
import com.cmoney.kolfanci.ui.screens.search.viewmodel.SearchViewModel
import com.cmoney.kolfanci.ui.screens.shared.ChatUsrAvatarScreen
import com.cmoney.kolfanci.ui.screens.shared.EmojiCountScreen
import com.cmoney.kolfanci.ui.screens.shared.toolbar.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.kolfanci.ui.theme.White_767A7F
import com.cmoney.kolfanci.utils.Utils
import com.google.accompanist.flowlayout.FlowRow
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

/**
 *  搜尋聊天結果顯示畫面, 不能與使用者互動, 展示用, 可以上下滑動看更多
 *
 * @param group 目前社團
 * @param channel 目前頻道下
 * @param message 搜尋點擊的訊息
 */
@Destination
@Composable
fun SearchChatInfoScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    channel: Channel,
    message: ChatMessage,
    searchViewModel: SearchViewModel = koinViewModel()
) {
    val TAG = "SearchChatInfoScreen"
    val chatMessageInfo by searchViewModel.chatInfoMessage.collectAsState(null)

    val scrollToPosition by searchViewModel.scrollToPosition.collectAsState()

    LaunchedEffect(key1 = Unit) {
        if (chatMessageInfo == null) {
            searchViewModel.onChatItemClick(
                channel = channel,
                searchChatMessage = message
            )
        }
    }

    chatMessageInfo?.let {
        SearchChatInfoViewScreen(
            modifier = modifier,
            navController = navController,
            channelTitle = channel.name.orEmpty(),
            message = it,
            scrollToPosition = scrollToPosition,
            onForwardChatClick = {
                KLog.i(TAG, "onForwardChatClick:$message")
                navController.navigate(
                    ChannelScreenDestination(
                        channel = channel,
                        jumpChatMessage = message
                    )
                )
            }
        )
    }
}

@Composable
private fun SearchChatInfoViewScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    channelTitle: String,
    message: List<ChatMessageWrapper>,
    scrollToPosition: Int,
    onForwardChatClick: () -> Unit
) {
    val listState: LazyListState = rememberLazyListState()

    LaunchedEffect(key1 = scrollToPosition) {
        listState.scrollToItem(index = scrollToPosition)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        backgroundColor = LocalColor.current.env_80,
        topBar = {
            TopBarScreen(
                title = channelTitle,
                backClick = {
                    navController.popBackStack()
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                state = listState,
                reverseLayout = true
            ) {
                if (message.isNotEmpty()) {
                    itemsIndexed(message) { index, chatMessageWrapper ->
                        SearchChatMessageContent(
                            navController = navController,
                            chatMessageWrapper = chatMessageWrapper
                        )
                    }
                }
            }

            //Footer
            Box(
                modifier = Modifier
                    .height(95.dp)
                    .fillMaxWidth()
                    .background(LocalColor.current.env_100)
                    .clickable {
                        onForwardChatClick.invoke()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.jump_to_chatroom),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalColor.current.primary
                )
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun SearchChatInfoScreenPreview() {
    FanciTheme {
        SearchChatInfoViewScreen(
            navController = EmptyDestinationsNavigator,
            channelTitle = "\uD83D\uDC57｜金針菇穿什麼",
            message = listOf(
                ChatMessageWrapper(
                    message = MockData.mockMessage,
                    uploadAttachPreview = listOf(
                        ImageAttachState(
                            uri = Uri.parse("")
                        )
                    ),
                    isPendingSendMessage = true
                )
            ),
            scrollToPosition = 0,
            onForwardChatClick = {}
        )
    }
}

@Composable
private fun SearchChatMessageContent(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    chatMessageWrapper: ChatMessageWrapper
) {
    val messageModel = chatMessageWrapper.message

    val contentPaddingModifier = Modifier.padding(top = 10.dp, start = 40.dp, end = 10.dp)

    Column(modifier = modifier.padding(top = 10.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)) {

        when (chatMessageWrapper.messageType) {
            //時間標記
            ChatMessageWrapper.MessageType.TimeBar -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(LocalColor.current.background),
                    contentAlignment = Alignment.Center
                ) {
                    val createTime = chatMessageWrapper.message.createUnixTime?.times(1000) ?: 0
                    Text(
                        text = Utils.getTimeGroupByKey(
                            createTime
                        ),
                        fontSize = 14.sp,
                        color = White_767A7F
                    )
                }
            }
            //普通內文
            ChatMessageWrapper.MessageType.Default -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    //大頭貼
                    messageModel.author?.let {
                        val firstRoleColorName = it.roleInfos?.firstOrNull()?.color

                        val roleColor = LocalColor.current.roleColor.colors.filter { color ->
                            color.name == firstRoleColorName
                        }.map { fanciColor ->
                            fanciColor.hexColorCode?.toColor()
                                ?: LocalColor.current.text.default_100
                        }.firstOrNull()

                        ChatUsrAvatarScreen(
                            user = it,
                            nickNameColor = roleColor ?: LocalColor.current.text.default_100
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    //發文時間
                    ChatTimeText(
                        Utils.getDisplayTime(
                            messageModel.createUnixTime?.times(1000) ?: 0
                        )
                    )
                }

                //Reply
                messageModel.replyMessage?.apply {
                    MessageReplayScreen(
                        reply = this,
                        modifier = contentPaddingModifier
                            .clip(RoundedCornerShape(9.dp))
                            .background(LocalColor.current.background)
                    )
                }

                //內文
                messageModel.content?.text?.apply {
                    if (this.isNotEmpty()) {
                        AutoLinkText(
                            modifier = contentPaddingModifier,
                            text = this,
                            fontSize = 17.sp,
                            color = LocalColor.current.text.default_100
                        )

                        //OG
                        Utils.extractLinks(this).forEach { url ->
                            MessageOGScreen(modifier = contentPaddingModifier, url = url)
                        }
                    }
                }

                messageModel.content?.medias?.let {
                    MediaContent(
                        navController = navController,
                        medias = it,
                        isClickable = false)
                }

                //Emoji
                messageModel.emojiCount?.apply {
                    FlowRow(
                        modifier = contentPaddingModifier.fillMaxWidth(),
                        crossAxisSpacing = 10.dp
                    ) {
                        Utils.emojiMapping(this).forEach { emoji ->
                            if (emoji.second != 0) {
                                EmojiCountScreen(
                                    modifier = Modifier
                                        .padding(end = 10.dp),
                                    emojiResource = emoji.first,
                                    countText = emoji.second.toString()
                                ) {
                                }
                            }
                        }
                    }
                }
            }

            ChatMessageWrapper.MessageType.Blocker -> {}
            ChatMessageWrapper.MessageType.Blocking -> {}
            ChatMessageWrapper.MessageType.Delete -> {}
            ChatMessageWrapper.MessageType.RecycleMessage -> {}
        }
    }
}

@Preview
@Composable
fun SearchChatMessageContentPreview() {
    FanciTheme {
        SearchChatMessageContent(
            navController = EmptyDestinationsNavigator,
            chatMessageWrapper = ChatMessageWrapper(
                message = MockData.mockMessage,
                uploadAttachPreview = listOf(
                    ImageAttachState(
                        uri = Uri.parse("")
                    )
                ),
                isPendingSendMessage = true
            )
        )
    }
}