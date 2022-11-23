package com.cmoney.fanci.ui.screens.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.MainStateHolder
import com.cmoney.fanci.ui.screens.chat.dialog.DeleteMessageDialogScreen
import com.cmoney.fanci.ui.screens.chat.dialog.HideUserDialogScreen
import com.cmoney.fanci.ui.screens.chat.dialog.ReportUserDialogScreen
import com.cmoney.fanci.ui.screens.chat.message.MessageScreen
import com.cmoney.fanci.ui.screens.chat.state.ChatRoomState
import com.cmoney.fanci.ui.screens.chat.state.rememberChatRoomState
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.screens.shared.snackbar.FanciSnackBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.socks.library.KLog

@Composable
fun ChatRoomScreen(
    channelId: String,
    channelTitle: String,
    navController: NavHostController,
    route: (MainStateHolder.Route) -> Unit,
    stateHolder: ChatRoomState = rememberChatRoomState(navController = navController)
) {
    val TAG = "ChatRoomScreen"

    val uiState = stateHolder.viewModel.uiState

    //Screen Callback like onActivityResult
    val bundle = navController.currentBackStackEntryAsState().value

    KLog.i(TAG, "bundle:$bundle")

    KLog.i(TAG, "channelId:$channelId")

    stateHolder.messageViewModel.startPolling(channelId)

    BackHandler {
        stateHolder.messageViewModel.stopPolling()
        navController.popBackStack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = stateHolder.scaffoldState,
        topBar = {
            TopBarScreen(
                stateHolder.navController,
                title = channelTitle,
                moreEnable = true
            ) {
                KLog.i(TAG, "more click.")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(LocalColor.current.env_80)
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Bottom
        ) {
            //公告訊息
            bundle?.apply {
                this.arguments?.apply {
                    // TODO:
//                    val announceModel = this.getParcelable<ChatMessage>(AnnounceBundleKey)
//                    announceModel?.apply {
//                        MessageAnnounceScreen(
//                            this,
//                            modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp)
//                        )
//                    }
                }
            }

            //訊息 內文
            MessageScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
                    .weight(1f),
                channelId = channelId,
                onMsgDismissHide = {
                    stateHolder.viewModel.onMsgDismissHide(it)
                }
            )

            //回覆
            stateHolder.messageViewModel.uiState.replyMessage?.apply {
                ChatReplyScreen(this) {
                    stateHolder.messageViewModel.removeReply(it)
                }
            }

            //附加圖片
            MessageAttachImageScreen(stateHolder.messageViewModel.uiState.imageAttach) {
                stateHolder.messageViewModel.removeAttach(it)
            }

            //輸入匡
            MessageInput(
                onMessageSend = {
                    stateHolder.messageViewModel.messageSend(channelId, it)
                },
                onAttach = {
                    stateHolder.messageViewModel.attachImage(it)
                }
            )
        }

        //Alert Dialog
        //檢舉用戶
        uiState.reportUser?.author?.apply {
            ReportUserDialogScreen(user = this,
                onConfirm = {
                    stateHolder.viewModel.onReportUser(it)
                }
            ) {
                stateHolder.viewModel.onReportUserDialogDismiss()
            }
        }

        //刪除訊息 彈窗
        uiState.deleteMessage?.apply {
            DeleteMessageDialogScreen(chatMessageModel = this,
                onConfirm = {
                    stateHolder.viewModel.onDeleteClick(it)
                }) {
                stateHolder.viewModel.onDeleteMessageDialogDismiss()
            }
        }

        //隱藏用戶 彈窗
        uiState.hideUserMessage?.author?.apply {
            HideUserDialogScreen(
                this,
                onConfirm = {
                    stateHolder.viewModel.onHideUserConfirm(it)
                }
            ) {
                stateHolder.viewModel.onHideUserDialogDismiss()
            }
        }

        //SnackBar
        FanciSnackBarScreen(
            modifier = Modifier.padding(bottom = 70.dp),
            message = uiState.snackBarMessage
        ) {
            stateHolder.viewModel.snackBarDismiss()
        }

        //Route
        //跳轉 公告 page
        uiState.announceMessage?.apply {
            route.invoke(MainStateHolder.Route.Announce(this))
            stateHolder.viewModel.routeDone()
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
            "",
            "",
            rememberNavController(),
            route
        )
    }
}