package com.cmoney.kolfanci.ui.screens.group.setting.apply

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.showToast
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.screens.group.setting.apply.viewmodel.GroupApplyViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.apply.viewmodel.GroupRequirementApplySelected
import com.cmoney.kolfanci.ui.screens.shared.CenterTopAppBar
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.*
import com.cmoney.kolfanci.ui.screens.shared.CircleCheckedScreen
import com.cmoney.kolfanci.ui.screens.shared.member.HorizontalMemberItemScreen
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * 加入申請
 */
@Destination
@Composable
fun GroupApplyScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    viewModel: GroupApplyViewModel = koinViewModel(),
    group: Group,
    resultBackNavigator: ResultBackNavigator<Boolean>
) {
    val TAG = "GroupApplyScreen"

    val uiState = viewModel.uiState

    if (uiState.applyList == null) {
        viewModel.fetchApplyQuestion(
            groupId = group.id.orEmpty()
        )
    }

    GroupApplyScreenView(
        modifier = modifier,
        navigator = navigator,
        groupRequirementApplyList = uiState.applyList.orEmpty(),
        loading = uiState.loading,
        onApplyClick = {
            KLog.i(TAG, "onApplyClick:$it")
            viewModel.onApplyItemClick(it)
        },
        onSelectAllClick = {
            KLog.i(TAG, "onSelectAllClick")
            viewModel.selectAllClick()
        },
        onReject = {
            viewModel.onApplyOrReject(
                groupId = group.id.orEmpty(),
                applyStatus = ApplyStatus.denied
            )
        },
        onApply = {
            viewModel.onApplyOrReject(
                groupId = group.id.orEmpty(),
                applyStatus = ApplyStatus.confirmed
            )
        },
        onBackClick = {
            resultBackNavigator.navigateBack(result = uiState.isComplete)
        }
    )

    if (uiState.tips?.isNotEmpty() == true) {
        LocalContext.current.showToast(uiState.tips)
        viewModel.dismissTips()
    }

    //通知前一頁,是否需要刷新
    BackHandler {
        resultBackNavigator.navigateBack(result = uiState.isComplete)
    }
}

@Composable
private fun GroupApplyScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    groupRequirementApplyList: List<GroupRequirementApplySelected>,
    loading: Boolean,
    onApplyClick: (GroupRequirementApplySelected) -> Unit,
    onSelectAllClick: () -> Unit,
    onReject: () -> Unit,
    onApply: () -> Unit,
    onBackClick: () -> Unit
) {
    val TAG = "GroupApplyScreenView"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopAppBar(
                backClick = {
                    onBackClick.invoke()
                },
                onSelectedAll = {
                    KLog.i(TAG, "onSelectedAll click.")
                    onSelectAllClick.invoke()
                }
            )
        },
        backgroundColor = LocalColor.current.env_80
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(groupRequirementApplyList) { question ->
                    ApplyQuestionItem(question) {
                        onApplyClick.invoke(it)
                    }
                }

                if (loading) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(size = 32.dp),
                                color = LocalColor.current.primary
                            )
                        }
                    }
                }
            }

            BottomButton(
                onReject = {
                    onReject.invoke()
                },
                onApply = {
                    onApply.invoke()
                }
            )
        }
    }
}

@Composable
private fun TopAppBar(
    backClick: (() -> Unit)? = null,
    onSelectedAll: (() -> Unit)? = null
) {
    CenterTopAppBar(
        leading = {
            IconButton(onClick = {
                backClick?.invoke()
            }) {
                Icon(
                    Icons.Filled.ArrowBack, null,
                    tint = Color.White
                )
            }
        },
        title = { Text("加入申請", fontSize = 17.sp, color = Color.White) },
        backgroundColor = LocalColor.current.env_100,
        contentColor = Color.White,
        trailing = {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .offset(x = (-15).dp)
                    .clickable {
                        onSelectedAll?.invoke()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "全選", fontSize = 17.sp, color = LocalColor.current.primary)
            }
        }
    )
}

@Composable
private fun ApplyQuestionItem(
    groupRequirementApplySelected: GroupRequirementApplySelected,
    onClick: (GroupRequirementApplySelected) -> Unit
) {
    val groupRequirementApply = groupRequirementApplySelected.groupRequirementApply
    val user = groupRequirementApply.user

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalColor.current.background)
            .clickable {
                onClick.invoke(groupRequirementApplySelected)
            }
            .padding(top = 20.dp, bottom = 20.dp, start = 24.dp, end = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            user?.apply {
                HorizontalMemberItemScreen(
                    modifier = Modifier.weight(1f),
                    groupMember = GroupMember(
                    id = this.id,
                    name = this.name,
                    serialNumber = this.serialNumber,
                    isVip = this.isVip
                ))
            }

            CircleCheckedScreen(
                isChecked = groupRequirementApplySelected.isSelected
            )
        }

        groupRequirementApply.answers?.forEach { answer ->
            Text(
                text = answer.question.orEmpty(),
                fontSize = 14.sp,
                color = LocalColor.current.text.default_50
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = answer.answer.orEmpty(),
                fontSize = 14.sp,
                color = LocalColor.current.text.default_100
            )

            Spacer(modifier = Modifier.height(15.dp))
        }

        val date =
            Date(groupRequirementApply.updateUnixTime?.times(1000) ?: System.currentTimeMillis())
        val dayString = SimpleDateFormat("yyyy/MM/dd").format(date)

        Row {
            Text(
                text = "申請日期：%s".format(dayString),
                fontSize = 12.sp,
                color = LocalColor.current.text.default_30
            )

            Spacer(modifier = Modifier.weight(1f))


            if (groupRequirementApplySelected.groupRequirementApply.wasKicked == true) {
                Text(
                    text = "此用戶曾經被踢出社團",
                    fontSize = 12.sp,
                    color = LocalColor.current.specialColor.red
                )
            }
        }
    }
}

@Composable
private fun BottomButton(
    onReject: () -> Unit,
    onApply: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .background(LocalColor.current.env_100)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row {
            BorderButton(
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp),
                text = "拒絕",
                borderColor = LocalColor.current.text.default_50,
                textColor = LocalColor.current.text.default_100
            ) {
                onReject.invoke()
            }


            Spacer(modifier = Modifier.width(24.dp))

            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = LocalColor.current.primary),
                onClick = {
                    onApply.invoke()
                }) {
                Text(
                    text = "批准加入",
                    color = LocalColor.current.text.other,
                    fontSize = 16.sp
                )
            }
        }
    }
}


@Preview
@Composable
fun GroupApplyScreenPreview() {
    FanciTheme {
        GroupApplyScreenView(
            navigator = EmptyDestinationsNavigator,
            groupRequirementApplyList = listOf(
                GroupRequirementApplySelected(
                    groupRequirementApply = GroupRequirementApply(
                        user = User(name = "Name", serialNumber = 123456),
                        answers = listOf(
                            IGroupRequirementAnswer(
                                question = "Q1：金針菇是哪國人？",
                                answer = "\uD83C\uDDF0\uD83C\uDDF7韓國"
                            ),
                            IGroupRequirementAnswer(
                                question = "Q2：金針菇第一隻破十萬觀看數的影片是哪一隻呢？（敘述即可不用寫出全名）",
                                answer = "金針菇快閃韓國，回家嚇到爸媽"
                            )
                        ),
                        wasKicked = true
                    ),
                    isSelected = true
                )
            ),
            onApplyClick = {
            },
            onSelectAllClick = {},
            onReject = {},
            onApply = {},
            onBackClick = {},
            loading = true
        )
    }
}