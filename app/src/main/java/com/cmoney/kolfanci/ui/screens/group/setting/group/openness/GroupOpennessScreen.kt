package com.cmoney.kolfanci.ui.screens.group.setting.group.openness

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.globalGroupViewModel
import com.cmoney.kolfanci.extension.showToast
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.CreateApplyQuestionScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.group.openness.viewmodel.GroupOpennessViewModel
import com.cmoney.kolfanci.ui.screens.shared.toolbar.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.AlertDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.EditDialogScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
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
    resultRecipient: ResultRecipient<CreateApplyQuestionScreenDestination, String>
) {
    val TAG = "GroupOpennessScreen"
    val globalGroupViewModel = globalGroupViewModel()
    val uiState = viewModel.uiState
    val showDialog = remember { mutableStateOf(false) }
    val defaultEdit = Pair(false, "")
    val showEditDialog = remember { mutableStateOf(defaultEdit) }
    var showDeleteConfirmDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = group) {
        AppUserLogger.getInstance().log(Page.GroupSettingsGroupOpennessOpenness)
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
        isNeedApproval = uiState.isNeedApproval,
        questions = uiState.groupQuestionList.orEmpty(),
        onSwitch = {
            viewModel.onSwitchClick(it)
            showDialog.value = it
        },
        onAddQuestion = {
            navigator.navigate(
                CreateApplyQuestionScreenDestination(
                    keyinTracking = Clicked.QuestionTextArea.eventName,
                    from = From.GroupSettingsAddQuestion
                )
            )
            AppUserLogger.getInstance().log(Page.GroupSettingsGroupOpennessNonPublicReviewQuestionAddReviewQuestion)
        },
        onEditClick = {
            showEditDialog.value = Pair(true, it)
        },
        onBack = {
            group.id?.let { id ->
                viewModel.onSave(id = id)
            }
        }
    )

    //不公開 提示
    if (showDialog.value) {
        TipDialog(
            onAddTopic = {
                navigator.navigate(
                    CreateApplyQuestionScreenDestination(
                        keyinTracking = Clicked.QuestionTextArea.eventName,
                        from = From.GroupSettingsAddQuestion
                    )
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
                        question = showEditDialog.value.second,
                        keyinTracking = Clicked.QuestionTextArea.eventName,
                        from = From.GroupSettingsAddQuestion
                    )
                )
                AppUserLogger.getInstance().log(Clicked.GroupOpennessManageEdit)
                AppUserLogger.getInstance().log(Page.GroupSettingsGroupOpennessNonPublicReviewQuestionEdit)
                showEditDialog.value = Pair(false, "")
            },
            onRemove = {
                KLog.i(TAG, "onRemove click.")
                AppUserLogger.getInstance().log(Clicked.GroupOpennessManageRemove)
                showDeleteConfirmDialog = true
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
                            textColor = LocalColor.current.specialColor.red
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
                            showDeleteConfirmDialog = false
                        }
                    }
                }
            }
        )
    }

    val context = LocalContext.current
    uiState.saveComplete?.let { saveComplete ->
        if (saveComplete) {
            globalGroupViewModel.changeOpenness(openness = !uiState.isNeedApproval)
            navigator.popBackStack()
        } else {
            context.showToast(stringResource(id = R.string.save_failed))
        }
        viewModel.onSaveFinish()
    }
}

@Composable
fun GroupOpennessScreenView(
    modifier: Modifier = Modifier,
    isNeedApproval: Boolean,
    questions: List<String> = emptyList(),
    onSwitch: (Boolean) -> Unit,
    onAddQuestion: () -> Unit,
    onEditClick: (String) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = stringResource(id = R.string.group_openness),
                backClick = onBack
            )
        },
        backgroundColor = LocalColor.current.env_80
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            OpennessOptionItem(
                title = stringResource(id = R.string.full_public),
                description = stringResource(id = R.string.full_public_group_desc),
                selected = !isNeedApproval,
                onSwitch = {
                    AppUserLogger.getInstance()
                        .log(Clicked.GroupPermissionsOpenness, From.Public)
                    onSwitch(false)
                }
            )

            Spacer(modifier = Modifier.height(1.dp))

            OpennessOptionItem(
                title = stringResource(id = R.string.not_public),
                description = stringResource(id = R.string.not_public_group_desc),
                selected = isNeedApproval,
                onSwitch = {
                    AppUserLogger.getInstance()
                        .log(Clicked.GroupPermissionsOpenness, From.NonPublic)
                    onSwitch(true)
                }
            )

            if (isNeedApproval) {

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp, start = 25.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.review_question),
                        fontSize = 14.sp,
                        color = LocalColor.current.text.default_100
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(horizontal = 24.dp),
                    text = stringResource(id = R.string.add_review_question),
                    borderColor = LocalColor.current.text.default_50,
                    textColor = LocalColor.current.text.default_100
                ) {
                    AppUserLogger.getInstance()
                        .log(Clicked.GroupOpennessAddReviewQuestion)
                    onAddQuestion.invoke()
                }

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(questions) { question ->
                        QuestionItem(
                            question = question,
                            onClick = {
                                AppUserLogger.getInstance()
                                    .log(Clicked.GroupOpennessManage)
                                onEditClick.invoke(question)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OpennessOptionItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    selected: Boolean,
    onSwitch: () -> Unit
) {
    val fanciColor = LocalColor.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(fanciColor.background)
            .clickable {
                onSwitch.invoke()
            }
            .padding(horizontal = 24.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 17.sp,
                color = if (selected) {
                    fanciColor.primary
                } else {
                    fanciColor.text.default_100
                }
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = fanciColor.text.default_50
            )
        }

        if (selected) {
            Image(
                painter = painterResource(id = R.drawable.checked),
                contentDescription = null,
                colorFilter = ColorFilter.tint(fanciColor.primary)
            )
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
            color = LocalColor.current.env_80
        ) {
            Box(
                modifier = Modifier.padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.lock),
                            colorFilter = ColorFilter.tint(LocalColor.current.primary),
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
                            AppUserLogger.getInstance()
                                .log(Clicked.GroupOpennessAddQuestion)
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
                        AppUserLogger.getInstance()
                            .log(Clicked.GroupOpennessSkipForNow)
                        onDismiss.invoke()
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionItem(question: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalColor.current.background)
            .clickable(onClick = onClick)
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
            isNeedApproval = true,
            questions = listOf("1. 金針菇是哪國人？", "2. 金針菇第一隻破十萬觀看數的影片是哪一隻呢？（敘述即可不用寫出全名）"),
            onSwitch = {},
            onAddQuestion = {},
            onEditClick = {}
        ) {}
    }
}