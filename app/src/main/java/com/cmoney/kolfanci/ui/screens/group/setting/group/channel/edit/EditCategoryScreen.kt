package com.cmoney.kolfanci.ui.screens.group.setting.group.channel.edit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.ui.destinations.EditInputScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.group.channel.viewmodel.ChannelSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DeleteAlertDialogScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
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
    resultNavigator: ResultBackNavigator<Group>,
    editInputResult: ResultRecipient<EditInputScreenDestination, String>
) {
    val TAG = "EditCategoryScreen"
    val showDialog = remember { mutableStateOf(false) }
    val textState = viewModel.uiState.categoryName

    LaunchedEffect(Unit) {
        if (textState.isEmpty()) {
            viewModel.setCategoryName(category.name.orEmpty())
        }
    }

    viewModel.uiState.group?.let {
        resultNavigator.navigateBack(result = it)
    }

    EditCategoryScreenView(
        modifier,
        navigator,
        textState = textState,
        onConfirm = {
            viewModel.editCategory(group, category, it)
        }
    ) {
        KLog.i(TAG, "onDelete click")
        showDialog.value = true
    }

    BackHandler {
        viewModel.editCategory(group, category, textState)
    }

    if (showDialog.value) {
        DeleteAlertDialogScreen(
            title = "確定刪除分類「%s」".format(category.name),
            subTitle = "分類刪除後，頻道會保留下來。",
            onConfirm = {
                showDialog.value = false
                viewModel.deleteCategory(group, category)
            },
            onCancel = {
                showDialog.value = false
            }
        )
    }

    editInputResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                viewModel.setCategoryName(result.value)
            }
        }
    }

}

@Composable
fun EditCategoryScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    textState: String,
    onConfirm: (String) -> Unit,
    onDelete: () -> Unit
) {
    val TAG = "EditCategoryScreenView"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "編輯分類",
                backClick = {
                    KLog.i(TAG, "saveClick click.")
                    onConfirm.invoke(textState)
                }
            )
        }
    ) { padding ->
        val context = LocalContext.current
        Column(modifier = Modifier.padding(padding)) {
            Text(
                modifier = Modifier.padding(
                    top = 20.dp,
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 20.dp
                ),
                text = stringResource(id = R.string.category_name),
                fontSize = 14.sp,
                color = LocalColor.current.text.default_100
            )

            Row(
                modifier = Modifier
                    .background(LocalColor.current.background)
                    .clickable {
                        navigator.navigate(
                            EditInputScreenDestination(
                                defaultText = textState,
                                toolbarTitle = "編輯分類",
                                placeholderText = context.getString(R.string.input_category_name),
                                emptyAlertTitle = context.getString(R.string.category_name_empty),
                                emptyAlertSubTitle = context.getString(R.string.category_name_empty_desc)
                            )
                        )
                    }
                    .padding(
                        top = 10.dp,
                        bottom = 10.dp,
                        start = 25.dp,
                        end = 10.dp
                    )
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (textState.isEmpty()) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.input_category_name),
                        fontSize = 17.sp,
                        color = LocalColor.current.text.default_30
                    )
                } else {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = textState,
                        fontSize = 17.sp,
                        color = LocalColor.current.text.default_100
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                Image(
                    painter = painterResource(id = R.drawable.next),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = LocalColor.current.text.default_80)
                )
            }

            if (Constant.isCanDeleteCategory()) {
                Spacer(modifier = Modifier.weight(1f))

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

                Spacer(modifier = Modifier.height(25.dp))
            }
        }

//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(LocalColor.current.env_80)
//                .padding(padding)
//        ) {
//
//            Column(modifier = Modifier.weight(1f)) {
//                Row(
//                    modifier = Modifier.padding(
//                        top = 20.dp,
//                        start = 24.dp,
//                        end = 24.dp,
//                        bottom = 20.dp
//                    )
//                ) {
//                    Text(
//                        text = "類別名稱",
//                        fontSize = 14.sp,
//                        color = LocalColor.current.text.default_100
//                    )
//                    Spacer(modifier = Modifier.width(10.dp))
//                    Text(
//                        text = "%d/%d".format(textState.length, maxLength),
//                        fontSize = 14.sp,
//                        color = LocalColor.current.text.default_50
//                    )
//                }
//
//                TextField(
//                    modifier = Modifier.fillMaxWidth(),
//                    value = textState,
//                    colors = TextFieldDefaults.textFieldColors(
//                        textColor = LocalColor.current.text.default_100,
//                        backgroundColor = LocalColor.current.background,
//                        cursorColor = LocalColor.current.primary,
//                        disabledLabelColor = LocalColor.current.text.default_30,
//                        focusedIndicatorColor = Color.Transparent,
//                        unfocusedIndicatorColor = Color.Transparent
//                    ),
//                    onValueChange = {
//                        if (it.length <= maxLength) {
//                            textState = it
//                        }
//                    },
//                    maxLines = 1,
//                    textStyle = TextStyle.Default.copy(fontSize = 16.sp),
//                    placeholder = {
//                        Text(
//                            text = "輸入類別名稱",
//                            fontSize = 16.sp,
//                            color = LocalColor.current.text.default_30
//                        )
//                    },
//                    enabled = Constant.isCanEditCategoryPermission()
//                )
//
//                if (Constant.isCanDeleteCategory()) {
//                    Spacer(modifier = Modifier.height(35.dp))
//
//                    Text(
//                        modifier = Modifier.padding(start = 24.dp, bottom = 10.dp),
//                        text = "刪除分類", fontSize = 14.sp, color = LocalColor.current.text.default_100
//                    )
//
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(50.dp)
//                            .background(LocalColor.current.background)
//                            .clickable {
//                                onDelete.invoke()
//                            },
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = "刪除分類",
//                            fontSize = 17.sp,
//                            color = LocalColor.current.specialColor.red
//                        )
//
//                    }
//                }
//            }
//
//            //========== 儲存 ==========
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(135.dp)
//                    .background(LocalColor.current.env_100),
//                contentAlignment = Alignment.Center
//            ) {
//                BlueButton(
//                    text = "儲存"
//                ) {
//                    onConfirm.invoke(textState)
//                }
//            }
//        }

    }
}

@Preview(showBackground = true)
@Composable
fun EditCategoryScreenViewPreview() {
    FanciTheme {
        EditCategoryScreenView(
            navigator = EmptyDestinationsNavigator,
            textState = "嘿嘿分類",
            onConfirm = {}
        ) {}
    }
}