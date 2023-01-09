package com.cmoney.fanci.ui.screens.group.setting.group.openness

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cmoney.fanci.R
import com.cmoney.fanci.destinations.CreateApplyQuestionScreenDestination
import com.cmoney.fanci.ui.common.BlueButton
import com.cmoney.fanci.ui.common.BorderButton
import com.cmoney.fanci.ui.screens.group.setting.group.openness.viewmodel.GroupOpennessViewModel
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.screens.shared.dialog.EditDialogScreen
import com.cmoney.fanci.ui.screens.shared.setting.BottomButtonScreen
import com.cmoney.fanci.ui.theme.Color_29787880
import com.cmoney.fanci.ui.theme.Color_2B313C
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Destination
@Composable
fun GroupOpennessScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    viewModel: GroupOpennessViewModel = koinViewModel(
        parameters = {
            parametersOf(group)
        }
    ),
    resultRecipient: ResultRecipient<CreateApplyQuestionScreenDestination, String>,
    resultBackNavigator: ResultBackNavigator<Group>
) {
    val TAG = "GroupOpennessScreen"
    val uiState = viewModel.uiState
    val showDialog = remember { mutableStateOf(false) }
    val defaultEdit = Pair(false, "")
    val showEditDialog = remember { mutableStateOf(defaultEdit) }

    //不公開社團, 抓取問題清單
    group.isNeedApproval?.let {
        if (it && uiState.isFirstFetchQuestion) {
            viewModel.fetchGroupQuestion(group)
        }
    }

    //新增 題目 callback
    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                val question = result.value
                if (uiState.isEditMode) {
                    viewModel.editQuestion(
                        question = uiState.orgQuestion,
                        update = question
                    )
                } else {
                    viewModel.addQuestion(question = question)
                }
            }
        }
    }

    GroupOpennessScreenView(
        modifier = modifier,
        navigator = navigator,
        isNeedApproval = uiState.isNeedApproval,
        question = uiState.groupQuestionList.orEmpty(),
        onSwitch = {
            viewModel.onSwitchClick(it)
            showDialog.value = it
        },
        onAddQuestion = {
            navigator.navigate(
                CreateApplyQuestionScreenDestination()
            )
        },
        onEditClick = {
            showEditDialog.value = Pair(true, it)
        },
        onSave = {
            viewModel.onSave(groupId = group.id.orEmpty())
        }
    )

    //不公開 提示
    if (showDialog.value) {
        TipDialog(
            onAddTopic = {
                navigator.navigate(
                    CreateApplyQuestionScreenDestination()
                )
            },
            onDismiss = {
                showDialog.value = false
            }
        )
    }

    //編輯 彈窗
    if (showEditDialog.value.first) {
        EditDialogScreen(
            onDismiss = {
                KLog.i(TAG, "onDismiss click.")
                showEditDialog.value = Pair(false, "")
            },
            onEdit = {
                KLog.i(TAG, "onEdit click.")
                viewModel.openEditMode(showEditDialog.value.second)
                navigator.navigate(
                    CreateApplyQuestionScreenDestination(
                        question = showEditDialog.value.second
                    )
                )
                showEditDialog.value = Pair(false, "")
            },
            onRemove = {
                KLog.i(TAG, "onRemove click.")
                viewModel.removeQuestion(showEditDialog.value.second)
                showEditDialog.value = Pair(false, "")
            }
        )
    }

    //設定完成
    if (uiState.saveComplete) {
        resultBackNavigator.navigateBack(
            group.copy(
                isNeedApproval = uiState.isNeedApproval
            )
        )
    }

}

@Composable
fun GroupOpennessScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    isNeedApproval: Boolean,
    question: List<String> = emptyList(),
    onSwitch: (Boolean) -> Unit,
    onAddQuestion: () -> Unit,
    onEditClick: (String) -> Unit,
    onSave: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "社團公開度",
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    navigator.popBackStack()
                }
            )
        }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LocalColor.current.background)
                    .padding(top = 10.dp, bottom = 10.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = R.drawable.lock),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.component.other)
                )
                Spacer(modifier = Modifier.width(19.dp))

                Text(
                    modifier = Modifier.weight(1f),
                    text = "社團公開度", fontSize = 17.sp, color = LocalColor.current.text.default_100
                )

                val publicText = if (isNeedApproval) {
                    "不公開"
                } else {
                    "公開"
                }

                Text(
                    text = publicText,
                    fontSize = 17.sp,
                    color = LocalColor.current.specialColor.red
                )

                Spacer(modifier = Modifier.width(17.dp))

                Switch(
                    modifier = Modifier.size(51.dp, 31.dp),
                    checked = isNeedApproval,
                    onCheckedChange = {
                        onSwitch.invoke(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = LocalColor.current.primary,
                        checkedTrackAlpha = 1f,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color_29787880,
                        uncheckedTrackAlpha = 1f
                    ),
                )
            }

            Text(
                modifier = Modifier.padding(top = 20.dp, start = 16.dp),
                text = "*社團無論公開與否，都能被搜尋到*",
                fontSize = 14.sp,
                color = LocalColor.current.text.default_50
            )

            if (isNeedApproval) {
                Text(
                    modifier = Modifier.padding(top = 25.dp, start = 16.dp),
                    text = "審核問題",
                    fontSize = 14.sp,
                    color = LocalColor.current.text.default_50
                )

                Spacer(modifier = Modifier.height(15.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LocalColor.current.background)
                ) {
                    question.forEach {
                        QuestionItem(question = it, onClick = {
                            onEditClick.invoke(it)
                        })
                        Spacer(modifier = Modifier.height(1.dp))
                    }

                    BorderButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .background(LocalColor.current.background)
                            .padding(top = 8.dp, bottom = 8.dp, start = 24.dp, end = 24.dp),
                        text = "新增審核題目",
                        borderColor = LocalColor.current.text.default_50,
                        textColor = LocalColor.current.text.default_100
                    ) {
                        onAddQuestion.invoke()
                    }

                }
            }


            Spacer(modifier = Modifier.weight(1f))

            BottomButtonScreen(
                text = "儲存"
            ) {
                onSave.invoke()
            }
        }
    }
}

@Composable
fun TipDialog(
    onAddTopic: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss.invoke() }) {
        Surface(
            modifier = Modifier,
            shape = RoundedCornerShape(16.dp),
            color = Color_2B313C
        ) {
            Box(
                modifier = Modifier.padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.lock),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(9.dp))

                        Text(
                            text = "將此社團設為「不公開」社團",
                            fontSize = 19.sp,
                            color = LocalColor.current.text.default_100
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "不公開社團加入前需要經過審核。",
                        fontSize = 17.sp,
                        color = LocalColor.current.text.default_100
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    BlueButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        text = "新增審核題目",
                        onClick = {
                            onAddTopic.invoke()
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    BorderButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        text = "略過新增",
                        borderColor = LocalColor.current.text.default_50,
                        textColor = LocalColor.current.text.default_100
                    ) {
                        onDismiss.invoke()
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionItem(question: String, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalColor.current.background)
            .clickable {
                onClick.invoke(question)
            }
            .padding(top = 12.dp, bottom = 12.dp, start = 30.dp, end = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            modifier = Modifier.weight(1f),
            text = question, fontSize = 17.sp, color = LocalColor.current.text.default_100
        )

        Spacer(modifier = Modifier.width(20.dp))

        Text(text = "管理", fontSize = 14.sp, color = LocalColor.current.primary)

    }
}

@Preview(showBackground = true)
@Composable
fun GroupOpennessScreenPreview() {
    FanciTheme {
        GroupOpennessScreenView(
            navigator = EmptyDestinationsNavigator,
            isNeedApproval = true,
            question = listOf("1. 金針菇是哪國人？", "2. 金針菇第一隻破十萬觀看數的影片是哪一隻呢？（敘述即可不用寫出全名）"),
            onSwitch = {},
            onAddQuestion = {},
            onEditClick = {},
            onSave = {}
        )
    }
}