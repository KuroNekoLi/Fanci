package com.cmoney.kolfanci.ui.screens.tutorial

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.kolfanci.ui.theme.Blue_4F70E5
import com.cmoney.kolfanci.R

@Composable
fun TutorialItemScreen(
    modifier: Modifier = Modifier,
    page: Int,
    isFinalPage: Boolean,
    onStart: () -> Unit
) {
    val imageResource = when (page) {
        0 -> {
            R.drawable.tutorial1
        }
        1 -> {
            R.drawable.tutorial2
        }
        else -> {
            R.drawable.tutorial3
        }
    }

    val title = when (page) {
        0 -> {
            "一手掌握當紅名人所有資訊"
        }
        1 -> {
            "跟同溫層一起聊天嘻嘻哈哈"
        }
        else -> {
            "打開通知即時訊息不漏接"
        }
    }

    val desc = when (page) {
        0 -> {
            "你喜歡的偶像、網紅、KOL都在這！\n" +
                    "最新消息、周邊搶賣，加入社團再也不錯過"
        }
        1 -> {
            "生活中沒有人可以跟你一起聊喜愛事物？\n" +
                    "懂你的朋友都在這，快來一起嘰哩呱啦！"
        }
        else -> {
            "Fanci 能讓你知道最即時的消息！\n" +
                    "所以...打開通知是你最棒的選擇"
        }
    }

    Column {
        Column(modifier = modifier
            .weight(1f)
            .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(375.dp)
                    .padding(top = 30.dp, bottom = 30.dp),
                model = imageResource,
                contentScale = ContentScale.Fit,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.placeholder)
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                fontSize = 21.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = desc,
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }

        if (isFinalPage) {
            BlueButton(text = "開始使用 Fanci") {
                onStart.invoke()
            }
        }
    }
}

@Composable
fun BlueButton(
    modifier: Modifier = Modifier
        .padding(25.dp)
        .fillMaxWidth()
        .height(50.dp),
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(backgroundColor = Blue_4F70E5),
        onClick = {
            onClick.invoke()
        }) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TutorialItemScreenPreview() {
    TutorialItemScreen(
        page = 1,
        isFinalPage = true,
        onStart = {})
}