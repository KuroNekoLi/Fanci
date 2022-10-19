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
import com.cmoney.fanci.databinding.MyFragmentLayoutBinding
import com.cmoney.fanci.model.MainTab
import com.cmoney.fanci.model.mainTabItems
import com.cmoney.fanci.ui.screens.follow.FollowScreen
import com.socks.library.KLog


class MainActions(navController: NavHostController) {
    val channelPage: (String) -> Unit = { url ->
        navController.navigate("channel/$url")
    }
}

/**
 * 決定頁面跳轉路徑
 */
@Composable
fun MyAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: MainTab = MainTab.FOLLOW
) {
    var pos by remember { mutableStateOf(0) }

    val actions = remember(navController) { MainActions(navController) }

    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier,
    ) {
        //test
        composable("channel/{channelId}") { backStackEntry ->
            val test = backStackEntry.arguments?.getString("channelId")
            KLog.i("Warren", test.orEmpty())
            AndroidViewBinding(MyFragmentLayoutBinding::inflate) {
            }
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
                            actions.channelPage("123")
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