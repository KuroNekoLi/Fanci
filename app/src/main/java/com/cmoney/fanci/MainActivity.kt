package com.cmoney.fanci

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.ui.MainNavHost
import com.cmoney.fanci.ui.MyAppNavHost
import com.cmoney.fanci.ui.screens.BottomBarScreen
import com.cmoney.fanci.ui.screens.follow.DrawerMenuScreen
import com.cmoney.fanci.ui.theme.Black_99000000
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.loginlibrary.module.variable.loginlibraryenum.ApiAction
import com.cmoney.loginlibrary.module.variable.loginlibraryenum.EventCode
import com.cmoney.loginlibrary.view.base.LoginLibraryMainActivity
import com.cmoney.xlogin.XLoginHelper
import com.cmoney.xlogin.base.BaseLoginAppCompactActivity
import com.socks.library.KLog
import kotlinx.android.parcel.Parcelize
import org.koin.android.ext.android.inject

@Parcelize
class MainActivity : BaseLoginAppCompactActivity() {
    private val TAG = MainActivity::class.java.simpleName

    val viewModel by inject<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!XLoginHelper.isLogin) {
            processLogin()
        }

        setContent {
            val theme = viewModel.theme.observeAsState()

            FanciTheme(themeSetting = theme.value ?: ThemeSetting.Default) {
                val mainState = rememberMainState()
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background),
                ) {
                    mainState.setStatusBarColor()
                    MyAppNavHost(
                        mainState.navController,
                        mainState.mainNavController,
                        mainState.route
                    ) {
                        viewModel.settingTheme(it)
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
        viewModel.registerUser()
    }
}

@Composable
fun MainScreen(
    mainNavController: NavHostController,
    route: (MainStateHolder.Route) -> Unit,
    theme: (ThemeSetting) -> Unit
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
            theme = theme
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MainScreen(rememberNavController(), route = {}) {
    }
}