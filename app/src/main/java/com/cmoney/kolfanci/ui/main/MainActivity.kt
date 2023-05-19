package com.cmoney.kolfanci.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.cmoney.kolfanci.model.notification.Payload
import com.cmoney.kolfanci.ui.NavGraphs
import com.cmoney.kolfanci.ui.destinations.MainScreenDestination
import com.cmoney.kolfanci.ui.screens.follow.FollowScreen
import com.cmoney.kolfanci.ui.screens.tutorial.TutorialScreen
import com.cmoney.kolfanci.ui.theme.Black_242424
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

                val theme by globalViewModel.theme.collectAsState()

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
                                    DestinationsNavHost(
                                        navGraph = NavGraphs.root,
                                        startRoute = MainScreenDestination
                                    )
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


    override fun autoLoginFailCallback() {
        KLog.i(TAG, "autoLoginFailCallback")
        globalViewModel.startFetchFollowData()
    }

    override fun loginCancel() {
        KLog.i(TAG, "loginCancel")
    }

    override fun loginFailCallback(errorMessage: String) {
        KLog.i(TAG, "loginFailCallback:$errorMessage")
    }

    override fun loginSuccessCallback() {
        KLog.i(TAG, "loginSuccessCallback")
        globalViewModel.registerUser()
        globalViewModel.loginSuccess()
        globalViewModel.startFetchFollowData()
    }
}

@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
    navigator: DestinationsNavigator,
) {
    val globalViewModel = LocalDependencyContainer.current.globalViewModel
    val mainNavController = rememberNavController()
    val theme by globalViewModel.theme.collectAsState()

    FanciTheme(fanciColor = theme) {
//        val mainState = rememberMainState()
//        mainState.setStatusBarColor()

        FollowScreen(
            modifier = Modifier,
            globalViewModel = globalViewModel,
            navigator = navigator
        )

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
    }
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