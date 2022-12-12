package com.cmoney.fanci.ui.screens.group.setting.channel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanci.LocalDependencyContainer
import com.cmoney.fanci.destinations.AddChannelScreenDestination
import com.cmoney.fanci.ui.common.BorderButton
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.screens.shared.channel.ChannelEditScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.Group
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
    setChannelResult: ResultRecipient<AddChannelScreenDestination, Channel>
) {
    val globalViewModel = LocalDependencyContainer.current.globalViewModel
    var groupParam = group
    viewModel.uiState.group?.let {
        groupParam = it
        globalViewModel.setCurrentGroup(it)
    }

    setChannelResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                val channel = result.value
                viewModel.addChannelToGroup(channel = channel, group = group)
            }
        }
    }

    ChannelSettingScreenView(
        modifier,
        navigator,
        groupParam
    )
}

@Composable
fun ChannelSettingScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group
) {
    val TAG = "ChannelSettingScreenView"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "頻道管理",
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
                .background(LocalColor.current.env_80)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.padding(24.dp)
            ) {
                BorderButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    text = "新增分類",
                    borderColor = LocalColor.current.text.default_100
                ) {
                    KLog.i(TAG, "new category click.")
                }

                Spacer(modifier = Modifier.width(23.dp))

                BorderButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    text = "重新排列",
                    borderColor = LocalColor.current.text.default_100
                ) {
                    KLog.i(TAG, "category sort click.")
                }
            }


            group.categories?.forEach { category ->
                ChannelEditScreen(
                    category = category,
                    channelList = category.channels.orEmpty(),
                    onCategoryEdit = {
                        KLog.i(TAG, "onCategoryEdit:$it")
                    },
                    onChanelEdit = {
                        KLog.i(TAG, "onChanelEdit:$it")
                    },
                    onAddChannel = {
                        KLog.i(TAG, "onAddChannel:$it")
                        navigator.navigate(
                            AddChannelScreenDestination(
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
            )
        )
    }
}