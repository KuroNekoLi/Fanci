package com.cmoney.kolfanci.ui.screens.group.setting.apply

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.ApplyStatus
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupRequirementApply
import com.cmoney.fanciapi.fanci.model.IGroupRequirementAnswer
import com.cmoney.fanciapi.fanci.model.User
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.asGroupMember
import com.cmoney.kolfanci.extension.showToast
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.screens.group.setting.apply.viewmodel.GroupApplyViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.apply.viewmodel.GroupRequirementApplySelected
import com.cmoney.kolfanci.ui.screens.shared.CenterTopAppBar
import com.cmoney.kolfanci.ui.screens.shared.CircleCheckedScreen
import com.cmoney.kolfanci.ui.screens.shared.member.HorizontalMemberItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 加入申請
 */
@Destination
@Composable
fun GroupApplyScreen(
    modifier: Modifier = Modifier,
    viewModel: GroupApplyViewModel = koinViewModel(),
    group: Group,
    resultBackNavigator: ResultBackNavigator<Boolean>
) {
    val TAG = "GroupApplyScreen"

    LaunchedEffect(key1 = group) {
        AppUserLogger.getInstance().log(Page.GroupSettingsJoinApplication)
    }

    val uiState = viewModel.uiState

    if (uiState.applyList == null) {
        viewModel.fetchApplyQuestion(
            groupId = group.id.orEmpty()
        )
    }

    GroupApplyScreenView(
        modifier = modifier,
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
            val isSelectAll = remember(groupRequirementApplyList) {
                groupRequirementApplyList.all { selected ->
                    selected.isSelected
                }
            }
            TopAppBar(
                isSelectAll = isSelectAll,
                backClick = {
                    onBackClick.invoke()
                },
                onSelectedAll = {
                    KLog.i(TAG, "onSelectedAll click.")
                    val clicked = if (isSelectAll) {
                        Clicked.JoinApplicationUnselectAll
                    } else {
                        Clicked.JoinApplicationSelectAll
                    }
                    AppUserLogger.getInstance()
                        .log(clicked)
                    onSelectAllClick.invoke()
                }
            )
        },
        backgroundColor = LocalColor.current.env_80
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (groupRequirementApplyList.isNotEmpty()) {
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
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.flower_box),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = LocalColor.current.text.default_30)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = stringResource(id = R.string.apply_is_empty),
                        fontSize = 16.sp,
                        color = LocalColor.current.text.default_30
                    )
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
    isSelectAll: Boolean,
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
                    .width(70.dp)
                    .offset(x = (-15).dp)
                    .clickable {
                        onSelectedAll?.invoke()
                    },
                contentAlignment = Alignment.CenterEnd
            ) {
                val text = if (isSelectAll) {
                    stringResource(id = R.string.unselect_all)
                } else {
                    stringResource(id = R.string.select_all)
                }
                Text(
                    text = text,
                    fontSize = 17.sp,
                    maxLines = 1,
                    color = LocalColor.current.primary
                )
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
            user?.let {
                HorizontalMemberItemScreen(
                    modifier = Modifier.weight(1f),
                    groupMember = it.asGroupMember()
                )
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
        val dayString = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(date)

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
                AppUserLogger.getInstance()
                    .log(Clicked.JoinApplicationRejectJoin)
                onReject.invoke()
            }


            Spacer(modifier = Modifier.width(24.dp))

            Button(
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = LocalColor.current.primary),
                onClick = {
                    AppUserLogger.getInstance()
                        .log(Clicked.JoinApplicationApproveJoin)
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