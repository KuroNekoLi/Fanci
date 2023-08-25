package com.cmoney.kolfanci.ui.screens.follow

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.platform.LocalContext
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
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.*
import com.cmoney.kolfanci.ui.main.MainActivity
import com.cmoney.kolfanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import com.cmoney.kolfanci.ui.screens.follow.model.GroupItem
import com.cmoney.kolfanci.ui.screens.follow.viewmodel.FollowViewModel
import com.cmoney.kolfanci.ui.screens.group.dialog.GroupItemDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.LoginDialogScreen
import com.cmoney.kolfanci.ui.theme.Black_99000000
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.xlogin.XLoginHelper
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
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
    onDismissInvite: () -> Unit,
    onChangeGroup: (Group) -> Unit
) {
    val uiState = viewModel.uiState

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val scrollableState = rememberScrollableState { delta ->
        // 若目前應該捲動頻道列表
        if (uiState.lazyColumnScrollEnabled) {
            // delta > 0 表示向下滑
            // 若向下滑時頻道列表已不能向下滑動則透過 viewModel 重新設定目前UI的狀態
            // 其餘狀態則用於捲動頻道列表
            if (delta > 0 && !lazyListState.canScrollBackward) {
                viewModel.scrollOffset(delta, density, configuration)
                delta
            } else {
                coroutineScope.launch {
                    // lazyListState 要向下捲動參數為正數，向上捲動參數為負數，與 delta 正負剛好相反
                    lazyListState.scrollBy(-delta)
                }
                // 回傳 0f 表示不允許 scrollableState 滑動
                0f
            }
        } else {
            viewModel.scrollOffset(delta, density, configuration)
            delta
        }
    }

    //禁止進入頻道彈窗
    val openDialog = remember { mutableStateOf(false) }

    //刷新我的社團清單
    val isRefreshMyGroupList by viewModel.refreshMyGroup.collectAsState()

    if (isRefreshMyGroupList) {
        onRefreshMyGroupList.invoke()
        viewModel.refreshMyGroupDone()
    }

    //查看該社團info dialog
    val openGroupDialog by viewModel.openGroupDialog.collectAsState()

    openGroupDialog?.let { targetGroup ->
        val isJoined = myGroupList.any { groupItem ->
            groupItem.groupModel.id == targetGroup.id
        }

        GroupItemDialogScreen(
            isJoined = isJoined,
            groupModel = targetGroup,
            onDismiss = {
                viewModel.closeGroupItemDialog()
                onDismissInvite.invoke()
            },
            onConfirm = {
                //via invite link
                inviteGroup?.run {
                    if (targetGroup.isNeedApproval == true) {
                        AppUserLogger.getInstance().log(Clicked.GroupApplyToJoin, From.Link)
                    } else {
                        AppUserLogger.getInstance().log(Clicked.GroupJoin, From.Link)
                    }
                }

                if (isJoined) {
                    onChangeGroup.invoke(it)
                }
                else {
                    viewModel.joinGroup(it)
                }

                viewModel.closeGroupItemDialog()
                onDismissInvite.invoke()
            }
        )
    }

    //點擊channel權限檢查完
    LaunchedEffect(Unit) {
        chatRoomViewModel.updatePermissionDone.collect {
            if (Constant.canReadMessage()) {
                navigator.navigate(
                    ChannelScreenDestination(
                        channel = it
                    )
                )
            } else {
                //禁止進入該頻道,show dialog
                openDialog.value = true
            }
        }
    }

    //邀請加入社團
    LaunchedEffect(inviteGroup) {
        inviteGroup?.let {
            viewModel.openGroupItemDialog(it)
        }
    }

    if (openDialog.value) {
        DialogScreen(
            title = "不具有此頻道的權限",
            subTitle = "這是個上了鎖的頻道，你目前沒有權限能夠進入喔！",
            onDismiss = { openDialog.value = false }
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

    if (uiState.showLoginDialog) {
        val context = LocalContext.current
        LoginDialogScreen(
            onDismiss = {
                viewModel.dismissLoginDialog()
            },
            onLogin = {
                viewModel.dismissLoginDialog()
                (context as? MainActivity)?.startLogin()
            }
        )
    }

    //打開 建立社團
    if (uiState.navigateToCreateGroup) {
        AppUserLogger.getInstance().log(Clicked.CreateGroup, From.NonGroup)
        navigator.navigate(CreateGroupScreenDestination)
        viewModel.navigateDone()
    }

    //前往社團認證
    uiState.navigateToApproveGroup?.let {
        navigator.navigate(
            ApplyForGroupScreenDestination(
                group = it
            )
        )
        viewModel.navigateDone()
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
        lazyListState = lazyListState,
        visibleAvatar = uiState.visibleAvatar,
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
        onLoadMoreServerGroup = onLoadMoreServerGroup,
        onGoToMy = {
            if (XLoginHelper.isLogin) {
                navigator.navigate(MyScreenDestination)
            } else {
                viewModel.showLoginDialog()
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FollowScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    groupList: List<GroupItem>,
    group: Group?,
    imageOffset: Int,
    spaceHeight: Int,
    scrollableState: ScrollableState,
    lazyListState: LazyListState,
    visibleAvatar: Boolean,
    onGroupItemClick: (GroupItem) -> Unit,
    lazyColumnAtTop: () -> Unit,
    isLoading: Boolean,
    onChannelClick: (Channel) -> Unit,
    onLoadMoreServerGroup: () -> Unit,
    onGoToMy: () -> Unit,
    emptyGroupList: List<Group>
) {
    val TAG = "FollowScreenView"

    val coroutineScope = rememberCoroutineScope()
    val scaffoldState: ScaffoldState =
        rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))

    LaunchedEffect(key1 = scaffoldState) {
        snapshotFlow {
            scaffoldState.drawerState.currentValue
        }.filter {
            it == DrawerValue.Open
        }.onEach {
            AppUserLogger.getInstance().log("Home_NavigationBar_show")
        }.collect()
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        backgroundColor = LocalColor.current.env_60,
        scaffoldState = scaffoldState,
        drawerContent = {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                DrawerMenuScreen(
                    modifier = Modifier.fillMaxHeight(),
                    groupList = groupList,
                    onClick = {
                        KLog.i(TAG, "onGroup item click.")

                        AppUserLogger.getInstance().log(Clicked.NavigationBarGroup)

                        //Close Drawer
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }

                        onGroupItemClick.invoke(it)
                    },
                    onSearch = {
                        KLog.i(TAG, "onSearch click.")

                        AppUserLogger.getInstance().log(Clicked.NavigationBarExploreGroup)

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
                        KLog.i(TAG, "onProfile click.")

                        AppUserLogger.getInstance().log(Clicked.NavigationBarMemberPage)

                        //Close Drawer
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }

                        onGoToMy()
                    }
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable(
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = null,
                            onClick = {
                                coroutineScope.launch {
                                    scaffoldState.drawerState.close()
                                }
                            }
                        )
                )
            }
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
                    EmptyFollowScreenWithMenu(
                        onLoadMoreServerGroup = onLoadMoreServerGroup,
                        emptyGroupList = emptyGroupList,
                        isLoading = false,
                        openDrawer = {
                            coroutineScope.launch {
                                scaffoldState.drawerState.open()
                            }
                        }
                    )
                } else {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        val cardHeight = maxHeight
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
                                    colorFilter = ColorFilter.tint(color = LocalColor.current.primary)
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
                                    .height(cardHeight)
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
                                LaunchedEffect(key1 = Unit) {
                                    snapshotFlow {
                                        lazyListState.canScrollBackward
                                    }
                                        .onEach { canScrollBackward ->
                                            if (!canScrollBackward) {
                                                lazyColumnAtTop.invoke()
                                            }
                                        }
                                        .collect()
                                }

                                LazyColumn(
                                    state = lazyListState,
                                    userScrollEnabled = false,
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
                                        ) {
                                            AppUserLogger.getInstance()
                                                .log(Clicked.GroupGroupSettings)
                                            navigator.navigate(
                                                GroupSettingScreenDestination
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
            //Empty follow group
            EmptyFollowScreenWithMenu(
                onLoadMoreServerGroup = onLoadMoreServerGroup,
                emptyGroupList = emptyGroupList,
                isLoading = isLoading,
                openDrawer = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            )
        }
    }
}

/**
 * 未加入社團 畫面
 */
@Composable
private fun EmptyFollowScreenWithMenu(
    onLoadMoreServerGroup: () -> Unit,
    emptyGroupList: List<Group>,
    isLoading: Boolean,
    openDrawer: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        EmptyFollowScreen(
            onLoadMore = onLoadMoreServerGroup,
            groupList = emptyGroupList,
            isLoading = isLoading
        )
        //Menu
        Box(
            modifier = Modifier
                .padding(20.dp)
                .size(45.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(LocalColor.current.env_80)
                .clickable {
                    openDrawer.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.menu),
                contentDescription = null
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
            modifier = Modifier,
            navigator = EmptyDestinationsNavigator,
            groupList = emptyList(),
            group = Group(),
            imageOffset = 0,
            spaceHeight = 0,
            scrollableState = rememberScrollableState {
                it
            },
            lazyListState = rememberLazyListState(),
            visibleAvatar = false,
            onGroupItemClick = {},
            lazyColumnAtTop = {},
            isLoading = true,
            onChannelClick = {},
            onLoadMoreServerGroup = {},
            onGoToMy = {},
            emptyGroupList = emptyList(),
        )
    }
}
