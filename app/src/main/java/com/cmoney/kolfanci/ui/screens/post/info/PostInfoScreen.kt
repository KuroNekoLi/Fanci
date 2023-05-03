package com.cmoney.kolfanci.ui.screens.post.info

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.kolfanci.extension.displayPostTime
import com.cmoney.kolfanci.extension.findActivity
import com.cmoney.kolfanci.extension.showPostMoreActionDialogBottomSheet
import com.cmoney.kolfanci.ui.screens.chat.MessageAttachImageScreen
import com.cmoney.kolfanci.ui.screens.chat.MessageInput
import com.cmoney.kolfanci.ui.screens.post.CommentCount
import com.cmoney.kolfanci.ui.screens.post.BasePostContentScreen
import com.cmoney.kolfanci.ui.screens.post.info.viewmodel.PostInfoViewModel
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.PhotoPickDialogScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

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


    PostInfoScreenView(
        modifier = modifier,
        navController = navController,
        post = postData,
        imageAttachList = imageAttachList,
        comments = comments,
        onEmojiClick = { message, resourceId ->
            viewModel.onEmojiClick(message, resourceId)
        },
        onBackClick = {
            resultNavigator.navigateBack(viewModel.post.value)
        },
        onCommentSend = {
            viewModel.onCommentSend(it)
        },
        onAttachClick = {
            openImagePickDialog = true
        },
        onDeleteAttach = {
            viewModel.onDeleteAttach(it)
        },
        onCommentEmojiClick = {comment, resourceId ->
            viewModel.onCommentEmojiClick(comment, resourceId)
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
    navController: DestinationsNavigator,
    post: BulletinboardMessage,
    onEmojiClick: (BulletinboardMessage, Int) -> Unit,
    onCommentSend: (text: String) -> Unit,
    onBackClick: () -> Unit,
    imageAttachList: List<Uri>,
    onAttachClick: () -> Unit,
    onDeleteAttach: (Uri) -> Unit,
    comments: List<BulletinboardMessage>,
    onCommentEmojiClick: (BulletinboardMessage, Int) -> Unit,
) {
    val context = LocalContext.current

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
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(modifier = Modifier.weight(1f)) {
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
                            CommentBottomContent(comment)
                        },
                        onEmojiClick = {
                            onCommentEmojiClick.invoke(comment, it)
                        }
                    )
                }
            }

            //================== Bottom ==================

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
    }
}

/**
 * 留言 底部內容
 */
@Composable
private fun CommentBottomContent(comment: BulletinboardMessage) {
    //TODO mock data
    val reply = PostViewModel.mockListMessage

    var expandReply by remember {
        mutableStateOf(false)
    }

    Column {
        //貼文留言,底部 n天前, 回覆
        CommentBottomView(comment) {

        }

        if (comment.commentCount != 0) {
            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier
                    .clickable {
                        expandReply = !expandReply
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
                    text = if (expandReply) {
                        "隱藏回覆"
                    } else {
                        "查看 %d 則 回覆".format(comment.commentCount ?: 0)
                    },
                    fontSize = 14.sp,
                    color = LocalColor.current.text.default_100
                )
            }
        }


        //TODO
        if (expandReply) {
            Spacer(modifier = Modifier.height(5.dp))

            //TODO improve performance, 改為上一層 item 一環
            reply.forEach { item ->
                BasePostContentScreen(
                    post = item,
                    defaultDisplayLine = Int.MAX_VALUE,
                    contentModifier = Modifier.padding(start = 40.dp),
                    hasMoreAction = false,
                    backgroundColor = Color.Transparent,
                    bottomContent = {
                        Text(
                            text = "1天以前",
                            fontSize = 14.sp,
                            color = LocalColor.current.text.default_100
                        )
                    },
                    onEmojiClick = {

                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommentBottomContentPreview() {
    FanciTheme {
        CommentBottomContent(BulletinboardMessage())
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


@Preview(showBackground = true)
@Composable
fun PostInfoScreenPreview() {
    FanciTheme {
        PostInfoScreenView(
            navController = EmptyDestinationsNavigator,
            post = PostViewModel.mockPost,
            onEmojiClick = { post, resourceId ->
            },
            onCommentEmojiClick = { post, resourceId ->
            },
            onCommentSend = {},
            onBackClick = {},
            imageAttachList = emptyList(),
            onAttachClick = {},
            onDeleteAttach = {},
            comments = emptyList()
        )
    }
}