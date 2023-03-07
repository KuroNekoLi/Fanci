package com.cmoney.kolfanci.ui.screens.chat.message

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.MediaIChatContent
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.OnBottomReached
import com.cmoney.kolfanci.extension.findActivity
import com.cmoney.kolfanci.extension.showInteractDialogBottomSheet
import com.cmoney.kolfanci.model.ChatMessageWrapper
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.MessageViewModel
import com.cmoney.kolfanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import com.cmoney.kolfanci.ui.screens.shared.bottomSheet.MessageInteract
import com.cmoney.kolfanci.ui.theme.Color_80FFFFFF
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.socks.library.KLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * 聊天室 區塊
 */
@Composable
fun MessageScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    listState: LazyListState = rememberLazyListState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    channelId: String,
    messageViewModel: MessageViewModel = koinViewModel(),
    viewModel: ChatRoomViewModel = koinViewModel(),
    onMsgDismissHide: (ChatMessage) -> Unit
) {
    val isScrollToBottom = messageViewModel.uiState.isSendComplete
    val onInteractClick = object : (MessageInteract) -> Unit {
        override fun invoke(messageInteract: MessageInteract) {
            messageViewModel.onInteractClick(messageInteract)
        }
    }

    if (messageViewModel.uiState.message.isNotEmpty()) {
        MessageScreenView(
            modifier = modifier,
            message = messageViewModel.uiState.message,
            blockingList = viewModel.uiState.blockingList.map {
                it.id.orEmpty()
            },
            blockerList = viewModel.uiState.blockerList.map {
                it.id.orEmpty()
            },
            listState = listState,
            coroutineScope = coroutineScope,
            onInteractClick = onInteractClick,
            onMsgDismissHide = onMsgDismissHide,
            isScrollToBottom = isScrollToBottom,
            onLoadMore = {
                messageViewModel.onLoadMore(channelId)
            }
        )
    } else {
        //Empty Message
        EmptyMessageContent(modifier = modifier)
    }

}

@Composable
private fun MessageScreenView(
    modifier: Modifier = Modifier,
    message: List<ChatMessageWrapper>,
    blockingList: List<String>,
    blockerList: List<String>,
    listState: LazyListState,
    coroutineScope: CoroutineScope,
    onInteractClick: (MessageInteract) -> Unit,
    onMsgDismissHide: (ChatMessage) -> Unit,
    isScrollToBottom: Boolean,
    onLoadMore: () -> Unit
) {
    Surface(
        color = LocalColor.current.env_80,
        modifier = modifier,
    ) {
        val context = LocalContext.current
        LazyColumn(state = listState, reverseLayout = true) {
            if (message.isNotEmpty()) {
                itemsIndexed(message) { index, chatMessageWrapper ->
                    var isBlocking = false
                    chatMessageWrapper.message.author?.id?.let { authUserId ->
                        isBlocking = blockingList.contains(authUserId)
                    }

                    var isBlocker = false
                    chatMessageWrapper.message.author?.id?.let { authUserId ->
                        isBlocker = blockerList.contains(authUserId)
                    }

                    MessageContentScreen(
                        chatMessageWrapper = chatMessageWrapper.copy(
                            isBlocking = isBlocking,
                            isBlocker = isBlocker
                        ),
                        coroutineScope = coroutineScope,
                        onMessageContentCallback = {
                            when (it) {
                                is MessageContentCallback.EmojiClick -> {
                                    onInteractClick.invoke(
                                        MessageInteract.EmojiClick(
                                            it.message,
                                            it.resourceId
                                        )
                                    )
                                }
                                is MessageContentCallback.LongClick -> {
                                    showInteractDialog(
                                        context.findActivity(),
                                        chatMessageWrapper.message,
                                        onInteractClick
                                    )
                                }
                                is MessageContentCallback.MsgDismissHideClick -> {
                                    onMsgDismissHide.invoke(chatMessageWrapper.message)
                                }
                            }
                        }
                    )
                }
            }
        }

        listState.OnBottomReached {
            KLog.i("TAG", "load more....")
            onLoadMore.invoke()
        }

        if (isScrollToBottom) {
            LaunchedEffect(message.size) {
                CoroutineScope(Dispatchers.Main).launch {
//                    delay(800)
                    listState.scrollToItem(index = 0)
                }
            }
        }
    }
}

@Composable
private fun EmptyMessageContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(LocalColor.current.env_80),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AsyncImage(
            modifier = Modifier.size(186.dp, 248.dp),
            model = R.drawable.empty_message, contentDescription = "empty message"
        )

        Spacer(modifier = Modifier.height(43.dp))

        Text(text = "快成為第一個在聊天室發言的人！", fontSize = 16.sp, color = Color_80FFFFFF)
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyMessageContentPreview() {
    FanciTheme {
        EmptyMessageContent()
    }
}

/**
 * 互動式 彈窗
 */
fun showInteractDialog(
    activity: Activity,
    message: ChatMessage,
    onInteractClick: (MessageInteract) -> Unit
) {
    val TAG = "MessageScreen"
    KLog.i(TAG, "showInteractDialog:$message")
    activity.showInteractDialogBottomSheet(message, onInteractClick)
}

@Preview(showBackground = true)
@Composable
fun MessageScreenPreview() {
    FanciTheme {
        MessageScreenView(
            message = listOf(
                ChatMessageWrapper(
                    message = ChatMessage(
                        content = MediaIChatContent(
                            text = "Message"
                        )
                    )
                )
            ),
            coroutineScope = rememberCoroutineScope(),
            listState = rememberLazyListState(),
            isScrollToBottom = false,
            onInteractClick = {},
            onMsgDismissHide = {},
            onLoadMore = {},
            blockingList = emptyList(),
            blockerList = emptyList()
        )
    }
}