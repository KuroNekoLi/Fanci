package com.cmoney.kolfanci.ui.screens.group.create

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.globalGroupViewModel
import com.cmoney.kolfanci.extension.showToast
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.destinations.CreateApplyQuestionScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupSettingAvatarScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupSettingBackgroundScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupSettingLogoScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupSettingThemeScreenDestination
import com.cmoney.kolfanci.ui.destinations.MainScreenDestination
import com.cmoney.kolfanci.ui.screens.group.create.viewmodel.CreateGroupViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.avatar.ImageChangeData
import com.cmoney.kolfanci.ui.screens.group.setting.group.openness.TipDialog
import com.cmoney.kolfanci.ui.screens.group.setting.group.openness.viewmodel.GroupOpennessViewModel
import com.cmoney.kolfanci.ui.screens.shared.dialog.EditDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.SaveConfirmDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.toolbar.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciColor
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * 建立社團
 */
@Destination
@Composable
fun CreateGroupScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: CreateGroupViewModel = koinViewModel(),
    groupOpennessViewModel: GroupOpennessViewModel = koinViewModel(
        parameters = {
            parametersOf(Group())
        }
    ),
    resultRecipient: ResultRecipient<CreateApplyQuestionScreenDestination, String>,
    setAvatarResult: ResultRecipient<GroupSettingAvatarScreenDestination, ImageChangeData>,
    setLogoResult: ResultRecipient<GroupSettingLogoScreenDestination, ImageChangeData>,
    setBackgroundResult: ResultRecipient<GroupSettingBackgroundScreenDestination, ImageChangeData>,
    setThemeResult: ResultRecipient<GroupSettingThemeScreenDestination, String>
) {
    val TAG = "CreateGroupScreen"
    val globalGroupViewModel = globalGroupViewModel()
    val approvalUiState = groupOpennessViewModel.uiState
    val uiState = viewModel.uiState
    val showDialog = remember { mutableStateOf(false) }
    val defaultEdit = Pair(false, "")
    val showEditDialog = remember { mutableStateOf(defaultEdit) }
    var showSaveTip by remember {
        mutableStateOf(false)
    }

    val currentStep by viewModel.currentStep.collectAsState()
    val settingGroup by viewModel.group.collectAsState()
    val fanciColor by viewModel.fanciColor.collectAsState()
    val isShowLoading by viewModel.loading.collectAsState()

    CreateGroupScreenView(
        navController = navigator,
        modifier = modifier,
        settingGroup = settingGroup,
        fanciColor = fanciColor,
        currentStep = currentStep,
        approvalUiState = approvalUiState,
        isShowLoading = isShowLoading,
        question = approvalUiState.groupQuestionList.orEmpty(),
        onGroupName = {
            KLog.i(TAG, "onGroupName:$it")
            KLog.i(TAG, "step1 next click.")
            AppUserLogger.getInstance().log(Clicked.CreateGroupNextStep, From.GroupName)
            viewModel.setGroupName(it)
            viewModel.nextStep()
        },
        onSwitchApprove = {
            groupOpennessViewModel.onSwitchClick(it)
            showDialog.value = it
        },
        onAddQuestion = {
            navigator.navigate(
                CreateApplyQuestionScreenDestination(
                    keyinTracking = Clicked.CreateGroupQuestionKeyin.eventName,
                    from = From.CreateGroupAddQuestion
                )
            )
        },
        onEditClick = {
            showEditDialog.value = Pair(true, it)
        },
        onNextStep = {
            when (currentStep) {
                2 -> {
                    KLog.i(TAG, "step2 next click.")
                    AppUserLogger.getInstance().log(Clicked.CreateGroupNextStep, From.GroupOpenness)
                }

                else -> {
                    KLog.i(TAG, "step3 next click.")
                    AppUserLogger.getInstance()
                        .log(Clicked.CreateGroupNextStep, From.GroupArrangement)
                }
            }

            if (currentStep == viewModel.finalStep) {
                viewModel.createGroup(
                    isNeedApproval = groupOpennessViewModel.uiState.isNeedApproval
                )
            } else {
                viewModel.nextStep()
            }
        },
        onPreStep = {
            when (currentStep) {
                2 -> {
                    KLog.i(TAG, "step2 back click.")
                    AppUserLogger.getInstance().log(Clicked.CreateGroupBackward, From.GroupOpenness)
                }

                else -> {
                    KLog.i(TAG, "step3 back click.")
                    AppUserLogger.getInstance()
                        .log(Clicked.CreateGroupBackward, From.GroupArrangement)
                }
            }

            viewModel.preStep()
        }
    ) {
        showSaveTip = true
    }

    SaveConfirmDialogScreen(
        isShow = showSaveTip,
        onContinue = {
            showSaveTip = false
        },
        onGiveUp = {
            showSaveTip = false
            navigator.popBackStack()
        }
    )

    //不公開 提示
    if (showDialog.value) {
        TipDialog(
            onAddTopic = {
                AppUserLogger.getInstance()
                    .log(Clicked.CreateGroupAddQuestionPopup, From.AddQuestion)
                navigator.navigate(
                    CreateApplyQuestionScreenDestination(
                        keyinTracking = Clicked.CreateGroupQuestionKeyin.eventName,
                        from = From.CreateGroupAddQuestion
                    )
                )
            },
            onDismiss = {
                AppUserLogger.getInstance().log(Clicked.CreateGroupAddQuestionPopup, From.Skip)
                showDialog.value = false
            }
        )
    }

    //編輯 問提 彈窗
    if (showEditDialog.value.first) {
        EditDialogScreen(
            onDismiss = {
                KLog.i(TAG, "onDismiss click.")
                showEditDialog.value = Pair(false, "")
            },
            onEdit = {
                KLog.i(TAG, "onEdit click.")
                AppUserLogger.getInstance().log(Clicked.CreateGroupQuestionEdit)

                groupOpennessViewModel.openEditMode(showEditDialog.value.second)
                navigator.navigate(
                    CreateApplyQuestionScreenDestination(
                        question = showEditDialog.value.second,
                        keyinTracking = Clicked.CreateGroupQuestionKeyin.eventName,
                        from = From.CreateGroupAddQuestion
                    )
                )
                showEditDialog.value = Pair(false, "")
            },
            onRemove = {
                KLog.i(TAG, "onRemove click.")
                AppUserLogger.getInstance().log(Clicked.CreateGroupQuestionRemove)

                groupOpennessViewModel.removeQuestion(showEditDialog.value.second)
                showEditDialog.value = Pair(false, "")
            }
        )
    }

    //錯誤提示
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.warningText.collect { warningText ->
            if (warningText.isNotEmpty()) {
                context.showToast(warningText)
            }
        }
    }

    //新增 題目 callback
    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val question = result.value
                if (approvalUiState.isEditMode) {
                    groupOpennessViewModel.editQuestion(
                        question = approvalUiState.orgQuestion,
                        update = question
                    )
                } else {
                    groupOpennessViewModel.addQuestion(question = question)
                }
            }
        }
    }
    //更改Logo
    setLogoResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val uri = result.value
                viewModel.changeGroupLogo(uri)
            }
        }
    }
    //更改頭貼
    setAvatarResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val uri = result.value
                viewModel.changeGroupAvatar(uri)
            }
        }
    }

    //更改背景
    setBackgroundResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val uri = result.value
                viewModel.changeGroupCover(uri)
            }
        }
    }

    //主題色
    setThemeResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val groupThemeId = result.value
                viewModel.setGroupTheme(groupThemeId)
            }
        }
    }

    // 建立社團完成
    uiState.createComplete?.let { createComplete ->
        if (createComplete) {
            uiState.createdGroup?.let { group ->
                group.id?.let { id ->
                    groupOpennessViewModel.onSave(id = id)
                }
            }
        }
        viewModel.onCreateFinish()
    }

    // 建立完成, 回到首頁並顯示所建立群組
    approvalUiState.saveComplete?.let { saveComplete ->
        if (saveComplete) {
            uiState.createdGroup?.let { group ->
                globalGroupViewModel.setCurrentGroup(group)
            }
            navigator.popBackStack(
                route = MainScreenDestination,
                inclusive = false
            )
        } else {
            context.showToast(stringResource(id = R.string.save_failed))
        }
        viewModel.dismissLoading()
        groupOpennessViewModel.onSaveFinish()
    }
}

@Composable
private fun CreateGroupScreenView(
    navController: DestinationsNavigator,
    modifier: Modifier = Modifier,
    settingGroup: Group,
    fanciColor: FanciColor?,
    currentStep: Int,
    approvalUiState: com.cmoney.kolfanci.ui.screens.group.setting.group.openness.viewmodel.UiState,
    question: List<String>,
    isShowLoading: Boolean,
    onGroupName: (String) -> Unit,
    onSwitchApprove: (Boolean) -> Unit,
    onAddQuestion: () -> Unit,
    onEditClick: (String) -> Unit,
    onNextStep: () -> Unit,
    onPreStep: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBarScreen(
                title = "建立社團",
                backClick = {
                    onBack.invoke()
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(LocalColor.current.env_80)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp),
                    text = "馬上就能擁有你自己的社團囉！",
                    fontSize = 17.sp,
                    color = LocalColor.current.text.default_80,
                    textAlign = TextAlign.Center
                )

                StepProgressBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    numberOfSteps = 3,
                    currentStep = currentStep,
                    titleList = listOf("名稱", "權限", "佈置")
                )

                when (currentStep) {
                    //step 1.
                    1 -> {
                        Step1Screen(
                            settingGroup.name.orEmpty()
                        ) {
                            onGroupName.invoke(it)
                        }
                    }
                    //step 2.
                    2 -> {
                        Step2Screen(
                            questions = question,
                            isNeedApproval = approvalUiState.isNeedApproval,
                            onSwitchApprove = onSwitchApprove,
                            onAddQuestion = onAddQuestion,
                            onEditClick = onEditClick,
                            onNext = onNextStep,
                            onPre = onPreStep
                        )
                    }
                    //step 3.
                    3 -> {
                        Step3Screen(
                            groupLogo = settingGroup.logoImageUrl.orEmpty(),
                            groupIcon = settingGroup.thumbnailImageUrl.orEmpty(),
                            groupBackground = settingGroup.coverImageUrl.orEmpty(),
                            fanciColor = fanciColor,
                            onChangeLogo = {
                                navController.navigate(
                                    GroupSettingLogoScreenDestination(
                                        group = settingGroup
                                    )
                                )
                            },
                            onChangeIcon = {
                                navController.navigate(
                                    GroupSettingAvatarScreenDestination(
                                        group = settingGroup
                                    )
                                )
                            },
                            onChangeBackground = {
                                navController.navigate(
                                    GroupSettingBackgroundScreenDestination(
                                        group = settingGroup
                                    )
                                )
                            },
                            onThemeChange = {
                                navController.navigate(
                                    GroupSettingThemeScreenDestination(
                                        group = settingGroup
                                    )
                                )
                            },
                            onPre = onPreStep,
                            onNext = onNextStep
                        )
                    }
                }
            }

            if (isShowLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(45.dp)
                        .align(Alignment.Center),
                    color = LocalColor.current.primary
                )
            }
        }
    }
}

/**
 * 設定進度條
 */
@Composable
private fun StepProgressBar(
    modifier: Modifier = Modifier,
    numberOfSteps: Int,
    currentStep: Int,
    titleList: List<String>
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (step in 0 until numberOfSteps) {
            val title = if (step < titleList.size) {
                titleList[step]
            } else {
                ""
            }

            Step(
                modifier = Modifier.weight(1F),
                isComplete = step < currentStep,
                isFirstStep = step == 0,
                title = title
            )
        }
    }
}

@Composable
private fun Step(
    modifier: Modifier = Modifier,
    isComplete: Boolean,
    isFirstStep: Boolean,
    title: String
) {
    val color = if (isComplete) LocalColor.current.primary else LocalColor.current.env_60
    val textColor =
        if (isComplete) LocalColor.current.text.default_100 else LocalColor.current.text.default_30
    var offset: Dp

    BoxWithConstraints(modifier = modifier) {
        val width = maxWidth.value.toInt()
        offset = width.dp / 2

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = -offset)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (!isFirstStep) {
                    //Line
                    Divider(
                        modifier = Modifier
                            .align(Alignment.CenterStart),
                        color = color,
                        thickness = 2.dp
                    )
                }

                //Circle
                Canvas(modifier = Modifier
                    .size(15.dp)
                    .align(Alignment.CenterEnd)
                    .border(
                        shape = CircleShape,
                        width = 2.dp,
                        color = color
                    ),
                    onDraw = {
                        drawCircle(color = color)
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(x = 5.dp),
                text = title, color = textColor, fontSize = 14.sp,
                textAlign = TextAlign.End
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun CreateGroupScreenPreview() {
    FanciTheme {
        CreateGroupScreenView(
            navController = EmptyDestinationsNavigator,
            settingGroup = Group(),
            fanciColor = null,
            currentStep = 1,
            approvalUiState = com.cmoney.kolfanci.ui.screens.group.setting.group.openness.viewmodel.UiState(),
            question = emptyList(),
            isShowLoading = false,
            onGroupName = {},
            onSwitchApprove = {},
            onAddQuestion = {},
            onEditClick = {},
            onNextStep = {},
            onPreStep = {}
        ) {}
    }
}