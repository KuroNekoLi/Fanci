package com.cmoney.fanci.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.MainStateHolder
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.model.viewmodel.ChatRoomViewModelFactory
import com.cmoney.fanci.ui.screens.chat.dialog.DeleteMessageDialogScreen
import com.cmoney.fanci.ui.screens.chat.state.rememberChatRoomState
import com.cmoney.fanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.screens.shared.snackbar.FanciSnackBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.socks.library.KLog

@Composable
fun ChatRoomScreen(
    channelId: String?,
    navController: NavHostController,
    route: (MainStateHolder.Route) -> Unit,
    viewModel: ChatRoomViewModel = viewModel(
        factory =
        ChatRoomViewModelFactory(LocalContext.current)
    )
) {
    val TAG = "ChatRoomScreen"

    val uiState = viewModel.uiState

    val stateHolder = rememberChatRoomState(navController = navController)

    //Screen Callback like onActivityResult
    val bundle = navController.currentBackStackEntryAsState().value

    KLog.i(TAG, "bundle:$bundle")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = stateHolder.scaffoldState,
        topBar = {
            TopBarScreen(
                stateHolder.navController,
                title = "\uD83D\uDC57｜金針菇穿什麼",
                moreEnable = true
            ) {
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
            //公告訊息
            bundle?.apply {
                this.arguments?.apply {
                    val announceModel = this.getParcelable<ChatMessageModel>(AnnounceBundleKey)
                    announceModel?.apply {
                        MessageAnnounceScreen(
                            this,
                            modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp)
                        )
                    }
                }
            }

            //訊息 內文
            MessageScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
                    .weight(1f),
                message = uiState.message,
                coroutineScope = stateHolder.scope
            ) {
                viewModel.onInteractClick(it)
            }

            //回覆
            uiState.replyMessage?.apply {
                ChatReplyScreen(this) {
                    viewModel.removeReply(it)
                }
            }

            //附加圖片
            MessageAttachImageScreen(uiState.imageAttach) {
                viewModel.removeAttach(it)
            }

            //輸入匡
            MessageInput(
                onMessageSend = {
                    viewModel.messageInput(it)
                },
                onAttach = {
                    viewModel.attachImage(it)
                }
            )
        }

        //Alert Dialog
        //刪除訊息 彈窗
        uiState.deleteMessage?.apply {
            DeleteMessageDialogScreen(chatMessageModel = this,
            onConfirm = {
                viewModel.onDeleteClick(it)
            }) {
                viewModel.onDeleteMessageDialogDismiss()
            }
        }

        //隱藏用戶 彈窗
        uiState.hideUserMessage?.apply {
            HideUserDialogScreen(
                this.poster
            ) {
                viewModel.onHideUserDialogDismiss()
            }
        }

        //SnackBar
        FanciSnackBarScreen(message = uiState.snackBarMessage) {
            viewModel.snackBarDismiss()
        }

        //Route
        //跳轉 公告 page
        uiState.announceMessage?.apply {
            route.invoke(MainStateHolder.Route.Announce(this))
            viewModel.routeDone()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatRoomScreenPreview() {
    FanciTheme {
        val route: (MainStateHolder.Route) -> Unit = {
        }
        ChatRoomScreen(
            null,
            rememberNavController(),
            route
        )
    }
}