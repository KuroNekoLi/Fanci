package com.cmoney.kolfanci.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.extension.findActivity
import com.cmoney.kolfanci.model.notification.Payload
import com.cmoney.kolfanci.ui.NavGraphs
import com.cmoney.kolfanci.ui.destinations.MainScreenDestination
import com.cmoney.kolfanci.ui.screens.follow.FollowScreen
import com.cmoney.kolfanci.model.viewmodel.GroupViewModel
import com.cmoney.kolfanci.ui.screens.tutorial.TutorialScreen
import com.cmoney.kolfanci.ui.theme.Black_242424
import com.cmoney.kolfanci.ui.theme.Blue_4F70E5
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.xlogin.base.BaseWebLoginActivity
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog
import org.koin.android.ext.android.inject

val LocalDependencyContainer = staticCompositionLocalOf<MainActivity> {
    error("No dependency container provided!")
}

class MainActivity : BaseWebLoginActivity() {
    private val TAG = MainActivity::class.java.simpleName

    val globalViewModel by inject<MainViewModel>()

    val globalGroupViewModel by inject<GroupViewModel>()

    companion object {
        const val FOREGROUND_NOTIFICATION_BUNDLE = "foreground_notification_bundle"

        fun start(context: Context, payload: Payload) {
            KLog.i("MainActivity", "start by Payload")
            val starter = Intent(context, MainActivity::class.java)
                .putExtra(FOREGROUND_NOTIFICATION_BUNDLE, payload)
            starter.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalDependencyContainer provides this) {
                val isOpenTutorial by globalViewModel.isOpenTutorial.collectAsState()

                val theme by globalGroupViewModel.theme.collectAsState()

                val isLoginLoading by globalViewModel.loginLoading.collectAsState()

                isOpenTutorial?.let { isOpenTutorial ->
                    if (isOpenTutorial) {
                        FanciTheme(fanciColor = theme) {
                            setStatusBarColor()

                            Scaffold(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(LocalColor.current.primary),
                            ) { padding ->
                                Column(
                                    modifier = Modifier
                                        .padding(padding)
                                ) {
                                    if (isLoginLoading) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(LocalColor.current.background),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(size = 64.dp),
                                                color = Blue_4F70E5
                                            )
                                        }
                                    } else {
                                        DestinationsNavHost(
                                            navGraph = NavGraphs.root,
                                            startRoute = MainScreenDestination
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        rememberSystemUiController().setStatusBarColor(
                            color = Black_242424,
                            darkIcons = false
                        )
                        MaterialTheme {
                            TutorialScreen(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                globalViewModel.tutorialOnOpen()
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun setStatusBarColor() {
        val statusBarColor = MaterialTheme.colors.primary
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.setStatusBarColor(
                color = statusBarColor,
                darkIcons = false
            )
        }
    }


    //==================== auto login callback ====================
    override fun autoLoginFailCallback() {
        KLog.e(TAG, "autoLoginFailCallback")
        globalViewModel.loginFail("autoLoginFailCallback")
        globalGroupViewModel.fetchAllGroupList()
    }

    override fun loginCancel() {
        KLog.i(TAG, "loginCancel")
        globalViewModel.loginFail("loginCancel")
    }

    override fun loginFailCallback(errorMessage: String) {
        KLog.e(TAG, "loginFailCallback:$errorMessage")
        globalViewModel.loginFail(errorMessage)
        globalGroupViewModel.fetchAllGroupList()
    }

    override fun loginSuccessCallback() {
        KLog.i(TAG, "loginSuccessCallback")
        globalViewModel.loginSuccess()
        globalGroupViewModel.fetchMyGroup()
    }
}

@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
    navigator: DestinationsNavigator
) {
    val TAG = "MainScreen"
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
            EmptyDestinationsNavigator
        )
    }
}