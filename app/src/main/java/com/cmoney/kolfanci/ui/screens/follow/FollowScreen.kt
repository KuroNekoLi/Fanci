package com.cmoney.kolfanci.ui.screens.follow

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.*
import com.cmoney.kolfanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import com.cmoney.kolfanci.ui.screens.follow.model.GroupItem
import com.cmoney.kolfanci.ui.screens.follow.viewmodel.FollowViewModel
import com.cmoney.kolfanci.ui.screens.group.dialog.GroupItemDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.theme.Black_99000000
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.xlogin.XLoginHelper
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun FollowScreen(
    modifier: Modifier,
    navigator: DestinationsNavigator,
    group: Group?,
    serverGroupList: List<Group>,
    viewModel: FollowViewModel = koinViewModel(),
    chatRoomViewModel: ChatRoomViewModel = koinViewModel(),
    myGroupList: List<GroupItem>,
    onGroupItemClick: (Group) -> Unit,
    onLoadMoreServerGroup: () -> Unit,
    onRefreshMyGroupList: () -> Unit,
    isLoading: Boolean,
    inviteGroup: Group?,
    onDismissInvite: () -> Unit
) {
    val uiState = viewModel.uiState
    val chatRoomUiState = chatRoomViewModel.uiState

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val scrollableState = rememberScrollableState {
        viewModel.scrollOffset(it, density, configuration)
        it
    }

    //禁止進入頻道彈窗
    val openDialog = remember { mutableStateOf(false) }

    //刷新我的社團清單
    val isRefreshMyGroupList by viewModel.refreshMyGroup.collectAsState()

    if (isRefreshMyGroupList) {
        onRefreshMyGroupList.invoke()
    }

    //查看該社團info dialog
    val openGroupDialog by viewModel.openGroupDialog.collectAsState()

    openGroupDialog?.let { group ->
        GroupItemDialogScreen(
            groupModel = group,
            onDismiss = {
                viewModel.closeGroupItemDialog()
                onDismissInvite.invoke()
            },
            onConfirm = {
                viewModel.joinGroup(it)
                onDismissInvite.invoke()
            }
        )
    }

    //點擊channel權限檢查完
    chatRoomUiState.enterChannel?.let { channel ->
        if (Constant.canReadMessage()) {
            navigator.navigate(
                ChannelScreenDestination(
                    channel = channel
                )
            )
        } else {
            //禁止進入該頻道,show dialog
            openDialog.value = true
        }
        chatRoomViewModel.resetChannel()
    }

    //邀請加入社團
    LaunchedEffect(inviteGroup) {
        inviteGroup?.let {
            viewModel.openGroupItemDialog(it)
        }
    }

    if (openDialog.value) {
        DialogScreen(
            onDismiss = { openDialog.value = false },
            titleIconRes = R.drawable.minus_people,
            title = "不具有此頻道的權限",
            subTitle = "這是個上了鎖的頻道，你目前沒有權限能夠進入喔！"
        ) {
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = "返回",
                borderColor = LocalColor.current.text.default_50,
                textColor = LocalColor.current.text.default_100
            ) {
                run {
                    openDialog.value = false
                }
            }
        }
    }

    FollowScreenView(
        modifier = modifier,
        navigator = navigator,
        groupList = myGroupList,
        group = group,
        emptyGroupList = serverGroupList,
        imageOffset = uiState.imageOffset,
        spaceHeight = uiState.spaceHeight,
        scrollableState = scrollableState,
        visibleAvatar = uiState.visibleAvatar,
        lazyColumnScrollEnabled = uiState.lazyColumnScrollEnabled,
        onGroupItemClick = {
            onGroupItemClick.invoke(it.groupModel)
        },
        lazyColumnAtTop = {
            viewModel.lazyColumnAtTop()
        },
        isLoading = isLoading,
        onChannelClick = {
            chatRoomViewModel.fetchChannelPermission(it)
        },
        onLoadMoreServerGroup = onLoadMoreServerGroup
    )
}

fun LazyListState.isScrolledToTop() = firstVisibleItemScrollOffset == 0

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FollowScreenView(
    navigator: DestinationsNavigator,
    groupList: List<GroupItem>,
    group: Group?,
    imageOffset: Int,
    spaceHeight: Int,
    scrollableState: ScrollableState,
    visibleAvatar: Boolean,
    lazyColumnScrollEnabled: Boolean,
    onGroupItemClick: (GroupItem) -> Unit,
    lazyColumnAtTop: () -> Unit,
    isLoading: Boolean,
    onChannelClick: (Channel) -> Unit,
    onLoadMoreServerGroup: () -> Unit,
    emptyGroupList: List<Group>,
    modifier: Modifier
) {
    val TAG = "FollowScreenView"

    val coroutineScope = rememberCoroutineScope()

    val scaffoldState: ScaffoldState =
        rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))

    val lazyListState = rememberLazyListState()

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        backgroundColor = LocalColor.current.env_60,
        scaffoldState = scaffoldState,
        drawerContent = if (XLoginHelper.isLogin) {
            {
                DrawerMenuScreen(
                    groupList = groupList,
                    onClick = {
                        //Close Drawer
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }

                        onGroupItemClick.invoke(it)
                    },
                    onSearch = {
                        //Close Drawer
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }

                        val arrayGroupItems = arrayListOf<Group>()
                        arrayGroupItems.addAll(groupList.map {
                            it.groupModel
                        })

                        navigator.navigate(
                            DiscoverGroupScreenDestination(
                                groupItems = arrayGroupItems
                            )
                        )
                    },
                    onProfile = {
                        navigator.navigate(MyScreenDestination)
                    }
                )
            }
        } else {
            null
        },
        drawerBackgroundColor = Color.Transparent,
        drawerElevation = 0.dp,
        drawerScrimColor = Black_99000000
    ) { innerPadding ->
        //是否登入
        if (XLoginHelper.isLogin) {
            //是否讀取資料中
            if (isLoading) {
                //Loading View
                LoadingView()
            } else {
                //是否已經加入群組
                if (group == null) {
                    //Empty follow group
                    EmptyFollowScreen(
                        navigator = navigator,
                        onLoadMore = onLoadMoreServerGroup,
                        groupList = emptyGroupList,
                        isLoading = isLoading
                    )
                } else {
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
                                model = group.coverImageUrl,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .offset {
                                        IntOffset(
                                            x = 0,
                                            y = imageOffset
                                        ) //设置偏移量
                                    },
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                placeholder = painterResource(id = R.drawable.placeholder)
                            )

                            //Menu
                            Box(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LocalColor.current.env_80)
                                    .clickable {
                                        //Open Drawer
                                        coroutineScope.launch {
                                            scaffoldState.drawerState.open()
                                        }
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
                                        spaceHeight.toDp()
                                    }
                                )
                                .width(65.dp)
                                .clickable {
                                    //Open Drawer
                                    coroutineScope.launch {
                                        scaffoldState.drawerState.open()
                                    }
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
                                        lazyListState.isScrolledToTop()
                                    }
                                }

                                LaunchedEffect(endOfListReached) {
                                    lazyColumnAtTop.invoke()
                                }

                                LazyColumn(
                                    state = lazyListState,
                                    userScrollEnabled = lazyColumnScrollEnabled,
                                    verticalArrangement = Arrangement.spacedBy(15.dp),
                                    modifier = Modifier.padding(
                                        start = 20.dp,
                                        end = 20.dp,
                                        top = 20.dp
                                    )
                                ) {
                                    //置頂 縮圖
                                    stickyHeader {
                                        GroupHeaderScreen(
                                            followGroup = group,
                                            visibleAvatar = visibleAvatar,
                                            modifier = Modifier.background(LocalColor.current.env_80)
                                        ) { group ->
                                            navigator.navigate(
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
                                            onChannelClick.invoke(channel)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            EmptyFollowScreen(
                navigator = navigator,
                onLoadMore = onLoadMoreServerGroup,
                groupList = emptyGroupList,
                isLoading = isLoading
            )
        }
    }
}

@Composable
fun LoadingView() {
    Column(
        modifier = Modifier
            .padding(top = 20.dp, start = 20.dp, end = 20.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.fanci),
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                color = LocalColor.current.primary
            )
        )
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.3f),
            contentScale = ContentScale.Fit,
            painter = painterResource(id = R.drawable.follow_empty),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "加入Fanci社團跟我們一起快快樂樂！\n立即建立、加入熱門社團",
            fontSize = 14.sp,
            color = LocalColor.current.text.default_100,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(size = 64.dp),
                color = LocalColor.current.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingViewPreview() {
    FanciTheme {
        LoadingView()
    }
}

@Preview(showBackground = true)
@Composable
fun FollowScreenPreview() {
    FanciTheme {
        FollowScreenView(
            navigator = EmptyDestinationsNavigator,
            groupList = emptyList(),
            group = Group(),
            imageOffset = 0,
            spaceHeight = 0,
            scrollableState = rememberScrollableState {
                it
            },
            visibleAvatar = false,
            lazyColumnScrollEnabled = true,
            onGroupItemClick = {},
            lazyColumnAtTop = {},
            isLoading = true,
            onChannelClick = {},
            onLoadMoreServerGroup = {},
            emptyGroupList = emptyList(),
            modifier = Modifier
        )
    }
}
