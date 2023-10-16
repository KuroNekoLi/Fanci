package com.cmoney.kolfanci.ui.screens.group.setting

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.PushNotificationSetting
import com.cmoney.fanciapi.fanci.model.ReportInformation
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.findActivity
import com.cmoney.kolfanci.extension.globalGroupViewModel
import com.cmoney.kolfanci.extension.isNotificationsEnabled
import com.cmoney.kolfanci.extension.lifecycleEventListener
import com.cmoney.kolfanci.extension.share
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.Constant.isShowApproval
import com.cmoney.kolfanci.model.Constant.isShowGroupManage
import com.cmoney.kolfanci.model.Constant.isShowVipManager
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.GroupApplyScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupReportScreenDestination
import com.cmoney.kolfanci.ui.destinations.NotificationSettingScreenDestination
import com.cmoney.kolfanci.ui.destinations.VipManagerScreenDestination
import com.cmoney.kolfanci.ui.main.MainActivity
import com.cmoney.kolfanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.AlertDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.MemberViewModel
import com.cmoney.kolfanci.ui.screens.shared.setting.SettingItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

/**
 * 社團-設定頁面
 */
@Destination
@Composable
fun GroupSettingScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    viewModel: GroupSettingViewModel = koinViewModel(),
    memberViewModel: MemberViewModel = koinViewModel(),
    applyResultRecipient: ResultRecipient<GroupApplyScreenDestination, Boolean>,
    reportResultRecipient: ResultRecipient<GroupReportScreenDestination, Boolean>,
    leaveGroupResultBackNavigator: ResultBackNavigator<String>,
    setNotificationResult: ResultRecipient<NotificationSettingScreenDestination, PushNotificationSetting>
) {
    val globalGroupViewModel = globalGroupViewModel()
    val currentGroup by globalGroupViewModel.currentGroup.collectAsState()
    val nowGroup = currentGroup ?: return
    val uiState = viewModel.uiState
    val loading = memberViewModel.uiState.loading
    val context = LocalContext.current

    //邀請加入社團連結
    val shareText by memberViewModel.shareText.collectAsState()
    if (shareText.isNotEmpty()) {
        LocalContext.current.share(shareText)
        memberViewModel.resetShareText()
    }

    //檢舉審核 清單
    val reportList by viewModel.reportList.collectAsState()

    //是否刷新檢舉
    reportResultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val refreshReport = result.value
                if (refreshReport) {
                    viewModel.fetchReportList(groupId = nowGroup.id.orEmpty())
                }
            }
        }
    }

    applyResultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val isNeedRefresh = result.value
                if (isNeedRefresh) {
                    viewModel.fetchUnApplyCount(groupId = nowGroup.id.orEmpty())
                }
            }
        }
    }

    //推播 設定 callback
    setNotificationResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                viewModel.setCurrentNotificationSetting(result.value)
            }
        }
    }

    fun backClick() {
        navController.popBackStack()
    }

    BackHandler {
        backClick()
    }

    GroupSettingScreenView(
        modifier = modifier,
        navController = navController,
        group = uiState.settingGroup ?: nowGroup,
        unApplyCount = uiState.unApplyCount ?: 0,
        reportList = reportList,
        onBackClick = {
            backClick()
        },
        onInviteClick = {
            memberViewModel.onInviteClick(uiState.settingGroup ?: nowGroup)
        },
        onLeaveGroup = {
            val group = uiState.settingGroup ?: nowGroup
            val groupId = group.id
            if (groupId != null) {
                leaveGroupResultBackNavigator.navigateBack(groupId)
            } else {
                leaveGroupResultBackNavigator.navigateBack()
            }
        },
        onDisbandGroup = {
            viewModel.onFinalConfirmDelete(group = uiState.settingGroup ?: nowGroup)
        },
        loading = loading,
        pushNotificationSetting = uiState.pushNotificationSetting
    )

    //最終解散社團, 動作
    if (uiState.popToMain) {
        val intent = Intent(LocalContext.current, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        LocalContext.current.findActivity().finish()
        LocalContext.current.startActivity(intent)
    }

    LaunchedEffect(key1 = uiState.unApplyCount) {
        //抓取加入申請 數量
        if (uiState.unApplyCount == null) {
            viewModel.fetchUnApplyCount(groupId = nowGroup.id.orEmpty())
        }
    }
    LaunchedEffect(key1 = reportList) {
        //抓取檢舉內容
        if (reportList.isEmpty()) {
            viewModel.fetchReportList(groupId = nowGroup.id.orEmpty())
        }
    }

    LaunchedEffect(key1 = currentGroup) {
        viewModel.settingGroup(group = nowGroup)
    }

    val activity = LocalContext.current as ComponentActivity
    activity.lifecycleEventListener { event ->
        when (event.targetState) {
            Lifecycle.State.RESUMED -> {
                if (context.isNotificationsEnabled()) {
                    //抓取推播通知設定
                    viewModel.fetchNotificationSetting(groupId = nowGroup.id.orEmpty())
                }
                else {
                    viewModel.clearNotificationSetting()
                }
            }
            else ->{
            }
        }
    }

//    LaunchedEffect(key1 = uiState.pushNotificationSetting) {
//        //抓取之前推播通知設定
//        if (uiState.pushNotificationSetting == null) {
//            viewModel.fetchNotificationSetting()
//        }
//    }
}

@Composable
fun GroupSettingScreenView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    onBackClick: () -> Unit,
    unApplyCount: Long,
    reportList: List<ReportInformation>?,
    onInviteClick: () -> Unit,
    onLeaveGroup: () -> Unit,
    onDisbandGroup: () -> Unit,
    loading: Boolean,
    pushNotificationSetting: PushNotificationSetting? = null
) {
    val TAG = "GroupSettingScreenView"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "設定",
                backClick = {
                    onBackClick.invoke()
                }
            )
        },
        backgroundColor = LocalColor.current.env_80
    ) { innerPadding ->
        var showLeaveGroupDialog by remember {
            mutableStateOf(false)
        }
        var showDisbandGroupDialog by remember {
            mutableStateOf(false)
        }
        var showFinalDisbandGroupDialog by remember {
            mutableStateOf(false)
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                top = innerPadding.calculateTopPadding() + 20.dp,
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = innerPadding.calculateBottomPadding() + 50.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (loading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(size = 32.dp),
                            color = LocalColor.current.primary
                        )
                    }
                }
            } else {
                // 關於社團
                item {
                    GroupAboutScreen(
                        modifier = Modifier.fillMaxWidth(),
                        onInviteClick = onInviteClick
                    )
                }

                //社團管理
                item {
                    GroupManageScreen(
                        group = group,
                        navController = navController,
                        pushNotificationSetting = pushNotificationSetting
                    )
                }

                //成員管理
                item {
                    GroupMemberManageScreen(
                        group = group,
                        navController = navController,
                        unApplyCount = unApplyCount
                    )
                }
                //VIP 方案管理
                if (isShowVipManager()) {
                    item {
                        VipPlanManager {
                            KLog.i(TAG, "VipPlanManager click.")
                            AppUserLogger.getInstance().log(Clicked.GroupSettingsVIPPlanManagement)
                            navController.navigate(
                                VipManagerScreenDestination(
                                    group = group
                                )
                            )
                        }
                    }
                }
                //秩序管理
                if (isShowApproval()) {
                    item {
                        GroupRuleManageScreen(
                            group = group,
                            reportList = reportList,
                            navController = navController
                        )
                    }
                }
                // 是否為社團建立者，true 解散社團，false 退出社團
                val isCreator = group.creatorId == Constant.MyInfo?.id
                item {
                    Spacer(modifier = Modifier.height(98.dp))
                    if (!isCreator) {
                        Box(
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                                .background(LocalColor.current.background)
                                .clickable {
                                    AppUserLogger
                                        .getInstance()
                                        .log(Clicked.GroupSettingsLeaveGroup)
                                    showLeaveGroupDialog = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.leave_group),
                                fontSize = 17.sp,
                                color = LocalColor.current.specialColor.red
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth()
                                .background(LocalColor.current.background)
                                .clickable {
                                    AppUserLogger
                                        .getInstance()
                                        .log(Clicked.DissolveGroup)
                                    showDisbandGroupDialog = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.disband_group),
                                fontSize = 17.sp,
                                color = LocalColor.current.specialColor.red
                            )
                        }
                    }
                }
            }
        }

        if (showLeaveGroupDialog) {
            AlertDialogScreen(
                title = stringResource(id = R.string.leave_group),
                onDismiss = {
                    showLeaveGroupDialog = false
                }
            ) {
                Text(
                    text = stringResource(id = R.string.leave_group_remind),
                    color = LocalColor.current.text.default_100,
                    fontSize = 17.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = stringResource(id = R.string.confirm),
                    borderColor = LocalColor.current.component.other,
                    textColor = LocalColor.current.specialColor.hintRed,
                    shape = RoundedCornerShape(4.dp),
                    onClick = {
                        AppUserLogger.getInstance()
                            .log(Clicked.LeaveGroupConfirmLeave)
                        showLeaveGroupDialog = false
                        onLeaveGroup()
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = stringResource(id = R.string.cancel),
                    borderColor = LocalColor.current.component.other,
                    textColor = LocalColor.current.text.default_100,
                    shape = RoundedCornerShape(4.dp),
                    onClick = {
                        AppUserLogger.getInstance()
                            .log(Clicked.LeaveGroupCancelLeave)
                        showLeaveGroupDialog = false
                    }
                )
            }
        }

        //第一次確認解散
        if (showDisbandGroupDialog) {
            AlertDialogScreen(
                onDismiss = {
                    showDisbandGroupDialog = false
                },
                title = "你確定要把社團解散嗎？",
            ) {
                Column {
                    Text(
                        text = "社團解散，所有內容、成員將會消失 平台「不會」有備份、復原功能。",
                        fontSize = 17.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    BorderButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        text = "返回",
                        borderColor = LocalColor.current.component.other,
                        textColor = Color.White
                    ) {
                        AppUserLogger.getInstance()
                            .log(Clicked.DissolveGroupReturn)
                        showDisbandGroupDialog = false
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    BorderButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        text = "確定解散",
                        borderColor = LocalColor.current.specialColor.red,
                        textColor = LocalColor.current.specialColor.red
                    ) {
                        AppUserLogger.getInstance()
                            .log(Clicked.DissolveGroupConfirmDissolution)
                        showDisbandGroupDialog = false
                        showFinalDisbandGroupDialog = true
                    }
                }
            }
        }

        //最終確認刪除
        if (showFinalDisbandGroupDialog) {
            AlertDialogScreen(
                onDismiss = {
                    showFinalDisbandGroupDialog = false
                },
                title = "社團解散，最後確認通知！",
            ) {
                Column {
                    Text(
                        text = "社團解散，所有內容、成員將會消失 平台「不會」有備份、復原功能。",
                        fontSize = 17.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    BorderButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        text = "返回",
                        borderColor = LocalColor.current.component.other,
                        textColor = Color.White
                    ) {
                        AppUserLogger.getInstance()
                            .log(Clicked.ConfirmDissolutionReturn)
                        showFinalDisbandGroupDialog = false
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    BorderButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        text = "確定解散",
                        borderColor = LocalColor.current.specialColor.red,
                        textColor = LocalColor.current.specialColor.red
                    ) {
                        AppUserLogger.getInstance()
                            .log(Clicked.ConfirmDissolutionConfirmDissolution)
                        onDisbandGroup()
                    }
                }
            }
        }
    }
}

/**
 * Vip 方案管理
 */
@Composable
private fun VipPlanManager(
    onVipManagerClick: () -> Unit
) {
    Column {
        Text(
            modifier = Modifier.padding(start = 25.dp, bottom = 9.dp),
            text = stringResource(id = R.string.vip_manager),
            fontSize = 14.sp,
            color = LocalColor.current.text.default_100
        )

        SettingItemScreen(
            iconRes = R.drawable.vip_crown,
            text = stringResource(id = R.string.vip_manager),
            onItemClick = onVipManagerClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GroupSettingScreenPreview() {
    FanciTheme {
        GroupSettingScreenView(
            navController = EmptyDestinationsNavigator,
            group = Group(),
            onBackClick = {},
            unApplyCount = 20,
            reportList = emptyList(),
            onInviteClick = {},
            onLeaveGroup = {},
            onDisbandGroup = {},
            loading = false,
            pushNotificationSetting = MockData.mockNotificationSettingItem
        )
    }
}