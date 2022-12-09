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
import com.cmoney.fanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.screens.shared.snackbar.FanciSnackBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun ChatRoomScreen(
    channelId: String,
    channelTitle: String,
    navController: DestinationsNavigator,
//    route: (MainStateHolder.Route) -> Unit,
    stateHolder: ChatRoomState = rememberChatRoomState()
) {
    val TAG = "ChatRoomScreen"

    val uiState = stateHolder.viewModel.uiState
    val viewModel = stateHolder.viewModel
    val messageViewModel = stateHolder.messageViewModel

    //Screen Callback like onActivityResult
//    val bundle = navController.currentBackStackEntryAsState().value

//    KLog.i(TAG, "bundle:$bundle")
    KLog.i(TAG, "channelId:$channelId")

    messageViewModel.startPolling(channelId)

    viewModel.fetchAnnounceMessage(channelId)

    BackHandler {
        messageViewModel.stopPolling()
        navController.popBackStack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = stateHolder.scaffoldState,
        topBar = {
            TopBarScreen(
                title = channelTitle,
                moreEnable = true,
                moreClick = {
                    KLog.i(TAG, "more click.")
                },
                backClick = {
                    navController.popBackStack()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(LocalColor.current.env_80)
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Bottom
        ) {
            //公告訊息 send to server
//            bundle?.apply {
//                this.arguments?.apply {
//                    val announceModel = this.getParcelable<ChatMessage>(AnnounceBundleKey)
//                    announceModel?.apply {
//                        viewModel.announceMessageToServer(
//                            channelId,
//                            this
//                        )
//                    }
//                }
//            }

            //公告訊息 display
            uiState.announceMessage?.let {
                MessageAnnounceScreen(
                    it,
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp)
                )
            }

            //訊息 內文
            MessageScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
                    .weight(1f),
                channelId = channelId,
                onMsgDismissHide = {
                    viewModel.onMsgDismissHide(it)
                }
            )

            //回覆
            messageViewModel.uiState.replyMessage?.apply {
                ChatReplyScreen(this) {
                    messageViewModel.removeReply(it)
                }
            }

            //附加圖片
            MessageAttachImageScreen(messageViewModel.uiState.imageAttach) {
                messageViewModel.removeAttach(it)
            }

            //輸入匡
            MessageInput(
                onMessageSend = {
                    messageViewModel.messageSend(channelId, it)
                },
                onAttach = {
                    messageViewModel.attachImage(it)
                }
            )
        }

        //Alert Dialog
        //檢舉用戶
        uiState.reportUser?.author?.apply {
            ReportUserDialogScreen(user = this,
                onConfirm = {
                    viewModel.onReportUser(it)
                }
            ) {
                viewModel.onReportUserDialogDismiss()
            }
        }

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
        uiState.hideUserMessage?.author?.apply {
            HideUserDialogScreen(
                this,
                onConfirm = {
                    viewModel.onHideUserConfirm(it)
                }
            ) {
                viewModel.onHideUserDialogDismiss()
            }
        }

        //SnackBar
        FanciSnackBarScreen(
            modifier = Modifier.padding(bottom = 70.dp),
            message = uiState.snackBarMessage
        ) {
            viewModel.snackBarDismiss()
        }

        //Route
        //跳轉 公告 page
        messageViewModel.uiState.routeAnnounceMessage?.apply {
            // TODO:
//            route.invoke(MainStateHolder.Route.Announce(this))
            messageViewModel.announceRouteDone()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatRoomScreenPreview() {
    FanciTheme {
        val route: (MainStateHolder.Route) -> Unit = {
        }
//        ChatRoomScreen(
//            "",
//            "",
//            rememberNavController(),
//            route
//        )
    }
}