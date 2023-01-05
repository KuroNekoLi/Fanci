package com.cmoney.fanci.ui.screens.group.setting

import androidx.activity.compose.BackHandler
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
import com.cmoney.fanci.LocalDependencyContainer
import com.cmoney.fanci.destinations.GroupOpennessScreenDestination
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.EmptyResultRecipient
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient

@Destination
@Composable
fun GroupSettingScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    initGroup: Group,
    resultRecipient: ResultRecipient<GroupOpennessScreenDestination, Group>
) {
    val globalViewModel = LocalDependencyContainer.current.globalViewModel
    var group = initGroup

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                val resultGroup = result.value
                group = resultGroup
            }
        }
    }

    BackHandler {
        globalViewModel.setCurrentGroup(group)
        navController.popBackStack()
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
                    globalViewModel.setCurrentGroup(group)
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
                group = group,
                navController = navController
            )

            Spacer(modifier = Modifier.height(28.dp))

            //成員管理
            GroupMemberManageScreen(
                group = group,
                navController = navController
            )

            Spacer(modifier = Modifier.height(28.dp))

            //秩序管理
            GroupRuleManageScreen(
                group = group,
                navController = navController
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupSettingScreenPreview() {
    FanciTheme {
        GroupSettingScreen(
            navController = EmptyDestinationsNavigator,
            initGroup = Group(),
            resultRecipient = EmptyResultRecipient()
        )
    }
}