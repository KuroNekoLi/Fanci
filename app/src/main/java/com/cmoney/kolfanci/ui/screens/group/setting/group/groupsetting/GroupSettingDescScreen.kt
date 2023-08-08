package com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.screens.shared.dialog.SaveConfirmDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.toolbar.EditToolbarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.onEach

/**
 * 社團設定-社團簡介頁
 */
@Destination
@Composable
fun GroupSettingDescScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    resultNavigator: ResultBackNavigator<String>
) {
    var showSaveTip by remember {
        mutableStateOf(false)
    }

    GroupSettingDescView(
        modifier = modifier,
        group = group, onChangeDesc = { desc ->
            resultNavigator.navigateBack(desc)
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
            navController.popBackStack()
        }
    )

    LaunchedEffect(key1 = group) {
        AppUserLogger.getInstance()
            .log(Page.GroupSettingsGroupSettingsGroupIntroduction)
    }
}

@Composable
fun GroupSettingDescView(
    modifier: Modifier = Modifier,
    group: Group,
    onChangeDesc: (String) -> Unit,
    onBack: () -> Unit
) {
    var textState by remember { mutableStateOf(group.description.orEmpty()) }
    val maxLength = 100

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            EditToolbarScreen(
                title = stringResource(id = R.string.group_description),
                saveClick = {
                    AppUserLogger.getInstance().log(Clicked.GroupIntroduction)
                    onChangeDesc.invoke(textState)
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
                        .height(200.dp)
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
                    textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.group_description_placeholder),
                            fontSize = 16.sp,
                            color = LocalColor.current.text.default_30
                        )
                    },
                    interactionSource = remember {
                        MutableInteractionSource()
                    }.also { interactionSource ->
                        LaunchedEffect(key1 = interactionSource) {
                            interactionSource.interactions
                                .filterIsInstance<PressInteraction.Release>()
                                .onEach {
                                    AppUserLogger
                                        .getInstance()
                                        .log(Clicked.GroupIntroductionIntroduction)
                                }
                                .collect()
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupSettingDescViewPreview() {
    FanciTheme {
        GroupSettingDescView(
            group = Group(
                name = "韓勾ㄟ金針菇討論區",
                description = "我愛金針菇\uD83D\uDC97這裡是一群超愛金針菇的人類！喜歡的人就趕快來參加吧吧啊！"
            ),
            onChangeDesc = {}
        ) {}
    }
}