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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Voting
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.toPercentageList
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 選擇題 結果
 *
 * @param isShowResultText 是否呈現 查看結果 按鈕
 * @param onResultClick 點擊查看結果
 */
@Composable
fun ChoiceResultScreen(
    modifier: Modifier = Modifier,
    voting: Voting,
    isShowResultText: Boolean,
    onResultClick: (() -> Unit)? = null
) {
    val question = voting.title.orEmpty()
    val choices = voting.votingOptionStatistics?.toPercentageList() ?: emptyList()
    val title = if (voting.isEnded == true) {
        stringResource(id = R.string.voting_end)
    } else if (voting.isMultipleChoice == true) {
        stringResource(id = R.string.multi_choice)
    } else {
        stringResource(id = R.string.single_choice)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LocalColor.current.background)
            .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 10.dp)
    ) {
        Column {
            Text(
                text = title,
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

            choices.forEach { choiceItem ->
                val question = choiceItem.first
                val percentage = choiceItem.second

                ChoiceResultItem(
                    question = question,
                    percentage = percentage,
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
 * 選項結果 item
 */
@Composable
private fun ChoiceResultItem(
    question: String,
    percentage: Float
) {
    val localDensity = LocalDensity.current

    //內文選項高度
    var textHeight by remember {
        mutableStateOf(0.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(textHeight.coerceAtLeast(40.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxSize()
                .height(textHeight),
            color = LocalColor.current.primary,
            progress = percentage,
            strokeCap = StrokeCap.Round
        )

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

@Preview(showBackground = true)
@Composable
fun ChoiceResultScreenPreview() {
    FanciTheme {
        ChoiceResultScreen(
            voting = MockData.mockSingleVoting,
            isShowResultText = true,
            onResultClick = {}
        )
    }
}