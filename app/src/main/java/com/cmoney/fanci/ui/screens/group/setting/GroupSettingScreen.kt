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
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group

@Composable
fun GroupSettingScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    group: Group
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
            GroupManageScreen()

            Spacer(modifier = Modifier.height(28.dp))

            //成員管理
            GroupMemberManageScreen()

            Spacer(modifier = Modifier.height(28.dp))

            //秩序管理
            GroupRuleManageScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupSettingScreenPreview() {
    FanciTheme {
        GroupSettingScreen(
            navController = rememberNavController(),
            group = Group()
        )
    }
}