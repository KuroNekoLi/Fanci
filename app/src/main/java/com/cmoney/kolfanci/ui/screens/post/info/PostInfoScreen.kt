package com.cmoney.kolfanci.ui.screens.post.info

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.MediaIChatContent
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.OnBottomReached
import com.cmoney.kolfanci.extension.displayPostTime
import com.cmoney.kolfanci.extension.findActivity
import com.cmoney.kolfanci.extension.showPostMoreActionDialogBottomSheet
import com.cmoney.kolfanci.ui.common.ReplyText
import com.cmoney.kolfanci.ui.common.ReplyTitleText
import com.cmoney.kolfanci.ui.screens.chat.MessageAttachImageScreen
import com.cmoney.kolfanci.ui.screens.chat.MessageInput
import com.cmoney.kolfanci.ui.screens.post.BaseDeletedContentScreen
import com.cmoney.kolfanci.ui.screens.post.BasePostContentScreen
import com.cmoney.kolfanci.ui.screens.post.CommentCount
import com.cmoney.kolfanci.ui.screens.post.dialog.PostInteract
import com.cmoney.kolfanci.ui.screens.post.dialog.PostMoreActionType
import com.cmoney.kolfanci.ui.screens.post.info.model.ReplyData
import com.cmoney.kolfanci.ui.screens.post.info.model.UiState
import com.cmoney.kolfanci.ui.screens.post.info.viewmodel.PostInfoViewModel
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DeleteConfirmDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.PhotoPickDialogScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun PostInfoScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    channel: Channel,
    post: BulletinboardMessage,
    viewModel: PostInfoViewModel = koinViewModel(
        parameters = {
            parametersOf(post, channel)
        }
    ),
    resultNavigator: ResultBackNavigator<BulletinboardMessage>
) {
    val context = LocalContext.current

    var showCommentDeleteTip by remember { mutableStateOf(Pair<Boolean, BulletinboardMessage?>(false, null)) }

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

    //Control keyboard
    val keyboard = LocalSoftwareKeyboardController.current

    //貼文 callback listener
    val postInfoListener = object : PostInfoListener {
        override fun onEmojiClick(comment: BulletinboardMessage, resourceId: Int) {
            viewModel.onEmojiClick(comment, resourceId)
        }

        override fun onCommentSend(text: String) {
            viewModel.onCommentReplySend(text)
            keyboard?.hide()
        }

        override fun onBackClick() {
            resultNavigator.navigateBack(viewModel.post.value)
        }

        override fun onAttachClick() {
            openImagePickDialog = true
        }

        override fun onDeleteAttach(uri: Uri) {
            viewModel.onDeleteAttach(uri)
        }

        override fun onCommentEmojiClick(comment: BulletinboardMessage, resourceId: Int) {
            viewModel.onCommentEmojiClick(comment, resourceId)
        }

        override fun onCommentReplyClose() {
            viewModel.onCommentReplyClose()
        }

        override fun onCommentLoadMore() {
            viewModel.onCommentLoadMore()
        }

        override fun onPostMoreClick(post: BulletinboardMessage) {
            context.findActivity().showPostMoreActionDialogBottomSheet(
                postMessage = post,
                postMoreActionType = PostMoreActionType.Post,
                onInteractClick = {
                    //todo
                }
            )
        }
    }

    //底部留言 callback listener
    val commentBottomContentListener = object : CommentBottomContentListener {
        override fun onCommentReplyClick(comment: BulletinboardMessage) {
            viewModel.onCommentReplyClick(comment)
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
            viewModel.onReplyEmojiClick(comment, reply, resourceId)
        }

        override fun onCommentMoreActionClick(comment: BulletinboardMessage) {
            context.findActivity().showPostMoreActionDialogBottomSheet(
                postMessage = post,
                postMoreActionType = PostMoreActionType.Comment,
                onInteractClick = {
                    when (it) {
                        is PostInteract.Announcement -> TODO()
                        is PostInteract.Delete -> {
                            showCommentDeleteTip = Pair(true, comment)
                        }
                        is PostInteract.Edit -> TODO()
                        is PostInteract.Report -> TODO()
                    }
                }
            )
        }

        override fun onReplyMoreActionClick(comment: BulletinboardMessage) {
            context.findActivity().showPostMoreActionDialogBottomSheet(
                postMessage = post,
                postMoreActionType = PostMoreActionType.Reply,
                onInteractClick = {
                    //todo
                }
            )
        }
    }

    PostInfoScreenView(
        modifier = modifier,
        post = postData,
        imageAttachList = imageAttachList,
        comments = comments,
        commentReply = commentReply,
        showLoading = (uiState == UiState.ShowLoading),
        replyMapData = replyMapData.toMap(),
        postInfoListener = postInfoListener,
        commentBottomContentListener = commentBottomContentListener
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

    //==================== 彈窗提示 ====================
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
            viewModel.onDeleteComment(it)
        }
    )

    BackHandler {
        resultNavigator.navigateBack(viewModel.post.value)
    }
}

@Composable
private fun PostInfoScreenView(
    modifier: Modifier = Modifier,
    post: BulletinboardMessage,
    imageAttachList: List<Uri>,
    comments: List<BulletinboardMessage>,
    commentReply: BulletinboardMessage?,
    showLoading: Boolean,
    replyMapData: Map<String, ReplyData>,
    postInfoListener: PostInfoListener,
    commentBottomContentListener: CommentBottomContentListener
) {
    val listState = rememberLazyListState()

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = "貼文",
                moreEnable = false,
                backClick = {
                    postInfoListener.onBackClick()
                }
            )
        },
        backgroundColor = LocalColor.current.background
    ) { innerPadding ->

        Box(
            modifier = Modifier.padding(innerPadding),
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
                                    post = post
                                )
                            },
                            onMoreClick = {
                                postInfoListener.onPostMoreClick(post)
                            },
                            onEmojiClick = {
                                postInfoListener.onEmojiClick(post, it)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(15.dp))
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

                    item {
                        Spacer(modifier = Modifier.height(1.dp))
                    }

                    //留言內容
                    items(comments) { comment ->
                        //如果被刪除
                        if (comment.isDeleted == true) {
                            BaseDeletedContentScreen(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(LocalColor.current.background)
                                    .padding(top = 10.dp, start = 20.dp, end = 20.dp, bottom = 5.dp),
                                title = "這則留言已被刪除",
                                content = "已經刪除的留言，你是看不到的！"
                            )
                        }
                        else {
                            BasePostContentScreen(
                                post = comment,
                                defaultDisplayLine = Int.MAX_VALUE,
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
                                    postInfoListener.onCommentEmojiClick(comment, it)
                                },
                                onMoreClick = {
                                    commentBottomContentListener.onCommentMoreActionClick(comment)
                                }
                            )
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
                MessageAttachImageScreen(
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
                MessageInput(
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
            reply.replyList.forEach { item ->
                if (item.isDeleted == true) {
                    BaseDeletedContentScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 20.dp, bottom = 5.dp),
                        title = "這則回覆已被本人刪除",
                        content = "已經刪除的回覆，你是看不到的！"
                    )
                }
                else {
                    BasePostContentScreen(
                        post = item,
                        defaultDisplayLine = Int.MAX_VALUE,
                        contentModifier = Modifier.padding(start = 40.dp),
                        hasMoreAction = true,
                        backgroundColor = Color.Transparent,
                        bottomContent = {
                            Text(
                                text = item.displayPostTime(),
                                fontSize = 14.sp,
                                color = LocalColor.current.text.default_100
                            )
                        },
                        onEmojiClick = {
                            listener.onReplyEmojiClick(comment, item, it)
                        },
                        onMoreClick = {
                            listener.onReplyMoreActionClick(item)
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

@Preview(showBackground = true)
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

@Preview(showBackground = true)
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

@Preview(showBackground = true)
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
            commentBottomContentListener = EmptyCommentBottomContentListener
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
    fun onReplyMoreActionClick(comment: BulletinboardMessage)
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

    override fun onReplyMoreActionClick(comment: BulletinboardMessage) {
    }
}