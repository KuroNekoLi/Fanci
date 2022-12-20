package com.cmoney.fanci.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.google.errorprone.annotations.Immutable

@Immutable
data class Images(@DrawableRes val lockupLogo: Int)

internal val LocalImages = staticCompositionLocalOf<Images> {
    error("No LocalImages specified")
}

@Immutable
data class FanciColor(
    val primary: Color,             //主題色, 按鈕提示色
    val background: Color,          //打底色, List色、輸入匡等
    val env_40: Color,              //環境色 40, 其他配色
    val env_60: Color,              //環境色 60, 其他配色
    val env_80: Color,              //環境色 80, 物件背景色
    val env_100: Color,             //環境色 100, Nav/Tab色
    val inputFrame: Color,          //輸入框
    val component: FanciComponentColor,  //元件色
    val text: FanciTextColor,            //文字色
    val inputText: FanciInputText,       //文字色-輸入框
    val specialColor: SpecialColor,      //特殊色
    val roleColor: RoleColor             //角色顏色
)

@Immutable
data class RoleColor(
    val colors: List<com.cmoney.fanciapi.fanci.model.Color>
)

@Immutable
data class SpecialColor(
    val blue: Color,
    val blueGreen: Color,
    val green: Color,
    val hintRed: Color,
    val orange: Color,
    val pink: Color,
    val purple: Color,
    val red: Color,
    val yellow: Color
)


@Immutable
data class FanciInputText(
    val input_30: Color,
    val input_50: Color,
    val input_80: Color,
    val input_100: Color
)

@Immutable
data class FanciComponentColor(
    val tabUnSelect: Color,
    val tabSelected: Color,
    val other: Color
)

@Immutable
data class FanciTextColor(
    val default_30: Color,
    val default_50: Color,
    val default_80: Color,
    val default_100: Color,
    val other: Color
)

internal val LocalColor = compositionLocalOf<FanciColor> {
    error("No LocalColor specified")
}