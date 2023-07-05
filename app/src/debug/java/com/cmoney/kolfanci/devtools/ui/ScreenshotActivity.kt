package com.cmoney.kolfanci.devtools.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import com.cmoney.kolfanci.model.viewmodel.GroupViewModel
import com.cmoney.kolfanci.ui.main.MainViewModel
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.xlogin.base.BaseWebLoginActivity
import com.socks.library.KLog
import org.koin.android.ext.android.inject

/**
 * 顯示螢幕截圖的頁面
 */
class ScreenshotActivity: BaseWebLoginActivity() {
    private val TAG = ScreenshotActivity::class.java.simpleName
    private val globalViewModel by inject<MainViewModel>()
    private val globalGroupViewModel by inject<GroupViewModel>()

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
                        ScreenshotScreen()
                    }
                }
            }
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