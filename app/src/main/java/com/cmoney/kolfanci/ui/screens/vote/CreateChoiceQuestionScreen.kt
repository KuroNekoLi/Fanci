package com.cmoney.kolfanci.ui.screens.vote

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.showToast
import com.cmoney.kolfanci.model.vote.VoteModel
import com.cmoney.kolfanci.ui.common.DashPlusButton
import com.cmoney.kolfanci.ui.screens.shared.toolbar.EditToolbarScreen
import com.cmoney.kolfanci.ui.screens.vote.viewmodel.VoteViewModel
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel

/**
 * 建立選擇題
 *
 * setResult
 * @param resultBackNavigator result callback [VoteModel]
 */
@Destination
@Composable
fun CreateChoiceQuestionScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    voteModel: VoteModel? = null,
    viewModel: VoteViewModel = koinViewModel(),
    resultBackNavigator: ResultBackNavigator<VoteModel>
) {
    val question by viewModel.question.collectAsState()
    val choice by viewModel.choice.collectAsState()
    val isSingleChoice by viewModel.isSingleChoice.collectAsState()

    LaunchedEffect(key1 = voteModel) {
        voteModel?.let {
            viewModel.setVoteModel(it)
        }
    }

    CreateChoiceQuestionScreenView(
        modifier = modifier,
        question = question,
        choice = choice,
        isSingleChoice = isSingleChoice,
        onQuestionValueChange = {
            viewModel.setQuestion(it)
        },
        onAddChoice = {
            viewModel.addEmptyChoice()
        },
        onChoiceValueChange = { index, text ->
            viewModel.setChoiceQuestion(index, text)
        },
        onDeleteChoice = {
            viewModel.removeChoice(it)
        },
        onChoiceTypeClick = {
            if (it) {
                viewModel.onSingleChoiceClick()
            } else {
                viewModel.onMultiChoiceClick()
            }
        },
        backClick = {
            navController.popBackStack()
        },
        onConfirm = {
            viewModel.onConfirmClick(
                question = question,
                choice = choice,
                isSingleChoice = isSingleChoice,
                id = voteModel?.id
            )
        }
    )

    //建立投票
    val voteModel by viewModel.voteModel.collectAsState()
    voteModel?.let {
        resultBackNavigator.navigateBack(it)
    }

    //錯誤訊息提示
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.toast.collect {
            if (it.isNotEmpty()) {
                context.showToast(it)
            }
        }
    }
}

/**
 *
 * @param question 主問題
 * @param choice 選項 List
 * @param isSingleChoice 是否為單選題
 * @param onQuestionValueChange 主問題輸入 callback
 * @param onChoiceValueChange 選項輸入 callback
 * @param backClick 返回
 * @param onAddChoice 增加選項
 * @param onDeleteChoice 刪除選項
 * @param onChoiceTypeClick 選項類型, true -> 單選題, false -> 多選題
 */
@Composable
fun CreateChoiceQuestionScreenView(
    modifier: Modifier = Modifier,
    question: String,
    choice: List<String>,
    isSingleChoice: Boolean,
    onQuestionValueChange: (String) -> Unit,
    onChoiceValueChange: (Int, String) -> Unit,
    backClick: (() -> Unit)? = null,
    onAddChoice: () -> Unit,
    onDeleteChoice: (Int) -> Unit,
    onChoiceTypeClick: (Boolean) -> Unit,
    onConfirm: () -> Unit
) {
    //輸入限制, 250 bytes
    val maxTextLengthBytes = 250

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            EditToolbarScreen(
                title = stringResource(id = R.string.create_choice),
                saveClick = onConfirm,
                backClick = backClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            //輸入區塊
            Column(
                modifier = Modifier
                    .background(LocalColor.current.env_80)
                    .fillMaxSize()
                    .padding(top = 20.dp, start = 24.dp, end = 24.dp, bottom = 20.dp)
            ) {
                //Title
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.input_choice_question),
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = LocalColor.current.text.default_100
                        )
                    )
                }

                //問題 輸入匡
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(177.dp)
                        .padding(top = 10.dp),
                    value = question,
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = LocalColor.current.text.default_100,
                        backgroundColor = LocalColor.current.background,
                        cursorColor = LocalColor.current.primary,
                        disabledLabelColor = LocalColor.current.text.default_30,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    onValueChange = {
                        val input = it.trim()
                        val inputBytes = input.encodeToByteArray()
                        if (inputBytes.size <= maxTextLengthBytes) {
                            onQuestionValueChange.invoke(input)
                        }
                    },
                    shape = RoundedCornerShape(4.dp),
                    textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.input_question_placeholder),
                            fontSize = 16.sp,
                            color = LocalColor.current.text.default_30
                        )
                    }
                )

                //選項 List
                choice.forEachIndexed { index, choice ->
                    Spacer(modifier = Modifier.height(15.dp))

                    //前2選項 不出現刪除按鈕
                    ChoiceEditItem(
                        title = stringResource(id = R.string.choice_title).format((index + 1)),
                        isShowDelete = (index > 1),
                        choiceQuestion = choice,
                        onValueChange = {
                            onChoiceValueChange.invoke(index, it)
                        },
                        onDeleteClick = {
                            onDeleteChoice.invoke(index)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                if (choice.size > 4) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.choice_count_limit),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = LocalColor.current.text.default_50
                            )
                        )
                    }
                } else {
                    //新增選項 按鈕
                    DashPlusButton(
                        onClick = onAddChoice
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            //單選/多選 控制
            QuestionTypeItem(
                title = stringResource(id = R.string.single_choice),
                subTitle = stringResource(id = R.string.single_choice_desc),
                isSelected = isSingleChoice,
                onClick = {
                    onChoiceTypeClick.invoke(true)
                }
            )

            Spacer(modifier = Modifier.height(2.dp))

            QuestionTypeItem(
                title = stringResource(id = R.string.multi_choice),
                subTitle = stringResource(id = R.string.multi_choice_desc),
                isSelected = !isSingleChoice,
                onClick = {
                    onChoiceTypeClick.invoke(false)
                }
            )
        }
    }
}

/**
 * 選擇題-選項 item
 *
 * @param title
 * @param choiceQuestion 選項問題
 * @param onValueChange input callback
 */
@Composable
fun ChoiceEditItem(
    title: String,
    choiceQuestion: String,
    isShowDelete: Boolean = false,
    onValueChange: (String) -> Unit,
    onDeleteClick: (() -> Unit)? = null
) {
    //輸入限制, 100 bytes
    val maxTextLengthBytes = 100

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = LocalColor.current.text.default_100
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isShowDelete) {
                Image(
                    modifier = Modifier
                        .size(23.dp)
                        .clickable {
                            onDeleteClick?.invoke()
                        },
                    painter = painterResource(id = R.drawable.close), contentDescription = "delete"
                )
            }
        }

        //問題 輸入匡
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            value = choiceQuestion,
            colors = TextFieldDefaults.textFieldColors(
                textColor = LocalColor.current.text.default_100,
                backgroundColor = LocalColor.current.background,
                cursorColor = LocalColor.current.primary,
                disabledLabelColor = LocalColor.current.text.default_30,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            onValueChange = {
                val input = it.trim()
                if (input.toByteArray().size < maxTextLengthBytes) {
                    onValueChange.invoke(input)
                }
            },
            shape = RoundedCornerShape(4.dp),
            maxLines = 2,
            textStyle = TextStyle.Default.copy(fontSize = 16.sp),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.input_choice_question_placeholder),
                    fontSize = 16.sp,
                    color = LocalColor.current.text.default_30
                )
            }
        )
    }
}

/**
 * 選項類型 item
 *
 * @param title 大標題
 * @param subTitle 下標說明
 * @param isSelected 是否被勾選
 * @param onClick 點擊 callback
 */
@Composable
fun QuestionTypeItem(
    title: String,
    subTitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalColor.current.env_80)
            .clickable { onClick.invoke() }
            .padding(horizontal = 24.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = if (isSelected) {
                        LocalColor.current.primary
                    } else {
                        LocalColor.current.text.default_100
                    }
                )
            )

            Text(
                text = subTitle,
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = LocalColor.current.text.default_50
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (isSelected) {
            Image(
                painter = painterResource(id = R.drawable.checked),
                contentDescription = null,
                colorFilter = ColorFilter.tint(LocalColor.current.primary)
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun QuestionTypeItemPreview() {
    FanciTheme {
        QuestionTypeItem(
            title = "單選題",
            subTitle = "每個人只能選一個選項",
            isSelected = true,
            onClick = {}
        )
    }
}

@Preview
@Composable
fun ChoiceEditItemPreview() {
    FanciTheme {
        ChoiceEditItem(
            title = "選項 1",
            isShowDelete = true,
            choiceQuestion = "",
            onValueChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MultipleChoiceQuestionScreenPreview() {
    FanciTheme {
        CreateChoiceQuestionScreenView(
            question = "",
            choice = emptyList(),
            onQuestionValueChange = {},
            onChoiceValueChange = { index, text ->
            },
            onAddChoice = {},
            onDeleteChoice = {},
            isSingleChoice = true,
            onChoiceTypeClick = {},
            onConfirm = {}
        )
    }
}