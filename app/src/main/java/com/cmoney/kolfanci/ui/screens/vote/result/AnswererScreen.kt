package com.cmoney.kolfanci.ui.screens.vote.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.IVotingOptionStatisticWithVoter
import com.cmoney.fanciapi.fanci.model.IVotingOptionStatisticsWithVoter
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.globalGroupViewModel
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.screens.shared.member.MemberItemScreen
import com.cmoney.kolfanci.ui.screens.shared.toolbar.TopBarScreen
import com.cmoney.kolfanci.ui.screens.vote.viewmodel.VoteViewModel
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

/**
 * 選擇題 - 答題者 清單
 *
 * @param iVotingOptionStatisticWithVoter 投票選項 model
 */
@Destination
@Composable
fun AnswererScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    iVotingOptionStatisticWithVoter: IVotingOptionStatisticWithVoter,
    viewModel: VoteViewModel = koinViewModel()
) {
    val currentGroup by globalGroupViewModel().currentGroup.collectAsState()

    val groupMember by viewModel.voterGroupMember.collectAsState()

    AnswererScreenView(
        modifier = modifier,
        questionItem = iVotingOptionStatisticWithVoter.text.orEmpty(),
        members = groupMember,
        onBackClick = {
            navController.popBackStack()
        }
    )

    LaunchedEffect(key1 = currentGroup) {
        currentGroup?.let {
            viewModel.getChoiceGroupMember(
                groupId = it.id.orEmpty(),
                voterIds = iVotingOptionStatisticWithVoter.voterIds.orEmpty()
            )
        }
    }

}

@Composable
private fun AnswererScreenView(
    modifier: Modifier = Modifier,
    questionItem: String,
    members: List<GroupMember>,
    onBackClick: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBarScreen(
                title = stringResource(id = R.string.answerer),
                leadingIcon = Icons.Filled.Close,
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
                text = questionItem,
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = LocalColor.current.text.default_100
                )
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(members) { member ->
                    MemberItemScreen(
                        groupMember = member,
                        isShowRemove = false
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnswererScreenPreview() {
    FanciTheme {
        AnswererScreenView(
            questionItem = "日本 \uD83D\uDDFC （10票）",
            members = listOf(
                MockData.mockGroupMember,
                MockData.mockGroupMember,
                MockData.mockGroupMember,
                MockData.mockGroupMember,
                MockData.mockGroupMember
            ),
            onBackClick = {}
        )
    }
}