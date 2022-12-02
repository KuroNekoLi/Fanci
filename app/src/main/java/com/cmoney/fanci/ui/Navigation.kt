package com.cmoney.fanci.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.MainScreen
import com.cmoney.fanci.MainStateHolder
import com.cmoney.fanci.MainViewModel
import com.cmoney.fanci.databinding.MyFragmentLayoutBinding
import com.cmoney.fanci.extension.goBackWithParams
import com.cmoney.fanci.model.MainTab
import com.cmoney.fanci.model.mainTabItems
import com.cmoney.fanci.ui.screens.chat.AnnounceBundleKey
import com.cmoney.fanci.ui.screens.chat.AnnouncementScreen
import com.cmoney.fanci.ui.screens.chat.ChatRoomScreen
import com.cmoney.fanci.ui.screens.follow.FollowScreen
import com.cmoney.fanci.ui.screens.group.search.DiscoverGroupScreen
import com.cmoney.fanci.ui.screens.group.setting.GroupSettingScreen
import com.cmoney.fanci.ui.screens.group.setting.groupsetting.*
import com.cmoney.fanci.ui.screens.group.setting.groupsetting.fancilib.FanciDefaultAvatarScreen
import com.cmoney.fanci.ui.screens.group.setting.groupsetting.fancilib.FanciDefaultCoverScreen
import com.cmoney.fanci.ui.screens.group.setting.groupsetting.theme.GroupSettingThemeScreen
import com.cmoney.fanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.fanci.ui.screens.my.MyCallback
import com.cmoney.fanci.ui.screens.my.MyScreen
import com.cmoney.fanci.ui.screens.shared.setting.UserInfoSettingScreen
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.Group
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

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
        composable("main") {
            MainScreen(navController, route, globalViewModel)
        }

        //頻道頁面
        composable("${MainStateHolder.Route.Channel}/{channelId}/{channelName}") { backStackEntry ->
            val channelId = backStackEntry.arguments?.getString("channelId").orEmpty()
            val channelName = backStackEntry.arguments?.getString("channelName").orEmpty()
            KLog.i(TAG, "open chanel page id:$channelId , name:$channelName")
            ChatRoomScreen(
                channelId, channelName, mainNavController, route
            )
        }

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
            DiscoverGroupScreen(mainNavController)
        }

        navGroupSetting(
            this,
            mainNavController,
            route,
            globalViewModel,
            viewModelStoreOwner
        )

    }
}

/**
 * 社團設定頁 Route 路徑
 *  -社團設定頁面
 *  -社團設定頁面-設定社團
 *  -社團設定頁面-設定社團-社團名稱
 *  -社團設定頁面-設定社團-社團簡介
 *  -社團設定頁面-設定社團-社團圖示
 *  -社團設定頁面-設定社團-社團背景
 *  -社團設定頁面-設定社團-社團圖示-Fanci預設
 *  -社團設定頁面-設定社團-社團背景-Fanci預設
 *  -社團設定頁面-設定社團-主題色彩
 */
private fun navGroupSetting(
    navGraphBuilder: NavGraphBuilder,
    mainNavController: NavHostController,
    route: (MainStateHolder.Route) -> Unit,
    globalViewModel: MainViewModel,
    viewModelStoreOwner: ViewModelStoreOwner,
) {
    navGraphBuilder.apply {
        //社團設定頁面
        composable(MainStateHolder.Route.GroupSetting) {
            val group =
                mainNavController.previousBackStackEntry?.savedStateHandle?.get<Group>("group")
            group?.let {
                GroupSettingScreen(
                    navController = mainNavController,
                    group = it,
                    route = route
                )
            }
        }

        //社團設定頁面-設定社團
        composable(MainStateHolder.Route.GroupSetting_Setting) {
            val groupSettingViewModel: GroupSettingViewModel =
                koinViewModel(owner = viewModelStoreOwner)
            val group =
                mainNavController.previousBackStackEntry?.savedStateHandle?.get<Group>("group")
            group?.let { group ->
                GroupSettingSettingScreen(
                    navController = mainNavController,
                    group = group,
                    route = route,
                    viewModel = groupSettingViewModel,
                    globalViewModel = globalViewModel
                )
            }
        }

        //社團設定頁面-設定社團-社團名稱
        composable(MainStateHolder.Route.GroupSetting_Setting_Name) {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner
            ) {
                val group =
                    mainNavController.previousBackStackEntry?.savedStateHandle?.get<Group>("group")
                group?.let { group ->
                    GroupSettingNameScreen(
                        navController = mainNavController,
                        group = group,
                        viewModel = koinViewModel()
                    )
                }
            }
        }

        //社團設定頁面-設定社團-社團簡介
        composable(MainStateHolder.Route.GroupSetting_Setting_Desc) {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner
            ) {
                val group =
                    mainNavController.previousBackStackEntry?.savedStateHandle?.get<Group>("group")
                group?.let { group ->
                    GroupSettingDescScreen(
                        navController = mainNavController,
                        group = group,
                        viewModel = koinViewModel()
                    )
                }
            }
        }

        //社團設定頁面-設定社團-社團圖示
        composable(MainStateHolder.Route.GroupSetting_Setting_Avatar) {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner
            ) {
                val group =
                    mainNavController.previousBackStackEntry?.savedStateHandle?.get<Group>("group")
                group?.let { group ->
                    GroupSettingAvatarScreen(
                        navController = mainNavController,
                        group = group,
                        viewModel = koinViewModel(),
                        route = route
                    )
                }
            }
        }

        //社團設定頁面-設定社團-社團背景
        composable(MainStateHolder.Route.GroupSetting_Setting_Background) {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner
            ) {
                val group =
                    mainNavController.previousBackStackEntry?.savedStateHandle?.get<Group>("group")
                group?.let { group ->
                    GroupSettingBackgroundScreen(
                        navController = mainNavController,
                        group = group,
                        viewModel = koinViewModel(),
                        route = route
                    )
                }
            }
        }

        //社團設定頁面-設定社團-社團圖示-Fanci預設
        composable(MainStateHolder.Route.GroupSetting_Setting_Avatar_Fanci) {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner
            ) {
                val group =
                    mainNavController.previousBackStackEntry?.savedStateHandle?.get<Group>("group")
                group?.let { group ->
                    FanciDefaultAvatarScreen(
                        navController = mainNavController,
                        group = group,
                        viewModel = koinViewModel()
                    )
                }
            }
        }

        //社團設定頁面-設定社團-社團背景-Fanci預設
        composable(MainStateHolder.Route.GroupSetting_Setting_Background_Fanci) {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner
            ) {
                val group =
                    mainNavController.previousBackStackEntry?.savedStateHandle?.get<Group>("group")
                group?.let { group ->
                    FanciDefaultCoverScreen(
                        navController = mainNavController,
                        group = group,
                        viewModel = koinViewModel()
                    )
                }
            }
        }

        //社團設定頁面-設定社團-主題色彩
        composable(MainStateHolder.Route.GroupSetting_Setting_Theme) {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner
            ) {
                val group =
                    mainNavController.previousBackStackEntry?.savedStateHandle?.get<Group>("group")
                group?.let { group ->
                    GroupSettingThemeScreen(
                        navController = mainNavController,
                        group = group,
                        viewModel = koinViewModel(),
                        globalViewModel = globalViewModel,
                        route = route
                    )
                }
            }
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
    globalViewModel: MainViewModel
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
                                route.invoke(
                                    MainStateHolder.Route.Channel(
                                        channelId = it.id.orEmpty(),
                                        channelName = it.name.orEmpty()
                                    )
                                )
                            },
                            onSearchClick = {
                                route.invoke(MainStateHolder.Route.DiscoverGroup())
                            },
                            onGroupSettingClick = {
                                //前往社團設定
                                route.invoke(MainStateHolder.GroupRoute.GroupSetting(group = it))
                            },
                            navController = navController,
                            globalViewModel = globalViewModel
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