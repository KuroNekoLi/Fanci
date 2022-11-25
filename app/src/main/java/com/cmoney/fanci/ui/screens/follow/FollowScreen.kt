package com.cmoney.fanci.ui.screens.follow

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.screens.follow.state.FollowScreenState
import com.cmoney.fanci.ui.screens.follow.state.rememberFollowScreenState
import com.cmoney.fanci.ui.theme.Black_99000000
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.Group
import com.socks.library.KLog
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FollowScreen(
    followScreenState: FollowScreenState = rememberFollowScreenState(),
    onChannelClick: ((channel: Channel) -> Unit)?,
    onSearchClick: () -> Unit,
    onGroupSettingClick: (Group) -> Unit
) {
    val TAG = "FollowScreen"
    val followCategoryList = followScreenState.viewModel.followData.observeAsState()
    val groupList = followScreenState.viewModel.myGroupList.observeAsState()

    KLog.i(TAG, "FollowScreen create.")

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        backgroundColor = LocalColor.current.env_60,
        scaffoldState = followScreenState.scaffoldState,
        drawerContent = {
            DrawerMenuScreen(
                groupList = groupList.value.orEmpty(),
                onClick = {
                    followScreenState.viewModel.groupItemClick(it)
                    followScreenState.closeDrawer()
                },
                onSearch = {
                    followScreenState.closeDrawer()
                    onSearchClick.invoke()
                }
            )
        },
        drawerBackgroundColor = Color.Transparent,
        drawerElevation = 0.dp,
        drawerScrimColor = Black_99000000
    ) { innerPadding ->
        if (followCategoryList.value != null) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    //Cover Image
                    AsyncImage(
                        alignment = Alignment.TopCenter,
                        model = followCategoryList.value?.coverImageUrl,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .offset {
                                IntOffset(
                                    x = 0,
                                    y = followScreenState.imageOffsetHeightPx.value.roundToInt()
                                ) //设置偏移量
                            },
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        placeholder = painterResource(id = R.drawable.resource_default)
                    )

                    //Menu
                    Box(
                        modifier = Modifier
                            .padding(20.dp)
                            .size(45.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(LocalColor.current.env_80)
                            .clickable {
                                followScreenState.openDrawer()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.menu),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color = LocalColor.current.env_100)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .scrollable(
                            state = rememberScrollableState {
                                followScreenState.scrollOffset(it)
                                it
                            },
                            orientation = Orientation.Vertical
                        )
                ) {
                    Spacer(modifier = Modifier
                        .height(
                            with(LocalDensity.current) { followScreenState.spaceOffsetHeightPx.value.toDp() }
                        )
                        .width(45.dp)
                        .clickable {
                            followScreenState.openDrawer()
                        })
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(850.dp)
                            .padding(
                                top = 0.dp,
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                        elevation = 0.dp,
                        shape = RoundedCornerShape(20.dp),
                        backgroundColor = LocalColor.current.env_80
                    ) {
                        val scrollState = rememberLazyListState()

                        // observer when reached end of list
                        val endOfListReached by remember {
                            derivedStateOf {
                                scrollState.isScrolledToTop()
                            }
                        }

                        LaunchedEffect(endOfListReached) {
                            // do your stuff
                            followScreenState.lazyColumnAtTop()
                        }

                        LazyColumn(
                            state = scrollState,
                            userScrollEnabled = followScreenState.lazyColumnScrollEnabled,
                            verticalArrangement = Arrangement.spacedBy(15.dp),
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)
                        ) {
                            //置頂 縮圖
                            stickyHeader {
                                followCategoryList.value?.let {
                                    GroupHeaderScreen(
                                        followGroup = it,
                                        visibleAvatar = followScreenState.visibleAvatar,
                                        modifier = Modifier.background(LocalColor.current.env_80)
                                    ) { group ->
                                        onGroupSettingClick.invoke(group)
                                    }
                                }
                            }

                            //頻道
                            followCategoryList.value?.let {
                                items(it.categories.orEmpty()) { category ->
                                    CategoryScreen(category = category) { channel ->
                                        KLog.i(TAG, "Category click:$channel")
                                        onChannelClick?.invoke(channel)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            //Empty follow group
            EmptyFollowScreen()
        }
    }
}

fun LazyListState.isScrolledToTop() = firstVisibleItemScrollOffset == 0

@Preview(showBackground = true)
@Composable
fun FollowScreenPreview() {
    FanciTheme {
        FollowScreen(
            onChannelClick = {},
            onSearchClick = {},
            onGroupSettingClick = {}
        )
    }
}
