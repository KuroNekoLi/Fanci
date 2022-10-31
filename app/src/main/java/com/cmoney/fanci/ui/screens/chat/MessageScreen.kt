package com.cmoney.fanci.ui.screens.chat

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.cmoney.fanci.extension.findActivity
import com.cmoney.fanci.extension.showInteractDialogBottomSheet
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import com.cmoney.fanci.ui.screens.shared.bottomSheet.MessageInteract
import com.cmoney.fanci.ui.theme.FanciTheme
import com.socks.library.KLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 聊天室 區塊
 */
@Composable
fun MessageScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    listState: LazyListState = rememberLazyListState(),
    message: List<ChatMessageModel>,
    coroutineScope: CoroutineScope,
    onInteractClick: (MessageInteract) -> Unit,
    onMsgDismissHide: (ChatMessageModel) -> Unit
) {
    Surface(
        color = MaterialTheme.colors.surface,
        modifier = modifier,
    ) {
        val context = LocalContext.current
        LazyColumn(state = listState) {
            if (message.isNotEmpty()) {
                items(message) { message ->
                    MessageContentScreen(
                        messageModel = message,
                        coroutineScope = coroutineScope,
                        onMsgLongClick = {
                            showInteractDialog(context.findActivity(), message, onInteractClick)
                        },
                        onMsgDismissHide = {
                            onMsgDismissHide.invoke(it)
                        }
                    )
                }
            }
        }

        LaunchedEffect(message.size) {
            CoroutineScope(Dispatchers.Main).launch {
                listState.scrollToItem(index = message.size)
            }
        }
    }
}

/**
 * 互動式 彈窗
 */
fun showInteractDialog(
    activity: Activity,
    message: ChatMessageModel,
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
            message = listOf(
                ChatRoomUseCase.allMessageType
            ),
            onInteractClick = {}
        ) {

        }
    }
}