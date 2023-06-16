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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.cmoney.kolfanci.model.notification.Payload
import com.cmoney.kolfanci.model.viewmodel.GroupViewModel
import com.cmoney.kolfanci.ui.NavGraphs
import com.cmoney.kolfanci.ui.destinations.MainScreenDestination
import com.cmoney.kolfanci.ui.screens.tutorial.TutorialScreen
import com.cmoney.kolfanci.ui.theme.DefaultThemeColor
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.xlogin.base.BaseWebLoginActivity
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.DestinationsNavHost
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
                isOpenTutorial?.let { isCurrentOpenTutorial ->
                    FanciTheme(fanciColor = DefaultThemeColor) {
                        if (isCurrentOpenTutorial) {
                            MainScreen()
                        } else {
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
    fun MainScreen() {
        StatusBarColorEffect()
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalColor.current.primary),
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
            ) {
                val theme by globalGroupViewModel.theme.collectAsState()
                FanciTheme(fanciColor = theme) {
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        startRoute = MainScreenDestination
                    )
                }
            }
        }
    }

    @Composable
    fun StatusBarColorEffect() {
        val statusBarColor = MaterialTheme.colors.primary
        val systemUiController = rememberSystemUiController()
        LaunchedEffect(key1 = statusBarColor) {
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