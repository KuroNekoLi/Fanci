package com.cmoney.kolfanci.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.findActivity
import com.cmoney.kolfanci.extension.showToast
import com.cmoney.kolfanci.model.notification.Payload
import com.cmoney.kolfanci.ui.destinations.GroupSettingScreenDestination
import com.cmoney.kolfanci.ui.screens.follow.FollowScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.EmptyResultRecipient
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog

/**
 * Main screen
 *
 * @param navigator
 * @param leaveResultRecipient 如果有收到則退出此社團
 */
@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
    navigator: DestinationsNavigator,
    leaveResultRecipient: ResultRecipient<GroupSettingScreenDestination, String>
) {
    val TAG = "MainScreen"
    val context = LocalContext.current
    val globalViewModel = LocalDependencyContainer.current.globalViewModel
    val globalGroupViewModel = LocalDependencyContainer.current.globalGroupViewModel
    val activity = LocalContext.current.findActivity()
    val isLoading by globalGroupViewModel.loading.collectAsState()

    //我的社團清單
    val myGroupList by globalGroupViewModel.myGroupList.collectAsState()
    //目前選中社團
    val currentGroup by globalGroupViewModel.currentGroup.collectAsState()
    //server 入門社團清單
    val serverGroupList by globalGroupViewModel.groupList.collectAsState()
    //邀請加入社團
    val inviteGroup by globalViewModel.inviteGroup.collectAsState()

    FollowScreen(
        modifier = Modifier,
        group = currentGroup,
        serverGroupList = serverGroupList,
        inviteGroup = inviteGroup,
        navigator = navigator,
        myGroupList = myGroupList,
        onGroupItemClick = {
            globalGroupViewModel.setCurrentGroup(it)
        },
        onLoadMoreServerGroup = {
            globalGroupViewModel.onLoadMore()
        },
        onRefreshMyGroupList = {
            globalGroupViewModel.fetchMyGroup()
        },
        isLoading = isLoading,
        onDismissInvite = {
            globalViewModel.openedInviteGroup()
            activity.intent.replaceExtras(Bundle())
        }
    )

    /**
     * 檢查 推播 or dynamic link
     */
    fun checkPayload(intent: Intent) {
        val payLoad =
            intent.getParcelableExtra<Payload>(MainActivity.FOREGROUND_NOTIFICATION_BUNDLE)
        KLog.d(TAG, "payLoad = $payLoad")
        if (payLoad != null) {
            globalViewModel.setNotificationBundle(payLoad)
        }
    }

    LaunchedEffect(Unit) {
        KLog.i(TAG, "checkPayload")
        checkPayload(activity.intent)
    }

    leaveResultRecipient.onNavResult { navResult ->
        when (navResult) {
            NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                context.showToast(context.getString(R.string.leaving_group))
                val groupId = navResult.value
                globalGroupViewModel.leaveGroup(id = groupId)
            }
        }
    }

    //TODO 暫時移除 Tab, 之後有新功能才會加回來.
//        Scaffold(
//            bottomBar = {
//                BottomBarScreen(
//                    mainNavController
//                )
//            }
//        ) { innerPadding ->
//            mainState.setStatusBarColor()
//
//            MainNavHost(
//                modifier = Modifier.padding(innerPadding),
//                navController = mainNavController,
//                route = {
//                },
//                globalViewModel = globalViewModel,
//                navigator = navigator
//            )
//        }
//    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FanciTheme {
        MainScreen(
            navigator = EmptyDestinationsNavigator,
            leaveResultRecipient = EmptyResultRecipient()
        )
    }
}