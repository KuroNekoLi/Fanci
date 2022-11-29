package com.cmoney.fanci.ui.screens.group.setting.groupsetting

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.extension.goBackWithParams
import com.cmoney.fanci.extension.showToast
import com.cmoney.fanci.ui.screens.chat.AnnounceBundleKey
import com.cmoney.fanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import org.koin.androidx.compose.koinViewModel

const val GroupSettingNameBundleKey = "GroupSettingNameBundleKey"

@Composable
fun GroupSettingNameScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    group: Group,
    viewModel: GroupSettingViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var textState by remember { mutableStateOf(group.name.orEmpty()) }
    val maxLength = 20

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                navController,
                title = "社團名稱",
                leadingEnable = true,
                moreEnable = false
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
                            context.showToast("社團名稱不可以是空白的唷！")
                        } else {
                            viewModel.changeGroupName(textState, group)
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

    //setResult 通知之前的頁面刷新
    LaunchedEffect(viewModel.uiState.changeGroupName) {
        viewModel.uiState.changeGroupName?.let {
            navController.goBackWithParams {
                putParcelable(GroupSettingNameBundleKey, it)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupSettingNameScreenPreview() {
    FanciTheme {
        GroupSettingNameScreen(
            group = Group(
                name = "韓勾ㄟ金針菇討論區",
                description = "我愛金針菇\uD83D\uDC97這裡是一群超愛金針菇的人類！喜歡的人就趕快來參加吧吧啊！"
            ),
            navController = rememberNavController()
        )
    }
}