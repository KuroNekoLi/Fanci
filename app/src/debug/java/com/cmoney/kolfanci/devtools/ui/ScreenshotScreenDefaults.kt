package com.cmoney.kolfanci.devtools.ui

import com.cmoney.kolfanci.devtools.ui.data.ScreenshotTarget
import com.cmoney.kolfanci.ui.screens.follow.FollowScreenPreview
import com.cmoney.kolfanci.ui.screens.group.dialog.GroupItemDialogScreenPreview
import com.cmoney.kolfanci.ui.screens.group.search.DiscoverGroupLatestScreenPreview
import com.cmoney.kolfanci.ui.screens.group.search.DiscoverGroupPopularScreenPreview
import com.cmoney.kolfanci.ui.screens.group.setting.GroupSettingScreenPreview
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.GroupSettingBackgroundPreview
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.GroupSettingDescViewPreview
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.avatar.GroupSettingAvatarScreenPreview
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.theme.GroupSettingThemeScreenPreview
import com.cmoney.kolfanci.ui.screens.group.setting.group.openness.GroupOpennessScreenPreview
import com.cmoney.kolfanci.ui.screens.my.AccountManageScreenPreview
import com.cmoney.kolfanci.ui.screens.my.MyScreenPreview
import com.cmoney.kolfanci.ui.screens.my.avatar.AvatarNicknameChangeScreenPreview
import com.cmoney.kolfanci.ui.screens.shared.dialog.LoginDialogScreenPreview
import com.cmoney.kolfanci.ui.screens.shared.edit.EditInputScreenPreview

/**
 * 螢幕截圖元件預設
 */
object ScreenshotScreenDefaults {

    /**
     * 預設目標
     *
     * [分類來源](https://docs.google.com/spreadsheets/d/1GQMoQ6K35_oMpRRrLY0SmdSrfpKieP6JyMZj_MwjV4U/edit?usp=sharing)
     */
    val targets: List<ScreenshotTarget> by lazy {
        mutableListOf<ScreenshotTarget>().apply {
            addAll(mainTargets)
            addAll(discoverTargets)
            addAll(groupTargets)
            addAll(myTargets)
            addAll(chatTargets)
            addAll(postTargets)
            addAll(settingTargets)
        }
    }

    private val mainTargets by lazy {
        listOf(
            ScreenshotTarget(
                relativePath = "main",
                name = "FollowScreen"
            ) {
                FollowScreenPreview()
            }
        )
    }

    private val discoverTargets by lazy {
        listOf(
            ScreenshotTarget(
                relativePath = "discover",
                name = "DiscoverGroupPopularScreen"
            ) {
                DiscoverGroupPopularScreenPreview()
            },
            ScreenshotTarget(
                relativePath = "discover",
                name = "DiscoverGroupLatestScreen"
            ) {
                DiscoverGroupLatestScreenPreview()
            }
        )
    }

    private val groupTargets by lazy {
        listOf(
            ScreenshotTarget(
                relativePath = "group",
                name = "GroupItemDialogScreen"
            ) {
                GroupItemDialogScreenPreview()
            }
        )
    }

    private val myTargets by lazy {
        listOf(
            ScreenshotTarget(
                relativePath = "my",
                name = "MyScreen"
            ) {
                MyScreenPreview()
            },
            ScreenshotTarget(
                relativePath = "my",
                name = "AccountManageScreen"
            ) {
                AccountManageScreenPreview()
            },
            ScreenshotTarget(
                relativePath = "my",
                name = "AvatarNicknameChangeScreen"
            ) {
                AvatarNicknameChangeScreenPreview()
            },
            ScreenshotTarget(
                relativePath = "my",
                name = "LoginDialogScreen"
            ) {
                LoginDialogScreenPreview()
            }
        )
    }

    private val chatTargets by lazy {
        listOf<ScreenshotTarget>()
    }

    private val postTargets by lazy {
        listOf<ScreenshotTarget>()
    }

    private val settingTargets by lazy {
        listOf(
            ScreenshotTarget(
                relativePath = "setting",
                name = "GroupSettingScreen"
            ) {
                GroupSettingScreenPreview()
            },
            ScreenshotTarget(
                relativePath = "setting",
                name = "GroupSettingAvatarScreen"
            ) {
                GroupSettingAvatarScreenPreview()
            },
            ScreenshotTarget(
                relativePath = "setting",
                name = "GroupSettingBackground"
            ) {
                GroupSettingBackgroundPreview()
            },
            ScreenshotTarget(
                relativePath = "setting",
                name = "GroupSettingDescScreen"
            ) {
                GroupSettingDescViewPreview()
            },
            ScreenshotTarget(
                relativePath = "setting",
                name = "GroupSettingThemeScreen"
            ) {
                GroupSettingThemeScreenPreview()
            },
            ScreenshotTarget(
                relativePath = "setting",
                name = "GroupSettingNameScreen"
            ) {
                EditInputScreenPreview()
            },
            ScreenshotTarget(
                relativePath = "setting",
                name = "GroupOpennessScreen"
            ) {
                GroupOpennessScreenPreview()
            }
        )
    }
}