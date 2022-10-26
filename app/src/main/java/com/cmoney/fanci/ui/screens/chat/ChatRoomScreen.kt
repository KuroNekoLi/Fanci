package com.cmoney.fanci.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.ui.screens.chat.state.rememberChatRoomState
import com.cmoney.fanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import com.cmoney.fanci.ui.theme.FanciTheme
import com.socks.library.KLog

@Composable
fun ChatRoomScreen(
    channelId: String?,
    navController: NavHostController,
    viewModel: ChatRoomViewModel = viewModel()
) {
    val TAG = "ChatRoomScreen"

    val uiState = viewModel.uiState

    val stateHolder = rememberChatRoomState(navController = navController)

    Scaffold(
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
                onReply = {
                    viewModel.replyMessage(it)
                }
            )

            uiState.replyMessage?.apply {
                ChatReplyScreen(this) {
                    viewModel.removeReply(it)
                }
            }

            MessageAttachImageScreen(uiState.imageAttach) {
                viewModel.removeAttach(it)
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
}

@Preview(showBackground = true)
@Composable
fun ChatRoomScreenPreview() {
    FanciTheme {
        ChatRoomScreen(null, rememberNavController())
    }
}