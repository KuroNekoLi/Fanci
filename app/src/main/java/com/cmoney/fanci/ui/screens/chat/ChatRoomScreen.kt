package com.cmoney.fanci.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.ui.screens.chat.state.rememberChatRoomState
import com.cmoney.fanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import com.cmoney.fanci.ui.screens.shared.bottomSheet.MessageInteract
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.White_494D54
import com.socks.library.KLog
import kotlinx.coroutines.launch

@Composable
fun ChatRoomScreen(
    channelId: String?,
    navController: NavHostController,
    viewModel: ChatRoomViewModel = viewModel()
) {
    val TAG = "ChatRoomScreen"

    val uiState = viewModel.uiState

    val stateHolder = rememberChatRoomState(navController = navController)

    Column {
        Scaffold(
            modifier = Modifier.weight(1f),
            scaffoldState = stateHolder.scaffoldState,
            topBar = {
                ChatRoomTopBarScreen(stateHolder.navController) {
                    KLog.i(TAG, "more click.")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colors.surface)
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Bottom
            ) {
                MessageScreen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
                        .weight(1f),
                    message = uiState.message,
                    coroutineScope = stateHolder.scope
                ) {
                    viewModel.onInteractClick(it)

                    //todo
                    if (it is MessageInteract.Recycle) {
                        stateHolder.scope.launch {
                            stateHolder.showRecycleMessageSnackBar()
                        }
                    }
                }

                uiState.replyMessage?.apply {
                    ChatReplyScreen(this) {
                        viewModel.removeReply(it)
                    }
                }

                MessageAttachImageScreen(uiState.imageAttach) {
                    viewModel.removeAttach(it)
                }
            }
        }

        MessageInput(
            onMessageSend = {
                viewModel.messageInput(it)
            },
            onAttach = {
                viewModel.attachImage(it)
            }
        )
    }

}

@Composable
fun SnackBar(snackbarHostState: SnackbarHostState) {
    SnackbarHost(
        hostState = snackbarHostState
    ) {
        Box(
            modifier = Modifier
                .background(White_494D54)
                .clip(RoundedCornerShape(19.dp))
        ) {

            Row {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = "")
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = it.message, color = Color.White, fontSize = 16.sp)
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun ChatRoomScreenPreview() {
    FanciTheme {
        ChatRoomScreen(null, rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun SnackBarPreview() {
    FanciTheme {
        SnackBar(SnackbarHostState())
    }
}