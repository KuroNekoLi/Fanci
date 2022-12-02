package com.cmoney.fanci.model.usecase

import androidx.core.graphics.toColorInt
import com.cmoney.fanci.extension.checkResponseBody
import com.cmoney.fanci.ui.theme.FanciColor
import com.cmoney.fanci.ui.theme.FanciComponentColor
import com.cmoney.fanci.ui.theme.FanciInputText
import com.cmoney.fanci.ui.theme.FanciTextColor
import com.cmoney.fanciapi.fanci.api.ThemeColorApi
import com.cmoney.fanciapi.fanci.model.Color
import com.cmoney.fanciapi.fanci.model.ColorTheme
import com.socks.library.KLog

class ThemeUseCase(private val themeColorApi: ThemeColorApi) {
    private val TAG = ThemeUseCase::class.java.simpleName

    /**
     * 抓取 所有 主題設定檔
     */
    suspend fun fetchAllThemeConfig() = kotlin.runCatching {
        KLog.i(TAG, "fetchAllThemeConfig")
        themeColorApi.apiV1ThemeColorGet().checkResponseBody()
    }

    /**
     * 抓取 特定 主題設定檔
     */
    suspend fun fetchThemeConfig(colorTheme: ColorTheme) = kotlin.runCatching {
        KLog.i(TAG, "fetchThemeConfig")
        themeColorApi.apiV1ThemeColorColorThemeGet(colorTheme).checkResponseBody().let {
            serverColorTransfer(it)
        }
    }

    /**
     * 將 Server 回傳的 Model 轉換成 app theme model
     */
    private fun serverColorTransfer(colors: List<Color>): FanciColor {
        val primary = colors.first {
            it.name == "Env_Theme"
        }.hexColorCode.orEmpty()

        val background = colors.first {
            it.name == "Env_Base"
        }.hexColorCode.orEmpty()

        val env_40 = colors.first {
            it.name == "Env_Env40"
        }.hexColorCode.orEmpty()

        val env_60 = colors.first {
            it.name == "Env_Env60"
        }.hexColorCode.orEmpty()

        val env_80 = colors.first {
            it.name == "Env_Env80"
        }.hexColorCode.orEmpty()

        val env_100 = colors.first {
            it.name == "Env_Env100"
        }.hexColorCode.orEmpty()

        val inputFrame = colors.first {
            it.name == "Env_InputField"
        }.hexColorCode.orEmpty()

        val tabUnSelect = colors.first {
            it.name == "Component_tabNotSelected"
        }.hexColorCode.orEmpty()

        val tabSelected = colors.first {
            it.name == "Component_tabIsSelected"
        }.hexColorCode.orEmpty()

        val other = colors.first {
            it.name == "Component_OtherDisplay"
        }.hexColorCode.orEmpty()

        val default_30 = colors.first {
            it.name == "Text_Display_DefaultDisplay30"
        }.hexColorCode.orEmpty()

        val default_50 = colors.first {
            it.name == "Text_Display_DefaultDisplay50"
        }.hexColorCode.orEmpty()

        val default_80 = colors.first {
            it.name == "Text_Display_DefaultDisplay80"
        }.hexColorCode.orEmpty()

        val default_100 = colors.first {
            it.name == "Text_Display_DefaultDisplay100"
        }.hexColorCode.orEmpty()

        val textOther = colors.first {
            it.name == "Text_Display_OtherDisplay"
        }.hexColorCode.orEmpty()

        val input_30 = colors.first {
            it.name == "Text_InputField_30"
        }.hexColorCode.orEmpty()

        val input_50 = colors.first {
            it.name == "Text_InputField_50"
        }.hexColorCode.orEmpty()

        val input_80 = colors.first {
            it.name == "Text_InputField_80"
        }.hexColorCode.orEmpty()

        val input_100 = colors.first {
            it.name == "Text_InputField_100"
        }.hexColorCode.orEmpty()

        return FanciColor(
            primary = androidx.compose.ui.graphics.Color(primary.toLong(16)),
            background = androidx.compose.ui.graphics.Color(background.toLong(16)),
            env_40 = androidx.compose.ui.graphics.Color(env_40.toLong(16)),
            env_60 = androidx.compose.ui.graphics.Color(env_60.toLong(16)),
            env_80 = androidx.compose.ui.graphics.Color(env_80.toLong(16)),
            env_100 = androidx.compose.ui.graphics.Color(env_100.toLong(16)),
            inputFrame = androidx.compose.ui.graphics.Color(inputFrame.toLong(16)),
            component = FanciComponentColor(
                tabUnSelect = androidx.compose.ui.graphics.Color(tabUnSelect.toLong(16)),
                tabSelected = androidx.compose.ui.graphics.Color(tabSelected.toLong(16)),
                other = androidx.compose.ui.graphics.Color(other.toLong(16))
            ),
            text = FanciTextColor(
                default_30 = androidx.compose.ui.graphics.Color(default_30.toLong(16)),
                default_50 = androidx.compose.ui.graphics.Color(default_50.toLong(16)),
                default_80 = androidx.compose.ui.graphics.Color(default_80.toLong(16)),
                default_100 = androidx.compose.ui.graphics.Color(default_100.toLong(16)),
                other = androidx.compose.ui.graphics.Color(textOther.toLong(16))
            ),
            inputText = FanciInputText(
                input_30 = androidx.compose.ui.graphics.Color(input_30.toLong(16)),
                input_50 = androidx.compose.ui.graphics.Color(input_50.toLong(16)),
                input_80 = androidx.compose.ui.graphics.Color(input_80.toLong(16)),
                input_100 = androidx.compose.ui.graphics.Color(input_100.toLong(16)),
            )
        )
    }

}