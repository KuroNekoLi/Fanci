package com.cmoney.kolfanci.ui.screens.group.setting.group.openness

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.setting.BottomButtonScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.socks.library.KLog

@Destination
@Composable
fun CreateApplyQuestionScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    question: String = "",
    resultBackNavigator: ResultBackNavigator<String>
) {
    val TAG = "CreateApplyQuestionScreen"
    var showEmptyTipDialog by remember {
        mutableStateOf(false)
    }

    CreateApplyQuestionScreenView(
        modifier = modifier,
        navigator = navigator,
        question = question,
        onAdd = {
            KLog.i(TAG, "onAdd click.")
            if (it.isEmpty()) {
                showEmptyTipDialog = true
            } else {
                resultBackNavigator.navigateBack(it)
            }
        }
    )

    if (showEmptyTipDialog) {
        DialogScreen(
            onDismiss = { showEmptyTipDialog = false },
            title = "審核題目空白",
            subTitle = "審核題目不可以是空白的唷！"
        ) {
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = "修改",
                borderColor = LocalColor.current.component.other,
                textColor = Color.White
            ) {
                run {
                    showEmptyTipDialog = false
                }
            }
        }
    }

}

@Composable
private fun CreateApplyQuestionScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    question: String = "",
    onAdd: (String) -> Unit
) {
    var textState by remember { mutableStateOf(question) }
    val maxLength = 50

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "新增審核題目",
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    navigator.popBackStack()
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(25.dp)
        ) {
            Text(
                text = "%d/%d".format(textState.length, maxLength),
                fontSize = 14.sp,
                color = LocalColor.current.text.default_50
            )

            Spacer(modifier = Modifier.height(5.dp))

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                value = textState,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = LocalColor.current.text.default_100,
                    backgroundColor = LocalColor.current.background,
                    cursorColor = LocalColor.current.primary,
                    disabledLabelColor = LocalColor.current.text.default_30,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                onValueChange = {
                    if (it.length <= maxLength) {
                        textState = it
                    }
                },
                textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                placeholder = {
                    Text(
                        text = "輸入題目",
                        fontSize = 16.sp,
                        color = LocalColor.current.text.default_30
                    )
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            BottomButtonScreen(text = "新增") {
                onAdd.invoke(textState)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CreateApplyQuestionScreenPreview() {
    FanciTheme {
        CreateApplyQuestionScreenView(
            navigator = EmptyDestinationsNavigator,
            onAdd = {
            }
        )
    }
}