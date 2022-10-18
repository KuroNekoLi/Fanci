package com.cmoney.fanci.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.PageDisplay
import com.cmoney.fanci.R
import com.cmoney.fanci.databinding.MyFragmentLayoutBinding
import com.cmoney.fanci.model.MainTab
import com.cmoney.fanci.model.mainTabItems
import com.cmoney.fanci.ui.screens.follow.FollowScreen
import com.cmoney.fanci.ui.screens.shared.ChannelBar
import com.cmoney.fanci.ui.screens.shared.ChannelBarScreen

@Composable
fun MyAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: MainTab = MainTab.FOLLOW
) {
    var pos by remember { mutableStateOf(0) }

    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier,
    ) {

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
                        FollowScreen()
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