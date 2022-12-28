package com.cmoney.fanci.ui.screens.group.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanci.MainStateHolder
import com.cmoney.fanci.destinations.ChannelSettingScreenDestination
import com.cmoney.fanci.destinations.GroupSettingSettingScreenDestination
import com.cmoney.fanci.destinations.RoleManageScreenDestination
import com.cmoney.fanci.ui.screens.group.setting.GroupSettingRoute.*
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

@Destination
@Composable
fun GroupSettingScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group
) {
    val onGroupSetting: (GroupSettingRoute) -> Unit = {
        when (it) {
            AllMember -> TODO()
            BanList -> TODO()
            BlockList -> TODO()
            ChannelManage -> navController.navigate(
                ChannelSettingScreenDestination(
                    group = group
                )
            )
            GroupPublic -> TODO()
            GroupSetting -> navController.navigate(
                GroupSettingSettingScreenDestination(
                    group = group
                )
            )
            JoinApprove -> TODO()
            ReportApprove -> TODO()
            UserManage -> navController.navigate(
                RoleManageScreenDestination(group = group)
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "社團設定",
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    navController.popBackStack()
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
            GroupManageScreen(
                onGroupSetting = onGroupSetting
            )

            Spacer(modifier = Modifier.height(28.dp))

            //成員管理
            GroupMemberManageScreen(
                group = group,
                navController = navController,
                onGroupSetting = onGroupSetting
            )

            Spacer(modifier = Modifier.height(28.dp))

            //秩序管理
            GroupRuleManageScreen()
        }
    }
}

/**
 * 社團 設定各種點擊
 */
sealed class GroupSettingRoute {
    //社團設定
    object GroupSetting : GroupSettingRoute()

    //頻道管理
    object ChannelManage : GroupSettingRoute()

    //社團公開度
    object GroupPublic : GroupSettingRoute()

    //角色管理
    object UserManage : GroupSettingRoute()

    //所有成員
    object AllMember : GroupSettingRoute()

    //加入申請
    object JoinApprove : GroupSettingRoute()

    //檢舉審核
    object ReportApprove : GroupSettingRoute()

    //禁言列表
    object BanList : GroupSettingRoute()

    //黑名單列表
    object BlockList : GroupSettingRoute()
}

private fun groupSettingRouteProcess(
    group: Group,
    mainRoute: (MainStateHolder.Route) -> Unit,
    route: GroupSettingRoute,
) {
    when (route) {
        GroupSetting -> mainRoute.invoke(
            MainStateHolder.GroupRoute.GroupSettingSetting(
                group = group
            )
        )
        ChannelManage -> TODO()
        GroupPublic -> TODO()
        AllMember -> TODO()
        BanList -> TODO()
        BlockList -> TODO()
        JoinApprove -> TODO()
        ReportApprove -> TODO()
        UserManage -> TODO()
    }
}

@Preview(showBackground = true)
@Composable
fun GroupSettingScreenPreview() {
    FanciTheme {
        GroupSettingScreen(
            navController = EmptyDestinationsNavigator,
            group = Group(),
        )
    }
}