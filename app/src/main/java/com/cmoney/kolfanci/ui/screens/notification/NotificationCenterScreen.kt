package com.cmoney.kolfanci.ui.screens.notification

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.OnBottomReached
import com.cmoney.kolfanci.extension.globalGroupViewModel
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.GroupJoinStatus
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.model.viewmodel.NotificationViewModel
import com.cmoney.kolfanci.model.viewmodel.PushDataWrapper
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.ChannelScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupApplyScreenDestination
import com.cmoney.kolfanci.ui.destinations.PostInfoScreenDestination
import com.cmoney.kolfanci.ui.main.MainActivity
import com.cmoney.kolfanci.ui.screens.group.dialog.GroupItemDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun NotificationCenterScreen(
    navController: DestinationsNavigator,
    viewModel: NotificationCenterViewModel = koinViewModel()
) {
    val notificationCenterDataList by viewModel.notificationCenter.collectAsState()
    val payload by viewModel.payload.collectAsState()

    NotificationCenterView(
        navController = navController,
        notificationCenterDataList = notificationCenterDataList,
        onClick = {
            AppUserLogger.getInstance()
                .log(Clicked.NotificationNotification)
            viewModel.onNotificationClick(it)
        },
        onLoadMore = {
            viewModel.onLoadMore()
        }
    )

    //點擊動作
    payload?.let { payload ->
        val context = LocalContext.current
        (context as? MainActivity)?.checkPayload(payload)
        viewModel.clickPayloadDone()
    }

    //TODO: 需要優化整合 目前跟 MainScreen 處理一樣的東西
    //禁止進入頻道彈窗
    val channelAlertDialog = remember { mutableStateOf(false) }
    val globalGroupViewModel = globalGroupViewModel()
    val notificationViewModel = koinViewModel<NotificationViewModel>(
        viewModelStoreOwner = LocalContext.current as? ComponentActivity ?: checkNotNull(
            LocalViewModelStoreOwner.current
        )
    )

    //打開指定社團
    val openGroup by notificationViewModel.openGroup.collectAsState()

    //前往申請加入審核畫面
    val groupApprovePage by notificationViewModel.groupApprovePage.collectAsState()

    //沒有權限 彈窗
    val showNoPermissionTip by notificationViewModel.showNoPermissionTip.collectAsState()

    //邀請加入社團
    val openGroupDialog by notificationViewModel.inviteGroup.collectAsState()

    //解散社團 彈窗
    val showDissolveDialog by notificationViewModel.showDissolveGroupDialog.collectAsState()

    //前往指定 訊息/文章...
    val pushDataWrapper by notificationViewModel.jumpToChannelDest.collectAsState()
    LaunchedEffect(pushDataWrapper) {
        if (Constant.canReadMessage()) {
            pushDataWrapper?.let { pushDataWrapper ->
                when (pushDataWrapper) {
                    //設定指定社團並前往指定訊息
                    is PushDataWrapper.ChannelMessage -> {
                        navController.navigate(
                            ChannelScreenDestination(
                                channel = pushDataWrapper.channel,
                                jumpChatMessage = ChatMessage(
                                    id = pushDataWrapper.messageId
                                )
                            )
                        )
                    }

                    //設定指定社團並打開貼文
                    is PushDataWrapper.ChannelPost -> {
                        navController.navigate(
                            PostInfoScreenDestination(
                                channel = pushDataWrapper.channel,
                                post = pushDataWrapper.bulletinboardMessage
                            )
                        )
                    }
                }
            }
        } else {
            //禁止進入該頻道,show dialog
            channelAlertDialog.value = true
        }
        notificationViewModel.finishJumpToChannelDest()
    }

    if (channelAlertDialog.value) {
        DialogScreen(
            title = "不具有此頻道的權限",
            subTitle = "這是個上了鎖的頻道，你目前沒有權限能夠進入喔！",
            onDismiss = { channelAlertDialog.value = false }
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
                    channelAlertDialog.value = false
                }
            }
        }
        LaunchedEffect(key1 = payload) {
            AppUserLogger.getInstance()
                .log(clicked = Clicked.CannotUse, from = From.Notification)
        }
    }

    LaunchedEffect(key1 = Unit) {
        AppUserLogger.getInstance()
            .log(Page.Notification)
    }

    //打開社團 審核畫面
    groupApprovePage?.let { group ->
        navController.navigate(
            GroupApplyScreenDestination(
                group = group
            )
        )
        notificationViewModel.afterOpenApprovePage()
    }

    //打開指定社團
    openGroup?.let { group ->
        globalGroupViewModel.setCurrentGroup(group)
        notificationViewModel.afterOpenGroup()
    }

    //沒有權限 彈窗
    if (showNoPermissionTip) {
        NoPermissionDialog(
            onDismiss = {
                AppUserLogger.getInstance().log(Clicked.CannotUse, From.Notification)
                notificationViewModel.afterOpenApprovePage()
            },
            onClick = {
                AppUserLogger.getInstance().log(Clicked.CannotUse, From.Notification)
                notificationViewModel.afterOpenApprovePage()
            }
        )
    }

    //加入彈窗
    openGroupDialog?.let { targetGroup ->
        GroupItemDialogScreen(
            groupModel = targetGroup,
            onDismiss = {
                notificationViewModel.openedInviteGroup()
            },
            onConfirm = { group, joinStatus ->
                when (joinStatus) {
                    GroupJoinStatus.InReview -> {
                        notificationViewModel.openedInviteGroup()
                    }

                    GroupJoinStatus.Joined -> {
                        globalGroupViewModel.setCurrentGroup(group)
                        notificationViewModel.openedInviteGroup()
                        navController.popBackStack()
                    }

                    GroupJoinStatus.NotJoin -> {
                    }
                }
            }
        )
    }


    //解散社團 彈窗
    showDissolveDialog?.let { groupId ->
        DialogScreen(
            title = stringResource(id = R.string.dissolve_group_title),
            subTitle = stringResource(id = R.string.dissolve_group_description),
            onDismiss = {
                notificationViewModel.dismissDissolveDialog()
                notificationViewModel.onCheckDissolveGroup(
                    groupId,
                    globalGroupViewModel.currentGroup.value
                )
            }) {
            BlueButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = stringResource(id = R.string.confirm)
            ) {
                AppUserLogger.getInstance().log(Clicked.AlreadyDissolve)

                notificationViewModel.dismissDissolveDialog()
                notificationViewModel.onCheckDissolveGroup(
                    groupId,
                    globalGroupViewModel.currentGroup.value
                )
            }
        }
    }
}

@Composable
fun NoPermissionDialog(onDismiss: () -> Unit, onClick: () -> Unit) {
    DialogScreen(
        title = stringResource(id = R.string.has_no_permission_title),
        subTitle = stringResource(id = R.string.has_no_permission_description),
        onDismiss = onDismiss
    ) {
        BlueButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            text = stringResource(id = R.string.i_know),
            onClick = onClick
        )
    }
}

@Composable
fun NotificationCenterView(
    navController: DestinationsNavigator,
    modifier: Modifier = Modifier,
    notificationCenterDataList: List<NotificationCenterData>,
    onClick: (NotificationCenterData) -> Unit,
    onLoadMore: () -> Unit
) {
    val listState: LazyListState = rememberLazyListState()

    Scaffold(modifier = modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = stringResource(id = R.string.notification_center),
                backClick = {
                    navController.popBackStack()
                }
            )
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .background(LocalColor.current.env_80)
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(notificationCenterDataList) { notificationCenterData ->
                    NotificationItem(
                        notificationCenterData = notificationCenterData,
                        onClick = onClick
                    )

                    Divider(color = LocalColor.current.background, thickness = 1.dp)
                }
            }
        }

        listState.OnBottomReached {
            onLoadMore.invoke()
        }
    }
}

@Composable
private fun NotificationItem(
    modifier: Modifier = Modifier,
    notificationCenterData: NotificationCenterData,
    onClick: (NotificationCenterData) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                (if (notificationCenterData.isRead) {
                    Color.Transparent
                } else {
                    LocalColor.current.background
                })
            )
            .clickable {
                onClick.invoke(notificationCenterData)
            }
            .padding(top = 10.dp, bottom = 10.dp, start = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        //Icon
        AsyncImage(
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            model = notificationCenterData.image,
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            //Title
            Text(
                text = notificationCenterData.title,
                fontSize = 16.sp,
                color = LocalColor.current.text.default_100
            )

            Spacer(modifier = Modifier.width(5.dp))

            //Description
            Text(
                text = notificationCenterData.description,
                fontSize = 14.sp,
                color = LocalColor.current.text.default_80
            )

            Spacer(modifier = Modifier.width(5.dp))

            //Time
            Text(
                text = notificationCenterData.displayTime,
                fontSize = 12.sp,
                color = LocalColor.current.text.default_50
            )

        }
    }
}

@Preview
@Composable
fun NotificationCenterScreenPreview() {
    FanciTheme {
        NotificationCenterView(
            navController = EmptyDestinationsNavigator,
            notificationCenterDataList = MockData.mockNotificationCenter,
            onClick = {},
            onLoadMore = {}
        )
    }
}

@Preview
@Composable
fun NotificationCenterItemPreview() {
    FanciTheme {
        NotificationItem(
            notificationCenterData = MockData.mockNotificationCenter.first(),
            onClick = {}
        )
    }
}