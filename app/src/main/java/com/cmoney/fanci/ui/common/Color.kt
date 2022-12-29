package com.cmoney.fanci.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.cmoney.fanci.extension.toColor
import com.cmoney.fanci.ui.theme.LocalColor

/**
 * 16進位文字 對應 角色 顏色
 */
@Composable
fun HexStringMapRoleColor(hexString: String): Color {
    return if (hexString.isNotEmpty()) {
        val findColor = LocalColor.current.roleColor.colors.firstOrNull {
            it.name == hexString
        }?.hexColorCode?.toColor()

        findColor ?: LocalColor.current.primary
    } else {
        LocalColor.current.primary
    }
}