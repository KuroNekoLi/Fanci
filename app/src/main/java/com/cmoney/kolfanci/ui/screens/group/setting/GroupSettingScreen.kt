package com.cmoney.kolfanci.ui.screens.group.setting

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.ReportInformation
import com.cmoney.kolfanci.model.Constant.isShowApproval
import com.cmoney.kolfanci.model.Constant.isShowGroupManage
import com.cmoney.kolfanci.ui.destinations.GroupApplyScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupOpennessScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupReportScreenDestination
import com.cmoney.kolfanci.ui.main.LocalDependencyContainer
import com.cmoney.kolfanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun GroupSettingScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    initGroup: Group,
    viewModel: GroupSettingViewModel = koinViewModel(),
    resultRecipient: ResultRecipient<GroupOpennessScreenDestination, Group>,
    applyResultRecipient: ResultRecipient<GroupApplyScreenDestination, Boolean>,
    reportResultRecipient: ResultRecipient<GroupReportScreenDestination, Boolean>
) {
    val globalViewModel = LocalDependencyContainer.current.globalViewModel

    val uiState = viewModel.uiState

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
                globalViewModel.setCurrentGroup(resultGroup)
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

    GroupSettingScreenView(
        modifier = modifier,
        navController = navController,
        group = uiState.settingGroup ?: initGroup,
        unApplyCount = uiState.unApplyCount ?: 0,
        reportList = reportList,
        onBackClick = {
//            globalViewModel.setCurrentGroup(group)
            navController.popBackStack()
        }
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
    reportList: List<ReportInformation>?
) {
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(LocalColor.current.env_80)
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            //社團管理
            if (isShowGroupManage()) {
                GroupManageScreen(
                    group = group,
                    navController = navController
                )

                Spacer(modifier = Modifier.height(28.dp))
            }

            //成員管理
            GroupMemberManageScreen(
                group = group,
                navController = navController,
                unApplyCount = unApplyCount
            )

            Spacer(modifier = Modifier.height(28.dp))

            //秩序管理
            if (isShowApproval()) {
                GroupRuleManageScreen(
                    group = group,
                    reportList = reportList,
                    navController = navController
                )
            }
        }
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
            reportList = emptyList()
        )
    }
}