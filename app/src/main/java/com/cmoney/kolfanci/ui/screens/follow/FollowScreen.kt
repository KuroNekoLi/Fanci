package com.cmoney.kolfanci.ui.screens.follow

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cmoney.kolfanci.MainActivity
import com.cmoney.kolfanci.MainViewModel
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.destinations.ChatRoomScreenDestination
import com.cmoney.kolfanci.destinations.DiscoverGroupScreenDestination
import com.cmoney.kolfanci.destinations.GroupSettingScreenDestination
import com.cmoney.kolfanci.ui.screens.follow.state.FollowScreenState
import com.cmoney.kolfanci.ui.screens.follow.state.rememberFollowScreenState
import com.cmoney.kolfanci.ui.theme.Black_99000000
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.xlogin.XLoginHelper
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FollowScreen(
    followScreenState: FollowScreenState = rememberFollowScreenState(),
    globalViewModel: MainViewModel,
    navigator: DestinationsNavigator
) {
    val TAG = "FollowScreen"

    val globalUiState = globalViewModel.uiState
    val group = globalUiState.currentGroup
    val groupList = followScreenState.viewModel.myGroupList.observeAsState()
    if (group == null) {
        groupList.value?.first()?.let {
            globalViewModel.setCurrentGroup(it.groupModel)
        }
    } else {
        followScreenState.viewModel.checkGroupMenu(group)
    }

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val scrollableState = rememberScrollableState {
        followScreenState.viewModel.scrollOffset(it, density, configuration)
        it
    }

    val context = LocalContext.current

    KLog.i(TAG, "FollowScreen create.")

    if (globalUiState.isLoginSuccess) {
        followScreenState.viewModel.fetchMyGroup()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        backgroundColor = LocalColor.current.env_60,
        scaffoldState = followScreenState.scaffoldState,
        drawerContent = {
            DrawerMenuScreen(
                groupList = groupList.value.orEmpty(),
                onClick = {
                    globalViewModel.setCurrentGroup(it.groupModel)
                    followScreenState.viewModel.groupItemClick(it)
                    followScreenState.closeDrawer()
                },
                onSearch = {
                    followScreenState.closeDrawer()
                    val arrayGroupItems = arrayListOf<Group>()
                    arrayGroupItems.addAll(groupList.value.orEmpty().map {
                        it.groupModel
                    })
                    navigator.navigate(
                        DiscoverGroupScreenDestination(
                            groupItems = arrayGroupItems
                        )
                    )
                },
                onLogout = {
                    XLoginHelper.logOut(context)
                    val intent = Intent(context, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    context.startActivity(intent)
                    Runtime.getRuntime().exit(0)
                }
            )
        },
        drawerBackgroundColor = Color.Transparent,
        drawerElevation = 0.dp,
        drawerScrimColor = Black_99000000
    ) { innerPadding ->
        if (group != null) {
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
                        model = group?.coverImageUrl,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .offset {
                                IntOffset(
                                    x = 0,
                                    y = followScreenState.viewModel.uiState.imageOffset
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
                            state = scrollableState,
                            orientation = Orientation.Vertical
                        )
                ) {
                    Spacer(modifier = Modifier
                        .height(
                            with(LocalDensity.current) {
                                followScreenState.viewModel.uiState.spaceHeight.toDp()
                            }
                        )
                        .width(65.dp)
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
                        // observer when reached end of list
                        val endOfListReached by remember {
                            derivedStateOf {
                                followScreenState.scrollState.isScrolledToTop()
                            }
                        }

                        LaunchedEffect(endOfListReached) {
                            // do your stuff
                            followScreenState.viewModel.lazyColumnAtTop()
                        }

                        LazyColumn(
                            state = followScreenState.scrollState,
                            userScrollEnabled = followScreenState.viewModel.uiState.lazyColumnScrollEnabled,
                            verticalArrangement = Arrangement.spacedBy(15.dp),
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp)
                        ) {
                            //置頂 縮圖
                            stickyHeader {
                                GroupHeaderScreen(
                                    followGroup = group,
                                    visibleAvatar = followScreenState.viewModel.uiState.visibleAvatar,
                                    modifier = Modifier.background(LocalColor.current.env_80)
                                ) { group ->
                                    navigator?.navigate(
                                        GroupSettingScreenDestination(
                                            initGroup = group
                                        )
                                    )
                                }
                            }

                            //頻道
                            items(group.categories.orEmpty()) { category ->
                                CategoryScreen(category = category) { channel ->
                                    KLog.i(TAG, "Category click:$channel")
                                    navigator.navigate(
                                        ChatRoomScreenDestination(
                                            channelId = channel.id.orEmpty(),
                                            channelTitle = channel.name.orEmpty()
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            //Empty follow group
            EmptyFollowScreen(
                navigator = navigator
            )
        }
    }
}

fun LazyListState.isScrolledToTop() = firstVisibleItemScrollOffset == 0

@Preview(showBackground = true)
@Composable
fun FollowScreenPreview() {
    FanciTheme {
        FollowScreen(
            globalViewModel = koinViewModel(),
            navigator = EmptyDestinationsNavigator
        )
    }
}
