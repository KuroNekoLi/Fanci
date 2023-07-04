package com.cmoney.kolfanci.devtools.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.cmoney.fanciapi.fanci.model.ColorTheme
import com.cmoney.kolfanci.devtools.model.takeScreenshot
import com.cmoney.kolfanci.model.usecase.ThemeUseCase
import com.cmoney.kolfanci.ui.screens.channel.ChannelScreenPreview
import com.cmoney.kolfanci.ui.screens.follow.FollowScreenPreview
import com.cmoney.kolfanci.ui.screens.group.search.DiscoverGroupScreenPreview
import com.cmoney.kolfanci.ui.screens.group.setting.GroupSettingScreenPreview
import com.cmoney.kolfanci.ui.screens.my.MyScreenPreview
import com.cmoney.kolfanci.ui.theme.DefaultThemeColor
import com.cmoney.kolfanci.ui.theme.FanciTheme
import kotlinx.coroutines.delay
import org.koin.core.context.GlobalContext

@Composable
fun ScreenshotScreen(
    contents: List<@Composable () -> Unit> = DefaultTestContents
) {
    val themeUseCase = remember {
        GlobalContext.get().get<ThemeUseCase>()
    }
    var theme by remember {
        mutableStateOf(DefaultThemeColor)
    }
    val context = LocalContext.current
    var index by remember {
        mutableStateOf(0)
    }
    var themeIndex by remember {
        mutableStateOf(-1)
    }
    FanciTheme(
        fanciColor = theme
    ) {
        contents[index]()
    }
    LaunchedEffect(key1 = index) {
        // wait for content create
        delay(500L)
        takeScreenshot(context = context)
        // wait for bitmap create
        delay(100L)
        val targetIndex = index.plus(1)
        if (targetIndex <= contents.lastIndex) {
            // 切換內容
            index = targetIndex
        } else {
            // 切換 theme
            val colorThemes = ColorTheme.values()
            val themeTargetIndex = themeIndex.plus(1)
            if (themeTargetIndex <= colorThemes.lastIndex) {
                val colorTheme = colorThemes[themeTargetIndex]
                themeUseCase.fetchThemeConfig(colorTheme)
                    .onSuccess {
                        theme = it.theme
                        themeIndex = themeTargetIndex
                        index = 0
                    }
            } else {
                // TODO end of screenshot flow
            }
        }
    }
}

private val DefaultTestContents: List<@Composable () -> Unit> by lazy {
    listOf(
        {
            FollowScreenPreview()
        },
        {
            ChannelScreenPreview()
        },
        {
            GroupSettingScreenPreview()
        },
        {
            MyScreenPreview()
        },
        {
            DiscoverGroupScreenPreview()
        }
    )
}
