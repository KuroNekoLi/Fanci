package com.cmoney.kolfanci.ui.screens.follow

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.GroupJoinStatus
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.destinations.ApplyForGroupScreenDestination
import com.cmoney.kolfanci.ui.destinations.CreateGroupScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupSettingScreenDestination
import com.cmoney.kolfanci.ui.destinations.MyScreenDestination
import com.cmoney.kolfanci.ui.destinations.NotificationCenterScreenDestination
import com.cmoney.kolfanci.ui.main.MainActivity
import com.cmoney.kolfanci.ui.screens.follow.model.GroupItem
import com.cmoney.kolfanci.ui.screens.follow.viewmodel.FollowViewModel
import com.cmoney.kolfanci.ui.screens.group.dialog.GroupItemDialogScreen
import com.cmoney.kolfanci.ui.screens.media.audio.AudioViewModel
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.JoinGroupDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.LoginDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.item.InputInviteCodeScreen
import com.cmoney.kolfanci.ui.theme.Black_99000000
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.xlogin.XLoginHelper
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun FollowScreen(
    modifier: Modifier,
    navigator: DestinationsNavigator,
    group: Group?,
    viewModel: FollowViewModel = koinViewModel(),
    myGroupList: List<GroupItem>,
    onGroupItemClick: (Group) -> Unit,
    onRefreshMyGroupList: (isSilent: Boolean) -> Unit,
    isLoading: Boolean,
    inviteGroup: Group?,
    notificationUnReadCount: Long,
    onDismissInvite: () -> Unit,
    onChannelClick: (Channel) -> Unit,
    onChangeGroup: (Group) -> Unit,
    audioViewModel: AudioViewModel = koinViewModel(
        parameters = {
            parametersOf(Uri.EMPTY)
        }
    )
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

    //刷新我的社團清單
    val isRefreshMyGroupList by viewModel.refreshMyGroup.collectAsState()

    if (isRefreshMyGroupList) {
        onRefreshMyGroupList(false)
        viewModel.refreshMyGroupDone()
    }

    //查看該社團info dialog
    val openGroupDialog by viewModel.openGroupDialog.collectAsState()

    //是否出現 加入社團彈窗
    var openJoinGroupDialog by remember {
        mutableStateOf(false)
    }

    //輸入邀請碼彈窗
    val showInviteCodeDialog by viewModel.isShowInviteCodeDialog.collectAsState()

    openGroupDialog?.let { targetGroup ->
        val context = LocalContext.current
        GroupItemDialogScreen(
            groupModel = targetGroup,
            onDismiss = {
                viewModel.closeGroupItemDialog()
                onDismissInvite.invoke()
            },
            onConfirm = { group, joinStatus ->
                //via invite link
                if (inviteGroup != null) {
                    if (group.isNeedApproval == true) {
                        AppUserLogger.getInstance().log(Clicked.GroupApplyToJoin, From.Link)
                    } else {
                        if (joinStatus != GroupJoinStatus.Joined) {
                            AppUserLogger.getInstance().log(Clicked.GroupJoin, From.Link)
                        } else {
                            AppUserLogger.getInstance()
                                .log(Clicked.GroupEnter, From.Link)
                        }
                    }
                }

                when (joinStatus) {
                    GroupJoinStatus.InReview -> {
                        viewModel.closeGroupItemDialog()
                        onDismissInvite.invoke()
                    }

                    GroupJoinStatus.Joined -> {
                        onChangeGroup.invoke(group)
                        viewModel.closeGroupItemDialog()
                        onDismissInvite.invoke()
                    }

                    GroupJoinStatus.NotJoin -> {
                        if (XLoginHelper.isLogin) {
                            viewModel.joinGroup(group)
                        } else {
                            (context as? MainActivity)?.startLogin()
                        }
                    }
                }
            }
        )
    }

    //邀請加入社團
    LaunchedEffect(inviteGroup) {
        inviteGroup?.let {
            viewModel.openGroupItemDialog(it)
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

    if (uiState.needNotifyAllowNotificationPermission) {
        val activity = LocalContext.current as? Activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (activity != null && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    MainActivity.REQUEST_CODE_ALLOW_NOTIFICATION_PERMISSION
                )
            }
        }
        viewModel.alreadyNotifyAllowNotificationPermission()
    }

    //加入社團彈窗
    if (openJoinGroupDialog) {
        JoinGroupDialogScreen(
            onInviteCodeClick = {
                openJoinGroupDialog = false
                viewModel.onInputInviteCodeClick()
            },
            onCreateGroupClick = {
                openJoinGroupDialog = false
                navigator.navigate(
                    CreateGroupScreenDestination
                )
            },
            onDismiss = {
                openJoinGroupDialog = false
            }
        )
    }

    //輸入邀請碼彈窗
    if (showInviteCodeDialog) {
        DialogScreen(
            title = "輸入邀請碼",
            subTitle = "透過邀請碼加入社團吧！",
            onDismiss = {
                viewModel.closeInviteCodeDialog()
            }
        ) {
            InputInviteCodeScreen(
                onConfirm = { inviteCode ->
                    viewModel.onInputInviteCode(inviteCode)
                },
                onCancel = {
                    viewModel.closeInviteCodeDialog()
                }
            )
        }
    }


    FollowScreenView(
        modifier = modifier,
        navigator = navigator,
        groupList = myGroupList,
        group = group,
        imageOffset = uiState.imageOffset,
        spaceHeight = uiState.spaceHeight,
        scrollableState = scrollableState,
        lazyListState = lazyListState,
        visibleAvatar = uiState.visibleAvatar,
        onGroupItemClick = {
            onGroupItemClick.invoke(it.groupModel)
            viewModel.disableBubbleTip()
        },
        lazyColumnAtTop = {
            viewModel.lazyColumnAtTop()
        },
        isLoading = isLoading,
        onChannelClick = onChannelClick,
        onGoToMy = {
            if (XLoginHelper.isLogin) {
                navigator.navigate(MyScreenDestination)
            } else {
                viewModel.showLoginDialog()
            }
        },
        isShowBubbleTip = uiState.isShowBubbleTip,
        onMoreClick = {
            viewModel.disableBubbleTip()
            AppUserLogger.getInstance()
                .log(Clicked.GroupGroupSettings)
            navigator.navigate(
                GroupSettingScreenDestination
            )
        },
        notificationUnReadCount = notificationUnReadCount,
        onPlusClick = {
            openJoinGroupDialog = true
        }
    )

    LaunchedEffect(key1 = Unit) {
        viewModel.fetchSetting()

        audioViewModel.fetchIsShowMiniIcon()
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
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
    onGoToMy: () -> Unit,
    isShowBubbleTip: Boolean,
    onMoreClick: (Group) -> Unit,
    notificationUnReadCount: Long,
    onPlusClick: () -> Unit
) {
    val TAG = "FollowScreenView"

    val coroutineScope = rememberCoroutineScope()
    val scaffoldState: ScaffoldState =
        rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))

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
                    notificationUnReadCount = notificationUnReadCount,
                    onClick = {
                        KLog.i(TAG, "onGroup item click.")

                        AppUserLogger.getInstance()
                            .log(Clicked.SideBarGroup)

                        //Close Drawer
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }

                        onGroupItemClick.invoke(it)
                    },
                    onPlusClick = {
                        KLog.i(TAG, "onPlusClick.")

                        AppUserLogger.getInstance()
                            .log(clicked = Clicked.CreateGroup, from = From.SideBar)

                        //Close Drawer
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }

                        onPlusClick.invoke()
                    },
                    onProfile = {
                        KLog.i(TAG, "onProfile click.")

                        AppUserLogger.getInstance()
                            .log(Clicked.SideBarMemberPage)

                        //Close Drawer
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }

                        onGoToMy()
                    },
                    onNotification = {
                        KLog.i(TAG, "onNotification click.")

                        AppUserLogger.getInstance()
                            .log(Clicked.SideBarNotification)

                        //Close Drawer
                        coroutineScope.launch {
                            scaffoldState.drawerState.close()
                        }

                        navigator.navigate(
                            NotificationCenterScreenDestination
                        )
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
                        modifier = Modifier.fillMaxSize(),
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
                                        AppUserLogger
                                            .getInstance()
                                            .log("Home_SideBar_show")
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
                                            modifier = Modifier.background(LocalColor.current.env_80),
                                            isShowBubbleTip = isShowBubbleTip,
                                            onMoreClick = onMoreClick
                                        )
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
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        EmptyFollowScreen(modifier = Modifier.fillMaxSize())
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
            onGoToMy = {},
            isShowBubbleTip = false,
            onMoreClick = {},
            notificationUnReadCount = 99,
            onPlusClick = {}
        )
    }
}
