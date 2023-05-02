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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.OnBottomReached
import com.cmoney.kolfanci.extension.displayPostTime
import com.cmoney.kolfanci.extension.isMyPost
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.ui.destinations.EditPostScreenDestination
import com.cmoney.kolfanci.ui.destinations.PostInfoScreenDestination
import com.cmoney.kolfanci.ui.screens.post.viewmodel.PostViewModel
import com.cmoney.kolfanci.ui.theme.Color_80FFFFFF
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
    resultRecipient: ResultRecipient<EditPostScreenDestination, BulletinboardMessage>
) {
    val TAG = "PostScreen"

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val postList by viewModel.post.collectAsState()

    PostScreenView(
        modifier = modifier,
        postList = postList,
        navController = navController,
        listState = listState,
        onPostClick = {
            //OnClick Method
            navController.navigate(EditPostScreenDestination(channelId = channel.id.orEmpty()))
        }
    )

    LaunchedEffect(Unit) {
        viewModel.fetchPost()
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
                viewModel.onPostSuccess(result.value)
                coroutineScope.launch {
                    listState.scrollToItem(index = 0)
                }
            }
        }
    }
}

@Composable
private fun PostScreenView(
    modifier: Modifier = Modifier,
    postList: List<BulletinboardMessage>,
    navController: DestinationsNavigator,
    onPostClick: () -> Unit,
    listState: LazyListState
) {

    Box(modifier = Modifier.fillMaxSize()) {
        if (postList.isEmpty()) {
            EmptyPostContent(modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                state = listState,
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(items = postList) { post ->
                    PostContentScreen(
                        post = post,
                        hasMoreAction = post.isMyPost(Constant.MyInfo),
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

        FloatingActionButton(
            modifier = Modifier
                .padding(30.dp)
                .align(Alignment.BottomEnd),
            onClick = {
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

@Composable
fun CommentCount(
    navController: DestinationsNavigator? = null,
    post: BulletinboardMessage? = null
) {
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
            postList = PostViewModel.mockListMessage,
            navController = EmptyDestinationsNavigator,
            onPostClick = {},
            listState = rememberLazyListState()
        )
    }
}