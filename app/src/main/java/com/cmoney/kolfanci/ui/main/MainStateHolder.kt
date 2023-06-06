package com.cmoney.kolfanci.ui.main

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

class MainStateHolder(
    val navController: NavHostController,
    private val systemUiController: SystemUiController,
) {
    private val TAG = MainStateHolder::class.java.simpleName

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
    sealed class Route {
        abstract val route: String

        companion object {
            const val Channel = "channel"
            const val Announce = "announce"
            const val UserInfo = "userInfo"
            const val DiscoverGroup = "discoverGroup"
            const val GroupSetting = "groupsetting"
            const val GroupSetting_Setting = "GroupSetting_Setting" //社團設定 -> 社團設定
            const val GroupSetting_Setting_Name = "GroupSetting_Setting_Name" //社團設定 -> 社團設定 -> 社團名稱
            const val GroupSetting_Setting_Desc = "GroupSetting_Setting_Desc" //社團設定 -> 社團設定 -> 社團簡介
            const val GroupSetting_Setting_Avatar =
                "GroupSetting_Setting_Avatar" //社團設定 -> 社團設定 -> 社團圖示
            const val GroupSetting_Setting_Background =
                "GroupSetting_Setting_Background" //社團設定 -> 社團設定 -> 社團背景
            const val GroupSetting_Setting_Avatar_Fanci =
                "GroupSetting_Setting_Avatar_Fanci" //社團設定 -> 社團設定 -> 社團背景 -> Fanci預設
            const val GroupSetting_Setting_Background_Fanci =
                "GroupSetting_Setting_Background_Fanci" //社團設定 -> 社團設定 -> 社團背景 -> Fanci預設
            const val GroupSetting_Setting_Theme =
                "GroupSetting_Setting_Theme" //社團設定 -> 社團設定 -> 主題色彩
            const val GroupSetting_Setting_Theme_Preview =
                "GroupSetting_Setting_Theme_Preview" //社團設定 -> 社團設定 -> 主題色彩 -> Preview
        }

        data class Channel(
            val channelId: String,
            val channelName: String,
            override val route: String = "$Channel/${channelId}/${channelName}"
        ) :
            Route()

        data class Announce(val message: ChatMessage, override val route: String = Announce) :
            Route()

        data class UserInfo(override val route: String = UserInfo) : Route()

        data class DiscoverGroup(override val route: String = DiscoverGroup) : Route()
    }

    /**
     * Routh path with group model.
     */
    sealed class GroupRoute : Route() {
        abstract val group: Group

        data class GroupSetting(
            override val route: String = GroupSetting,
            override val group: Group
        ) :
            GroupRoute()

        data class GroupSettingSettingAvatar(
            override val route: String = GroupSetting_Setting_Avatar,
            override val group: Group
        ) : GroupRoute()

        data class GroupSettingSettingBackground(
            override val route: String = GroupSetting_Setting_Background,
            override val group: Group
        ) : GroupRoute()

        data class GroupSettingSetting(
            override val route: String = GroupSetting_Setting,
            override val group: Group
        ) : GroupRoute()

        data class GroupSettingSettingName(
            override val route: String = GroupSetting_Setting_Name,
            override val group: Group
        ) : GroupRoute()

        data class GroupSettingSettingDesc(
            override val route: String = GroupSetting_Setting_Desc,
            override val group: Group
        ) : GroupRoute()

        data class GroupSettingSettingAvatarFanci(
            override val route: String = GroupSetting_Setting_Avatar_Fanci,
            override val group: Group
        ) : GroupRoute()

        data class GroupSettingSettingCoverFanci(
            override val route: String = GroupSetting_Setting_Background_Fanci,
            override val group: Group
        ) : GroupRoute()

        data class GroupSettingSettingTheme(
            override val route: String = GroupSetting_Setting_Theme,
            override val group: Group
        ) : GroupRoute()

        data class GroupSettingSettingThemePreview(
            override val route: String = GroupSetting_Setting_Theme_Preview,
            override val group: Group,
            val themeId: String
        ) : GroupRoute()
    }
}


@Composable
fun rememberMainState(
    navController: NavHostController = rememberNavController(),
    systemUiController: SystemUiController = rememberSystemUiController()
) = remember {
    MainStateHolder(navController, systemUiController)
}