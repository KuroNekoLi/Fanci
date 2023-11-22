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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.IVotingOptionStatistics
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 單選題
 *
 * @param question 問題題目
 * @param choices 選項List
 * @param isShowResultText 是否呈現 查看結果 按鈕
 * @param onChoiceClick 點擊投票 IVotingOptionStatistics id
 * @param onResultClick 點擊查看結果
 */
@Composable
fun SingleChoiceScreen(
    modifier: Modifier = Modifier,
    question: String,
    choices: List<IVotingOptionStatistics>,
    isShowResultText: Boolean,
    onChoiceClick: (IVotingOptionStatistics) -> Unit,
    onResultClick: (() -> Unit)? = null
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

            choices.forEachIndexed { index, choiceItem ->
                ChoiceItem(
                    question = choiceItem.text.orEmpty(),
                    onChoiceClick = {
                        onChoiceClick.invoke(choiceItem)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            if (isShowResultText) {
                Box(
                    modifier = Modifier
                        .height(45.dp)
                        .fillMaxWidth()
                        .clickable {
                            onResultClick?.invoke()
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
}

/**
 * 選項 item
 */
@Composable
private fun ChoiceItem(
    question: String,
    onChoiceClick: () -> Unit
) {
    val localDensity = LocalDensity.current

    //內文選項高度
    var textHeight by remember {
        mutableStateOf(0.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(textHeight.coerceAtLeast(40.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(LocalColor.current.background)
            .clickable {
                onChoiceClick.invoke()
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, top = 9.dp, bottom = 9.dp)
                .onGloballyPositioned { coordinates ->
                    textHeight = with(localDensity) { coordinates.size.height.toDp() }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
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

@Preview
@Composable
fun ChoiceItemPreview() {
    FanciTheme {
        ChoiceItem(
            question = "1.日本 🗼",
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
                IVotingOptionStatistics(text = "1.日本 🗼"),
                IVotingOptionStatistics(text = "2.紐約 🗽"),
                IVotingOptionStatistics(text = "3.夏威夷 🏖️")
            ),
            onChoiceClick = {},
            isShowResultText = true
        )
    }
}