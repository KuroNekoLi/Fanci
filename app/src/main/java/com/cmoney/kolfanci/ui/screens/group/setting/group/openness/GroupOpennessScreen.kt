package com.cmoney.kolfanci.ui.screens.group.setting.group.openness

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.cmoney.fanciapi.fanci.model.BanPeriodOption
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.destinations.CreateApplyQuestionScreenDestination
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.screens.group.setting.group.openness.viewmodel.GroupOpennessViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.AlertDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.EditDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.setting.BottomButtonScreen
import com.cmoney.kolfanci.ui.theme.*
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
    var showDeleteConfirmDialog by remember {
        mutableStateOf(false)
    }

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
            viewModel.onSave(group = group)
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
                showDeleteConfirmDialog = true
                // TODO:
//                viewModel.removeQuestion(showEditDialog.value.second)
//                showEditDialog.value = Pair(false, "")
            }
        )
    }

    //再次確認刪除
    if (showDeleteConfirmDialog) {
        AlertDialogScreen(
            onDismiss = {
                showDeleteConfirmDialog = false
            },
            title = "確定移除這則題目",
            content = {
                Column {
                    Column(
                        modifier = modifier
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "移除題目之前已答題的人不會受影響。", fontSize = 17.sp, color = Color.White
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        BorderButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            text = "確定刪除",
                            borderColor = LocalColor.current.component.other,
                            textColor = Color.White
                        ) {
                            kotlin.run {
                                showDeleteConfirmDialog = false
                                viewModel.removeQuestion(showEditDialog.value.second)
                                showEditDialog.value = Pair(false, "")
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        BorderButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            text = "返回",
                            borderColor = LocalColor.current.component.other,
                            textColor = Color.White
                        ) {
                            kotlin.run {
                                showDeleteConfirmDialog = false
                            }
                        }
                    }
                }
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
    ) { padding ->
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LocalColor.current.background)
                    .clickable {
                        onSwitch.invoke(false)
                    }
                    .padding(top = 10.dp, bottom = 10.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "完全公開",
                        fontSize = 17.sp,
                        color = LocalColor.current.text.default_100
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(text = "任何人都能看到社團，任何人都能進入。", fontSize = 14.sp, color = Color_80FFFFFF)
                }

                if (!isNeedApproval) {
                    Image(
                        painter = painterResource(id = R.drawable.checked),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(25.dp))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LocalColor.current.background)
                    .clickable {
                        onSwitch.invoke(true)
                    }
                    .padding(top = 10.dp, bottom = 10.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "不公開",
                        fontSize = 17.sp,
                        color = LocalColor.current.text.default_100
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(text = "任何人都能看到社團，需要回答問題才能進入。", fontSize = 14.sp, color = Color_80FFFFFF)
                }

                if (isNeedApproval) {
                    Image(
                        painter = painterResource(id = R.drawable.checked),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(25.dp))
                }
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