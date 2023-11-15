package com.cmoney.kolfanci.ui.screens.mcq.result

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.destinations.AnswererScreenDestination
import com.cmoney.kolfanci.ui.screens.shared.toolbar.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun AnswerResultScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator
) {
    AnswerResultScreenView(
        question = "✈️ 投票決定我去哪裡玩！史丹利這次出國飛哪裡？",
        choiceItem = listOf(
            "日本" to 10,
            "紐約" to 25,
            "夏威夷" to 65
        ),
        onBackClick = {
            navController.popBackStack()
        },
        onItemClick = {
            navController.navigate(AnswererScreenDestination)
        }
    )
}

/**
 *
 * @param onBackClick 返回callback
 * @param question 問題
 * @param choiceItem 題目清單, first -> 選項 title, second > 幾個人投
 */
@Composable
private fun AnswerResultScreenView(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    question: String,
    choiceItem: List<Pair<String, Int>>,
    onItemClick: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBarScreen(
                title = stringResource(id = R.string.answer_result),
                backClick = {
                    onBackClick.invoke()
                }
            )
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {
            Text(
                modifier = Modifier.padding(
                    top = 10.dp,
                    start = 22.dp,
                    end = 22.dp,
                    bottom = 10.dp
                ),
                text = stringResource(id = R.string.single_choice),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = LocalColor.current.text.default_50
                )
            )

            Text(
                modifier = Modifier.padding(
                    start = 22.dp,
                    end = 22.dp,
                    bottom = 10.dp
                ),
                text = question,
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = LocalColor.current.text.default_100
                )
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {

                items(choiceItem) { item ->

                    QuestionResultItem(
                        choice = item.first,
                        count = item.second,
                        onClick = {
                            onItemClick.invoke()
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(bottom = 44.dp)
                    .fillMaxWidth()
                    .height(46.dp)
                    .background(LocalColor.current.background)
                    .clickable {

                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "結束答題",
                    style = TextStyle(
                        fontSize = 17.sp,
                        lineHeight = 25.5.sp,
                        color = LocalColor.current.specialColor.red
                    )
                )
            }

        }
    }
}

/**
 * 答題 結果 item
 * @param choice 選項
 */
@Composable
private fun QuestionResultItem(
    modifier: Modifier = Modifier,
    choice: String,
    count: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .background(LocalColor.current.background)
            .clickable {
                onClick.invoke()
            }
            .padding(start = 24.dp, top = 10.dp, bottom = 10.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = choice,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = LocalColor.current.text.default_100
                )
            )

            Text(
                text = stringResource(id = R.string.answer_result_count).format(count),
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = LocalColor.current.text.default_50
                )
            )
        }

        Text(
            text = "查看投票者",
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 21.sp,
                color = LocalColor.current.primary
            )
        )

    }
}

@Preview
@Composable
fun QuestionResultItemPreview() {
    FanciTheme {
        QuestionResultItem(
            choice = "日本",
            count = 10,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnswerResultScreenPreview() {
    FanciTheme {
        AnswerResultScreenView(
            question = "✈️ 投票決定我去哪裡玩！史丹利這次出國飛哪裡？",
            choiceItem = listOf(
                "日本" to 10,
                "紐約" to 25,
                "夏威夷" to 65
            ),
            onBackClick = {
            },
            onItemClick = {}
        )
    }
}