package com.cmoney.fanci.ui.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * 呈現 色卡資訊
 */
@Composable
fun ThemeColorCardScreen(
    modifier: Modifier = Modifier,
    primary: Color,
    env_100: Color,
    env_80: Color,
    env_60: Color
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(primary)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(env_100)
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(env_80)
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .background(env_60)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeColorCardScreenPreview() {
    ThemeColorCardScreen(
        primary = Color.Red,
        env_100 = Color.Blue,
        env_80 = Color.Gray,
        env_60 = Color.Cyan
    )
}