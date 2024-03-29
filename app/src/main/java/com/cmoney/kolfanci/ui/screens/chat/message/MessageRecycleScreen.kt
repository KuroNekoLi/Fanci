package com.cmoney.kolfanci.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.ui.theme.Black_181C23
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun MessageRecycleScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LocalColor.current.background)
            .padding(
                12.dp
            )

    ) {
        Text(
            text = "訊息已收回",
            style = TextStyle(
                fontSize = 17.sp,
                color = LocalColor.current.text.default_100
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MessageRecycleScreenPreview() {
    FanciTheme {
        MessageRecycleScreen(
            modifier = Modifier
                .clip(RoundedCornerShape(9.dp))
                .background(Black_181C23)
        )
    }
}