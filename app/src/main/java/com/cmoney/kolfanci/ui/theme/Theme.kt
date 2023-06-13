package com.cmoney.kolfanci.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.cmoney.kolfanci.R

//========== Text ==========
private val darkText = FanciTextColor(
    default_30 = Color_33FFFFFF,
    default_50 = Color_80FFFFFF,
    default_80 = Color_CCFFFFFF,
    default_100 = Color.White,
    other = Color.White
)

private val lightText = FanciTextColor(
    default_30 = Color_4D2D2D2D,
    default_50 = Color_802D2D2D,
    default_80 = Color_CC2D2D2D,
    default_100 = Color_2D2D2D,
    other = Color.White
)

//========== Component ==========
private val lightComponent = FanciComponentColor(
    tabUnSelect = Color_33000000,
    tabSelected = Color.White,
    other = Color_4D292929
)

private val darkComponent = FanciComponentColor(
    tabUnSelect = Color_33FFFFFF,
    tabSelected = Color.White,
    other = Color_80FFFFFF
)

//========== Input Text ==========
private val inputText = FanciInputText(
    input_30 = Color_4D2D2D2D,
    input_50 = Color_802D2D2D,
    input_80 = Color_CC2D2D2D,
    input_100 = Color_2D2D2D
)

private val specialColor = SpecialColor(
    blue = Color_37A2EF,
    blueGreen = Color_31B6A6,
    green = Color_38B035,
    hintRed = Color_CA4848,
    orange = Color_E18910,
    pink = Color_E24CA6,
    purple = Color_8559E1,
    red = Color.Red,
    yellow = Color_DDBA00
)

private val roleColor = RoleColor(
    listOf(
        com.cmoney.fanciapi.fanci.model.Color(
            hexColorCode = "FF37A2EF",
            displayName = "藍"
        ),
        com.cmoney.fanciapi.fanci.model.Color(
            hexColorCode = "FF31B6A6",
            displayName = "藍綠"
        ),
        com.cmoney.fanciapi.fanci.model.Color(
            hexColorCode = "FF38B035",
            displayName = "綠"
        ),
        com.cmoney.fanciapi.fanci.model.Color(
            hexColorCode = "FFE18910",
            displayName = "橘"
        ),
        com.cmoney.fanciapi.fanci.model.Color(
            hexColorCode = "FFE24CA6",
            displayName = "粉"
        ),
        com.cmoney.fanciapi.fanci.model.Color(
            hexColorCode = "FF8559E1",
            displayName = "紫"
        ),
        com.cmoney.fanciapi.fanci.model.Color(
            hexColorCode = "FFCA4848",
            displayName = "紅"
        ),
        com.cmoney.fanciapi.fanci.model.Color(
            hexColorCode = "FFDDBA00",
            displayName = "黃"
        )
    )
)


val DefaultThemeColor = FanciColor(
    primary = Color_4F70E5,
    background = Color_0DFFFFFF,
    env_40 = Color_3D4452,
    env_60 = Color_303744,
    env_80 = Color_2B313C,
    env_100 = Color_20262F,
    inputFrame = Color_E6FFFFFF,
    component = darkComponent,
    text = darkText,
    inputText = inputText,
    specialColor = specialColor,
    roleColor = roleColor
)

@Composable
fun DefaultTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val image = DarkImages
    MainTheme(darkTheme, DefaultThemeColor, content, image)
}

val CoffeeThemeColor = FanciColor(
    primary = Color_84603F,
    background = Color_0D000000,
    env_40 = Color_F5E7DA,
    env_60 = Color_FFF1E3,
    env_80 = Color_FCE7D4,
    env_100 = Color_A98F76,
    inputFrame = Color_CCFFFFFF,
    component = lightComponent,
    text = lightText,
    inputText = inputText,
    specialColor = specialColor,
    roleColor = roleColor
)

@Composable
fun CoffeeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val image = LightImages
    MainTheme(darkTheme, CoffeeThemeColor, content, image)
}

private val LightImages = Images(lockupLogo = R.drawable.fanci)

private val DarkImages = Images(lockupLogo = R.drawable.emoji_like)

@Composable
fun FanciTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    fanciColor: FanciColor = LocalColor.current,
    content: @Composable () -> Unit
) {
    val image = LightImages
    MainTheme(darkTheme, fanciColor, content, image)
}

@Composable
private fun MainTheme(
    darkTheme: Boolean,
    fanciColor: FanciColor,
    content: @Composable () -> Unit,
    image: Images
) {
    val rememberColor = remember {
        // Explicitly creating a new object here so we don't mutate the initial [fanciColor]
        // provided, and overwrite the values set in it.
        fanciColor.copy()
    }.apply { updateColorFrom(other = fanciColor) }
    CompositionLocalProvider(
        LocalImages provides image,
        LocalColor provides rememberColor
    ) {
        MaterialTheme(
            colors = MaterialTheme.colors.copy(
                primary = fanciColor.env_100,
                background = fanciColor.env_100
            ),
            content = content,
            typography = Typography,
            shapes = Shapes,
        )
    }
}