package com.cmoney.fanci

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.destinations.MainScreenDestination
import com.cmoney.fanci.destinations.testDestination
import com.cmoney.fanci.ui.MainNavHost
import com.cmoney.fanci.ui.screens.BottomBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.loginlibrary.module.variable.loginlibraryenum.ApiAction
import com.cmoney.loginlibrary.module.variable.loginlibraryenum.EventCode
import com.cmoney.loginlibrary.view.base.LoginLibraryMainActivity
import com.cmoney.xlogin.XLoginHelper
import com.cmoney.xlogin.base.BaseLoginAppCompactActivity
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog
import kotlinx.android.parcel.Parcelize
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

val LocalDependencyContainer = staticCompositionLocalOf<MainActivity> {
    error("No dependency container provided!")
}

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
            CompositionLocalProvider(LocalDependencyContainer provides this) {
                val state = globalViewModel.uiState
                FanciTheme(fanciColor = state.theme) {
                    val mainState = rememberMainState()
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(LocalColor.current.primary),
                    ) {
                        mainState.setStatusBarColor()

                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            startRoute = MainScreenDestination
                        )

//                    MyAppNavHost(
//                        mainState.navController,
//                        mainState.mainNavController,
//                        mainState.route,
//                        globalViewModel
//                    )
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

@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
    navigator: DestinationsNavigator,
) {
    val globalViewModel = LocalDependencyContainer.current.globalViewModel
    val mainNavController = rememberNavController()
    val state = globalViewModel.uiState

    //TODO
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
                    navigator.navigate(testDestination)
                },
                globalViewModel = globalViewModel,
                navigator = navigator
            )
        }
    }
}

@Destination
@Composable
fun test() {
    Scaffold(modifier = Modifier
        .fillMaxSize()
        .background(LocalColor.current.primary)) {

    }
}

//@Composable
//fun MainScreen(
//    mainNavController: NavHostController,
//    route: (MainStateHolder.Route) -> Unit,
//    globalViewModel: MainViewModel
//) {
//    Scaffold(
//        bottomBar = {
//            BottomBarScreen(
//                mainNavController
//            )
//        }
//    ) { innerPadding ->
//        MainNavHost(
//            modifier = Modifier.padding(innerPadding),
//            navController = mainNavController,
//            route = route,
//            globalViewModel = globalViewModel
//        )
//    }
//}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FanciTheme {
        MainScreen(
            EmptyDestinationsNavigator
        )
    }
}