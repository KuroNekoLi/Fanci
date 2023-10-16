package com.cmoney.kolfanci.devtools.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.viewmodel.GroupViewModel
import com.cmoney.kolfanci.ui.main.MainViewModel
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.xlogin.base.BaseWebLoginActivity
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.socks.library.KLog
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 顯示螢幕截圖的頁面
 */
class ScreenshotActivity: BaseWebLoginActivity() {
    private val TAG = ScreenshotActivity::class.java.simpleName
    private val globalViewModel by viewModel<MainViewModel>()
    private val globalGroupViewModel by viewModel<GroupViewModel>()
    
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FanciTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LocalColor.current.primary)
                ) { paddingValue ->
                    Box(
                        modifier = Modifier
                            .padding(paddingValue)
                            .fillMaxSize()
                    ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            ScreenshotScreen()
                        } else {
                            val permissionState =
                                rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            if (permissionState.status.isGranted) {
                                ScreenshotScreen()
                            } else {
                                Column(modifier = Modifier.align(Alignment.Center)) {
                                    Text(
                                        text = stringResource(id = R.string.must_provide_external_storage_permission),
                                        fontSize = 16.sp
                                    )
                                    Button(
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = LocalColor.current.env_80
                                        ),
                                        onClick = { permissionState.launchPermissionRequest() }
                                    ) {
                                        Text(
                                            text = stringResource(id = R.string.provide_permission),
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
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