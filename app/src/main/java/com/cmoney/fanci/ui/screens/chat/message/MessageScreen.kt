package com.cmoney.fanci.ui.screens.chat.message

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.cmoney.fanci.extension.findActivity
import com.cmoney.fanci.extension.showInteractDialogBottomSheet
import com.cmoney.fanci.model.ChatMessageWrapper
import com.cmoney.fanci.ui.screens.chat.message.viewmodel.MessageViewModel
import com.cmoney.fanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import com.cmoney.fanci.ui.screens.shared.bottomSheet.MessageInteract
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.MediaIChatContent
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
    onMsgDismissHide: (ChatMessage) -> Unit,
) {
    val isScrollToBottom = messageViewModel.uiState.isSendComplete
    val onInteractClick = object : (MessageInteract) -> Unit {
        override fun invoke(messageInteract: MessageInteract) {
            messageViewModel.onInteractClick(messageInteract)
        }
    }

//    viewModel.startPolling(channelId)
//
//    BackHandler {
//        viewModel.stopPolling()
//    }


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
fun LazyListState.OnBottomReached(
    loadMore: () -> Unit
) {
    // state object which tells us if we should load more
    val shouldLoadMore = remember {
        derivedStateOf {
            // get last visible item
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?:
                // list is empty
                // return false here if loadMore should not be invoked if the list is empty
                return@derivedStateOf true

            // Check if last visible item is the last item in the list
            lastVisibleItem.index == layoutInfo.totalItemsCount - 1
        }
    }

    // Convert the state into a cold flow and collect
    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .collect {
                // if should load more, then invoke loadMore
                if (it) loadMore()
            }
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