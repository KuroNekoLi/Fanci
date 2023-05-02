package com.cmoney.kolfanci.ui.screens.post.info

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.ui.screens.post.CommentCount
import com.cmoney.kolfanci.ui.screens.post.PostContentScreen
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@Destination
@Composable
fun PostInfoScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    post: BulletinboardMessage
) {
    PostInfoScreenView(
        modifier = modifier,
        navController = navController,
        post = post
    )
}

@Composable
private fun PostInfoScreenView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    post: BulletinboardMessage
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = "貼文",
                moreEnable = false,
                backClick = {
                    navController.popBackStack()
                }
            )
        },
        backgroundColor = LocalColor.current.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            item {
                PostContentScreen(
                    post = post,
                    defaultDisplayLine = Int.MAX_VALUE,
                    bottomContent = {
                        CommentCount()
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
            //TODO mock data
            items(PostViewModel.mockListMessage) { comment ->
                PostContentScreen(
                    post = comment,
                    defaultDisplayLine = Int.MAX_VALUE,
                    contentModifier = Modifier.padding(start = 40.dp),
                    hasMoreAction = false,
                    bottomContent = {
                        CommentBottomContent()
                    }
                )
            }
        }
    }
}

/**
 * 回覆底部內容
 */
@Composable
private fun CommentBottomContent() {
    //TODO mock data
    val reply = PostViewModel.mockListMessage

    var expandReply by remember {
        mutableStateOf(false)
    }

    Column {
        ReplyCount()

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
                    "查看 %d 則 回覆".format(reply.size)
                },
                fontSize = 14.sp,
                color = LocalColor.current.text.default_100
            )
        }

        if (expandReply) {
            Spacer(modifier = Modifier.height(5.dp))

            //TODO improve performance, 改為上一層 item 一環
            reply.forEach { item ->
                PostContentScreen(
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
        CommentBottomContent()
    }
}

@Composable
fun ReplyCount() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "1天以前", fontSize = 14.sp, color = LocalColor.current.text.default_100)

        Box(
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp)
                .size(3.8.dp)
                .clip(CircleShape)
                .background(LocalColor.current.text.default_100)
        )

        Text(text = "回覆", fontSize = 14.sp, color = LocalColor.current.text.default_100)
    }
}


@Preview(showBackground = true)
@Composable
fun PostInfoScreenPreview() {
    FanciTheme {
        PostInfoScreenView(
            post = PostViewModel.mockPost,
            navController = EmptyDestinationsNavigator
        )
    }
}