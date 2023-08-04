package com.cmoney.kolfanci.ui.screens.group.setting.group.openness

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.toolbar.EditToolbarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.socks.library.KLog

/**
 * @param keyinTracking 紀錄點擊輸入匡埋點事件
 */
@Destination
@Composable
fun CreateApplyQuestionScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    question: String = "",
    keyinTracking: String = Clicked.QuestionTextArea.eventName,
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
        },
        onTextFieldClick = {
            val clickEvent = when(keyinTracking) {
                Clicked.CreateGroupQuestionKeyin.eventName -> Clicked.CreateGroupQuestionKeyin
                else -> Clicked.QuestionTextArea
            }
            AppUserLogger.getInstance().log(clickEvent)
        }
    )

    if (showEmptyTipDialog) {
        DialogScreen(
            title = "審核題目空白",
            subTitle = "審核題目不可以是空白的唷！",
            onDismiss = { showEmptyTipDialog = false }
        ) {
            BlueButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = "修改",
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
    onAdd: (String) -> Unit,
    onTextFieldClick: () -> Unit
) {
    var textState by remember { mutableStateOf(question) }
    val maxLength = 50
    val TAG = "CreateApplyQuestionScreenView"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            EditToolbarScreen(
                title = "新增審核題目",
                backClick = {
                    navigator.popBackStack()
                },
                saveClick = {
                    KLog.i(TAG, "saveClick click.")
                    onAdd.invoke(textState)
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
                },
                interactionSource = remember{ MutableInteractionSource() }.also{ interactionSource->
                    LaunchedEffect(interactionSource){
                        interactionSource.interactions.collect{
                            if (it is PressInteraction.Release) {
                                onTextFieldClick.invoke()
                            }
                        }
                    }
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CreateApplyQuestionScreenPreview() {
    FanciTheme {
        CreateApplyQuestionScreenView(
            navigator = EmptyDestinationsNavigator,
            onAdd = {},
            onTextFieldClick = {}
        )
    }
}