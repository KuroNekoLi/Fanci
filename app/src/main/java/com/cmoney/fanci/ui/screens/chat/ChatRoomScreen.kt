package com.cmoney.fanci.ui.screens.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanci.MainStateHolder
import com.cmoney.fanci.destinations.AnnouncementScreenDestination
import com.cmoney.fanci.extension.showToast
import com.cmoney.fanci.ui.screens.chat.dialog.DeleteMessageDialogScreen
import com.cmoney.fanci.ui.screens.chat.dialog.HideUserDialogScreen
import com.cmoney.fanci.ui.screens.chat.dialog.ReportUserDialogScreen
import com.cmoney.fanci.ui.screens.chat.message.MessageScreen
import com.cmoney.fanci.ui.screens.chat.message.viewmodel.MessageViewModel
import com.cmoney.fanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.screens.shared.snackbar.FanciSnackBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun ChatRoomScreen(
    channelId: String,
    channelTitle: String,
    navController: DestinationsNavigator,
    messageViewModel: MessageViewModel = koinViewModel(),
    viewModel: ChatRoomViewModel = koinViewModel(),
    resultRecipient: ResultRecipient<AnnouncementScreenDestination, ChatMessage>
) {
    val TAG = "ChatRoomScreen"

    val uiState = viewModel.uiState

    KLog.i(TAG, "channelId:$channelId")

    messageViewModel.startPolling(channelId)

    viewModel.fetchAnnounceMessage(channelId)

    BackHandler {
        messageViewModel.stopPolling()
        navController.popBackStack()
    }

    uiState.errorMessage?.let {
        LocalContext.current.showToast(it)
        viewModel.errorMessageDone()
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                val announceMessage = result.value
                viewModel.announceMessageToServer(
                    channelId,
                    announceMessage
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = channelTitle,
                moreEnable = false,
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
            navController.navigate(
                AnnouncementScreenDestination(
                    this
                )
            )
            messageViewModel.announceRouteDone()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatRoomScreenPreview() {
    FanciTheme {
    }
}