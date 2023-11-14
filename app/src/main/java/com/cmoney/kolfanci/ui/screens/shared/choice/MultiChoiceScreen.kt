package com.cmoney.kolfanci.ui.screens.shared.choice

import androidx.compose.foundation.Image
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 多選題
 *
 * @param question 問題題目
 * @param choices 選項List, first -> question , second -> isChecked
 * @param onChoiceClick 點擊選項
 */
@Composable
fun MultiChoiceScreen(
    modifier: Modifier = Modifier,
    question: String,
    choices: List<Pair<String, Boolean>>,
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
                text = stringResource(id = R.string.multi_choice),
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
                val isChecked = choice.second

                CheckBoxChoiceItem(
                    question = question,
                    isChecked = isChecked,
                    onChoiceClick = {

                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            BlueButton(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                text = stringResource(id = R.string.confirm)
            ) {

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

/**
 * 帶有 checkbox 題目選單
 *
 * @param question 題目
 * @param isChecked 是否打勾
 *
 */
@Composable
private fun CheckBoxChoiceItem(
    question: String,
    isChecked: Boolean,
    onChoiceClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(LocalColor.current.background)
            .clickable {
                onChoiceClick.invoke()
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(start = 15.dp, end = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = question,
                // 主要內容/一般
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = LocalColor.current.text.default_100
                )
            )

            Image(
                painter = if (isChecked) {
                    painterResource(id = R.drawable.circle_checked)
                } else {
                    painterResource(id = R.drawable.circle_unchecked)
                },
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
private fun CheckBoxChoiceItemPreview() {
    FanciTheme {
        CheckBoxChoiceItem(
            question = "✈️ 投票決定我去哪裡玩！史丹利這次出國飛哪裡？",
            isChecked = true,
            onChoiceClick = {
            }
        )
    }
}


@Preview
@Composable
fun MultipleChoiceScreenPreview() {
    FanciTheme {
        MultiChoiceScreen(
            modifier = Modifier.fillMaxSize(),
            question = "✈️ 投票決定我去哪裡玩！史丹利這次出國飛哪裡？",
            choices = listOf(
                "1.日本 🗼" to true,
                "2.紐約 🗽" to false,
                "3.夏威夷 🏖️" to true,
            ),
            onChoiceClick = {}
        )
    }
}