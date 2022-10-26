package com.cmoney.fanci.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.MainScreen
import com.cmoney.fanci.databinding.MyFragmentLayoutBinding
import com.cmoney.fanci.model.MainTab
import com.cmoney.fanci.model.mainTabItems
import com.cmoney.fanci.ui.screens.chat.ChatRoomScreen
import com.cmoney.fanci.ui.screens.follow.FollowScreen
import com.socks.library.KLog

/**
 * 決定頁面跳轉路徑
 */
@Composable
fun MyAppNavHost(
    navController: NavHostController,
    mainNavController: NavHostController,
    channelPage: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "main",
        modifier = Modifier,
    ) {
        composable("main") {
            MainScreen(mainNavController, channelPage)
        }
        //頻道頁面
        composable("channel/{channelId}") { backStackEntry ->
            val channelId = backStackEntry.arguments?.getString("channelId")
            ChatRoomScreen(channelId, navController)
        }
    }
}

/**
 * Tab 路徑
 */
@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: MainTab = MainTab.FOLLOW,
    channelPage: (String) -> Unit
) {
    //test
    var pos by remember { mutableStateOf(0) }

    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier,
    ) {
        composable("profile") { backStackEntry ->

        }

        mainTabItems.forEach { mainTab ->
            when (mainTab) {
                MainTab.EXPLORE -> {
                    composable(MainTab.EXPLORE.route) {
                        AndroidViewBinding(MyFragmentLayoutBinding::inflate) {
                        }
                    }
                }
                MainTab.FOLLOW -> {
                    composable(MainTab.FOLLOW.route) {
                        FollowScreen {
                            KLog.i("Warren", "click callback.")
                            channelPage("123")
                        }
                    }
                }
                MainTab.MY -> {
                    composable(MainTab.MY.route) {
                        PageDisplay(MainTab.MY.title, pos) {
                            pos += 1
                        }
                    }
                }
                MainTab.NOTIFY -> {
                    composable(MainTab.NOTIFY.route) {
                        PageDisplay(MainTab.NOTIFY.title, pos) {
                            pos += 1
                        }
                    }
                }
                else -> {
                    composable(MainTab.FOLLOW.route) {
                        PageDisplay(MainTab.FOLLOW.title, pos) {
                            pos += 1
                        }
                    }
                }
            }
        }
    }
}

//test
@Composable
fun PageDisplay(name: String, pos: Int, onClick: () -> Unit) {
    Column {
        Text(text = "Hello $name!$pos")
        Button(onClick = {
            onClick.invoke()
        }) {
            Text(text = "Click me")
        }
    }
}