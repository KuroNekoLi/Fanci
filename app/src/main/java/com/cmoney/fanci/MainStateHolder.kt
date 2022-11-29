package com.cmoney.fanci

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.Group
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.socks.library.KLog

class MainStateHolder(
    val navController: NavHostController,
    val mainNavController: NavHostController,
    private val systemUiController: SystemUiController,
) {
    private val TAG = MainStateHolder::class.java.simpleName
    val route: (Route) -> Unit = {
        KLog.i(TAG, "route:$it")
        when (it) {
            is Route.Channel -> mainNavController.navigate(it.route)
            is Route.Announce -> {
                mainNavController.currentBackStackEntry?.savedStateHandle?.apply {
                    set("message", it.message)
                }
                mainNavController.navigate(it.route)
            }
            is Route.UserInfo -> {
                mainNavController.navigate(it.route)
            }
            is Route.DiscoverGroup -> {
                mainNavController.navigate(it.route)
            }
            is Route.GroupSetting -> {
                mainNavController.currentBackStackEntry?.savedStateHandle?.apply {
                    set("group", it.group)
                }
                mainNavController.navigate(it.route)
            }
            is Route.GroupSettingSetting -> {
                mainNavController.currentBackStackEntry?.savedStateHandle?.apply {
                    set("group", it.group)
                }
                mainNavController.navigate(it.route)
            }
            is Route.GroupSettingSettingName -> {
                mainNavController.currentBackStackEntry?.savedStateHandle?.apply {
                    set("group", it.group)
                }
                mainNavController.navigate(it.route)
            }
        }
    }

    @Composable
    fun setStatusBarColor() {
        val statusBarColor = MaterialTheme.colors.primary
        SideEffect {
            systemUiController.setStatusBarColor(
                color = statusBarColor,
                darkIcons = false
            )
        }
    }

    /**
     * 記得要去 Navigation 註冊
     */
    sealed class Route(route: String) {
        companion object {
            const val Channel = "channel"
            const val Announce = "announce"
            const val UserInfo = "userInfo"
            const val DiscoverGroup = "discoverGroup"
            const val GroupSetting = "groupsetting"
            const val GroupSetting_Setting = "GroupSetting_Setting" //社團設定 -> 社團設定
            const val GroupSetting_Setting_Name = "GroupSetting_Setting_Name" //社團設定 -> 社團設定 -> 社團名稱
        }

        data class Channel(
            val channelId: String,
            val channelName: String,
            val route: String = "$Channel/${channelId}/${channelName}"
        ) :
            Route(route)

        data class Announce(val message: ChatMessage, val route: String = Announce) :
            Route(route)

        data class UserInfo(val route: String = UserInfo) : Route(route)

        data class DiscoverGroup(val route: String = DiscoverGroup) : Route(route)

        data class GroupSetting(val route: String = GroupSetting, val group: Group) : Route(route)

        data class GroupSettingSetting(val route: String = GroupSetting_Setting, val group: Group) : Route(route)

        data class GroupSettingSettingName(val route: String = GroupSetting_Setting_Name, val group: Group) : Route(route)
    }
}


@Composable
fun rememberMainState(
    navController: NavHostController = rememberNavController(),
    mainNavController: NavHostController = rememberNavController(),
    systemUiController: SystemUiController = rememberSystemUiController()
) = remember {
    MainStateHolder(navController, mainNavController, systemUiController)
}