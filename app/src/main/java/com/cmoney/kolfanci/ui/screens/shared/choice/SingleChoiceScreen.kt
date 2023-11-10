package com.cmoney.kolfanci.ui.screens.shared.choice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 單選題
 *
 * @param question 問題題目
 * @param choices 選項List
 * @param isCanChoice 是否可以勾選
 * @param onChoiceClick 點擊選項
 */
@Composable
fun SingleChoiceScreen(
    modifier: Modifier = Modifier,
    question: String,
    choices: List<Pair<String, Float>>,
    isCanChoice: Boolean,
    onChoiceClick: (Pair<String, Float>) -> Unit
) {

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LocalColor.current.background)
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = stringResource(id = R.string.single_choice),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = LocalColor.current.text.default_50
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = question,
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = LocalColor.current.text.default_100
                )
            )

            Spacer(modifier = Modifier.height(15.dp))

            choices.forEach { choice ->
                val question = choice.first
                val percentage = choice.second
                ChoiceItem(
                    question = question,
                    percentage = percentage,
                    isShowPercentage = !isCanChoice,
                    onChoiceClick = {
                        onChoiceClick.invoke(choice)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .clickable {

                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "查看結果",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = LocalColor.current.primary
                    )
                )

            }
        }
    }
}

@Composable
private fun ChoiceItem(
    question: String,
    percentage: Float,
    isShowPercentage: Boolean,
    onChoiceClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .clickable(!isShowPercentage) {
                onChoiceClick.invoke()
            },
        contentAlignment = Alignment.CenterStart
    ) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            color = LocalColor.current.primary,
            progress = if (isShowPercentage) percentage else 0f,
            strokeCap = StrokeCap.Round
        )

        Row(modifier = Modifier.padding(start = 15.dp, end = 15.dp)) {
            Text(
                text = question,
                // 主要內容/一般
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = LocalColor.current.text.default_100
                )
            )

            if (isShowPercentage) {
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "%d".format((percentage * 100).toInt()) + "%",
                    // 主要內容/一般
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = LocalColor.current.text.default_100
                    )
                )
            }
        }

    }
}


@Preview
@Composable
fun ChoiceItemPreview() {
    FanciTheme {
        ChoiceItem(
            question = "1.日本 🗼",
            percentage = 0.1f,
            isShowPercentage = true,
            onChoiceClick = {}
        )
    }
}

@Preview
@Composable
fun SingleChoiceScreenPreview() {
    FanciTheme {
        SingleChoiceScreen(
            modifier = Modifier.fillMaxSize(),
            question = "✈️ 投票決定我去哪裡玩！史丹利這次出國飛哪裡？",
            choices = listOf(
                "1.日本 🗼" to 0.1f,
                "2.紐約 🗽" to 0.25f,
                "3.夏威夷 🏖️" to 0.65f,
            ),
            isCanChoice = true,
            onChoiceClick = {}
        )
    }
}