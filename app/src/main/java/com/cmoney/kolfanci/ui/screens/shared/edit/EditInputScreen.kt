package com.cmoney.kolfanci.ui.screens.shared.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.SaveConfirmDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.toolbar.EditToolbarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

/**
 * 單一輸入編輯畫面
 *
 * @param from log 紀錄用
 * @param textFieldClicked log 紀錄用
 * @param textFieldFrom log 紀錄用
 */
@Destination
@Composable
fun EditInputScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    defaultText: String,
    toolbarTitle: String,
    placeholderText: String,
    emptyAlertTitle: String,
    emptyAlertSubTitle: String,
    resultNavigator: ResultBackNavigator<String>,
    from: From? = null,
    textFieldClicked: Clicked? = null,
    textFieldFrom: From? = null
) {
    var showEmptyTip by remember {
        mutableStateOf(false)
    }

    var showSaveTip by remember {
        mutableStateOf(false)
    }

    EditInputScreenView(
        modifier = modifier,
        defaultText = defaultText,
        toolbarTitle = toolbarTitle,
        placeholderText = placeholderText,
        onChangeName = { name ->
            resultNavigator.navigateBack(name)
        },
        onShowEmptyTip = {
            showEmptyTip = true
        },
        onBack = {
            showSaveTip = true
        },
        from = from,
        textFieldFrom = textFieldFrom,
        textFieldClicked = textFieldClicked
    )

    if (showEmptyTip) {
        DialogScreen(
            title = emptyAlertTitle,
            subTitle = emptyAlertSubTitle,
            onDismiss = {
                showEmptyTip = false
            }
        ) {
            BlueButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = stringResource(id = R.string.modify)
            ) {
                showEmptyTip = false
            }
        }
    }

    SaveConfirmDialogScreen(
        isShow = showSaveTip,
        onContinue = {
            showSaveTip = false
        },
        onGiveUp = {
            showSaveTip = false
            navController.popBackStack()
        }
    )
}

@Composable
fun EditInputScreenView(
    modifier: Modifier = Modifier,
    defaultText: String,
    onChangeName: (String) -> Unit,
    onShowEmptyTip: () -> Unit,
    onBack: () -> Unit,
    toolbarTitle: String,
    placeholderText: String,
    from: From? = null,
    textFieldClicked: Clicked? = null,
    textFieldFrom: From? = null
) {
    var textState by remember { mutableStateOf(defaultText) }
    val maxLength = 20

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            EditToolbarScreen(
                title = toolbarTitle,
                saveClick = {
                    from?.let {
                        AppUserLogger.getInstance().log(Clicked.Confirm, it)
                    }

                    if (textState.isEmpty()) {
                        onShowEmptyTip.invoke()
                    } else {
                        onChangeName.invoke(textState)
                    }
                },
                backClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(LocalColor.current.env_80)
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Bottom
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 20.dp, start = 25.dp, end = 25.dp)
            ) {
                Text(
                    text = "%d/${maxLength}".format(textState.length),
                    fontSize = 14.sp,
                    color = LocalColor.current.text.default_50
                )

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
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
                    shape = RoundedCornerShape(4.dp),
                    maxLines = 1,
                    textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                    placeholder = {
                        Text(
                            text = placeholderText,
                            fontSize = 16.sp,
                            color = LocalColor.current.text.default_30
                        )
                    },
                    interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                if (it is PressInteraction.Release) {
                                    if (textFieldClicked != null) {
                                        AppUserLogger.getInstance()
                                            .log(textFieldClicked, textFieldFrom)
                                    }
                                }
                            }
                        }
                    }
                )

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditInputScreenPreview() {
    FanciTheme {
        EditInputScreenView(
            defaultText = "韓勾ㄟ金針菇討論區",
            onChangeName = {},
            onShowEmptyTip = {},
            onBack = {},
            toolbarTitle = stringResource(id = R.string.group_name),
            placeholderText = stringResource(R.string.group_name_placeholder)
        )
    }
}