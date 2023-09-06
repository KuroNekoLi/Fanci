package com.cmoney.kolfanci.ui.screens.post.info

import android.net.Uri
import android.os.Parcelable
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.common.ReplyText
import com.cmoney.kolfanci.ui.common.ReplyTitleText
import com.cmoney.kolfanci.ui.destinations.BaseEditMessageScreenDestination
import com.cmoney.kolfanci.ui.destinations.EditPostScreenDestination
import com.cmoney.kolfanci.ui.screens.chat.ChatRoomAttachImageScreen
import com.cmoney.kolfanci.ui.screens.chat.MessageInput
import com.cmoney.kolfanci.ui.screens.post.BaseDeletedContentScreen
import com.cmoney.kolfanci.ui.screens.post.BasePostContentScreen
import com.cmoney.kolfanci.ui.screens.post.CommentCount
import com.cmoney.kolfanci.ui.screens.post.dialog.PostInteract
import com.cmoney.kolfanci.ui.screens.post.dialog.PostMoreActionType
import com.cmoney.kolfanci.ui.screens.post.dialog.ReportPostDialogScreenScreen
import com.cmoney.kolfanci.ui.screens.post.edit.BaseEditMessageScreenResult
import com.cmoney.kolfanci.ui.screens.post.info.model.ReplyData
import com.cmoney.kolfanci.ui.screens.post.info.model.UiState
import com.cmoney.kolfanci.ui.screens.post.info.viewmodel.PostInfoViewModel
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DeleteConfirmDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.PhotoPickDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.snackbar.FanciSnackBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class PostInfoScreenResult(
    val post: BulletinboardMessage,
    val action: PostInfoAction = PostInfoAction.Default
) : Parcelable {
    @Parcelize
    sealed class PostInfoAction : Parcelable {
        object Default : PostInfoAction()
        object Pin : PostInfoAction()
        object Delete : PostInfoAction()
    }
}

/**
 * 貼文 詳細資訊
 */
@OptIn(ExperimentalComposeUiApi::class)
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
    resultNavigator: ResultBackNavigator<PostInfoScreenResult>,
    editResultRecipient: ResultRecipient<EditPostScreenDestination, PostViewModel.BulletinboardMessageWrapper>,
    editCommentReplyRecipient: ResultRecipient<BaseEditMessageScreenDestination, BaseEditMessageScreenResult>
) {
    val TAG = "PostInfoScreen"
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        AppUserLogger.getInstance().log(Page.PostInnerPage)
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

    //是否打開 圖片選擇
    var openImagePickDialog by remember { mutableStateOf(false) }

    //目前所選,附加圖片
    val imageAttachList by viewModel.imageAttach.collectAsState()

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
                viewModel.onCommentReplySend(text)
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
            openImagePickDialog = true
        }

        override fun onDeleteAttach(uri: Uri) {
            viewModel.onDeleteAttach(uri)
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
                    }
                }
            )
        }
    }

    PostInfoScreenView(
        modifier = modifier,
        post = postData,
        isPinPost = isPinPost,
        imageAttachList = imageAttachList,
        comments = comments,
        commentReply = commentReply,
        showLoading = (uiState == UiState.ShowLoading),
        replyMapData = replyMapData.toMap(),
        postInfoListener = postInfoListener,
        commentBottomContentListener = commentBottomContentListener,
        inputText = inputText
    )

    //圖片選擇
    if (openImagePickDialog) {
        PhotoPickDialogScreen(
            onDismiss = {
                openImagePickDialog = false
            },
            onAttach = {
                openImagePickDialog = false
                viewModel.attachImage(it)
            }
        )
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
    post: BulletinboardMessage,
    isPinPost: Boolean,
    imageAttachList: List<Uri>,
    comments: List<BulletinboardMessage>,
    commentReply: BulletinboardMessage?,
    showLoading: Boolean,
    replyMapData: Map<String, ReplyData>,
    postInfoListener: PostInfoListener,
    commentBottomContentListener: CommentBottomContentListener,
    inputText: String
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
                                    post = comment,
                                    contentModifier = Modifier.padding(start = 40.dp),
                                    hasMoreAction = true,
                                    bottomContent = {
                                        CommentBottomContent(
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

                //附加圖片
                ChatRoomAttachImageScreen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primary),
                    imageAttach = imageAttachList,
                    onDelete = {
                        postInfoListener.onDeleteAttach(it)
                    },
                    onAdd = {
                        postInfoListener.onAttachClick()
                    }
                )

                //輸入匡
                key(inputText) {
                    MessageInput(
                        tabType = ChannelTabType.bulletinboard,
                        defaultText = inputText,
                        onMessageSend = {
                            postInfoListener.onCommentSend(it)
                        },
                        onAttachClick = {
                            postInfoListener.onAttachClick()
                        },
                        showOnlyBasicPermissionTip = {
                        }
                    )
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
            BulletinboardMessage(),
            reply = ReplyData(
                emptyList(), false
            ),
            listener = EmptyCommentBottomContentListener
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
            post = PostViewModel.mockPost,
            imageAttachList = emptyList(),
            comments = emptyList(),
            commentReply = null,
            showLoading = false,
            replyMapData = hashMapOf(),
            postInfoListener = EmptyPostInfoListener,
            commentBottomContentListener = EmptyCommentBottomContentListener,
            inputText = "",
            isPinPost = false
        )
    }
}

//==================== Callback ====================
interface PostInfoListener {
    fun onEmojiClick(comment: BulletinboardMessage, resourceId: Int)

    //送出留言
    fun onCommentSend(text: String)

    //點擊返回
    fun onBackClick()

    //點擊附加圖片
    fun onAttachClick()

    //刪除附加圖片
    fun onDeleteAttach(uri: Uri)

    //點擊留言的 Emoji
    fun onCommentEmojiClick(comment: BulletinboardMessage, resourceId: Int)

    //關閉回覆UI
    fun onCommentReplyClose()

    //讀取更多留言
    fun onCommentLoadMore()

    //文章點擊更多
    fun onPostMoreClick(post: BulletinboardMessage)
}

interface CommentBottomContentListener {
    //點擊 回覆按鈕
    fun onCommentReplyClick(comment: BulletinboardMessage)

    //點擊 展開/隱藏
    fun onExpandClick(comment: BulletinboardMessage)

    //點擊 讀取更多回覆
    fun onLoadMoreReply(comment: BulletinboardMessage)

    //點擊 Emoji
    fun onReplyEmojiClick(
        comment: BulletinboardMessage,
        reply: BulletinboardMessage,
        resourceId: Int
    )

    //點擊 留言的更多
    fun onCommentMoreActionClick(comment: BulletinboardMessage)

    //點擊 回覆的更多
    fun onReplyMoreActionClick(comment: BulletinboardMessage, reply: BulletinboardMessage)
}

object EmptyPostInfoListener : PostInfoListener {
    override fun onEmojiClick(comment: BulletinboardMessage, resourceId: Int) {
    }

    override fun onCommentSend(text: String) {
    }

    override fun onBackClick() {
    }

    override fun onAttachClick() {
    }

    override fun onDeleteAttach(uri: Uri) {
    }

    override fun onCommentEmojiClick(comment: BulletinboardMessage, resourceId: Int) {
    }

    override fun onCommentReplyClose() {
    }

    override fun onCommentLoadMore() {
    }

    override fun onPostMoreClick(post: BulletinboardMessage) {
    }
}

object EmptyCommentBottomContentListener : CommentBottomContentListener {
    override fun onCommentReplyClick(comment: BulletinboardMessage) {
    }

    override fun onExpandClick(comment: BulletinboardMessage) {
    }

    override fun onLoadMoreReply(comment: BulletinboardMessage) {
    }

    override fun onReplyEmojiClick(
        comment: BulletinboardMessage,
        reply: BulletinboardMessage,
        resourceId: Int
    ) {
    }

    override fun onCommentMoreActionClick(comment: BulletinboardMessage) {
    }

    override fun onReplyMoreActionClick(
        comment: BulletinboardMessage,
        reply: BulletinboardMessage
    ) {
    }
}