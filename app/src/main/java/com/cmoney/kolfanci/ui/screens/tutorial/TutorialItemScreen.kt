package com.cmoney.kolfanci.ui.screens.tutorial

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun TutorialItemScreen(
    modifier: Modifier = Modifier,
    tutorialItems: List<TutorialItem> = TutorialItemScreenDefaults.defaultItems,
    page: Int,
    onStart: () -> Unit
) {
    val (imageResource, title, desc) = tutorialItems.getOrNull(page) ?: return
    Column {
        Column(
            modifier = modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
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

        if (page == tutorialItems.lastIndex) {
            BlueButton(text = "開始使用 Fanci") {
                onStart.invoke()
            }
        }
    }
}

@Composable
fun BlueButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .padding(25.dp)
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = LocalColor.current.primary),
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

@Preview
@Composable
fun TutorialItemScreenPreview() {
    FanciTheme {
        TutorialItemScreen(
            page = 1,
            onStart = {}
        )
    }
}

/**
 * 新手導覽頁面資訊
 *
 * @property imageRes 圖片
 * @property title 標題
 * @property description 描述
 */
data class TutorialItem(
    @DrawableRes val imageRes: Int,
    val title: String,
    val description: String
)

object TutorialItemScreenDefaults {
    /**
     * 預設新手導覽頁面資訊集合
     */
    val defaultItems by lazy {
        listOf(
            TutorialItem(
                imageRes = R.drawable.tutorial1,
                title = "一手掌握當紅名人所有資訊",
                description = "你喜歡的偶像、網紅、KOL都在這！\n最新消息、周邊搶賣，加入社團再也不錯過"
            ),
            TutorialItem(
                imageRes = R.drawable.tutorial2,
                title = "跟同溫層一起聊天嘻嘻哈哈",
                description = "生活中沒有人可以跟你一起聊喜愛事物？\n懂你的朋友都在這，快來一起嘰哩呱啦！"
            )
        )
    }
}
