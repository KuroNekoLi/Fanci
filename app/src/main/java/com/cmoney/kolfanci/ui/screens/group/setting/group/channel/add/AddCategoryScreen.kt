package com.cmoney.kolfanci.ui.screens.group.setting.group.channel.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.showToast
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.destinations.EditInputScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.group.channel.viewmodel.ChannelSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.toolbar.EditToolbarScreen
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
 * 新增分類
 */
@Destination
@Composable
fun AddCategoryScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    viewModel: ChannelSettingViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<Group>,
    editInputResult: ResultRecipient<EditInputScreenDestination, String>
) {
    val TAG = "AddCategoryScreen"
    val context = LocalContext.current

    val textState = viewModel.uiState.categoryName

    viewModel.uiState.group?.let {
        KLog.i(TAG, "category add.")
        resultNavigator.navigateBack(result = it)
    }

    AddCategoryScreenView(
        modifier,
        navigator,
        textState = textState,
        onConfirm = {
            if (it.isNotEmpty()) {
                viewModel.addCategory(group, it)
            } else {
                context.showToast("請輸入類別名稱")
            }
        }
    )

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
fun AddCategoryScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    textState: String,
    onConfirm: (String) -> Unit
) {
    val TAG = "AddCategoryScreenView"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            EditToolbarScreen(
                title = stringResource(id = R.string.add_category),
                backClick = {
                    navigator.popBackStack()
                },
                saveClick = {
                    KLog.i(TAG, "saveClick click.")
                    AppUserLogger.getInstance().log(Clicked.Confirm, From.AddCategory)
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
                        with(AppUserLogger.getInstance()) {
                            log(Clicked.CategoryName, From.Create)
                            log(Page.GroupSettingsChannelManagementAddCategoryCategoryName)
                        }
                        navigator.navigate(
                            EditInputScreenDestination(
                                defaultText = textState,
                                toolbarTitle = context.getString(R.string.category_name),
                                placeholderText = context.getString(R.string.input_category_name),
                                emptyAlertTitle = context.getString(R.string.category_name_empty),
                                emptyAlertSubTitle = context.getString(R.string.category_name_empty_desc),
                                from = From.KeyinCategoryName
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
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddCategoryScreenPreview() {
    FanciTheme {
        AddCategoryScreenView(
            navigator = EmptyDestinationsNavigator,
            textState = "Hello",
            onConfirm = {}
        )
    }
}