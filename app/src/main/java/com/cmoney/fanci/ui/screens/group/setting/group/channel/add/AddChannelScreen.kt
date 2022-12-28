package com.cmoney.fanci.ui.screens.group.setting.group.channel.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.R
import com.cmoney.fanci.extension.showToast
import com.cmoney.fanci.ui.common.BlueButton
import com.cmoney.fanci.ui.screens.group.setting.group.channel.viewmodel.ChannelSettingViewModel
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.Group
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

/**
 * 分類下新增頻道
 */
@Destination
@Composable
fun AddChannelScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    category: Category,
    viewModel: ChannelSettingViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<Group>
) {
    val context = LocalContext.current

    viewModel.uiState.group?.let {
        KLog.i("TAG", "channel add.")
        resultNavigator.navigateBack(result = it)
    }

    AddChannelScreenView(
        modifier,
        navigator
    ) {
        if (it.isNotEmpty()) {
            viewModel.addChannel(group, category.id.orEmpty(), it)
        } else {
            context.showToast("請輸入頻道名稱")
        }
    }
}

@Composable
fun AddChannelScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    onConfirm: (String) -> Unit
) {
    val maxLength = 10
    var textState by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "新增頻道",
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    navigator.popBackStack()
                }
            )
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalColor.current.env_80)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.padding(
                        top = 20.dp,
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 20.dp
                    )
                ) {
                    Text(
                        text = "頻道名稱",
                        fontSize = 14.sp,
                        color = LocalColor.current.text.default_100
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "%d/%d".format(textState.length, maxLength),
                        fontSize = 14.sp,
                        color = LocalColor.current.text.default_50
                    )
                }

                TextField(
                    modifier = Modifier.fillMaxWidth(),
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
                    maxLines = 1,
                    textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                    placeholder = {
                        Text(
                            text = "輸入頻道名稱",
                            fontSize = 16.sp,
                            color = LocalColor.current.text.default_30
                        )
                    }
                )

                Spacer(modifier = Modifier.height(35.dp))

                Text(
                    modifier = Modifier.padding(start = 24.dp, bottom = 10.dp),
                    text = "頻道類別", fontSize = 14.sp, color = LocalColor.current.text.default_100
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(LocalColor.current.background),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        modifier = Modifier.padding(start = 25.dp),
                        painter = painterResource(id = R.drawable.message),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = LocalColor.current.component.other)
                    )

                    Text(
                        modifier = Modifier.padding(start = 17.dp),
                        text = "文字聊天頻道",
                        fontSize = 17.sp,
                        color = LocalColor.current.text.default_100
                    )
                }
            }

            //========== 儲存 ==========
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .background(LocalColor.current.env_100),
                contentAlignment = Alignment.Center
            ) {
                BlueButton(
                    text = "建立"
                ) {
                    onConfirm.invoke(textState)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddChannelScreenPreview() {
    FanciTheme {
        AddChannelScreenView(
            navigator = EmptyDestinationsNavigator,
        ) {}
    }
}