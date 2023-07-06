package com.cmoney.kolfanci.ui.screens.shared.vip

import androidx.compose.foundation.Image
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R

/**
 * 取得 VIP 方案鑽石在文字中顯示的設定
 *
 * @param placeholder 預留給 inlineTextContent 顯示的空間
 * @return
 */
fun getVipDiamondInlineContent(
    placeholder: Placeholder = Placeholder(
        width = 17.sp,
        height = 17.sp,
        placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
    )
): InlineTextContent {
    return InlineTextContent(
        placeholder = placeholder,
        children = {
            Image(
                painter = painterResource(id = R.drawable.vip_diamond),
                contentDescription = "vip icon"
            )
        }
    )
}