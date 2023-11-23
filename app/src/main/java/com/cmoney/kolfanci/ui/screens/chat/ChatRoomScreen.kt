package com.cmoney.kolfanci.ui.screens.chat

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.IReplyMessage
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.showToast
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.ReSendFile
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.model.usecase.AttachmentController
import com.cmoney.kolfanci.model.viewmodel.AttachmentViewModel
import com.cmoney.kolfanci.model.vote.VoteModel
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.AnnouncementScreenDestination
import com.cmoney.kolfanci.ui.destinations.CreateChoiceQuestionScreenDestination
import com.cmoney.kolfanci.ui.screens.chat.attachment.ChatRoomAttachmentScreen
import com.cmoney.kolfanci.ui.screens.chat.dialog.DeleteMessageDialogScreen
import com.cmoney.kolfanci.ui.screens.chat.dialog.HideUserDialogScreen
import com.cmoney.kolfanci.ui.screens.chat.dialog.ReportUserDialogScreen
import com.cmoney.kolfanci.ui.screens.chat.message.MessageScreen
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.MessageViewModel
import com.cmoney.kolfanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import com.cmoney.kolfanci.ui.screens.shared.bottomSheet.mediaPicker.MediaPickerBottomSheet
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.snackbar.FanciSnackBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * 聊天室
 *
 * @param channelId 目前頻道id
 * @param jumpChatMessage 指定前往的message
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatRoomScreen(
    channelId: String,
    jumpChatMessage: ChatMessage? = null,
    navController: DestinationsNavigator,
    messageViewModel: MessageViewModel = koinViewModel(),
    attachmentViewModel: AttachmentViewModel = koinViewModel(),
    viewModel: ChatRoomViewModel = koinViewModel(),
    resultRecipient: ResultRecipient<AnnouncementScreenDestination, ChatMessage>,
    choiceRecipient: ResultRecipient<CreateChoiceQuestionScreenDestination, VoteModel>
) {
    val TAG = "ChatRoomScreen"

    //公告訊息
    val announceMessage by viewModel.announceMessage.collectAsState()

    //通知訊息 tip
    val snackBarMessage by messageViewModel.snackBarMessage.collectAsState(null)

    //要回覆的訊息
    val replyMessage by messageViewModel.replyMessage.collectAsState()

    KLog.i(TAG, "open ChatRoomScreen channelId:$channelId")

    //控制 BottomSheet
    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    //是否有讀的權限
    if (Constant.canReadMessage()) {
        if (jumpChatMessage != null) {
            messageViewModel.forwardToMessage(channelId, jumpChatMessage)
        } else {
            messageViewModel.chatRoomFirstFetch(channelId)
        }
    }

    //抓取 公告
    viewModel.fetchAnnounceMessage(channelId)

    //離開頁面處理
    BackHandler(enabled = false) {
        messageViewModel.stopPolling()
//        navController.popBackStack()
    }

    //錯誤訊息提示
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.errorMessage.collect {
            if (it.isNotEmpty()) {
                context.showToast(it)
            }
        }
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

    //附加選擇題 callback
    choiceRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val announceMessage = result.value
                attachmentViewModel.addChoiceAttachment(announceMessage)

                coroutineScope.launch {
                    state.hide()
                }
            }
        }
    }

    //複製訊息
    val copyMessage by messageViewModel.copyMessage.collectAsState()
    copyMessage?.let {
        messageViewModel.copyMessage(it)
        messageViewModel.copyDone()
    }

    //是否訊息發送完成
    val isSendComplete by messageViewModel.isSendComplete.collectAsState()

    //發送完成, 清空附加檔案暫存
    if (isSendComplete) {
        attachmentViewModel.clear()
    }

    //是否呈現上傳中畫面
    var isShowLoading by remember { mutableStateOf(false) }

    //附加檔案
    val attachment by attachmentViewModel.attachment.collectAsState()

    //是否全部File上傳完成
    val attachmentUploadFinish by attachmentViewModel.uploadComplete.collectAsState()

    //有上傳失敗的檔案
    val hasUploadFailedFile by attachmentViewModel.uploadFailed.collectAsState()

    //User 輸入內容
    var inputContent by remember {
        mutableStateOf("")
    }

    if (attachmentUploadFinish.first) {
        isShowLoading = false
        val text = attachmentUploadFinish.second?.toString().orEmpty()

        messageViewModel.messageSend(channelId, text, attachment)

        attachmentViewModel.finishPost()
    }

    var reSendFileClick by remember {
        mutableStateOf<ReSendFile?>(null)
    }

    //是否只有圖片選擇
    val isOnlyPhotoSelector by attachmentViewModel.isOnlyPhotoSelector.collectAsState()

    //主畫面
    ChatRoomScreenView(
        navController = navController,
        channelId = channelId,
        announceMessage = announceMessage,
        isShowLoading = isShowLoading,
        onMsgDismissHide = {
            viewModel.onMsgDismissHide(it)
        },
        replyMessage = replyMessage,
        onDeleteReply = {
            messageViewModel.removeReply(it)
        },
        onDeleteAttach = {
            attachmentViewModel.removeAttach(it)
        },
        onMessageSend = { text ->
            inputContent = text
            AppUserLogger.getInstance().log(Clicked.MessageSendButton)
            isShowLoading = true
            attachmentViewModel.upload(
                channelId = channelId,
                other = text
            )
        },
        onAttachClick = {
            AppUserLogger.getInstance().log(Clicked.MessageInsertImage)
            attachmentViewModel.onAttachClick()
            coroutineScope.launch {
                state.show()
            }
        },
        showOnlyBasicPermissionTip = {
            messageViewModel.showPermissionTip()
        },
        onAttachImageAddClick = {
            attachmentViewModel.onAttachImageAddClick()
            coroutineScope.launch {
                state.show()
            }
        },
        onPreviewAttachmentClick = { attachmentInfoItem ->
            AttachmentController.onAttachmentClick(
                navController = navController,
                attachmentInfoItem = attachmentInfoItem,
                context = context
            )
        },
        attachment = attachment,
        onResend = {
            reSendFileClick = it
        }
    )

    //SnackBar 通知訊息
    if (snackBarMessage != null) {
        FanciSnackBarScreen(
            modifier = Modifier.padding(bottom = 70.dp),
            message = snackBarMessage
        ) {
        }
    }

    //==================== Alert Dialog ====================
    //上傳失敗 彈窗
    if (hasUploadFailedFile) {
        isShowLoading = false
        DialogScreen(
            title = stringResource(id = R.string.chat_fail_title),
            subTitle = stringResource(id = R.string.chat_fail_desc),
            onDismiss = {
                attachmentViewModel.clearUploadFailed()
            },
            content = {
                BlueButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = stringResource(id = R.string.back)
                ) {
                    attachmentViewModel.clearUploadFailed()
                }
            }
        )
    }

    //重新發送  彈窗
    reSendFileClick?.let { reSendFile ->
        ReSendFileDialog(
            reSendFile = reSendFile,
            onDismiss = {
                reSendFileClick = null
            },
            onRemove = {
                attachmentViewModel.removeAttach(reSendFile.attachmentInfoItem)
                reSendFileClick = null
            },
            onResend = {
                attachmentViewModel.onResend(
                    channelId = channelId,
                    uploadFileItem = reSendFile,
                    other = inputContent
                )
                reSendFileClick = null
            }
        )
    }

    //檢舉用戶 彈窗
    val reportMessage by messageViewModel.reportMessage.collectAsState()
    reportMessage?.author?.apply {
        ReportUserDialogScreen(user = this,
            onConfirm = {
                messageViewModel.onReportUser(
                    reason = it,
                    channelId = channelId,
                    contentId = reportMessage?.id.orEmpty()
                )
            }
        ) {
            messageViewModel.onReportUserDialogDismiss()
        }
    }

    //刪除訊息 彈窗
    val deleteMessage by messageViewModel.deleteMessage.collectAsState()
    deleteMessage?.apply {
        DeleteMessageDialogScreen(chatMessageModel = this,
            onConfirm = {
                AppUserLogger.getInstance().log(Clicked.DeleteMessageConfirmDelete)
                messageViewModel.onDeleteMessageDialogDismiss()
                messageViewModel.onDeleteClick(it)
            }) {
            AppUserLogger.getInstance().log(Clicked.DeleteMessageCancel)
            messageViewModel.onDeleteMessageDialogDismiss()
        }
    }

    //封鎖用戶 彈窗
    val hideUserMessage by messageViewModel.hideUserMessage.collectAsState()
    hideUserMessage?.author?.apply {
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
    val routeAnnounceMessage by messageViewModel.routeAnnounceMessage.collectAsState()
    routeAnnounceMessage?.apply {
        navController.navigate(
            AnnouncementScreenDestination(
                message = this,
                channelId = channelId
            )
        )
        messageViewModel.announceRouteDone()
    }

    //多媒體檔案選擇
    MediaPickerBottomSheet(
        navController = navController,
        state = state,
        selectedAttachment = attachment,
        isOnlyPhotoSelector = isOnlyPhotoSelector
    ) {
        attachmentViewModel.attachment(it)
    }
}

/**
 * 重新上傳 dialog
 */
@Composable
fun ReSendFileDialog(
    reSendFile: ReSendFile,
    onDismiss: () -> Unit,
    onRemove: () -> Unit,
    onResend: () -> Unit
) {
    DialogScreen(
        title = reSendFile.title,
        subTitle = reSendFile.description,
        onDismiss = onDismiss,
        content = {
            Column {
                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = stringResource(id = R.string.remove),
                    borderColor = LocalColor.current.text.default_100,
                    onClick = onRemove
                )

                Spacer(modifier = Modifier.height(20.dp))

                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = stringResource(id = R.string.resend),
                    borderColor = LocalColor.current.text.default_100,
                    onClick = onResend
                )
            }
        }
    )
}

@Composable
private fun ChatRoomScreenView(
    navController: DestinationsNavigator,
    channelId: String,
    announceMessage: ChatMessage?,
    onMsgDismissHide: (ChatMessage) -> Unit,
    replyMessage: IReplyMessage?,
    onDeleteReply: (IReplyMessage) -> Unit,
    onDeleteAttach: (AttachmentInfoItem) -> Unit,
    onMessageSend: (text: String) -> Unit,
    onAttachClick: () -> Unit,
    showOnlyBasicPermissionTip: () -> Unit,
    onAttachImageAddClick: () -> Unit,
    attachment: Map<AttachmentType, List<AttachmentInfoItem>>,
    onPreviewAttachmentClick: (AttachmentInfoItem) -> Unit,
    isShowLoading: Boolean,
    onResend: ((ReSendFile) -> Unit)
) {
    Column(
        modifier = Modifier
            .background(LocalColor.current.env_80)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        //公告訊息 display
        announceMessage?.let {
            ChatRoomAnnounceScreen(
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
            navController = navController,
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

        //附加檔案
        ChatRoomAttachmentScreen(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.primary),
            attachment = attachment,
            isShowLoading = isShowLoading,
            onDelete = {
                onDeleteAttach.invoke(it)
            },
            onAddImage = onAttachImageAddClick,
            onClick = onPreviewAttachmentClick,
            onResend = onResend
        )

        //輸入匡
        MessageInput(
            onMessageSend = {
                onMessageSend.invoke(it)
            },
            showOnlyBasicPermissionTip = showOnlyBasicPermissionTip
        ) {
            onAttachClick.invoke()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatRoomScreenPreview() {
    FanciTheme {
        ChatRoomScreenView(
            navController = EmptyDestinationsNavigator,
            channelId = "",
            announceMessage = ChatMessage(),
            onMsgDismissHide = {},
            replyMessage = IReplyMessage(),
            onDeleteReply = {},
            onDeleteAttach = {},
            onMessageSend = {},
            onAttachClick = {},
            showOnlyBasicPermissionTip = {},
            onAttachImageAddClick = {},
            attachment = emptyMap(),
            onPreviewAttachmentClick = {},
            isShowLoading = false,
            onResend = {}
        )
    }
}