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
import com.cmoney.kolfanci.devtools.ui.data.ScreenshotTarget
import com.cmoney.kolfanci.model.usecase.ThemeUseCase
import com.cmoney.kolfanci.ui.theme.DefaultThemeColor
import com.cmoney.kolfanci.ui.theme.FanciTheme
import kotlinx.coroutines.delay
import org.koin.core.context.GlobalContext

/**
 * 螢幕截圖畫面(會依據設定的目標依序切換並截圖儲存)
 *
 * @param targets 目標集合
 */
@Composable
fun ScreenshotScreen(
    targets: List<ScreenshotTarget> = ScreenshotScreenDefaults.targets
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
    val target = targets[index]
    FanciTheme(
        fanciColor = theme
    ) {
        target.content()
    }
    LaunchedEffect(key1 = index, key2 = targets) {
        // wait for content create
        delay(500L)
        takeScreenshot(context = context, relativePath = target.relativePath, name = target.name)
        val targetIndex = index.plus(1)
        if (targetIndex <= targets.lastIndex) {
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
