package com.cmoney.kolfanci.ui.screens.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.ui.destinations.PostInfoScreenDestination
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Composable
fun PostScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    channel: Channel,
    viewModel: PostViewModel = koinViewModel()
) {
    val postList by viewModel.post.collectAsState()

    PostScreenView(
        modifier = modifier,
        postList = postList,
        navController = navController
    )

    LaunchedEffect(Unit) {
        viewModel.fetchPost(channel.id.orEmpty())
    }
}

@Composable
private fun PostScreenView(
    modifier: Modifier = Modifier,
    postList: List<ChatMessage>,
    navController: DestinationsNavigator
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(items = postList) { post ->
            PostContentScreen(
                post = post,
                bottomContent = {
                    CommentCount(
                        post = post,
                        navController = navController
                    )
                }
            )
        }
    }
}

@Composable
fun CommentCount(navController: DestinationsNavigator? = null, post: ChatMessage? = null) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .clickable {
                post?.apply {
                    navController?.navigate(
                        PostInfoScreenDestination(
                            post = post
                        )
                    )
                }
            }
            .padding(10.dp),
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

        Text(text = "留言 142", fontSize = 14.sp, color = LocalColor.current.text.default_100)
    }
}

@Preview(showBackground = true)
@Composable
fun PostScreenPreview() {
    FanciTheme {
        PostScreenView(
            postList = ChatRoomUseCase.mockListMessage,
            navController = EmptyDestinationsNavigator
        )
    }
}