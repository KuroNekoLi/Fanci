package com.cmoney.kolfanci.ui.screens.group.setting.group.channel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.kolfanci.extension.globalGroupViewModel
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.common.GrayButton
import com.cmoney.kolfanci.ui.destinations.AddCategoryScreenDestination
import com.cmoney.kolfanci.ui.destinations.AddChannelScreenDestination
import com.cmoney.kolfanci.ui.destinations.EditCategoryScreenDestination
import com.cmoney.kolfanci.ui.destinations.SortCategoryScreenDestination
import com.cmoney.kolfanci.ui.destinations.SortChannelScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.group.channel.viewmodel.ChannelSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.channel.ChannelEditScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel


/**
 * 頻道管理
 */
@Destination
@Composable
fun ChannelSettingScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    viewModel: ChannelSettingViewModel = koinViewModel(),
    setChannelResult: ResultRecipient<AddChannelScreenDestination, Group>,
    setCategoryResult: ResultRecipient<AddCategoryScreenDestination, Group>,
    setEditCategoryResult: ResultRecipient<EditCategoryScreenDestination, Group>,
    sortCategoryResult: ResultRecipient<SortCategoryScreenDestination, Group>,
    sortChannelResult: ResultRecipient<SortChannelScreenDestination, Group>
) {
    val globalGroupViewModel = globalGroupViewModel()
    var groupParam = group

    val uiState = viewModel.uiState

    uiState.group?.let {
        groupParam = it
        globalGroupViewModel.setCurrentGroup(it)
    }

    //========== Result callback Start ==========
    sortChannelResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                viewModel.setGroup(result.value)
            }
        }
    }

    sortCategoryResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                viewModel.setGroup(result.value)
            }
        }
    }

    setEditCategoryResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                viewModel.setGroup(result.value)
            }
        }
    }

    setChannelResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                viewModel.setGroup(result.value)
            }
        }
    }

    setCategoryResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                viewModel.setGroup(result.value)
            }
        }
    }
    //========== Result callback End ==========
    ChannelSettingScreenView(
        modifier = modifier,
        navigator = navigator,
        group = groupParam
    ) {
        viewModel.onSortClick()
    }

    if (uiState.isOpenSortDialog) {
        SortDialog(
            onDismiss = {
                viewModel.closeSortDialog()
            },
            onChannelSort = {
                viewModel.closeSortDialog()
                navigator.navigate(
                    SortChannelScreenDestination(
                        group = uiState.group ?: group
                    )
                )
            },
            onCategorySort = {
                viewModel.closeSortDialog()
                navigator.navigate(
                    SortCategoryScreenDestination(
                        group = uiState.group ?: group
                    )
                )
            }
        )
    }

    LaunchedEffect(key1 = group) {
        AppUserLogger.getInstance().log(Page.GroupSettingsChannelManagement)
    }

}

@Composable
fun ChannelSettingScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    onSortClick: () -> Unit
) {
    val TAG = "ChannelSettingScreenView"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "頻道管理",
                backClick = {
                    navigator.popBackStack()
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .background(LocalColor.current.env_80)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            Row(
                modifier = Modifier.padding(24.dp)
            ) {

                if (Constant.MyGroupPermission.createOrEditCategory == true) {
                    BorderButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        text = "新增分類",
                        borderColor = LocalColor.current.text.default_100
                    ) {
                        KLog.i(TAG, "new category click.")
                        with(AppUserLogger.getInstance()) {
                            log(Clicked.ChannelManagementAddCategory)
                            log(Page.GroupSettingsChannelManagementAddCategory)
                        }
                        navigator.navigate(
                            AddCategoryScreenDestination(
                                group = group
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(23.dp))
                }

                if (Constant.MyGroupPermission.rearrangeChannelCategory == true) {
                    BorderButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        text = "重新排列",
                        borderColor = LocalColor.current.text.default_100
                    ) {
                        KLog.i(TAG, "category sort click.")
                        AppUserLogger.getInstance()
                            .log(Clicked.ChannelManagementEditOrder)
                        onSortClick.invoke()
                    }
                }
            }

            group.categories?.forEach { category ->
                ChannelEditScreen(
                    category = category,
                    channelList = category.channels.orEmpty(),
                    onCategoryEdit = {
                        KLog.i(TAG, "onCategoryEdit:$it")
                        with(AppUserLogger.getInstance()) {
                            log(Clicked.ChannelManagementEditCategory)
                            log(Page.GroupSettingsChannelManagementEditCategory)
                        }
                        navigator.navigate(
                            EditCategoryScreenDestination(
                                group = group,
                                category = it
                            )
                        )
                    },
                    onChanelEdit = { currentCategory, channel ->
                        KLog.i(TAG, "onChanelEdit:$channel")
                        AppUserLogger.getInstance()
                            .log(Clicked.ChannelManagementEditChannel)
                        navigator.navigate(
                            AddChannelScreenDestination(
                                group = group,
                                category = currentCategory,
                                channel = channel
                            )
                        )
                    },
                    onAddChannel = {
                        KLog.i(TAG, "onAddChannel:$it")
                        AppUserLogger.getInstance()
                            .log(Clicked.ChannelManagementAddChannel)
                        navigator.navigate(
                            AddChannelScreenDestination(
                                group = group,
                                category = it
                            )
                        )
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

    }
}

@Composable
private fun SortDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onChannelSort: () -> Unit,
    onCategorySort: () -> Unit
) {
    Dialog(onDismissRequest = {
        onDismiss()
    }) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier.padding(bottom = 25.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GrayButton(
                    text = "頻道排序",
                    shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                ) {
                    AppUserLogger.getInstance()
                        .log(Clicked.ChannelManagementOrderOption, From.Channel)
                    onChannelSort.invoke()
                }

                GrayButton(
                    text = "分類排序",
                    shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                ) {
                    AppUserLogger.getInstance()
                        .log(Clicked.ChannelManagementOrderOption, From.Category)
                    onCategorySort.invoke()
                }

                Spacer(modifier = Modifier.height(20.dp))

                GrayButton(
                    text = "返回"
                ) {
                    onDismiss()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChannelSettingScreenPreview() {
    FanciTheme {
        ChannelSettingScreenView(
            navigator = EmptyDestinationsNavigator,
            group = Group(
                categories = listOf(
                    Category(
                        name = "Welcome",
                        channels = listOf(
                            Channel(
                                name = "Channel 1"
                            ),
                            Channel(
                                name = "Channel 2"
                            )
                        )
                    ),
                    Category(
                        name = "Welcome2",
                        channels = listOf(
                            Channel(
                                name = "Channel 3"
                            ),
                            Channel(
                                name = "Channel 4"
                            )
                        )
                    )
                )
            ),
            onSortClick = {}
        )
    }
}