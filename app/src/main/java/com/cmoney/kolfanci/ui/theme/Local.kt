package com.cmoney.kolfanci.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color
import com.cmoney.kolfanci.R
import com.google.errorprone.annotations.Immutable

private val LightImages = Images(lockupLogo = R.drawable.fanci)

@Immutable
data class Images(@DrawableRes val lockupLogo: Int)

internal val LocalImages = staticCompositionLocalOf<Images> {
    LightImages
}

/**
 * Fanci 主題色
 *
 * @property primary 主題色, 按鈕提示色
 * @property background 打底色, List色、輸入匡等
 * @property env_40 環境色 40, 其他配色
 * @property env_60 環境色 60, 其他配色
 * @property env_80 環境色 80, 物件背景色
 * @property env_100 環境色 100, Nav/Tab色
 * @property inputFrame 輸入框
 * @property component 元件色
 * @property text 文字色
 * @property inputText 文字色-輸入框
 * @property specialColor 特殊色
 * @property roleColor 角色顏色
 */
@Stable
class FanciColor(
    primary: Color,
    background: Color,
    env_40: Color,
    env_60: Color,
    env_80: Color,
    env_100: Color,
    inputFrame: Color,
    component: FanciComponentColor,
    text: FanciTextColor,
    inputText: FanciInputText,
    specialColor: SpecialColor,
    roleColor: RoleColor
) {
    var primary by mutableStateOf(primary, structuralEqualityPolicy())
        internal set
    var background by mutableStateOf(background, structuralEqualityPolicy())
        internal set
    var env_40 by mutableStateOf(env_40, structuralEqualityPolicy())
        internal set
    var env_60 by mutableStateOf(env_60, structuralEqualityPolicy())
        internal set
    var env_80 by mutableStateOf(env_80, structuralEqualityPolicy())
        internal set
    var env_100 by mutableStateOf(env_100, structuralEqualityPolicy())
        internal set
    var inputFrame by mutableStateOf(inputFrame, structuralEqualityPolicy())
        internal set
    var component by mutableStateOf(component, structuralEqualityPolicy())
        internal set
    var text by mutableStateOf(text, structuralEqualityPolicy())
        internal set
    var inputText by mutableStateOf(inputText, structuralEqualityPolicy())
        internal set
    var specialColor by mutableStateOf(specialColor, structuralEqualityPolicy())
        internal set
    var roleColor by mutableStateOf(roleColor, structuralEqualityPolicy())
        internal set

    fun copy(
        primary: Color = this.primary,
        background: Color = this.background,
        env_40: Color = this.env_40,
        env_60: Color = this.env_60,
        env_80: Color = this.env_80,
        env_100: Color = this.env_100,
        inputFrame: Color = this.inputFrame,
        component: FanciComponentColor = this.component.copy(),
        text: FanciTextColor = this.text.copy(),
        inputText: FanciInputText = this.inputText.copy(),
        specialColor: SpecialColor = this.specialColor.copy(),
        roleColor: RoleColor = this.roleColor.copy()
    ): FanciColor {
        return FanciColor(
            primary = primary,
            background = background,
            env_40 = env_40,
            env_60 = env_60,
            env_80 = env_80,
            env_100 = env_100,
            inputFrame = inputFrame,
            component = component,
            text = text,
            inputText = inputText,
            specialColor = specialColor,
            roleColor = roleColor
        )
    }

    override fun toString(): String {
        return "FanciColor(" +
            "primary=$primary, " +
            "background=$background, " +
            "env_40=$env_40, " +
            "env_60=$env_60, " +
            "env_80=$env_80, " +
            "env_100=$env_100, " +
            "inputFrame=$inputFrame, " +
            "component=$component, " +
            "text=$text, " +
            "inputText=$inputText, " +
            "specialColor=$specialColor, " +
            "roleColor=$roleColor" +
            ")"
    }
}

/**
 * 角色顏色
 *
 * @property colors
 */
@Stable
class RoleColor(
    colors: List<com.cmoney.fanciapi.fanci.model.Color>
) {
    var colors by mutableStateOf(colors, structuralEqualityPolicy())
        internal set

    fun copy(
        colors: List<com.cmoney.fanciapi.fanci.model.Color> = this.colors.toList()
    ): RoleColor {
        return RoleColor(
            colors = colors
        )
    }

    override fun toString(): String {
        return "RoleColor(" +
            "colors=$colors" +
            ")"
    }
}

/**
 * 特殊色
 *
 * @property blue
 * @property blueGreen
 * @property green
 * @property hintRed
 * @property orange
 * @property pink
 * @property purple
 * @property red
 * @property yellow
 */
@Stable
class SpecialColor(
    blue: Color,
    blueGreen: Color,
    green: Color,
    hintRed: Color,
    orange: Color,
    pink: Color,
    purple: Color,
    red: Color,
    yellow: Color
) {
    var blue by mutableStateOf(blue, structuralEqualityPolicy())
        internal set
    var blueGreen by mutableStateOf(blueGreen, structuralEqualityPolicy())
        internal set
    var green by mutableStateOf(green, structuralEqualityPolicy())
        internal set
    var hintRed by mutableStateOf(hintRed, structuralEqualityPolicy())
        internal set
    var orange by mutableStateOf(orange, structuralEqualityPolicy())
        internal set
    var pink by mutableStateOf(pink, structuralEqualityPolicy())
        internal set
    var purple by mutableStateOf(purple, structuralEqualityPolicy())
        internal set
    var red by mutableStateOf(red, structuralEqualityPolicy())
        internal set
    var yellow by mutableStateOf(yellow, structuralEqualityPolicy())
        internal set

    fun copy(
        blue: Color = this.blue,
        blueGreen: Color = this.blueGreen,
        green: Color = this.green,
        hintRed: Color = this.hintRed,
        orange: Color = this.orange,
        pink: Color = this.pink,
        purple: Color = this.purple,
        red: Color = this.red,
        yellow: Color = this.yellow
    ): SpecialColor {
        return SpecialColor(
            blue = blue,
            blueGreen = blueGreen,
            green = green,
            hintRed = hintRed,
            orange = orange,
            pink = pink,
            purple = purple,
            red = red,
            yellow = yellow
        )
    }

    override fun toString(): String {
        return "SpecialColor(" +
            "blue=$blue, " +
            "blueGreen=$blueGreen, " +
            "green=$green, " +
            "hintRed=$hintRed, " +
            "orange=$orange, " +
            "pink=$pink, " +
            "purple=$purple, " +
            "red=$red, " +
            "yellow=$yellow" +
            ")"
    }
}

/**
 * 文字色-輸入框
 *
 * @property input_30
 * @property input_50
 * @property input_80
 * @property input_100
 */
@Stable
class FanciInputText(
    input_30: Color,
    input_50: Color,
    input_80: Color,
    input_100: Color
) {
    var input_30 by mutableStateOf(input_30, structuralEqualityPolicy())
        internal set
    var input_50 by mutableStateOf(input_50, structuralEqualityPolicy())
        internal set
    var input_80 by mutableStateOf(input_80, structuralEqualityPolicy())
        internal set
    var input_100 by mutableStateOf(input_100, structuralEqualityPolicy())
        internal set

    fun copy(
        input_30: Color = this.input_30,
        input_50: Color = this.input_50,
        input_80: Color = this.input_80,
        input_100: Color = this.input_100
    ): FanciInputText {
        return FanciInputText(
            input_30 = input_30,
            input_50 = input_50,
            input_80 = input_80,
            input_100 = input_100
        )
    }

    override fun toString(): String {
        return "FanciInputText(" +
            "input_30=$input_30, " +
            "input_50=$input_50, " +
            "input_80=$input_80, " +
            "input_100=$input_100" +
            ")"
    }
}

/**
 * 元件色
 *
 * @property tabUnSelect
 * @property tabSelected
 * @property other
 */
@Stable
class FanciComponentColor(
    tabUnSelect: Color,
    tabSelected: Color,
    other: Color
) {
    var tabUnSelect by mutableStateOf(tabUnSelect, structuralEqualityPolicy())
        internal set
    var tabSelected by mutableStateOf(tabSelected, structuralEqualityPolicy())
        internal set
    var other by mutableStateOf(other, structuralEqualityPolicy())
        internal set

    fun copy(
        tabUnSelect: Color = this.tabUnSelect,
        tabSelected: Color = this.tabSelected,
        other: Color = this.other
    ): FanciComponentColor {
        return FanciComponentColor(
            tabUnSelect = tabUnSelect,
            tabSelected = tabSelected,
            other = other
        )
    }

    override fun toString(): String {
        return "FanciComponentColor(" +
            "tabUnSelect=$tabUnSelect, " +
            "tabSelected=$tabSelected, " +
            "other=$other" +
            ")"
    }
}

/**
 * 文字色
 *
 * @property default_30
 * @property default_50
 * @property default_80
 * @property default_100
 * @property other
 */
@Stable
class FanciTextColor(
    default_30: Color,
    default_50: Color,
    default_80: Color,
    default_100: Color,
    other: Color
) {
    var default_30 by mutableStateOf(default_30, structuralEqualityPolicy())
        internal set
    var default_50 by mutableStateOf(default_50, structuralEqualityPolicy())
        internal set
    var default_80 by mutableStateOf(default_80, structuralEqualityPolicy())
        internal set
    var default_100 by mutableStateOf(default_100, structuralEqualityPolicy())
        internal set
    var other by mutableStateOf(other, structuralEqualityPolicy())
        internal set

    fun copy(
        default_30: Color = this.default_30,
        default_50: Color = this.default_50,
        default_80: Color = this.default_80,
        default_100: Color = this.default_100,
        other: Color = this.other
    ): FanciTextColor {
        return FanciTextColor(
            default_30 = default_30,
            default_50 = default_50,
            default_80 = default_80,
            default_100 = default_100,
            other = other
        )
    }

    override fun toString(): String {
        return "FanciTextColor(" +
            "default_30=$default_30, " +
            "default_50=$default_50, " +
            "default_80=$default_80, " +
            "default_100=$default_100, " +
            "other=$other" +
            ")"
    }
}

/**
 * Updates the internal values of the given [FanciColor] with values from the [other] [FanciColor]. This
 * allows efficiently updating a subset of [FanciColor], without recomposing every composable that
 * consumes values from [LocalColor].
 *
 * Because [FanciColor] is very wide-reaching, and used by many expensive composables in the
 * hierarchy, providing a new value to [LocalColor] causes every composable consuming
 * [LocalColor] to recompose, which is prohibitively expensive in cases such as animating one
 * color in the theme. Instead, [FanciColor] is internally backed by [mutableStateOf], and this
 * function mutates the internal state of [this] to match values in [other]. This means that any
 * changes will mutate the internal state of [this], and only cause composables that are reading
 * the specific changed value to recompose.
 *
 * 備註：這是從 Material 的 Colors.kt 複製的註解
 */
internal fun FanciColor.updateColorFrom(other: FanciColor) {
    primary = other.primary
    background = other.background
    env_40 = other.env_40
    env_60 = other.env_60
    env_80 = other.env_80
    env_100 = other.env_100
    inputFrame = other.inputFrame
    component.updateColorFrom(other.component)
    text.updateColorFrom(other.text)
    inputText.updateColorFrom(other.inputText)
    specialColor.updateColorFrom(other.specialColor)
    roleColor.updateColorFrom(other.roleColor)
}

private fun FanciComponentColor.updateColorFrom(other: FanciComponentColor) {
    tabUnSelect = other.tabSelected
    tabSelected = other.tabUnSelect
    this.other = other.other
}

private fun FanciTextColor.updateColorFrom(other: FanciTextColor) {
    default_30 = other.default_30
    default_50 = other.default_50
    default_80 = other.default_80
    default_100 = other.default_100
    this.other = other.other
}

private fun FanciInputText.updateColorFrom(other: FanciInputText) {
    input_30 = other.input_30
    input_50 = other.input_50
    input_80 = other.input_80
    input_100 = other.input_100
}

private fun SpecialColor.updateColorFrom(other: SpecialColor) {
    blue = other.blue
    blueGreen = other.blueGreen
    green = other.green
    hintRed = other.hintRed
    orange = other.orange
    pink = other.pink
    purple = other.purple
    red = other.red
    yellow = other.yellow
}

private fun RoleColor.updateColorFrom(other: RoleColor) {
    colors = other.colors
}

internal val LocalColor = staticCompositionLocalOf<FanciColor> {
    DefaultThemeColor
}