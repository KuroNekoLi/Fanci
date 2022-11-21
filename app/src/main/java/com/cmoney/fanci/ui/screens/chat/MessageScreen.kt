package com.cmoney.fanci.ui.screens.chat

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.cmoney.fanci.extension.findActivity
import com.cmoney.fanci.extension.showInteractDialogBottomSheet
import com.cmoney.fanci.model.ChatMessageWrapper
import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import com.cmoney.fanci.ui.screens.shared.bottomSheet.MessageInteract
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.socks.library.KLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 聊天室 區塊
 */
@Composable
fun MessageScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    listState: LazyListState = rememberLazyListState(),
    message: List<ChatMessageWrapper>,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    isScrollToBottom: Boolean = false,
    onInteractClick: (MessageInteract) -> Unit,
    onMsgDismissHide: (ChatMessage) -> Unit,
) {
    Surface(
        color = LocalColor.current.env_80,
        modifier = modifier,
    ) {
        val context = LocalContext.current
        LazyColumn(state = listState, reverseLayout = true) {
            if (message.isNotEmpty()) {
                items(message) { message ->
                    MessageContentScreen(
                        chatMessageWrapper = message,
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
                                        message.message,
                                        onInteractClick
                                    )
                                }
                                is MessageContentCallback.MsgDismissHideClick -> {
                                    onMsgDismissHide.invoke(message.message)
                                }
                            }
                        }
                    )
                }
            }
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
            message = listOf(
                ChatMessageWrapper(
                    message = ChatRoomUseCase.mockMessage
                )
            ),
            coroutineScope = rememberCoroutineScope(),
            onInteractClick = {},
            onMsgDismissHide = {},
        )
    }
}