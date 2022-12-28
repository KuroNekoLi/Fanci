package com.cmoney.fanci.ui.screens.group.setting.group.channel.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
 * 編輯分類
 */
@Destination
@Composable
fun EditCategoryScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    category: Category,
    viewModel: ChannelSettingViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<Group>
) {
    val context = LocalContext.current
    val TAG = "EditCategoryScreen"
    val showDialog = remember { mutableStateOf(false) }

    viewModel.uiState.group?.let {
        resultNavigator.navigateBack(result = it)
    }

    EditCategoryScreenView(
        modifier,
        navigator,
        category,
        onConfirm = {
            if (it.isNotEmpty()) {
                viewModel.editCategory(group, category, it)
            } else {
                context.showToast("請輸入類別名稱")
            }
        },
        onDelete = {
            KLog.i(TAG, "onDelete click")
            showDialog.value = true
        }
    )

    if (showDialog.value) {
        showDeleteAlert(
            categoryName = category.name.orEmpty(),
            onConfirm = {
                showDialog.value = false
                viewModel.deleteCategory(group, category)
            },
            onCancel = {
                showDialog.value = false
            }
        )
    }
}

@Composable
private fun showDeleteAlert(
    categoryName: String,
    onConfirm: () -> Unit, onCancel: () -> Unit
) {
    AlertDialog(
        backgroundColor = LocalColor.current.env_80,
        onDismissRequest = {
            onCancel.invoke()
        },
        title = {
            Text(
                text = "確定刪除分類「%s」".format(categoryName),
                color = LocalColor.current.specialColor.red
            )
        },
        text = {
            Text(
                text = "分類刪除後，頻道會保留下來。", color = LocalColor.current.text.default_100
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
fun EditCategoryScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    category: Category,
    onConfirm: (String) -> Unit,
    onDelete: () -> Unit
) {
    var textState by remember { mutableStateOf(category.name.orEmpty()) }
    val maxLength = 10

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "編輯分類:" + category.name.orEmpty(),
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
                        text = "類別名稱",
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
                            text = "輸入類別名稱",
                            fontSize = 16.sp,
                            color = LocalColor.current.text.default_30
                        )
                    }
                )

                Spacer(modifier = Modifier.height(35.dp))

                Text(
                    modifier = Modifier.padding(start = 24.dp, bottom = 10.dp),
                    text = "刪除分類", fontSize = 14.sp, color = LocalColor.current.text.default_100
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
                    Text(
                        text = "刪除分類",
                        fontSize = 17.sp,
                        color = LocalColor.current.specialColor.red
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
                    text = "儲存"
                ) {
                    onConfirm.invoke(textState)
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun EditCategoryScreenViewPreview() {
    FanciTheme {
        EditCategoryScreenView(
            navigator = EmptyDestinationsNavigator,
            category = Category(name = "嘿嘿分類"),
            onConfirm = {}
        ) {}
    }
}