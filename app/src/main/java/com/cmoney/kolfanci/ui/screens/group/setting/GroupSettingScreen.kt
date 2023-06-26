package com.cmoney.kolfanci.ui.screens.group.setting

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.ReportInformation
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.share
import com.cmoney.kolfanci.model.Constant.isShowApproval
import com.cmoney.kolfanci.model.Constant.isShowGroupManage
import com.cmoney.kolfanci.model.Constant.isShowVipManager
import com.cmoney.kolfanci.ui.destinations.GroupApplyScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupOpennessScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupReportScreenDestination
import com.cmoney.kolfanci.ui.destinations.VipManagerScreenDestination
import com.cmoney.kolfanci.ui.main.LocalDependencyContainer
import com.cmoney.kolfanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
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
    initGroup: Group,
    viewModel: GroupSettingViewModel = koinViewModel(),
    memberViewModel: MemberViewModel = koinViewModel(),
    resultRecipient: ResultRecipient<GroupOpennessScreenDestination, Group>,
    applyResultRecipient: ResultRecipient<GroupApplyScreenDestination, Boolean>,
    reportResultRecipient: ResultRecipient<GroupReportScreenDestination, Boolean>,
    leaveResultBackNavigator: ResultBackNavigator<String>
) {
    val globalGroupViewModel = LocalDependencyContainer.current.globalGroupViewModel

    val uiState = viewModel.uiState

    val loading = memberViewModel.uiState.loading

    //邀請加入社團連結
    val shareText by memberViewModel.shareText.collectAsState()
    if (shareText.isNotEmpty()) {
        LocalContext.current.share(shareText)
        memberViewModel.resetShareText()
    }

    //檢舉審核 清單
    val reportList by viewModel.reportList.collectAsState()

    //公開度
    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val resultGroup = result.value
                viewModel.settingGroup(resultGroup)
                globalGroupViewModel.setCurrentGroup(resultGroup)
            }
        }
    }

    //是否刷新檢舉
    reportResultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val refreshReport = result.value
                if (refreshReport) {
                    viewModel.fetchReportList(groupId = initGroup.id.orEmpty())
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
                    viewModel.fetchUnApplyCount(groupId = initGroup.id.orEmpty())
                }
            }
        }
    }

    BackHandler {
//        globalViewModel.setCurrentGroup(group)
        navController.popBackStack()
    }

    val group = uiState.settingGroup ?: initGroup

    GroupSettingScreenView(
        modifier = modifier,
        navController = navController,
        group = group,
        unApplyCount = uiState.unApplyCount ?: 0,
        reportList = reportList,
        onBackClick = {
//            globalViewModel.setCurrentGroup(group)
            navController.popBackStack()
        },
        onInviteClick = {
            memberViewModel.onInviteClick(group)
        },
        onLeaveGroup = {
            val groupId = group.id
            if (groupId != null) {
                leaveResultBackNavigator.navigateBack(groupId)
            } else {
                leaveResultBackNavigator.navigateBack()
            }
        },
        loading = loading
    )

    //抓取加入申請 數量
    if (uiState.unApplyCount == null) {
        viewModel.fetchUnApplyCount(groupId = initGroup.id.orEmpty())
    }

    //抓取檢舉內容
    if (reportList.isEmpty()) {
        viewModel.fetchReportList(groupId = initGroup.id.orEmpty())
    }
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
    loading: Boolean
) {
    val TAG = "GroupSettingScreenView"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "設定",
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    onBackClick.invoke()
                }
            )
        },
        backgroundColor = LocalColor.current.env_80
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                top = innerPadding.calculateTopPadding() + 20.dp,
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = innerPadding.calculateBottomPadding() + 20.dp
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
                        onInviteClick = onInviteClick,
                        onLeaveGroup = onLeaveGroup
                    )
                }
                //社團管理
                if (isShowGroupManage()) {
                    item {
                        GroupManageScreen(
                            group = group,
                            navController = navController
                        )
                    }
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
            loading = false
        )
    }
}