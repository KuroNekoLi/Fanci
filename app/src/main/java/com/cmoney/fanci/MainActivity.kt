package com.cmoney.fanci

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.ui.MainNavHost
import com.cmoney.fanci.ui.MyAppNavHost
import com.cmoney.fanci.ui.screens.BottomBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.loginlibrary.module.variable.loginlibraryenum.ApiAction
import com.cmoney.loginlibrary.module.variable.loginlibraryenum.EventCode
import com.cmoney.loginlibrary.view.base.LoginLibraryMainActivity
import com.cmoney.xlogin.XLoginHelper
import com.cmoney.xlogin.base.BaseLoginAppCompactActivity
import com.socks.library.KLog
import kotlinx.android.parcel.Parcelize
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

@Parcelize
class MainActivity : BaseLoginAppCompactActivity() {
    private val TAG = MainActivity::class.java.simpleName

    val globalViewModel by inject<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!XLoginHelper.isLogin) {
            processLogin()
        }

        setContent {
            val theme = globalViewModel.theme.observeAsState()
            FanciTheme(themeSetting = theme.value ?: ThemeSetting.Default) {
                val mainState = rememberMainState()
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LocalColor.current.primary),
                ) {
                    mainState.setStatusBarColor()
                    MyAppNavHost(
                        mainState.navController,
                        mainState.mainNavController,
                        mainState.route,
                        globalViewModel
                    ) {
                        globalViewModel.settingTheme(it)
                    }
                }
            }
        }
    }

    private fun processLogin() {
        val loginIntent = LoginLibraryMainActivity.IntentBuilder(
            // context
            this,
            LoginLibraryMainActivity.IntentBuilder.BackportSupport(
                resources.getInteger(R.integer.app_id),//your appId
                getString(R.string.cm_server_url),
                null,
                XLoginHelper.userAccount,
                XLoginHelper.userPassword,
                XLoginHelper.notificationToken
            ),
            this
        ).build()

        startCmLogin(loginIntent)
    }

    override fun autoLoginFailCallback() {
    }

    override fun fbLogin(
        callback: (() -> Unit)?,
        errorCallback: ((Boolean, EventCode, ApiAction) -> Unit)?
    ) {
    }

    override fun loginFailCallback(errorMessage: String) {
        KLog.i(TAG, "loginFailCallback:$errorMessage")
    }

    override fun loginSuccessCallback() {
        KLog.i(TAG, "loginSuccessCallback")
        globalViewModel.registerUser()
    }
}

@Composable
fun MainScreen(
    mainNavController: NavHostController,
    route: (MainStateHolder.Route) -> Unit,
    theme: (ThemeSetting) -> Unit,
    globalViewModel: MainViewModel
) {
    Scaffold(
        bottomBar = {
            BottomBarScreen(mainNavController)
        }
    ) { innerPadding ->
        MainNavHost(
            navController = mainNavController,
            modifier = Modifier.padding(innerPadding),
            route = route,
            theme = theme,
            globalViewModel = globalViewModel
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FanciTheme {
        MainScreen(rememberNavController(), route = {}, {
        }, koinViewModel())
    }
}