package com.cmoney.fanci.model.usecase

import com.cmoney.fanci.extension.checkResponseBody
import com.cmoney.fanci.ui.screens.group.setting.groupsetting.theme.model.GroupTheme
import com.cmoney.fanci.ui.theme.*
import com.cmoney.fanciapi.fanci.api.GroupApi
import com.cmoney.fanciapi.fanci.api.ThemeColorApi
import com.cmoney.fanciapi.fanci.model.*
import com.socks.library.KLog

class ThemeUseCase(private val themeColorApi: ThemeColorApi, val groupApi: GroupApi) {
    private val TAG = ThemeUseCase::class.java.simpleName

    /**
     * 更換 社團主題顏色
     */
    suspend fun changeGroupTheme(group: Group, groupTheme: GroupTheme) = kotlin.runCatching {
        groupApi.apiV1GroupGroupIdPut(
            group.id.orEmpty(),
            EditGroupParam(
                name = group.name.orEmpty(),
                description = group.description.orEmpty(),
                coverImageUrl = group.coverImageUrl.orEmpty(),
                thumbnailImageUrl = group.thumbnailImageUrl.orEmpty(),
                colorSchemeGroupKey = ColorTheme.decode(groupTheme.id)
            )
        ).checkResponseBody()
    }

    /**
     * 抓取 所有 主題設定檔
     */
    suspend fun fetchAllThemeConfig() = kotlin.runCatching {
        KLog.i(TAG, "fetchAllThemeConfig")
        themeColorApi.apiV1ThemeColorGet().checkResponseBody().toList().map {
            val id = it.first
            val theme = serverColorTransfer(it.second)
            val name = it.second.displayName
            val preview = it.second.previewImage
            GroupTheme(
                id = id,
                isSelected = false,
                theme = theme,
                name = name.orEmpty(),
                preview = preview.orEmpty()
            )
        }
    }

    /**
     * 抓取 特定 主題設定檔
     */
    suspend fun fetchThemeConfig(colorTheme: ColorTheme) = kotlin.runCatching {
        KLog.i(TAG, "fetchThemeConfig")
        themeColorApi.apiV1ThemeColorColorThemeGet(colorTheme).checkResponseBody().let {
            GroupTheme(
                id = colorTheme.value,
                isSelected = false,
                theme = serverColorTransfer(it),
                name = it.displayName.orEmpty(),
                preview = it.previewImage.orEmpty()
            )
        }
    }

    /**
     * 將 Server 回傳的 Model 轉換成 app theme model
     */
    private fun serverColorTransfer(colors: Theme): FanciColor {
        val colors = colors.categoryColors?.toList()?.map {
            it.second
        }?.flatten().orEmpty()


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

        val blue = colors.first {
            it.name == "SpecialColor_Blue"
        }.hexColorCode.orEmpty()

        val blueGreen = colors.first {
            it.name == "SpecialColor_BlueGreen"
        }.hexColorCode.orEmpty()

        val green = colors.first {
            it.name == "SpecialColor_Green"
        }.hexColorCode.orEmpty()

        val hintRed = colors.first {
            it.name == "SpecialColor_Hint_Red"
        }.hexColorCode.orEmpty()

        val orange = colors.first {
            it.name == "SpecialColor_Orange"
        }.hexColorCode.orEmpty()

        val pink = colors.first {
            it.name == "SpecialColor_Pink"
        }.hexColorCode.orEmpty()

        val purple = colors.first {
            it.name == "SpecialColor_Purple"
        }.hexColorCode.orEmpty()

        val red = colors.first {
            it.name == "SpecialColor_Red"
        }.hexColorCode.orEmpty()

        val yellow = colors.first {
            it.name == "SpecialColor_Yellow"
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
            ),
            specialColor = SpecialColor(
                blue = androidx.compose.ui.graphics.Color(blue.toLong(16)),
                blueGreen = androidx.compose.ui.graphics.Color(blueGreen.toLong(16)),
                green = androidx.compose.ui.graphics.Color(green.toLong(16)),
                hintRed = androidx.compose.ui.graphics.Color(hintRed.toLong(16)),
                orange = androidx.compose.ui.graphics.Color(orange.toLong(16)),
                pink = androidx.compose.ui.graphics.Color(pink.toLong(16)),
                purple = androidx.compose.ui.graphics.Color(purple.toLong(16)),
                red = androidx.compose.ui.graphics.Color(red.toLong(16)),
                yellow = androidx.compose.ui.graphics.Color(yellow.toLong(16))
            )
        )
    }

}