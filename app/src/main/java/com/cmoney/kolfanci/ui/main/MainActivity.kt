package com.cmoney.kolfanci.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.notification.Payload
import com.cmoney.kolfanci.model.notification.TargetType
import com.cmoney.kolfanci.model.viewmodel.GroupViewModel
import com.cmoney.kolfanci.model.viewmodel.NotificationViewModel
import com.cmoney.kolfanci.ui.NavGraphs
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.destinations.MainScreenDestination
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.LoginDialogScreen
import com.cmoney.kolfanci.ui.screens.tutorial.TutorialScreen
import com.cmoney.kolfanci.ui.theme.DefaultThemeColor
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.xlogin.XLoginHelper
import com.cmoney.xlogin.base.BaseWebLoginActivity
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.socks.library.KLog
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : BaseWebLoginActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private val globalViewModel by viewModel<MainViewModel>()
    private val globalGroupViewModel by viewModel<GroupViewModel>()
    private val notificationViewModel by viewModel<NotificationViewModel>()

    private var payLoad: Payload? = null

    companion object {
        const val FOREGROUND_NOTIFICATION_BUNDLE = "foreground_notification_bundle"
        const val REQUEST_CODE_ALLOW_NOTIFICATION_PERMISSION: Int = 1

        fun start(context: Context, payload: Payload) {
            KLog.i("MainActivity", "start by Payload")
            val starter = Intent(context, MainActivity::class.java)
                .putExtra(FOREGROUND_NOTIFICATION_BUNDLE, payload)
            starter.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(starter)
        }

        fun createIntent(context: Context, payload: Payload): Intent {
            return Intent(context, MainActivity::class.java)
                .putExtra(FOREGROUND_NOTIFICATION_BUNDLE, payload)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPayload(intent)

        setContent {
            val loginLoading by globalViewModel.loginLoading.collectAsState()

            val isOpenTutorial by globalViewModel.isOpenTutorial.collectAsState()

            val targetType by notificationViewModel.targetType.collectAsState()

            //我的社團清單
            val myGroupList by globalGroupViewModel.myGroupList.collectAsState()

            //登入彈窗
            val showLoginDialog by globalViewModel.showLoginDialog.collectAsState()

            //解散社團 彈窗
            val showDissolveDialog by notificationViewModel.showDissolveGroupDialog.collectAsState()

            //是否刷新我的社團
            val isRefreshMyGroup by notificationViewModel.refreshGroup.collectAsState()

            LaunchedEffect(key1 = targetType, key2 = loginLoading, key3 = myGroupList) {
                targetType?.let { targetType ->
                    if (!loginLoading) {
                        if (XLoginHelper.isLogin) {
                            when (targetType) {
                                TargetType.MainPage -> {}

                                is TargetType.InviteGroup -> {
                                    val groupId = targetType.groupId
                                    notificationViewModel.fetchInviteGroup(groupId)
                                }

                                is TargetType.ReceiveMessage -> {
                                    if (myGroupList.isNotEmpty()) {
                                        notificationViewModel.receiveNewMessage(
                                            receiveNewMessage = targetType,
                                            myGroupList = myGroupList
                                        )
                                    }
                                }

                                is TargetType.ReceivePostMessage -> {
                                    if (myGroupList.isNotEmpty()) {
                                        notificationViewModel.receiveNewPost(
                                            receivePostMessage = targetType,
                                            myGroupList = myGroupList
                                        )
                                    }
                                }

                                is TargetType.DissolveGroup -> {
                                    if (myGroupList.isNotEmpty()) {
                                        notificationViewModel.dissolveGroup(
                                            dissolveGroup = targetType
                                        )
                                    }
                                }

                                is TargetType.GroupApprove -> {
                                    val groupId = targetType.groupId
                                    notificationViewModel.groupApprove(groupId)
                                }

                                is TargetType.OpenGroup -> {
                                    if (myGroupList.isNotEmpty()) {
                                        val groupId = targetType.groupId
                                        notificationViewModel.openGroup(
                                            groupId = groupId,
                                            myGroupList = myGroupList
                                        )
                                    }
                                }
                            }

                            if (myGroupList.isNotEmpty()) {
                                notificationViewModel.clearPushDataState()
                            }
                        }
                        //Not Login
                        else {
                            when (targetType) {
                                is TargetType.InviteGroup -> {
                                    val groupId = targetType.groupId
                                    notificationViewModel.fetchInviteGroup(groupId)
                                }

                                else -> {
                                    globalViewModel.showLoginDialog()
                                }
                            }
                        }
                    }
                }
            }

            isOpenTutorial?.let { isCurrentOpenTutorial ->
                FanciTheme(fanciColor = DefaultThemeColor) {
                    if (isCurrentOpenTutorial) {
                        MainScreen()
                    } else {
                        //是否為 邀請啟動
                        if (isInvitePayload(payLoad)) {
                            MainScreen()
                        } else {
                            TutorialScreen(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                globalViewModel.tutorialOnOpen()
                            }
                        }
                    }

                    //刷新我的社團
                    if (isRefreshMyGroup) {
                        globalGroupViewModel.fetchMyGroup(false)
                        notificationViewModel.afterRefreshGroup()
                    }

                    //登入彈窗
                    if (showLoginDialog) {
                        LoginDialogScreen(
                            onDismiss = {
                                globalViewModel.dismissLoginDialog()
                            },
                            onLogin = {
                                globalViewModel.dismissLoginDialog()
                                startLogin()
                            }
                        )
                    }

                    //解散社團 彈窗
                    showDissolveDialog?.let { groupId ->
                        DialogScreen(
                            title = stringResource(id = R.string.dissolve_group_title),
                            subTitle = stringResource(id = R.string.dissolve_group_description),
                            onDismiss = {
                                notificationViewModel.dismissDissolveDialog()
                                notificationViewModel.onCheckDissolveGroup(
                                    groupId,
                                    globalGroupViewModel.currentGroup.value
                                )
                            }) {
                            BlueButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                text = stringResource(id = R.string.confirm)
                            ) {
                                AppUserLogger.getInstance().log(Clicked.AlreadyDissolve)

                                notificationViewModel.dismissDissolveDialog()
                                notificationViewModel.onCheckDissolveGroup(
                                    groupId,
                                    globalGroupViewModel.currentGroup.value
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkPayload(intent)
    }

    /**
     * 檢查 推播 or dynamic link
     */
    private fun checkPayload(intent: Intent?) {
        KLog.i(TAG, "checkPayload")
        intent?.let {
            payLoad =
                intent.getParcelableExtra<Payload>(FOREGROUND_NOTIFICATION_BUNDLE)
            KLog.d(TAG, "payLoad = $payLoad")

            payLoad?.let {
                notificationViewModel.setNotificationBundle(it)
            }
            this.intent = null
        }
    }

    /**
     * 是否為邀請連結啟動
     */
    private fun isInvitePayload(payload: Payload?): Boolean {
        return payload?.targetType == Payload.TYPE_1
    }

    fun checkPayload(payload: Payload) {
        KLog.d(TAG, "payLoad = $payload")
        notificationViewModel.setNotificationBundle(payload)
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
    }

    override fun loginCancel() {
        KLog.i(TAG, "loginCancel")
        globalViewModel.loginFail("loginCancel")
    }

    override fun loginFailCallback(errorMessage: String) {
        KLog.e(TAG, "loginFailCallback:$errorMessage")
        globalViewModel.loginFail(errorMessage)
    }

    override fun loginSuccessCallback() {
        KLog.i(TAG, "loginSuccessCallback")
        globalViewModel.loginSuccess()
        globalGroupViewModel.fetchMyGroup()
    }
}