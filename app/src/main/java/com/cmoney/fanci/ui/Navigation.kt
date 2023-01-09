package com.cmoney.fanci.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.MainStateHolder
import com.cmoney.fanci.MainViewModel
import com.cmoney.fanci.databinding.MyFragmentLayoutBinding
import com.cmoney.fanci.extension.goBackWithParams
import com.cmoney.fanci.ui.screens.TabItem
import com.cmoney.fanci.ui.screens.chat.AnnounceBundleKey
import com.cmoney.fanci.ui.screens.chat.AnnouncementScreen
import com.cmoney.fanci.ui.screens.follow.FollowScreen
import com.cmoney.fanci.ui.screens.group.search.DiscoverGroupScreen
import com.cmoney.fanci.ui.screens.my.MyCallback
import com.cmoney.fanci.ui.screens.my.MyScreen
import com.cmoney.fanci.ui.screens.shared.setting.UserInfoSettingScreen
import com.cmoney.fanci.ui.screens.tutorial.TutorialScreen
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.socks.library.KLog

/**
 * 決定頁面跳轉路徑
 */
@Composable
fun MyAppNavHost(
    navController: NavHostController,
    mainNavController: NavHostController,
    route: (MainStateHolder.Route) -> Unit,
    globalViewModel: MainViewModel,
) {
    val TAG = "MyAppNavHost"

    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }

    NavHost(
        navController = mainNavController,
        startDestination = "main",
        modifier = Modifier,
    ) {
//        composable("main") {
//            MainScreen(navController, route, globalViewModel)
//        }

        //頻道頁面
//        composable("${MainStateHolder.Route.Channel}/{channelId}/{channelName}") { backStackEntry ->
//            val channelId = backStackEntry.arguments?.getString("channelId").orEmpty()
//            val channelName = backStackEntry.arguments?.getString("channelName").orEmpty()
//            KLog.i(TAG, "open chanel page id:$channelId , name:$channelName")
//            ChatRoomScreen(
//                channelId, channelName, mainNavController, route
//            )
//        }

        //公告訊息
        composable(MainStateHolder.Route.Announce) { _ ->
            val message =
                mainNavController.previousBackStackEntry?.savedStateHandle?.get<ChatMessage>("message")
            message?.let {
                AnnouncementScreen(
                    navController = mainNavController,
                    message = message,
                    onConfirm = {
                        KLog.i("announce", "click:$it")
                        mainNavController.goBackWithParams {
                            putParcelable(AnnounceBundleKey, it)
                        }
                    })
            }
        }

        //設定個人資料
        composable(MainStateHolder.Route.UserInfo) {
            UserInfoSettingScreen(mainNavController)
        }

        //搜尋Group
        composable(MainStateHolder.Route.DiscoverGroup) {
//            DiscoverGroupScreen()
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
    startDestination: TabItem = TabItem.Follow,
    route: (MainStateHolder.Route) -> Unit,
    globalViewModel: MainViewModel,
    navigator: DestinationsNavigator
) {
    //test
    var pos by remember { mutableStateOf(0) }

    Column {
        TutorialScreen()
    }

    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier,
    ) {
        TabItem.values().forEach { mainTab ->
            when (mainTab) {
                TabItem.Follow -> {
                    composable(TabItem.Follow.route) {
                        FollowScreen(
//                            onChannelClick = {
//                                route.invoke(
//                                    MainStateHolder.Route.Channel(
//                                        channelId = it.id.orEmpty(),
//                                        channelName = it.name.orEmpty()
//                                    )
//                                )
//                            },
//                            onSearchClick = {
//                                route.invoke(MainStateHolder.Route.DiscoverGroup())
//                            },
//                            onGroupSettingClick = {
//                                //前往社團設定
//                                route.invoke(MainStateHolder.GroupRoute.GroupSetting(group = it))
//                            },
//                            navController = navController,
                            globalViewModel = globalViewModel,
                            navigator = navigator
                        )
                    }
                }
                TabItem.Chat -> {
                    composable(TabItem.Chat.route) {
                        PageDisplay(TabItem.Chat.title, pos) {
                            pos += 1
                        }
                    }
                }
                TabItem.Activity -> {
                    composable(TabItem.Activity.route) {
                        AndroidViewBinding(MyFragmentLayoutBinding::inflate) {
                        }
                    }
                }
                TabItem.Market -> {
                    composable(TabItem.Market.route) {
                        PageDisplay(TabItem.Market.title, pos) {
                            pos += 1
                        }
                    }
                }
                TabItem.My -> {
                    composable(TabItem.My.route) {
                        MyScreen {
                            when (it) {
                                MyCallback.ChangeAvatar -> {
                                    route.invoke(MainStateHolder.Route.UserInfo())
                                }
                            }
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
    KLog.i("TAG", "PageDisplay")
    Column {
        Text(text = "Hello $name!$pos")
        Button(onClick = {
            onClick.invoke()
        }) {
            Text(text = "Click me")
        }
    }
}