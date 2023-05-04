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
import com.cmoney.kolfanci.ui.screens.post.BasePostContentScreen
import com.cmoney.kolfanci.ui.screens.post.CommentCount
import com.cmoney.kolfanci.ui.screens.post.info.model.ReplyData
import com.cmoney.kolfanci.ui.screens.post.info.model.UiState
import com.cmoney.kolfanci.ui.screens.post.info.viewmodel.PostInfoViewModel
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
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

    PostInfoScreenView(
        modifier = modifier,
        post = postData,
        onEmojiClick = { message, resourceId ->
            viewModel.onEmojiClick(message, resourceId)
        },
        onCommentSend = {
            viewModel.onCommentReplySend(it)
            keyboard?.hide()
        },
        onBackClick = {
            resultNavigator.navigateBack(viewModel.post.value)
        },
        imageAttachList = imageAttachList,
        onAttachClick = {
            openImagePickDialog = true
        },
        onDeleteAttach = {
            viewModel.onDeleteAttach(it)
        },
        comments = comments,
        onCommentEmojiClick = { comment, resourceId ->
            viewModel.onCommentEmojiClick(comment, resourceId)
        },
        onCommentReplyClick = {
            viewModel.onCommentReplyClick(it)
        },
        commentReply = commentReply,
        onCommentReplyClose = {
            viewModel.onCommentReplyClose()
        },
        showLoading = (uiState == UiState.ShowLoading),
        onReplyExpandClick = {
            viewModel.onExpandOrCollapseClick(
                channelId = channel.id.orEmpty(),
                commentId = it.id.orEmpty()
            )
        },
        replyMapData = replyMapData.toMap(),
        onCommentLoadMore = {
            viewModel.onCommentLoadMore()
        },
        onLoadMoreReply = {
            viewModel.onLoadMoreReply(it)
        },
        onReplyEmojiClick = { comment, reply, resourceId ->
            viewModel.onReplyEmojiClick(comment, reply, resourceId)
        }
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

    BackHandler {
        resultNavigator.navigateBack(viewModel.post.value)
    }
}

@Composable
private fun PostInfoScreenView(
    modifier: Modifier = Modifier,
    post: BulletinboardMessage,
    onEmojiClick: (BulletinboardMessage, Int) -> Unit,
    onCommentSend: (text: String) -> Unit,
    onBackClick: () -> Unit,
    imageAttachList: List<Uri>,
    onAttachClick: () -> Unit,
    onDeleteAttach: (Uri) -> Unit,
    comments: List<BulletinboardMessage>,
    onCommentEmojiClick: (BulletinboardMessage, Int) -> Unit,
    onCommentReplyClick: (BulletinboardMessage) -> Unit,
    commentReply: BulletinboardMessage?,
    onCommentReplyClose: () -> Unit,
    showLoading: Boolean,
    onReplyExpandClick: (BulletinboardMessage) -> Unit,
    replyMapData: Map<String, ReplyData>,
    onCommentLoadMore: () -> Unit,
    onLoadMoreReply: (BulletinboardMessage) -> Unit,
    onReplyEmojiClick: (BulletinboardMessage, BulletinboardMessage, Int) -> Unit
) {
    val context = LocalContext.current

    val listState = rememberLazyListState()

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = "貼文",
                moreEnable = false,
                backClick = {
                    onBackClick.invoke()
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
                                //TODO
                                context.findActivity().showPostMoreActionDialogBottomSheet(
                                    postMessage = post,
                                    onInteractClick = {
                                        //todo
                                    }
                                )
                            },
                            onEmojiClick = {
                                onEmojiClick.invoke(post, it)
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
                        BasePostContentScreen(
                            post = comment,
                            defaultDisplayLine = Int.MAX_VALUE,
                            contentModifier = Modifier.padding(start = 40.dp),
                            hasMoreAction = false,
                            bottomContent = {
                                CommentBottomContent(
                                    comment = comment,
                                    onCommentReplyClick = onCommentReplyClick,
                                    onExpandClick = onReplyExpandClick,
                                    reply = replyMapData[comment.id],
                                    onLoadMoreReply = onLoadMoreReply,
                                    onReplyEmojiClick = onReplyEmojiClick
                                )
                            },
                            onEmojiClick = {
                                onCommentEmojiClick.invoke(comment, it)
                            }
                        )
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
                        onCommentReplyClose.invoke()
                    }
                }

                //附加圖片
                MessageAttachImageScreen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.primary),
                    imageAttach = imageAttachList,
                    onDelete = onDeleteAttach,
                    onAdd = onAttachClick
                )

                //輸入匡
                MessageInput(
                    onMessageSend = onCommentSend,
                    onAttachClick = {
                        onAttachClick.invoke()
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
        onCommentLoadMore.invoke()
    }
}

/**
 * 留言 底部內容
 */
@Composable
private fun CommentBottomContent(
    comment: BulletinboardMessage,
    reply: ReplyData?,
    onCommentReplyClick: (BulletinboardMessage) -> Unit,
    onExpandClick: (BulletinboardMessage) -> Unit,
    onLoadMoreReply: (BulletinboardMessage) -> Unit,
    onReplyEmojiClick: (BulletinboardMessage, BulletinboardMessage, Int) -> Unit
) {
    Column {
        //貼文留言,底部 n天前, 回覆
        CommentBottomView(comment, onCommentReplyClick)

        if (comment.commentCount != 0) {
            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier
                    .clickable {
                        onExpandClick.invoke(comment)
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

            reply.replyList.forEach { item ->
                BasePostContentScreen(
                    post = item,
                    defaultDisplayLine = Int.MAX_VALUE,
                    contentModifier = Modifier.padding(start = 40.dp),
                    hasMoreAction = false,
                    backgroundColor = Color.Transparent,
                    bottomContent = {
                        Text(
                            text = item.displayPostTime(),
                            fontSize = 14.sp,
                            color = LocalColor.current.text.default_100
                        )
                    },
                    onEmojiClick = {
                        onReplyEmojiClick.invoke(comment, item, it)
                    }
                )
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
                            onLoadMoreReply.invoke(comment)
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
            onCommentReplyClick = {},
            onExpandClick = {},
            reply = ReplyData(
                emptyList(), false
            ),
            onLoadMoreReply = {},
            onReplyEmojiClick = { _, _, _ -> }
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
            onEmojiClick = { post, resourceId ->
            },
            onCommentSend = {},
            onBackClick = {},
            imageAttachList = emptyList(),
            onAttachClick = {},
            onDeleteAttach = {},
            comments = emptyList(),
            onCommentEmojiClick = { post, resourceId ->
            },
            onCommentReplyClick = {},
            commentReply = null,
            onCommentReplyClose = {},
            showLoading = false,
            onReplyExpandClick = {},
            replyMapData = hashMapOf(),
            onCommentLoadMore = {},
            onLoadMoreReply = {},
            onReplyEmojiClick = { _, _, _ -> }
        )
    }
}