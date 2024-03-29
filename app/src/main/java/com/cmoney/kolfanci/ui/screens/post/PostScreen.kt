package com.cmoney.kolfanci.ui.screens.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.GroupMember
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
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.EditPostScreenDestination
import com.cmoney.kolfanci.ui.destinations.PostInfoScreenDestination
import com.cmoney.kolfanci.ui.screens.post.dialog.PostInteract
import com.cmoney.kolfanci.ui.screens.post.dialog.PostMoreActionType
import com.cmoney.kolfanci.ui.screens.post.dialog.ReportPostDialogScreenScreen
import com.cmoney.kolfanci.ui.screens.post.info.data.PostInfoScreenResult
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
import com.cmoney.kolfanci.ui.screens.shared.dialog.DeleteConfirmDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.snackbar.FanciSnackBarScreen
import com.cmoney.kolfanci.ui.screens.vote.viewmodel.VoteViewModel
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PostScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    channel: Channel,
    viewModel: PostViewModel = koinViewModel(
        parameters = {
            parametersOf(channel.id.orEmpty())
        }
    ),
    voteViewModel: VoteViewModel = koinViewModel(),
    resultRecipient: ResultRecipient<EditPostScreenDestination, PostViewModel.BulletinboardMessageWrapper>,
    postInfoResultRecipient: ResultRecipient<PostInfoScreenDestination, PostInfoScreenResult>
) {
    val TAG = "PostScreen"

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val postList by viewModel.post.collectAsState()
    val pinPost by viewModel.pinPost.collectAsState()
    val toastMessage by viewModel.toast.collectAsState()

    var showPostDeleteTip by remember {
        mutableStateOf(
            Pair<Boolean, BulletinboardMessage?>(
                false,
                null
            )
        )
    }

    //置頂 提示
    var showPinDialogTip by remember {
        mutableStateOf(
            //是否顯示dialog, data, isPin
            Triple<Boolean, BulletinboardMessage?, Boolean>(
                false,
                null,
                false
            )
        )
    }

    //檢舉提示 dialog
    var showPostReportTip by remember {
        mutableStateOf(
            Pair<Boolean, BulletinboardMessage?>(
                false,
                null
            )
        )
    }

    //投票成功
    val voteSuccess by voteViewModel.postVoteSuccess.collectAsState()
    voteSuccess?.let {
        viewModel.forceUpdatePost(it)

        voteViewModel.postVoteSuccessDone()
    }

    PostScreenView(
        modifier = modifier,
        postList = postList,
        navController = navController,
        listState = listState,
        channel = channel,
        pinPost = pinPost,
        onPostClick = {
            //OnClick Method
            navController.navigate(
                EditPostScreenDestination(
                    channelId = channel.id.orEmpty()
                )
            )
            AppUserLogger.getInstance().log(Page.PublishPost)
        },
        onMoreClick = { post ->
            KLog.i(TAG, "onMoreClick")

            AppUserLogger.getInstance().log(Clicked.MoreAction, From.PostList)

            context.findActivity().showPostMoreActionDialogBottomSheet(
                postMessage = post,
                postMoreActionType = PostMoreActionType.Post,
                onInteractClick = {
                    when (it) {
                        is PostInteract.Announcement -> {

                            if (post.isMyPost()) {
                                AppUserLogger.getInstance().log(Clicked.PostPinPost, From.Poster)
                            } else {
                                AppUserLogger.getInstance()
                                    .log(Clicked.PostPinPost, From.OthersPost)
                            }

                            showPinDialogTip = Triple(
                                true,
                                post,
                                viewModel.isPinPost(post)
                            )
                        }

                        is PostInteract.Delete -> {

                            if (post.isMyPost()) {
                                AppUserLogger.getInstance().log(Clicked.PostDeletePost, From.Poster)
                            } else {
                                AppUserLogger.getInstance()
                                    .log(Clicked.PostDeletePost, From.OthersPost)
                            }

                            showPostDeleteTip = Pair(true, post)
                        }

                        is PostInteract.Edit -> {
                            KLog.i(TAG, "PostInteract.Edit click.")
                            AppUserLogger.getInstance().log(Clicked.PostEditPost)
                            navController.navigate(
                                EditPostScreenDestination(
                                    channelId = channel.id.orEmpty(),
                                    editPost = post
                                )
                            )
                            AppUserLogger.getInstance().log(Page.EditPost)
                        }

                        is PostInteract.Report -> {
                            KLog.i(TAG, "PostInteract.Report click.")
                            AppUserLogger.getInstance().log(Clicked.PostReportPost)
                            showPostReportTip = Pair(true, post)
                        }
                    }
                }
            )
        },
        onEmojiClick = { postMessage, emoji ->
            viewModel.onEmojiClick(postMessage, emoji)
        }
    )

    LaunchedEffect(Unit) {
        viewModel.fetchPost()
    }

    //目前畫面最後一個item index
    val columnEndPosition by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            visibleItemsInfo.lastOrNull()?.let {
                it.index
            } ?: 0
        }
    }

    //監控滑動狀態, 停止的時候 polling 資料
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrolling ->
                //滑動停止
                if (!isScrolling) {
                    val firstItemIndex = listState.firstVisibleItemIndex

                    viewModel.pollingScopePost(
                        channelId = channel.id.orEmpty(),
                        startItemIndex = firstItemIndex,
                        lastIndex = columnEndPosition
                    )
                }
            }
    }

    listState.OnBottomReached {
        KLog.i(TAG, "load more....")
        viewModel.onLoadMore()
    }

    //onPost callback
    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val post = result.value
                if (post.isPin) {
                    viewModel.onUpdate(post.message)
                } else {
                    viewModel.onPostSuccess(post.message)
                    coroutineScope.launch {
                        listState.scrollToItem(index = 0)
                    }
                }

            }
        }
    }

    //post info callback
    postInfoResultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val postInfoResult = result.value
                viewModel.onUpdate(postInfoResult.post)
                viewModel.showPostInfoToast(postInfoResult.action)
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
    if (showPostReportTip.first) {
        ReportPostDialogScreenScreen(
            user = showPostReportTip.second?.author ?: GroupMember(),
            onDismiss = {
                showPostReportTip = Pair(false, null)
            },
            onConfirm = { reportReason ->
                viewModel.onReportPost(channel.id.orEmpty(), showPostReportTip.second, reportReason)
                showPostReportTip = Pair(false, null)
            }
        )
    }

    //是否刪貼文
    if (showPostDeleteTip.first) {
        DeleteConfirmDialogScreen(
            date = showPostDeleteTip.second,
            isShow = showPostDeleteTip.first,
            title = "確定刪除貼文",
            content = "貼文刪除後，內容將會完全消失。",
            onCancel = {
                AppUserLogger.getInstance().log(Clicked.PostDeletePostCancel)
                showPostDeleteTip = showPostDeleteTip.copy(first = false)
            },
            onConfirm = {
                AppUserLogger.getInstance().log(Clicked.PostDeletePostConfirmDelete)
                showPostDeleteTip = showPostDeleteTip.copy(first = false)
                showPostDeleteTip.second?.let {
                    viewModel.onDeletePostClick(it)
                }
            }
        )
    }

    //置頂提示彈窗
    if (showPinDialogTip.first) {
        val isPinPost = showPinDialogTip.third
        DialogScreen(
            title = "置頂文章",
            subTitle = if (isPinPost) {
                ""
            } else {
                "置頂這篇文章，重要貼文不再被淹沒！"
            },
            onDismiss = {
                showPinDialogTip = Triple(false, null, false)
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
                        AppUserLogger.getInstance().log(Clicked.PostUnpinPostConfirmUnpin)
                        viewModel.unPinPost(channel.id.orEmpty(), showPinDialogTip.second)
                    } else {
                        AppUserLogger.getInstance().log(Clicked.PostPinPostConfirmPin)
                        viewModel.pinPost(channel.id.orEmpty(), showPinDialogTip.second)
                    }
                    showPinDialogTip = Triple(false, null, false)
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
                    if (isPinPost) {
                        AppUserLogger.getInstance().log(Clicked.PostUnpinPostReturn)
                    } else {
                        AppUserLogger.getInstance().log(Clicked.PostPinPostCancel)
                    }

                    showPinDialogTip = Triple(false, null, false)
                }
            }
        }
    }
}

@Composable
private fun PostScreenView(
    modifier: Modifier = Modifier,
    pinPost: PostViewModel.BulletinboardMessageWrapper?,
    postList: List<PostViewModel.BulletinboardMessageWrapper>,
    navController: DestinationsNavigator,
    channel: Channel,
    onPostClick: () -> Unit,
    onMoreClick: (BulletinboardMessage) -> Unit,
    onEmojiClick: (PostViewModel.BulletinboardMessageWrapper, Int) -> Unit,
    listState: LazyListState
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColor.current.env_80)
    ) {
        if (postList.isEmpty()) {
            EmptyPostContent(modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                state = listState,
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                pinPost?.let { pinPost ->
                    item {
                        BasePostContentScreen(
                            channelId = channel.id.orEmpty(),
                            navController = navController,
                            post = pinPost.message,
                            bottomContent = {
                                CommentCount(
                                    post = pinPost.message,
                                    navController = navController,
                                    channel = channel,
                                    isPinPost = true
                                )
                            },
                            multiImageHeight = 250.dp,
                            onMoreClick = {
                                onMoreClick.invoke(pinPost.message)
                            },
                            onEmojiClick = {
                                if (Constant.isCanEmoji()) {
                                    AppUserLogger.getInstance()
                                        .log(Clicked.ExistingEmoji, From.PostList)
                                    onEmojiClick.invoke(pinPost, it)
                                }
                            },
                            onImageClick = {
                                AppUserLogger.getInstance().log(Clicked.Image, From.PostList)
                            },
                            onAddNewEmojiClick = {
                                AppUserLogger.getInstance().log(Clicked.AddEmoji, From.PostList)
                                onEmojiClick.invoke(pinPost, it)
                            },
                            onTextExpandClick = {
                                AppUserLogger.getInstance().log(Clicked.ShowMore, From.Post)
                            }
                        )
                    }
                }

                val filterPost = postList.filter { !it.isPin }

                items(items = filterPost) { post ->
                    val postMessage = post.message
                    BasePostContentScreen(
                        channelId = channel.id.orEmpty(),
                        navController = navController,
                        post = postMessage,
                        bottomContent = {
                            CommentCount(
                                post = postMessage,
                                navController = navController,
                                channel = channel
                            )
                        },
                        multiImageHeight = 250.dp,
                        onMoreClick = {
                            onMoreClick.invoke(postMessage)
                        },
                        onEmojiClick = {
                            if (Constant.isCanEmoji()) {
                                AppUserLogger.getInstance()
                                    .log(Clicked.ExistingEmoji, From.PostList)
                                onEmojiClick.invoke(post, it)
                            }
                        },
                        onImageClick = {
                            AppUserLogger.getInstance().log(Clicked.Image, From.PostList)
                        },
                        onAddNewEmojiClick = {
                            AppUserLogger.getInstance().log(Clicked.AddEmoji, From.PostList)
                            if (Constant.isCanEmoji()) {
                                onEmojiClick.invoke(post, it)
                            }
                        },
                        onTextExpandClick = {
                            AppUserLogger.getInstance().log(Clicked.ShowMore, From.Post)
                        }
                    )
                }
            }
        }

        if (Constant.canPostMessage()) {
            FloatingActionButton(
                modifier = Modifier
                    .padding(30.dp)
                    .align(Alignment.BottomEnd),
                onClick = {
                    AppUserLogger.getInstance().log(Clicked.PostPublishPost)
                    onPostClick.invoke()
                },
                backgroundColor = LocalColor.current.primary,
                shape = CircleShape,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add FAB",
                    tint = LocalColor.current.text.default_100,
                )
            }
        }
    }
}

@Composable
fun CommentCount(
    navController: DestinationsNavigator? = null,
    post: BulletinboardMessage? = null,
    channel: Channel? = null,
    isPinPost: Boolean = false
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .then(
                if (post != null && navController != null && channel != null) {
                    Modifier.clickable {
                        AppUserLogger
                            .getInstance()
                            .log(Clicked.PostEnterInnerLayer)
                        navController.navigate(
                            PostInfoScreenDestination(
                                post = post,
                                channel = channel,
                                isPinPost = isPinPost
                            )
                        )
                    }
                } else {
                    Modifier
                }
            )
            .padding(top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isPinPost) {
            Text(
                text = "置頂貼文",
                fontSize = 14.sp,
                color = LocalColor.current.text.default_100
            )

            Box(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp)
                    .size(3.8.dp)
                    .clip(CircleShape)
                    .background(LocalColor.current.text.default_100)
            )
        }

        Text(
            text = post?.displayPostTime().orEmpty(),
            fontSize = 14.sp,
            color = LocalColor.current.text.default_100
        )

        Box(
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp)
                .size(3.8.dp)
                .clip(CircleShape)
                .background(LocalColor.current.text.default_100)
        )

        Text(
            text = "留言 %d".format(post?.commentCount ?: 0),
            fontSize = 14.sp,
            color = LocalColor.current.text.default_100
        )
    }
}


@Composable
private fun EmptyPostContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
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
            text = "目前還沒有人發表貼文",
            fontSize = 16.sp,
            color = LocalColor.current.text.default_30
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PostScreenPreview() {
    FanciTheme {
        PostScreenView(
            postList = MockData.mockListBulletinboardMessage.map {
                PostViewModel.BulletinboardMessageWrapper(message = it)
            },
            pinPost = null,
            navController = EmptyDestinationsNavigator,
            channel = Channel(),
            onPostClick = {},
            listState = rememberLazyListState(),
            onMoreClick = {},
            onEmojiClick = { postMessage, emoji ->
            }
        )
    }
}