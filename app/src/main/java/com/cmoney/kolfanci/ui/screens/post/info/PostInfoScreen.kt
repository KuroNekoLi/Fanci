package com.cmoney.kolfanci.ui.screens.post.info

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChannelTabType
import com.cmoney.fanciapi.fanci.model.DeleteStatus
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.MediaIChatContent
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.OnBottomReached
import com.cmoney.kolfanci.extension.displayPostTime
import com.cmoney.kolfanci.extension.findActivity
import com.cmoney.kolfanci.extension.isMyPost
import com.cmoney.kolfanci.extension.showPostMoreActionDialogBottomSheet
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.ReSendFile
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.model.usecase.AttachmentController
import com.cmoney.kolfanci.model.viewmodel.AttachmentViewModel
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.common.ReplyText
import com.cmoney.kolfanci.ui.common.ReplyTitleText
import com.cmoney.kolfanci.ui.destinations.BaseEditMessageScreenDestination
import com.cmoney.kolfanci.ui.destinations.EditPostScreenDestination
import com.cmoney.kolfanci.ui.screens.chat.MessageInput
import com.cmoney.kolfanci.ui.screens.chat.ReSendFileDialog
import com.cmoney.kolfanci.ui.screens.media.audio.RecordingViewModel
import com.cmoney.kolfanci.ui.screens.post.BaseDeletedContentScreen
import com.cmoney.kolfanci.ui.screens.post.BasePostContentScreen
import com.cmoney.kolfanci.ui.screens.post.CommentCount
import com.cmoney.kolfanci.ui.screens.post.dialog.PostInteract
import com.cmoney.kolfanci.ui.screens.post.dialog.PostMoreActionType
import com.cmoney.kolfanci.ui.screens.post.dialog.ReportPostDialogScreenScreen
import com.cmoney.kolfanci.ui.screens.post.edit.BaseEditMessageScreenResult
import com.cmoney.kolfanci.ui.screens.post.edit.attachment.PostAttachmentScreen
import com.cmoney.kolfanci.ui.screens.post.info.data.PostInfoScreenResult
import com.cmoney.kolfanci.ui.screens.post.info.model.ReplyData
import com.cmoney.kolfanci.ui.screens.post.info.model.UiState
import com.cmoney.kolfanci.ui.screens.post.info.viewmodel.PostInfoViewModel
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
import com.cmoney.kolfanci.ui.screens.shared.bottomSheet.audio.RecordScreen
import com.cmoney.kolfanci.ui.screens.shared.bottomSheet.mediaPicker.AttachmentEnv
import com.cmoney.kolfanci.ui.screens.shared.bottomSheet.mediaPicker.MediaPickerBottomSheet
import com.cmoney.kolfanci.ui.screens.shared.dialog.DeleteConfirmDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.snackbar.FanciSnackBarScreen
import com.cmoney.kolfanci.ui.screens.shared.toolbar.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * 貼文 詳細資訊
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Destination
@Composable
fun PostInfoScreen(
    navController: DestinationsNavigator,
    modifier: Modifier = Modifier,
    channel: Channel,
    post: BulletinboardMessage,
    isPinPost: Boolean = false,
    viewModel: PostInfoViewModel = koinViewModel(
        parameters = {
            parametersOf(post, channel)
        }
    ),
    attachmentViewModel: AttachmentViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<PostInfoScreenResult>,
    editResultRecipient: ResultRecipient<EditPostScreenDestination, PostViewModel.BulletinboardMessageWrapper>,
    editCommentReplyRecipient: ResultRecipient<BaseEditMessageScreenDestination, BaseEditMessageScreenResult>
) {
    val TAG = "PostInfoScreen"
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        AppUserLogger.getInstance().log(Page.PostInnerPage)
    }

    //附加檔案
    val attachmentList by attachmentViewModel.attachmentList.collectAsState()
    val attachment by attachmentViewModel.attachment.collectAsState()

    //控制 BottomSheet 附加檔案
    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    //是否只有圖片選擇
    val isOnlyPhotoSelector by attachmentViewModel.isOnlyPhotoSelector.collectAsState()

    //是否呈現上傳中畫面
    var isShowLoading by remember { mutableStateOf(false) }

    //是否全部File上傳完成
    val attachmentUploadFinish by attachmentViewModel.uploadComplete.collectAsState()

    //有上傳失敗的檔案
    val hasUploadFailedFile by attachmentViewModel.uploadFailed.collectAsState()

    //User 輸入內容
    var inputContent by remember {
        mutableStateOf("")
    }

    //resend file
    var reSendFileClick by remember {
        mutableStateOf<ReSendFile?>(null)
    }

    //附加檔案上傳完成
    if (attachmentUploadFinish.first) {
        isShowLoading = false
        val text = attachmentUploadFinish.second?.toString().orEmpty()

        viewModel.onCommentReplySend(text, attachmentList)

        attachmentViewModel.finishPost()
    }

    //是否訊息發送完成
    val isSendComplete by viewModel.isSendComplete.collectAsState()

    //發送完成, 清空附加檔案暫存
    if (isSendComplete) {
        attachmentViewModel.clear()
    }

    var showPostDeleteTip by remember {
        mutableStateOf(
            Pair<Boolean, BulletinboardMessage?>(
                false,
                null
            )
        )
    }
    var showCommentDeleteTip by remember {
        mutableStateOf(
            Pair<Boolean, BulletinboardMessage?>(
                false,
                null
            )
        )
    }
    var showReplyDeleteTip by remember {
        mutableStateOf(
            //isShow, comment, reply
            Triple<Boolean, BulletinboardMessage?, BulletinboardMessage?>(
                false,
                null,
                null
            )
        )
    }
    //置頂 提示
    var showPinDialogTip by remember {
        mutableStateOf(
            Pair<Boolean, BulletinboardMessage?>(
                false,
                null
            )
        )
    }

    //檢舉提示 dialog
    var showReportTip by remember {
        mutableStateOf(
            Pair<Boolean, BulletinboardMessage?>(
                false,
                null
            )
        )
    }

    //貼文資料
    val postData by viewModel.post.collectAsState()

    //留言資料
    val comments by viewModel.comment.collectAsState()

    //回覆留言
    val commentReply by viewModel.commentReply.collectAsState()

    val uiState by viewModel.uiState.collectAsState()

    //回覆資料
    val replyMapData by viewModel.replyMap.collectAsState()

    //輸入匡預設值
    val inputText by viewModel.inputText.collectAsState()

    //返回更新貼文
    val updatePost by viewModel.updatePost.collectAsState()
    updatePost?.let {
        resultNavigator.navigateBack(it)
    }

    //Snackbar
    val toastMessage by viewModel.toast.collectAsState()

    //Control keyboard
    val keyboard = LocalSoftwareKeyboardController.current

    //貼文 callback listener
    val postInfoListener = object : PostInfoListener {
        override fun onEmojiClick(comment: BulletinboardMessage, resourceId: Int) {
            if (Constant.isCanEmoji()) {
                viewModel.onEmojiClick(comment, resourceId)
            }
        }

        override fun onCommentSend(text: String) {
            AppUserLogger.getInstance().log(Clicked.CommentSendButton)
            if (Constant.isCanReply()) {
                isShowLoading = true
                inputContent = text
                attachmentViewModel.upload(
                    channelId = channel.id.orEmpty(),
                    other = text
                )
                keyboard?.hide()
            }
        }

        override fun onBackClick() {
            resultNavigator.navigateBack(
                PostInfoScreenResult(
                    post = viewModel.post.value,
                )
            )
        }

        override fun onAttachClick() {
            AppUserLogger.getInstance().log(Clicked.CommentInsertImage)
            coroutineScope.launch {
                state.show()
            }
        }

        override fun onCommentEmojiClick(comment: BulletinboardMessage, resourceId: Int) {
            if (Constant.isCanEmoji()) {
                viewModel.onCommentEmojiClick(comment, resourceId)
            }
        }

        override fun onCommentReplyClose() {
            viewModel.onCommentReplyClose()
        }

        override fun onCommentLoadMore() {
            viewModel.onCommentLoadMore()
        }

        //貼文 更多動作
        override fun onPostMoreClick(post: BulletinboardMessage) {
            KLog.i(TAG, "onPostMoreClick")
            AppUserLogger.getInstance().log(Clicked.MoreAction, From.InnerLayer)

            context.findActivity().showPostMoreActionDialogBottomSheet(
                postMessage = post,
                postMoreActionType = PostMoreActionType.Post,
                onInteractClick = {
                    when (it) {
                        is PostInteract.Announcement -> {
                            showPinDialogTip = Pair(true, post)
                        }

                        is PostInteract.Delete -> {
                            showPostDeleteTip = Pair(true, post)
                        }

                        is PostInteract.Edit -> {
                            navController.navigate(
                                EditPostScreenDestination(
                                    channelId = channel.id.orEmpty(),
                                    editPost = post
                                )
                            )
                        }

                        is PostInteract.Report -> {
                            KLog.i(TAG, "PostInteract.Report click.")
                            showReportTip = Pair(true, post)
                        }

                        else -> {}
                    }
                }
            )
        }
    }

    //底部留言 callback listener
    val commentBottomContentListener = object : CommentBottomContentListener {
        override fun onCommentReplyClick(comment: BulletinboardMessage) {
            if (Constant.isCanReply()) {
                AppUserLogger.getInstance().log(Clicked.CommentReply)
                viewModel.onCommentReplyClick(comment)
            }
        }

        override fun onExpandClick(comment: BulletinboardMessage) {
            viewModel.onExpandOrCollapseClick(
                channelId = channel.id.orEmpty(),
                commentId = comment.id.orEmpty()
            )
        }

        override fun onLoadMoreReply(comment: BulletinboardMessage) {
            viewModel.onLoadMoreReply(comment)
        }

        override fun onReplyEmojiClick(
            comment: BulletinboardMessage,
            reply: BulletinboardMessage,
            resourceId: Int
        ) {
            if (Constant.isCanEmoji()) {
                viewModel.onReplyEmojiClick(comment, reply, resourceId)
            }
        }

        //留言 更多動做
        override fun onCommentMoreActionClick(comment: BulletinboardMessage) {
            KLog.i(TAG, "onCommentMoreActionClick")
            AppUserLogger.getInstance().log(Clicked.MoreAction, From.Comment)

            context.findActivity().showPostMoreActionDialogBottomSheet(
                postMessage = comment,
                postMoreActionType = PostMoreActionType.Comment,
                onInteractClick = {
                    when (it) {
                        is PostInteract.Announcement -> {}
                        is PostInteract.Delete -> {
                            if (post.isMyPost()) {
                                AppUserLogger.getInstance()
                                    .log(Clicked.CommentDeleteComment, From.Poster)
                            } else {
                                AppUserLogger.getInstance()
                                    .log(Clicked.CommentDeleteComment, From.OthersPost)
                            }

                            showCommentDeleteTip = Pair(true, comment)
                        }

                        is PostInteract.Edit -> {
                            KLog.i(TAG, "PostInteract.Edit click.")
                            AppUserLogger.getInstance().log(Clicked.CommentEditComment)
                            navController.navigate(
                                BaseEditMessageScreenDestination(
                                    channelId = channel.id.orEmpty(),
                                    editMessage = comment,
                                    isComment = true
                                )
                            )
                        }

                        is PostInteract.Report -> {
                            KLog.i(TAG, "PostInteract.Report click.")
                            AppUserLogger.getInstance().log(Clicked.CommentReportComment)
                            showReportTip = Pair(true, comment)
                        }

                        else -> {}
                    }
                }
            )
        }

        //回覆 更多動做
        override fun onReplyMoreActionClick(
            comment: BulletinboardMessage,
            reply: BulletinboardMessage
        ) {
            context.findActivity().showPostMoreActionDialogBottomSheet(
                postMessage = post,
                postMoreActionType = PostMoreActionType.Reply,
                onInteractClick = {
                    when (it) {
                        is PostInteract.Announcement -> {}
                        is PostInteract.Delete -> {
                            KLog.i(TAG, "PostInteract.Delete click.")
                            if (reply.isMyPost()) {
                                AppUserLogger.getInstance()
                                    .log(Clicked.CommentDeleteReply, From.Poster)
                            } else {
                                AppUserLogger.getInstance()
                                    .log(Clicked.CommentDeleteReply, From.OthersPost)
                            }

                            showReplyDeleteTip = Triple(true, comment, reply)
                        }

                        is PostInteract.Edit -> {
                            KLog.i(TAG, "PostInteract.Edit click.")
                            AppUserLogger.getInstance().log(Clicked.CommentEditReply)
                            navController.navigate(
                                BaseEditMessageScreenDestination(
                                    channelId = channel.id.orEmpty(),
                                    editMessage = reply,
                                    isComment = false,
                                    commentId = comment.id.orEmpty()
                                )
                            )
                        }

                        is PostInteract.Report -> {
                            KLog.i(TAG, "PostInteract.Report click.")
                            AppUserLogger.getInstance().log(Clicked.CommentReportReply)
                            showReportTip = Pair(true, reply)
                        }

                        else -> {}
                    }
                }
            )
        }
    }

    PostInfoScreenView(
        modifier = modifier,
        channel = channel,
        navController = navController,
        post = postData,
        isPinPost = isPinPost,
        attachment = attachmentList,
        comments = comments,
        commentReply = commentReply,
        showLoading = (uiState == UiState.ShowLoading),
        replyMapData = replyMapData.toMap(),
        postInfoListener = postInfoListener,
        commentBottomContentListener = commentBottomContentListener,
        inputText = inputText,
        isShowLoading = isShowLoading,
        onDeleteAttach = {
            attachmentViewModel.removeAttach(it)
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
        onResend = {
            reSendFileClick = it
        }
    )
    //錄音sheet控制
    var showAudioRecorderBottomSheet by remember { mutableStateOf(false) }
    //錄音
    val recordingViewModel: RecordingViewModel = koinViewModel()
    val recordingScreenState by recordingViewModel.recordingScreenState

    if (showAudioRecorderBottomSheet) {
        RecordScreen(
            recordingViewModel = recordingViewModel,
            onUpload = {
                showAudioRecorderBottomSheet = false
                coroutineScope.launch {
                    state.hide()
                }
                KLog.i(TAG, "uri: ${recordingScreenState.recordFileUri}")
                attachmentViewModel.setRecordingAttachmentType(recordingScreenState.recordFileUri)
            },
            onDismissRequest = {
                showAudioRecorderBottomSheet = false
            }
        )
    }

    //多媒體檔案選擇
    MediaPickerBottomSheet(
        navController = navController,
        state = state,
        attachmentEnv = AttachmentEnv.Post,
        selectedAttachment = attachment,
        onRecord = {
            showAudioRecorderBottomSheet = true
        },
        isOnlyPhotoSelector = isOnlyPhotoSelector
    ) {
        attachmentViewModel.attachment(it)
    }
    //編輯貼文 callback
    editResultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                viewModel.onUpdatePost(result.value.message)
            }
        }
    }

    //編輯回覆 callback
    editCommentReplyRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                if (result.value.isComment) {
                    viewModel.onUpdateComment(result.value.editMessage)
                } else {
                    viewModel.onUpdateReply(result.value.editMessage, result.value.commentId)
                }
            }
        }
    }

    //==================== 彈窗提示 ====================
    //重新發送  彈窗
    reSendFileClick?.let { reSendFile ->
        val file = reSendFile.attachmentInfoItem
        ReSendFileDialog(
            reSendFile = reSendFile,
            onDismiss = {
                reSendFileClick = null
            },
            onRemove = {
                attachmentViewModel.removeAttach(file)
                reSendFileClick = null
            },
            onResend = {
                attachmentViewModel.onResend(
                    channelId = channel.id.orEmpty(),
                    uploadFileItem = reSendFile,
                    other = inputContent
                )
                reSendFileClick = null
            }
        )
    }

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

    //提示 Snackbar
    toastMessage?.let {
        FanciSnackBarScreen(
            modifier = Modifier.padding(bottom = 70.dp),
            message = it
        ) {
            viewModel.dismissSnackBar()
        }
    }

    //檢舉貼文
    if (showReportTip.first) {
        ReportPostDialogScreenScreen(
            user = showReportTip.second?.author ?: GroupMember(),
            onDismiss = {
                showReportTip = Pair(false, null)
            },
            onConfirm = { reportReason ->
                viewModel.onReportPost(channel.id.orEmpty(), showReportTip.second, reportReason)
                showReportTip = Pair(false, null)
            }
        )
    }

    //是否刪貼文
    DeleteConfirmDialogScreen(
        date = showPostDeleteTip.second,
        isShow = showPostDeleteTip.first,
        title = "確定刪除貼文",
        content = "貼文刪除後，內容將會完全消失。",
        onCancel = {
            showPostDeleteTip = showPostDeleteTip.copy(first = false)
        },
        onConfirm = {
            showPostDeleteTip = showPostDeleteTip.copy(first = false)
            showPostDeleteTip.second?.let {
                viewModel.onDeletePostClick(it)
            }
        }
    )

    //是否刪留言
    DeleteConfirmDialogScreen(
        date = showCommentDeleteTip.second,
        isShow = showCommentDeleteTip.first,
        title = "確定刪除留言",
        content = "留言刪除後，內容將會完全消失。",
        onCancel = {
            showCommentDeleteTip = showCommentDeleteTip.copy(first = false)
        },
        onConfirm = {
            showCommentDeleteTip = showCommentDeleteTip.copy(first = false)
            viewModel.onDeleteCommentOrReply(
                comment = showCommentDeleteTip.second,
                reply = null,
                isComment = true
            )
        }
    )

    //是否刪回覆
    DeleteConfirmDialogScreen(
        date = Pair(showReplyDeleteTip.second, showReplyDeleteTip.third),
        isShow = showReplyDeleteTip.first,
        title = "確定刪除回覆",
        content = "回覆刪除後，內容將會完全消失。",
        onCancel = {
            showReplyDeleteTip = showReplyDeleteTip.copy(first = false)
        },
        onConfirm = {
            showReplyDeleteTip = showReplyDeleteTip.copy(first = false)
            viewModel.onDeleteCommentOrReply(
                comment = showReplyDeleteTip.second,
                reply = showReplyDeleteTip.third,
                isComment = false
            )
        }
    )

    //置頂提示彈窗
    if (showPinDialogTip.first) {
        DialogScreen(
            title = "置頂文章",
            subTitle = if (isPinPost) {
                ""
            } else {
                "置頂這篇文章，重要貼文不再被淹沒！"
            },
            onDismiss = {
                showPinDialogTip = Pair(false, null)
            }
        ) {
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = if (isPinPost) {
                    "取消置頂"
                } else {
                    "置頂文章"
                },
                borderColor = LocalColor.current.text.default_50,
                textColor = LocalColor.current.text.default_100
            ) {
                run {
                    if (isPinPost) {
                        viewModel.unPinPost(channel.id.orEmpty(), showPinDialogTip.second)
                    } else {
                        viewModel.pinPost(channel.id.orEmpty(), showPinDialogTip.second)
                    }
                    showPinDialogTip = Pair(false, null)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = "取消",
                borderColor = LocalColor.current.text.default_50,
                textColor = LocalColor.current.text.default_100
            ) {
                run {
                    showPinDialogTip = Pair(false, null)
                }
            }
        }
    }

    BackHandler {
        resultNavigator.navigateBack(
            PostInfoScreenResult(
                post = viewModel.post.value
            )
        )
    }
}

@Composable
private fun PostInfoScreenView(
    modifier: Modifier = Modifier,
    channel: Channel,
    navController: DestinationsNavigator,
    post: BulletinboardMessage,
    isPinPost: Boolean,
    attachment: List<Pair<AttachmentType, AttachmentInfoItem>>,
    comments: List<BulletinboardMessage>,
    commentReply: BulletinboardMessage?,
    showLoading: Boolean,
    replyMapData: Map<String, ReplyData>,
    postInfoListener: PostInfoListener,
    commentBottomContentListener: CommentBottomContentListener,
    inputText: String,
    isShowLoading: Boolean,
    onDeleteAttach: (AttachmentInfoItem) -> Unit,
    onAttachImageAddClick: () -> Unit,
    onPreviewAttachmentClick: (AttachmentInfoItem) -> Unit,
    onResend: (ReSendFile) -> Unit,
) {
    val listState = rememberLazyListState()

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = "貼文",
                backClick = {
                    postInfoListener.onBackClick()
                }
            )
        },
        backgroundColor = LocalColor.current.env_80
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column {
                LazyColumn(modifier = Modifier.weight(1f), state = listState) {
                    //貼文
                    item {
                        BasePostContentScreen(
                            channelId = channel.id.orEmpty(),
                            navController = navController,
                            post = post,
                            defaultDisplayLine = Int.MAX_VALUE,
                            bottomContent = {
                                CommentCount(
                                    post = post,
                                    isPinPost = isPinPost
                                )
                            },
                            onMoreClick = {
                                postInfoListener.onPostMoreClick(post)
                            },
                            onEmojiClick = {
                                AppUserLogger.getInstance()
                                    .log(Clicked.ExistingEmoji, From.InnerLayer)
                                postInfoListener.onEmojiClick(post, it)
                            },
                            onImageClick = {
                                AppUserLogger.getInstance().log(Clicked.Image, From.InnerLayer)
                            },
                            onAddNewEmojiClick = {
                                AppUserLogger.getInstance().log(Clicked.AddEmoji, From.InnerLayer)
                                postInfoListener.onEmojiClick(post, it)
                            }
                        )
                    }

                    //留言區塊
                    if (comments.isNotEmpty()) {
                        item {
                            Spacer(
                                modifier = Modifier
                                    .height(15.dp)
                                    .background(LocalColor.current.env_80)
                            )
                        }

                        //Comment title
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(45.dp)
                                    .background(LocalColor.current.background)
                                    .padding(start = 20.dp, top = 10.dp, bottom = 10.dp),
                            ) {
                                Text(
                                    text = "貼文留言",
                                    fontSize = 17.sp,
                                    color = LocalColor.current.text.default_100
                                )
                            }
                        }

                        //留言內容
                        items(comments) { comment ->
                            //如果被刪除
                            if (comment.isDeleted == true) {
                                //被管理員刪除
                                val title = if (comment.deleteStatus == DeleteStatus.deleted) {
                                    stringResource(id = R.string.comment_remove_from_manage)
                                } else {
                                    stringResource(id = R.string.comment_remove_from_poster)
                                }

                                BaseDeletedContentScreen(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(LocalColor.current.background)
                                        .padding(
                                            top = 10.dp,
                                            start = 20.dp,
                                            end = 20.dp,
                                            bottom = 5.dp
                                        ),
                                    title = title,
                                    content = "已經刪除的留言，你是看不到的！"
                                )
                            } else {
                                BasePostContentScreen(
                                    channelId = channel.id.orEmpty(),
                                    navController = navController,
                                    post = comment,
                                    contentModifier = Modifier.padding(start = 40.dp),
                                    hasMoreAction = true,
                                    bottomContent = {
                                        CommentBottomContent(
                                            channel = channel,
                                            navController = navController,
                                            comment = comment,
                                            reply = replyMapData[comment.id],
                                            listener = commentBottomContentListener
                                        )
                                    },
                                    onEmojiClick = {
                                        AppUserLogger.getInstance()
                                            .log(Clicked.ExistingEmoji, From.Comment)
                                        postInfoListener.onCommentEmojiClick(comment, it)
                                    },
                                    onMoreClick = {
                                        commentBottomContentListener.onCommentMoreActionClick(
                                            comment
                                        )
                                    },
                                    onImageClick = {
                                        AppUserLogger.getInstance().log(Clicked.Image, From.Comment)
                                    },
                                    onAddNewEmojiClick = {
                                        AppUserLogger.getInstance()
                                            .log(Clicked.AddEmoji, From.Comment)
                                        postInfoListener.onCommentEmojiClick(comment, it)
                                    },
                                    onTextExpandClick = {
                                        AppUserLogger.getInstance()
                                            .log(Clicked.ShowMore, From.Comment)
                                    }
                                )
                            }

                            Divider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(LocalColor.current.background)
                                    .padding(
                                        top = 15.dp,
                                        start = 20.dp,
                                        end = 20.dp,
                                        bottom = 15.dp
                                    ),
                                color = LocalColor.current.background,
                                thickness = 1.dp
                            )
                        }
                    } else {
                        //沒有人留言
                        item {
                            EmptyCommentView()
                        }
                    }
                }

                //================== Bottom ==================

                //回覆留言
                commentReply?.let {
                    CommentReplayScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LocalColor.current.env_100),
                        comment = it,
                    ) {
                        postInfoListener.onCommentReplyClose()
                    }
                }

                //附加檔案
                PostAttachmentScreen(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 1.dp)
                        .background(MaterialTheme.colors.primary),
                    attachment = attachment,
                    isShowLoading = isShowLoading,
                    onDelete = onDeleteAttach,
                    onClick = onPreviewAttachmentClick,
                    onAddImage = onAttachImageAddClick,
                    onResend = onResend
                )

                //輸入匡
                key(inputText) {
                    MessageInput(
                        tabType = ChannelTabType.bulletinboard,
                        defaultText = inputText,
                        onMessageSend = {
                            postInfoListener.onCommentSend(it)
                        },
                        showOnlyBasicPermissionTip = {
                        }
                    ) {
                        postInfoListener.onAttachClick()
                    }
                }
            }

            if (showLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(45.dp),
                    color = LocalColor.current.primary
                )
            }
        }
    }

    listState.OnBottomReached {
        postInfoListener.onCommentLoadMore()
    }
}

/**
 * 沒有人留言
 */
@Composable
private fun EmptyCommentView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(LocalColor.current.env_80),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AsyncImage(
            modifier = Modifier.size(105.dp),
            model = R.drawable.empty_chat, contentDescription = "empty message"
        )

        Spacer(modifier = Modifier.height(43.dp))

        Text(
            text = "目前還沒有人留言",
            fontSize = 16.sp,
            color = LocalColor.current.text.default_30
        )
    }
}

/**
 * 留言 底部內容
 */
@Composable
private fun CommentBottomContent(
    navController: DestinationsNavigator,
    channel: Channel,
    comment: BulletinboardMessage,
    reply: ReplyData?,
    listener: CommentBottomContentListener
) {
    Column {
        //貼文留言,底部 n天前, 回覆
        CommentBottomView(comment) {
            listener.onCommentReplyClick(it)
        }

        if (comment.commentCount != 0) {
            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier
                    .clickable {
                        listener.onExpandClick(comment)
                    }
                    .padding(top = 10.dp, bottom = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.width(30.dp),
                    color = LocalColor.current.component.other,
                    thickness = 1.dp
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = if (reply?.replyList?.isNotEmpty() == true) {
                        "隱藏回覆"
                    } else {
                        "查看 %d 則 回覆".format(comment.commentCount ?: 0)
                    },
                    fontSize = 14.sp,
                    color = LocalColor.current.text.default_100
                )
            }
        }

        if (reply != null && reply.replyList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(5.dp))

            //回覆 清單 內容
            reply.replyList.forEach { reply ->
                if (reply.isDeleted == true) {
                    BaseDeletedContentScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 20.dp, bottom = 5.dp),
                        title = "這則回覆已被本人刪除",
                        content = "已經刪除的回覆，你是看不到的！"
                    )
                } else {
                    BasePostContentScreen(
                        channelId = channel.id.orEmpty(),
                        navController = navController,
                        post = reply,
                        defaultDisplayLine = Int.MAX_VALUE,
                        contentModifier = Modifier.padding(start = 40.dp),
                        hasMoreAction = true,
                        backgroundColor = Color.Transparent,
                        bottomContent = {
                            Text(
                                text = reply.displayPostTime(),
                                fontSize = 14.sp,
                                color = LocalColor.current.text.default_100
                            )
                        },
                        onEmojiClick = {
                            listener.onReplyEmojiClick(comment, reply, it)
                        },
                        onMoreClick = {
                            listener.onReplyMoreActionClick(
                                comment = comment,
                                reply = reply
                            )
                        },
                        onAddNewEmojiClick = {
                            listener.onReplyEmojiClick(comment, reply, it)
                        }
                    )
                }
            }

            //如果有分頁,顯示更多留言
            if (reply.haveMore) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            listener.onLoadMoreReply(comment)
                        }
                        .padding(start = 40.dp),
                    text = "顯示更多",
                    fontSize = 14.sp,
                    color = LocalColor.current.text.default_100
                )
            }
        }
    }
}

@Preview
@Composable
fun CommentBottomContentPreview() {
    FanciTheme {
        CommentBottomContent(
            navController = EmptyDestinationsNavigator,
            comment = BulletinboardMessage(),
            reply = ReplyData(
                emptyList(), false
            ),
            listener = EmptyCommentBottomContentListener,
            channel = Channel()
        )
    }
}

/**
 * 留言 底部 發文時間, 回覆 View
 */
@Composable
fun CommentBottomView(
    comment: BulletinboardMessage,
    onCommentReplyClick: (BulletinboardMessage) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = comment.displayPostTime(),
            fontSize = 14.sp,
            color = LocalColor.current.text.default_100
        )

        Text(
            modifier = Modifier
                .clickable {
                    onCommentReplyClick.invoke(comment)
                }
                .padding(10.dp),
            text = "回覆", fontSize = 14.sp, color = LocalColor.current.text.default_100
        )
    }
}

/**
 * 貼文 回覆
 */
@Composable
fun CommentReplayScreen(
    comment: BulletinboardMessage,
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(
                top = 10.dp,
                bottom = 10.dp,
                start = 16.dp,
                end = 16.dp
            )
        ) {
            ReplyTitleText(text = "回覆・" + comment.author?.name)
            Spacer(modifier = Modifier.height(10.dp))
            ReplyText(text = comment.content?.text.orEmpty())
        }

        //Close icon
        Image(
            modifier = Modifier
                .padding(5.dp)
                .size(15.dp)
                .align(Alignment.TopEnd)
                .clickable {
                    onClose.invoke()
                },
            painter = painterResource(id = R.drawable.white_close),
            colorFilter = ColorFilter.tint(color = LocalColor.current.text.default_100),
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun CommentReplayScreenPreview() {
    FanciTheme {
        CommentReplayScreen(
            comment = BulletinboardMessage(
                author = GroupMember(name = "Hello"),
                content = MediaIChatContent(text = "Content Text")
            ),
            onClose = {}
        )
    }
}

@Preview
@Composable
fun PostInfoScreenPreview() {
    FanciTheme {
        PostInfoScreenView(
            navController = EmptyDestinationsNavigator,
            channel = Channel(),
            post = MockData.mockBulletinboardMessage,
            isPinPost = false,
            attachment = emptyList(),
            comments = emptyList(),
            commentReply = null,
            showLoading = false,
            replyMapData = hashMapOf(),
            postInfoListener = EmptyPostInfoListener,
            commentBottomContentListener = EmptyCommentBottomContentListener,
            inputText = "",
            isShowLoading = false,
            onDeleteAttach = {},
            onAttachImageAddClick = {},
            onPreviewAttachmentClick = {}
        ) {}
    }
}