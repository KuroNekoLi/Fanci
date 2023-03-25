package com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.extension.showToast
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination
@Composable
fun GroupSettingNameScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    resultNavigator: ResultBackNavigator<String>
) {
    var showEmptyTip by remember {
        mutableStateOf(false)
    }

    GroupSettingNameView(
        modifier = modifier,
        navController = navController,
        group = group,
        onChangeName = { name ->
            resultNavigator.navigateBack(name)
        },
        onShowEmptyTip = {
            showEmptyTip = true
        })

    if (showEmptyTip) {
        DialogScreen(
            onDismiss = {
                showEmptyTip = false
            },
            title = "社團名稱空白",
            subTitle = "社團名稱不可以是空白的唷！",
            content = {
                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = "修改",
                    borderColor = LocalColor.current.component.other,
                    textColor = Color.White,
                    onClick = {
                        showEmptyTip = false
                        Unit
                    }
                )
            }
        )
    }
}

@Composable
fun GroupSettingNameView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    onChangeName: (String) -> Unit,
    onShowEmptyTip: () -> Unit
) {
    val context = LocalContext.current
    var textState by remember { mutableStateOf(group.name.orEmpty()) }
    val maxLength = 20

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "社團名稱",
                leadingEnable = true,
                moreEnable = false,
                moreClick = {
                },
                backClick = {
                    navController.popBackStack()
                }
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
                    text = "%d/20".format(textState.length),
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
                            text = "填寫專屬於社團的名稱吧!",
                            fontSize = 16.sp,
                            color = LocalColor.current.text.default_30
                        )
                    }
                )

            }

            //========== 儲存 ==========
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .background(LocalColor.current.env_100),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    modifier = Modifier
                        .padding(25.dp)
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = LocalColor.current.primary),
                    onClick = {
                        if (textState.isEmpty()) {
                            onShowEmptyTip.invoke()
                            context.showToast("社團名稱不可以是空白的唷！")
                        } else {
                            onChangeName.invoke(textState)
                        }
                    }) {
                    Text(
                        text = "儲存",
                        color = LocalColor.current.text.other,
                        fontSize = 16.sp
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupSettingNameScreenPreview() {
    FanciTheme {
        GroupSettingNameView(
            group = Group(
                name = "韓勾ㄟ金針菇討論區",
                description = "我愛金針菇\uD83D\uDC97這裡是一群超愛金針菇的人類！喜歡的人就趕快來參加吧吧啊！"
            ),
            navController = EmptyDestinationsNavigator,
            onChangeName = {},
            onShowEmptyTip = {}
        )
    }
}