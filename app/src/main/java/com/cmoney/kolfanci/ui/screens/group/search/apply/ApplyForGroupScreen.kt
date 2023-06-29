package com.cmoney.kolfanci.ui.screens.group.search.apply

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.IGroupRequirementQuestion
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.showToast
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.screens.group.search.apply.viewmodel.ApplyForGroupViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.setting.BottomButtonScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import org.koin.androidx.compose.koinViewModel

/**
 * 加入申請
 */
@Destination
@Composable
fun ApplyForGroupScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    group: Group,
    viewModel: ApplyForGroupViewModel = koinViewModel(),
    resultBackNavigator: ResultBackNavigator<Boolean>
) {
    val uiState = viewModel.uiState

    uiState.questionList?.let {
        ApplyForGroupScreenView(
            modifier = modifier,
            navigator = navigator,
            group = group,
            questionList = it,
            answerList = uiState.answerList.orEmpty(),
            onAnswer = { answerPair ->
                viewModel.editAnswer(
                    index = answerPair.first,
                    answer = answerPair.second
                )
            },
            onApply = {
                viewModel.onApply(group.id.orEmpty())
            },
            onBackClick = {
                viewModel.checkAnswer()
            }
        )
    }

    if (uiState.questionList == null) {
        viewModel.fetchAllQuestion(group.id.orEmpty())
    }

    if (uiState.isComplete) {
        LocalContext.current.showToast("加入申請已送出")
        resultBackNavigator.navigateBack(result = true)
    }

    //未完成 Dialog
    if (uiState.notComplete) {
        DialogScreen(
            title = "尚有題目未完成",
            subTitle = "題目未完成前，無法送出邀請。",
            titleIconRes = R.drawable.edit,
            onDismiss = {
                viewModel.dismissWarning()
            }) {
            BlueButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = "繼續"
            ) {
                viewModel.dismissWarning()
            }
        }
    }

    //未送出 Dialog
    if (uiState.notSend) {
        DialogScreen(
            title = "答案未送出",
            subTitle = "你的答案沒有送出喔！",
            titleIconRes = R.drawable.edit,
            onDismiss = {
                viewModel.dismissWarning()
            }) {
            Column {
                BlueButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = "繼續作答"
                ) {
                    viewModel.dismissWarning()
                }

                Spacer(modifier = Modifier.height(20.dp))

                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = "取消並返回",
                    borderColor = LocalColor.current.text.default_100
                ) {
                    navigator.popBackStack()
                    Unit
                }
            }
        }
    }


    if (uiState.isPopupBack) {
        navigator.popBackStack()
    }

    BackHandler {
        viewModel.checkAnswer()
    }
}

@Composable
private fun ApplyForGroupScreenView(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    group: Group,
    questionList: List<IGroupRequirementQuestion>,
    answerList: List<String>,
    onAnswer: (Pair<Int, String>) -> Unit,
    onApply: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBarScreen(
                title = "加入申請",
                backClick = {
                    onBackClick.invoke()
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalColor.current.env_80)
                .padding(top = 27.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = group.thumbnailImageUrl,
                    modifier = Modifier
                        .size(55.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    placeholder = painterResource(id = R.drawable.placeholder)
                )

                Spacer(modifier = Modifier.width(20.dp))

                Text(
                    text = "申請加入社團\n%s\n請先完成下列問題的作答：".format(group.name.orEmpty()),
                    fontSize = 16.sp,
                    color = LocalColor.current.text.default_100
                )
            }

            Spacer(modifier = Modifier.height(34.dp))

            if (questionList.isNotEmpty()) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(start = 24.dp, end = 24.dp)
                ) {

                    questionList.forEachIndexed { index, question ->
                        Text(
                            text = question.question.orEmpty(),
                            fontSize = 17.sp,
                            color = LocalColor.current.text.default_100
                        )

                        Spacer(modifier = Modifier.height(15.dp))

                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            value = if (index < answerList.size) answerList[index] else "",
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = LocalColor.current.text.default_100,
                                backgroundColor = LocalColor.current.background,
                                cursorColor = LocalColor.current.primary,
                                disabledLabelColor = LocalColor.current.text.default_30,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            onValueChange = {
                                onAnswer.invoke(
                                    Pair(
                                        index, it
                                    )
                                )
                            },
                            shape = RoundedCornerShape(4.dp),
                            maxLines = 5,
                            textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                            placeholder = {
                                Text(
                                    text = "輸入文字作答",
                                    fontSize = 16.sp,
                                    color = LocalColor.current.text.default_30
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(25.dp))

                    }
                }

            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "社團沒有設定問題審核題目\n直接點選按鈕申請加入",
                        textAlign = TextAlign.Center,
                        fontSize = 17.sp,
                        color = LocalColor.current.text.default_50
                    )
                }
            }

            BottomButtonScreen(text = "申請加入") {
                onApply.invoke()
            }
        }
    }
}

//@Composable
//private fun TipDialog(
//    title: String,
//    isBackClick: Boolean,
//    onDismiss: () -> Unit,
//    onForceClose: () -> Unit
//) {
//    Dialog(onDismissRequest = { onDismiss.invoke() }) {
//        Surface(
//            modifier = Modifier,
//            shape = RoundedCornerShape(16.dp),
//            color = LocalColor.current.env_80
//        ) {
//            Box(
//                modifier = Modifier.padding(20.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Column {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Image(
//                            painter = painterResource(id = android.R.drawable.ic_menu_edit),
//                            contentDescription = null
//                        )
//
//                        Spacer(modifier = Modifier.width(9.dp))
//
//                        Text(
//                            text = title,
//                            fontSize = 19.sp,
//                            color = LocalColor.current.text.default_100
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(20.dp))
//
//                    BlueButton(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(50.dp),
//                        text = "繼續作答",
//                        onClick = {
//                            onDismiss.invoke()
//                        }
//                    )
//
//                    Spacer(modifier = Modifier.height(20.dp))
//
//                    if (isBackClick) {
//                        BorderButton(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(50.dp),
//                            text = "取消作答",
//                            borderColor = LocalColor.current.text.default_50,
//                            textColor = LocalColor.current.text.default_100
//                        ) {
//                            onForceClose.invoke()
//                        }
//                    }
//                }
//            }
//        }
//    }
//}


@Preview(showBackground = true)
@Composable
fun ApplyForGroupScreenPreview() {
    FanciTheme {
        ApplyForGroupScreenView(
            navigator = EmptyDestinationsNavigator,
            group = Group(
                name = "韓勾ㄟ\uD83C\uDDF0\uD83C\uDDF7金針菇討論區"
            ),
            questionList = listOf(
                IGroupRequirementQuestion(
                    question = "Hello?"
                ),
                IGroupRequirementQuestion(
                    question = "Hello2?"
                )
            ),
            answerList = listOf("", ""),
            onAnswer = {},
            onApply = {},
            onBackClick = {}
        )
    }
}