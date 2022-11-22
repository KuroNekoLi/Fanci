package com.cmoney.fanci.ui.screens.chat.message

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.cmoney.fanci.extension.findActivity
import com.cmoney.fanci.extension.showInteractDialogBottomSheet
import com.cmoney.fanci.ui.screens.chat.message.viewmodel.MessageViewModel
import com.cmoney.fanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import com.cmoney.fanci.ui.screens.shared.bottomSheet.MessageInteract
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.ChatMessage
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
    viewModel: MessageViewModel = koinViewModel(),
    isScrollToBottom: Boolean = false,
    onInteractClick: (MessageInteract) -> Unit,
    onMsgDismissHide: (ChatMessage) -> Unit,
) {
    viewModel.startPolling(channelId)

    BackHandler {
        viewModel.stopPolling()
    }

    Surface(
        color = LocalColor.current.env_80,
        modifier = modifier,
    ) {
        val message = viewModel.uiState.message

        val context = LocalContext.current
        LazyColumn(state = listState, reverseLayout = true) {
            if (message.isNotEmpty()) {
                itemsIndexed(message) {index, chatMessageWrapper ->
                    MessageContentScreen(
                        chatMessageWrapper = chatMessageWrapper,
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
            viewModel.onLoadMore(channelId)
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
    loadMore : () -> Unit
){
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
    LaunchedEffect(shouldLoadMore){
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
        MessageScreen(
            coroutineScope = rememberCoroutineScope(),
            channelId = "2177",
            onInteractClick = {},
            onMsgDismissHide = {},
        )
    }
}