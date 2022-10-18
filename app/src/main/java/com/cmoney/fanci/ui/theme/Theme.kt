package com.cmoney.fanci.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = Black_14171C,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = Black_191E24,
    onSurface = White_BBBCBF,
    onSecondary = Black_181C23,
    surface = Black_1B2129
)

private val LightColorPalette = lightColors(
    primary = Black_14171C,
    primaryVariant = Black_14171C,
    secondary = Teal200,
    background = Black_191E24,
    onSurface = White_BBBCBF,
    onSecondary = Black_181C23,
    surface = Black_1B2129
    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    */
)

@Composable
fun FanciTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = colors.background,
            darkIcons = false
        )
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}