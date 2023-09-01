package com.cmoney.kolfanci.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.core.app.ActivityCompat
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.notification.Payload
import com.cmoney.kolfanci.model.notification.TargetType
import com.cmoney.kolfanci.model.viewmodel.GroupViewModel
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

private const val REQUEST_REQUESTNOTIFICATIONPERMISSION: Int = 0

class MainActivity : BaseWebLoginActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private val globalViewModel by viewModel<MainViewModel>()
    private val globalGroupViewModel by viewModel<GroupViewModel>()

    companion object {
        const val FOREGROUND_NOTIFICATION_BUNDLE = "foreground_notification_bundle"

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

            val targetType by globalViewModel.targetType.collectAsState()

            //我的社團清單
            val myGroupList by globalGroupViewModel.myGroupList.collectAsState()

            //登入彈窗
            val showLoginDialog by globalViewModel.showLoginDialog.collectAsState()

            //解散社團 彈窗
            val showDissolveDialog by globalGroupViewModel.showDissolveGroupDialog.collectAsState()

            LaunchedEffect(key1 = targetType, key2 = loginLoading, key3 = myGroupList) {
                targetType?.let { targetType ->
                    if (!loginLoading) {
                        if (XLoginHelper.isLogin) {
                            when (targetType) {
                                is TargetType.InviteGroup -> {
                                    val groupId = targetType.groupId
                                    globalViewModel.fetchInviteGroup(groupId)
                                }

                                is TargetType.ReceiveMessage -> {
                                    if (myGroupList.isNotEmpty()) {
                                        globalGroupViewModel.receiveNewMessage(
                                            targetType
                                        )
                                    }
                                }

                                is TargetType.ReceivePostMessage -> {
                                    if (myGroupList.isNotEmpty()) {
                                        globalGroupViewModel.receiveNewPost(targetType)
                                    }
                                }

                                is TargetType.DissolveGroup -> {
                                    if (myGroupList.isNotEmpty()) {
                                        globalGroupViewModel.dissolveGroup(targetType)
                                    }
                                }

                                else -> {}
                            }

                            if (myGroupList.isNotEmpty()) {
                                globalViewModel.clearPushDataState()
                            }
                        }
                        //Not Login
                        else {
                            globalViewModel.showLoginDialog()
                        }
                    }
                }
            }

            isOpenTutorial?.let { isCurrentOpenTutorial ->
                FanciTheme(fanciColor = DefaultThemeColor) {
                    if (isCurrentOpenTutorial) {
                        MainScreen()
                    } else {
                        TutorialScreen(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                requestNotificationPermissionWithPermissionCheck()
                            } else {
                                globalViewModel.tutorialOnOpen()
                            }
                        }
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
                    showDissolveDialog?.let { group ->
                        DialogScreen(
                            title = stringResource(id = R.string.dissolve_group_title).format(
                                group.name.orEmpty()
                            ),
                            subTitle = stringResource(id = R.string.dissolve_group_description),
                            onDismiss = {
                                globalGroupViewModel.dismissDissolveDialog()
                                globalGroupViewModel.onCheckDissolveGroup(group)
                            }) {
                            BlueButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                text = stringResource(id = R.string.confirm)
                            ) {
                                globalGroupViewModel.dismissDissolveDialog()
                                globalGroupViewModel.onCheckDissolveGroup(group)
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
            val payLoad =
                intent.getParcelableExtra<Payload>(FOREGROUND_NOTIFICATION_BUNDLE)
            KLog.d(TAG, "payLoad = $payLoad")
            if (payLoad != null) {
                globalViewModel.setNotificationBundle(payLoad)
            }
            this.intent = null
        }
    }

    /**
     * 檢查是否有開啟通知權限
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermissionWithPermissionCheck() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_REQUESTNOTIFICATIONPERMISSION
            )
        } else {
            requestNotificationPermission()
        }
    }

    private fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_REQUESTNOTIFICATIONPERMISSION -> {
                //不管有沒有同意, 都繼續往下進行
                requestNotificationPermission()
            }
        }
    }

    private fun requestNotificationPermission() {
        globalViewModel.tutorialOnOpen()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }
}