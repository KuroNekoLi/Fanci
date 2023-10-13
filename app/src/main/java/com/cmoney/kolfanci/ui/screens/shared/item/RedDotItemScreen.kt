package com.cmoney.kolfanci.ui.screens.shared.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.ui.theme.FanciTheme

/**
 * 未讀 小紅點
 *
 * @param unreadCount 未讀數量
 */
@Composable
fun RedDotItemScreen(
    modifier: Modifier = Modifier,
    unreadCount: Long
) {
    if (unreadCount > 0) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(47.dp))
                .background(Color.Red)
                .padding(start = 7.dp, end = 7.dp, top = 2.dp, bottom = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            val text = if (unreadCount > 99) {
                "99+"
            } else {
                unreadCount.toString()
            }

            Text(
                text = text, fontSize = 12.sp, color = Color.White, maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun RedDotItemScreenPreview() {
    FanciTheme {
        RedDotItemScreen(
            unreadCount = 100
        )
    }
}