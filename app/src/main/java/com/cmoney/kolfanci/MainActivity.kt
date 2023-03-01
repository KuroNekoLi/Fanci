package com.cmoney.kolfanci

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.cmoney.kolfanci.destinations.MainScreenDestination
import com.cmoney.kolfanci.ui.MainNavHost
import com.cmoney.kolfanci.ui.screens.BottomBarScreen
import com.cmoney.kolfanci.ui.screens.tutorial.TutorialScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.xlogin.base.BaseWebLoginActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalDependencyContainer provides this) {
                val state = globalViewModel.uiState
                val isOpenTutorial = globalViewModel.isOpenTutorial.observeAsState()

                isOpenTutorial.value?.let { isOpenTutorial ->
                    if (isOpenTutorial) {
                        FanciTheme(fanciColor = state.theme) {
                            val mainState = rememberMainState()
                            mainState.setStatusBarColor()

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







    override fun autoLoginFailCallback() {
        KLog.i(TAG, "autoLoginFailCallback")
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
    val state = globalViewModel.uiState

    FanciTheme(fanciColor = state.theme) {
        KLog.i("TAG", "FanciTheme create.")
        val mainState = rememberMainState()

        Scaffold(
            bottomBar = {
                BottomBarScreen(
                    mainNavController
                )
            }
        ) { innerPadding ->
            mainState.setStatusBarColor()
            MainNavHost(
                modifier = Modifier.padding(innerPadding),
                navController = mainNavController,
                route = {
                },
                globalViewModel = globalViewModel,
                navigator = navigator
            )
        }
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