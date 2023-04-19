package com.cmoney.kolfanci.ui.screens.chat

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.kolfanci.extension.showToast
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.ui.destinations.AnnouncementScreenDestination
import com.cmoney.kolfanci.ui.screens.chat.dialog.DeleteMessageDialogScreen
import com.cmoney.kolfanci.ui.screens.chat.dialog.HideUserDialogScreen
import com.cmoney.kolfanci.ui.screens.chat.dialog.ReportUserDialogScreen
import com.cmoney.kolfanci.ui.screens.chat.message.MessageScreen
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.MessageViewModel
import com.cmoney.kolfanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.snackbar.CustomMessage
import com.cmoney.kolfanci.ui.screens.shared.snackbar.FanciSnackBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
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

    if (Constant.canReadMessage()) {
        messageViewModel.startPolling(channelId)
    }

    //抓取 公告
    viewModel.fetchAnnounceMessage(channelId)

    BackHandler {
        messageViewModel.stopPolling()
        navController.popBackStack()
    }

    //錯誤訊息提示
    uiState.errorMessage?.let {
        LocalContext.current.showToast(it)
        viewModel.errorMessageDone()
    }

    //設定公告 callback
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

    messageViewModel.uiState.copyMessage?.let {
        viewModel.copyMessage(it)
        messageViewModel.copyDone()
    }

    ChatRoomScreenView(
        channelId = channelId,
        channelTitle = channelTitle,
        navController = navController,
        announceMessage = uiState.announceMessage,
        onMsgDismissHide = {
            viewModel.onMsgDismissHide(it)
        },
        replyMessage = messageViewModel.uiState.replyMessage,
        onDeleteReply = {
            messageViewModel.removeReply(it)
        },
        imageAttach = messageViewModel.uiState.imageAttach,
        onDeleteAttach = {
            messageViewModel.removeAttach(it)
        },
        onMessageSend = {
            messageViewModel.messageSend(channelId, it)
        },
        onAttach = {
            messageViewModel.attachImage(it)
        },
        showOnlyBasicPermissionTip = {
            messageViewModel.showPermissionTip()
        },
        snackBarMessage = messageViewModel.uiState.snackBarMessage,
        onSnackBarDismiss = {
            messageViewModel.snackBarDismiss()
        }
    )

    //Alert Dialog
    //檢舉用戶
    messageViewModel.uiState.reportMessage?.author?.apply {
        ReportUserDialogScreen(user = this,
            onConfirm = {
                messageViewModel.onReportUser(
                    reason = it,
                    channelId = channelId,
                    contentId = messageViewModel.uiState.reportMessage?.id.orEmpty()
                )
            }
        ) {
            messageViewModel.onReportUserDialogDismiss()
        }
    }

    //刪除訊息 彈窗
    messageViewModel.uiState.deleteMessage?.apply {
        DeleteMessageDialogScreen(chatMessageModel = this,
            onConfirm = {
                messageViewModel.onDeleteMessageDialogDismiss()
                messageViewModel.onDeleteClick(it)
            }) {
            messageViewModel.onDeleteMessageDialogDismiss()
        }
    }

    //封鎖用戶 彈窗
    messageViewModel.uiState.hideUserMessage?.author?.apply {
        HideUserDialogScreen(
            this,
            onConfirm = {
                viewModel.onBlockingUserConfirm(it)
                messageViewModel.onHideUserDialogDismiss()
            }
        ) {
            messageViewModel.onHideUserDialogDismiss()
        }
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

@Composable
private fun ChatRoomScreenView(
    channelId: String,
    channelTitle: String,
    navController: DestinationsNavigator,
    announceMessage: ChatMessage?,
    onMsgDismissHide: (ChatMessage) -> Unit,
    replyMessage: ChatMessage?,
    onDeleteReply: (ChatMessage) -> Unit,
    imageAttach: List<Uri>,
    onDeleteAttach: (Uri) -> Unit,
    onMessageSend: (text: String) -> Unit,
    onAttach: (Uri) -> Unit,
    showOnlyBasicPermissionTip: () -> Unit,
    snackBarMessage: CustomMessage?,
    onSnackBarDismiss: () -> Unit
) {
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
            announceMessage?.let {
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
                    onMsgDismissHide.invoke(it)
                }
            )

            //回覆
            replyMessage?.apply {
                ChatReplyScreen(this) {
                    onDeleteReply.invoke(it)
                }
            }

            //附加圖片
            MessageAttachImageScreen(imageAttach) {
                onDeleteAttach.invoke(it)
            }

            //輸入匡
            MessageInput(
                onMessageSend = {
                    onMessageSend.invoke(it)
                },
                onAttach = {
                    onAttach.invoke(it)
                },
                showOnlyBasicPermissionTip = showOnlyBasicPermissionTip
            )
        }

        //SnackBar
        FanciSnackBarScreen(
            modifier = Modifier.padding(bottom = 70.dp),
            message = snackBarMessage
        ) {
            onSnackBarDismiss.invoke()
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ChatRoomScreenPreview() {
    FanciTheme {
        ChatRoomScreenView(
            channelId = "",
            channelTitle = "",
            navController = EmptyDestinationsNavigator,
            announceMessage = ChatMessage(),
            onMsgDismissHide = {},
            replyMessage = ChatMessage(),
            onDeleteReply = {},
            imageAttach = emptyList(),
            onDeleteAttach = {},
            onMessageSend = {},
            onAttach = {},
            showOnlyBasicPermissionTip = {},
            snackBarMessage = null,
            onSnackBarDismiss = {}
        )
    }
}