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
import com.cmoney.fanci.MainStateHolder
import com.cmoney.fanci.ThemeSetting
import com.cmoney.fanci.databinding.MyFragmentLayoutBinding
import com.cmoney.fanci.extension.goBackWithParams
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.model.MainTab
import com.cmoney.fanci.model.mainTabItems
import com.cmoney.fanci.ui.screens.chat.AnnounceBundleKey
import com.cmoney.fanci.ui.screens.chat.AnnouncementScreen
import com.cmoney.fanci.ui.screens.chat.ChatRoomScreen
import com.cmoney.fanci.ui.screens.follow.FollowScreen
import com.cmoney.fanci.ui.screens.group.search.DiscoverGroupScreen
import com.cmoney.fanci.ui.screens.my.MyCallback
import com.cmoney.fanci.ui.screens.my.MyScreen
import com.cmoney.fanci.ui.screens.shared.setting.UserInfoSettingScreen
import com.socks.library.KLog

/**
 * 決定頁面跳轉路徑
 */
@Composable
fun MyAppNavHost(
    navController: NavHostController,
    mainNavController: NavHostController,
    route: (MainStateHolder.Route) -> Unit,
    theme: (ThemeSetting) -> Unit
) {
    val TAG = "MyAppNavHost"

    NavHost(
        navController = mainNavController,
        startDestination = "main",
        modifier = Modifier,
    ) {
        composable("main") {
            MainScreen(navController, route, theme)
        }
        //頻道頁面
        composable("${MainStateHolder.Route.Channel}/{channelId}") { backStackEntry ->
            val channelId = backStackEntry.arguments?.getString("channelId").orEmpty()
            KLog.i(TAG, "open chanel page id:$channelId")

            ChatRoomScreen(channelId, mainNavController, route)
        }

        //公告訊息
        composable(MainStateHolder.Route.Announce) { _ ->
            // TODO:
//            val message =
//                mainNavController.previousBackStackEntry?.savedStateHandle?.get<ChatMessageModel>("message")
//            message?.let {
//                AnnouncementScreen(
//                    navController = mainNavController,
//                    message = message,
//                    onConfirm = {
//                        KLog.i("announce", "click:$it")
//                        mainNavController.goBackWithParams {
//                            putParcelable(AnnounceBundleKey, it)
//                        }
//                    })
//            }
        }

        //設定個人資料
        composable(MainStateHolder.Route.UserInfo) {
            UserInfoSettingScreen(mainNavController)
        }

        //搜尋Group
        composable(MainStateHolder.Route.DiscoverGroup) {
            DiscoverGroupScreen(mainNavController)
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
    route: (MainStateHolder.Route) -> Unit,
    theme: (ThemeSetting) -> Unit
) {
    //test
    var pos by remember { mutableStateOf(0) }

    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier,
    ) {
        mainTabItems.forEach { mainTab ->
            when (mainTab) {
                MainTab.ACTIVITY -> {
                    composable(MainTab.ACTIVITY.route) {
                        AndroidViewBinding(MyFragmentLayoutBinding::inflate) {
                        }
                    }
                }
                MainTab.FOLLOW -> {
                    composable(MainTab.FOLLOW.route) {
                        FollowScreen(
                            onChannelClick = {
                                route.invoke(MainStateHolder.Route.Channel(it.id.orEmpty()))
                            },
                            onSearchClick = {
                                route.invoke(MainStateHolder.Route.DiscoverGroup())
                            },
                            theme = theme
                        )
                    }
                }
                MainTab.MY -> {
                    composable(MainTab.MY.route) {
                        MyScreen {
                            when (it) {
                                MyCallback.ChangeAvatar -> {
                                    route.invoke(MainStateHolder.Route.UserInfo())
                                }
                            }
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
                MainTab.MARKET -> {
                    composable(MainTab.MARKET.route) {
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