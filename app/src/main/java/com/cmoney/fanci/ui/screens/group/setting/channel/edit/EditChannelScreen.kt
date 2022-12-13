package com.cmoney.fanci.ui.screens.group.setting.channel.edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.cmoney.fanci.ui.common.BorderButton
import com.cmoney.fanci.ui.screens.group.setting.channel.viewmodel.ChannelSettingViewModel
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

/**
 * 編輯頻道
 */
@Destination
@Composable
fun EditChannelScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    channel: Channel,
    viewModel: ChannelSettingViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<Group>
) {
    val context = LocalContext.current
    val TAG = "EditChannelScreen"
    val showDialog = remember { mutableStateOf(false) }

    viewModel.uiState.group?.let {
        resultNavigator.navigateBack(result = it)
    }

//    viewModel.uiState.channelRole?.let {
//        KLog.i("TAG", it)
//    }

    EditChannelScreenView(
        modifier,
        navigator,
        channel,
        viewModel.uiState.channelRole,
        onConfirm = {
            if (it.isNotEmpty()) {
                viewModel.editChannel(group, channel, it)
            } else {
                context.showToast("請輸入頻道名稱")
            }
        },
        onDelete = {
            KLog.i(TAG, "onDelete click")
            showDialog.value = true
        }
    )

    if (showDialog.value) {
        showDeleteAlert(
            channelName = channel.name.orEmpty(),
            onConfirm = {
                showDialog.value = false
                viewModel.deleteChannel(group, channel)
            },
            onCancel = {
                showDialog.value = false
            }
        )
    }

    if (viewModel.uiState.channelRole == null) {
        viewModel.getChannelRole(channel.id.orEmpty())
    }
}

@Composable
private fun showDeleteAlert(
    channelName: String,
    onConfirm: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        backgroundColor = LocalColor.current.env_80,
        onDismissRequest = {
            onCancel.invoke()
        },
        title = {
            Text(
                text = "確定刪除頻道「%s」".format(channelName), color = LocalColor.current.specialColor.red
            )
        },
        text = {
            Text(
                text = "頻道刪除後，內容將會完全消失。", color = LocalColor.current.text.default_100
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm.invoke()
                }) {
                Text("確定刪除")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onCancel.invoke()
                }) {
                Text("返回")
            }
        }
    )
}

@Composable
fun EditChannelScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    channel: Channel,
    fanciRole: List<FanciRole>?,
    onConfirm: (String) -> Unit,
    onDelete: () -> Unit
) {
    var textState by remember { mutableStateOf(channel.name.orEmpty()) }
    val tabList = listOf("樣式", "管理員")
    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "編輯頻道:" + channel.name.orEmpty(),
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
            Spacer(modifier = Modifier.height(20.dp))

            TabRow(
                selectedTabIndex = selectedIndex,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .height(40.dp)
                    .padding(1.dp)
                    .clip(RoundedCornerShape(35)),
                indicator = { tabPositions: List<TabPosition> ->
                    Box {}
                },
                backgroundColor = LocalColor.current.env_100
            ) {
                tabList.forEachIndexed { index, text ->
                    val selected = selectedIndex == index
                    Tab(
                        modifier = if (selected) Modifier
                            .padding(2.dp)
                            .clip(RoundedCornerShape(25))
                            .background(
                                LocalColor.current.env_60
                            )
                        else Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(15))
                            .background(
                                Color.Transparent
                            ),
                        selected = selected,
                        onClick = { selectedIndex = index },
                        text = {
                            Text(
                                text = text, color = Color.White, fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                if (selectedIndex == 0) {
                    StyleView(
                        modifier = Modifier.weight(1f),
                        channelName = channel.name.orEmpty(),
                        onValueChange = {
                            textState = it
                        },
                        onDelete = {
                            onDelete.invoke()
                        }
                    )
                } else {
                    ManageView(
                        modifier = Modifier.weight(1f),
                        navigator,
                        fanciRole
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
                    BlueButton(
                        text = "儲存"
                    ) {
                        onConfirm.invoke(textState)
                    }
                }
            }
        }

    }
}


/**
 * 管理員 角色 頁面
 */
@Composable
private fun ManageView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    fanciRole: List<FanciRole>?
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(20.dp),
            text = "選擇角色來建立頻道管理員：擁有該角色之成員，即可針對相對應的權限，進行頻道管理。",
            fontSize = 14.sp,
            color = LocalColor.current.component.other
        )

        //TODO Rule
        fanciRole?.let {

        }

        BorderButton(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .height(40.dp),
            text = "新增角色",
            borderColor = Color.White
        ) {
            // TODO: Route
//            navigator.navigate()
        }
    }
}

/**
 * 設定頻道名稱, 刪除頻道
 */
@Composable
private fun StyleView(
    modifier: Modifier = Modifier,
    channelName: String,
    onValueChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    val maxLength = 10
    var textState by remember { mutableStateOf(channelName) }

    Column(modifier = modifier) {
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
                    onValueChange.invoke(textState)
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

        Spacer(modifier = Modifier.height(35.dp))

        Text(
            modifier = Modifier.padding(start = 24.dp, bottom = 10.dp),
            text = "刪除頻道", fontSize = 14.sp, color = LocalColor.current.text.default_100
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(LocalColor.current.background)
                .clickable {
                    onDelete.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "刪除頻道", fontSize = 17.sp, color = LocalColor.current.specialColor.red)

        }
    }
}


@Preview(showBackground = true)
@Composable
fun EditChannelScreenPreview() {
    FanciTheme {
        EditChannelScreenView(
            navigator = EmptyDestinationsNavigator,
            channel = Channel(name = "嘿嘿"),
            fanciRole = null,
            onConfirm = {}
        ) {}
    }
}