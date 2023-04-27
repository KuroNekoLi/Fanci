package com.cmoney.kolfanci.ui.screens.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.ui.destinations.EditPostScreenDestination
import com.cmoney.kolfanci.ui.destinations.PostInfoScreenDestination
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
import com.cmoney.kolfanci.ui.theme.Color_80FFFFFF
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

    if (postList.isEmpty()) {
        EmptyPostContent(modifier = Modifier.fillMaxSize())
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(items = postList) { post ->
                    PostContentScreen(
                        post = post,
                        contentModifier = Modifier.padding(bottom = 15.dp),
                        bottomContent = {
                            CommentCount(
                                post = post,
                                navController = navController
                            )
                        }
                    )
                }
            }

            FloatingActionButton(
                modifier = Modifier.padding(30.dp).align(Alignment.BottomEnd),
                onClick = {
                    //OnClick Method
                    navController.navigate(EditPostScreenDestination)
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
fun CommentCount(navController: DestinationsNavigator? = null, post: ChatMessage? = null) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .then(
                if (post != null) {
                    Modifier.clickable {
                        navController?.navigate(
                            PostInfoScreenDestination(
                                post = post
                            )
                        )
                    }
                } else {
                    Modifier
                }
            )
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


@Composable
private fun EmptyPostContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(LocalColor.current.env_80),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AsyncImage(
            modifier = Modifier.size(186.dp, 248.dp),
            model = R.drawable.empty_message, contentDescription = "empty message"
        )

        Spacer(modifier = Modifier.height(43.dp))

        Text(text = "快成為第一個在貼文區發文的人！", fontSize = 16.sp, color = Color_80FFFFFF)
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