package com.cmoney.kolfanci.ui.screens.shared.choice

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanciapi.fanci.model.IVotingOptionStatistic
import com.cmoney.fanciapi.fanci.model.Voting
import com.cmoney.kolfanci.extension.isVoted
import com.cmoney.kolfanci.extension.toPercentageList
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.destinations.AnswerResultScreenDestination
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

/**
 * 選擇題 呈現畫面, 單選題 or 多選題 or 已經投過票
 *
 * @param channelId 頻道 id
 * @param votings 選擇題
 * @param isMyPost 是否為自己的發文
 * @param onVotingClick 點擊投票
 */
@Composable
fun ChoiceScreen(
    channelId: String,
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    votings: List<Voting>,
    isMyPost: Boolean,
    onVotingClick: (Voting, List<IVotingOptionStatistic>) -> Unit
) {
    votings.forEach { voting ->

        var showVoteResult by remember { mutableStateOf(false) }

        Spacer(modifier = Modifier.height(10.dp))

        //已經 投過票
        if (showVoteResult || voting.isVoted() || isMyPost || (voting.isEnded == true)) {
            ChoiceResultScreen(
                modifier = modifier,
                question = voting.title.orEmpty(),
                choices = voting.votingOptionStatistics?.toPercentageList() ?: emptyList(),
                isShowResultText = isMyPost,
                onResultClick = {
                    navController.navigate(
                        AnswerResultScreenDestination(
                            channelId = channelId,
                            voting = voting
                        )
                    )
                }
            )
        } else {
            //多選題
            if (voting.isMultipleChoice == true) {
                MultiChoiceScreen(
                    modifier = modifier,
                    question = voting.title.orEmpty(),
                    choices = voting.votingOptionStatistics.orEmpty(),
                    isShowResultText = isMyPost,
                    onConfirm = {
                        showVoteResult = true
                        onVotingClick.invoke(voting, it)
                    },
                    onResultClick = {
                        navController.navigate(
                            AnswerResultScreenDestination(
                                channelId = channelId,
                                voting = voting
                            )
                        )
                    }
                )
            } else {
                //單選題
                SingleChoiceScreen(
                    modifier = modifier,
                    question = voting.title.orEmpty(),
                    choices = voting.votingOptionStatistics.orEmpty(),
                    onChoiceClick = {
                        showVoteResult = true
                        onVotingClick.invoke(voting, listOf(it))
                    },
                    isShowResultText = isMyPost,
                    onResultClick = {
                        navController.navigate(
                            AnswerResultScreenDestination(
                                channelId = channelId,
                                voting = voting
                            )
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChoiceScreenPreview() {
    FanciTheme {
        ChoiceScreen(
            channelId = "",
            navController = EmptyDestinationsNavigator,
            votings = listOf(MockData.mockSingleVoting),
            isMyPost = true,
            onVotingClick = { _, _ -> }
        )
    }
}