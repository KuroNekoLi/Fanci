package com.cmoney.fanci.ui.screens.tutorial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.ui.theme.FanciTheme

@Composable
fun TutorialItemScreen(modifier: Modifier = Modifier, page: Int) {
    Column(modifier = Modifier.background(MaterialTheme.colors.surface)) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = page.toString(),
            fontSize = 110.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(120.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "打開通知即時訊息不漏接",
            fontSize = 21.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "文字文字文字文字文字文字文字文字文字文字\n" +
                    "文字文字文字文字文字文字文字文字文字文字\n" +
                    "文字文字文字文字文字文字文字文字文字文字\n" +
                    "文字文字文字文字文字文字文字文字文字文字",
            fontSize = 16.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TutorialItemScreenPreview() {
    FanciTheme {
        TutorialItemScreen(page = 0)
    }
}