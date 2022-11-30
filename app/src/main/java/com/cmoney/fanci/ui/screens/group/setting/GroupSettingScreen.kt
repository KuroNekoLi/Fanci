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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.MainStateHolder
import com.cmoney.fanci.ui.screens.group.setting.GroupSettingRoute.*
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group

@Composable
fun GroupSettingScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    group: Group,
    route: (MainStateHolder.Route) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                navController,
                title = "社團設定",
                leadingEnable = true,
                moreEnable = false
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
                onItemClick = {
                    groupSettingRouteProcess(
                        group = group,
                        mainRoute = route,
                        route = it
                    )
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            //成員管理
            GroupMemberManageScreen()

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
    object UsrManage : GroupSettingRoute()

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
        UsrManage -> TODO()
    }
}

@Preview(showBackground = true)
@Composable
fun GroupSettingScreenPreview() {
    FanciTheme {
        GroupSettingScreen(
            navController = rememberNavController(),
            group = Group(),
        ) {

        }
    }
}