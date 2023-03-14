package com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.LocalDependencyContainer
import com.cmoney.kolfanci.destinations.GroupSettingThemePreviewScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.theme.model.GroupTheme
import com.cmoney.kolfanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.theme.ThemeSettingItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.google.gson.Gson
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

/**
 * 主題色彩選擇畫面
 */
@Destination
@Composable
fun GroupSettingThemeScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    isFromCreate: Boolean = false,
    viewModel: GroupSettingViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<String>,
    themeResult: ResultRecipient<GroupSettingThemePreviewScreenDestination, String>
) {
    val TAG = "GroupSettingThemeScreen"
    val globalViewModel = LocalDependencyContainer.current.globalViewModel
    val state = viewModel.uiState

    var groupParam = globalViewModel.uiState.currentGroup
    viewModel.uiState.settingGroup?.let {
        groupParam = it
        globalViewModel.setCurrentGroup(it)
    }

    themeResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                val groupThemeStr = result.value
                if (isFromCreate) {
                    resultNavigator.navigateBack(groupThemeStr)
                }
            }
        }
    }

    GroupSettingThemeView(
        modifier,
        navController,
        isLoading = viewModel.uiState.isLoading,
        groupThemes = state.groupThemeList,
        isFromCreate = isFromCreate,
        onThemeConfirmClick = {
            KLog.i(TAG, "on theme click.")
            if (isFromCreate) {
                val gson = Gson()
                resultNavigator.navigateBack(gson.toJson(it))
            }
            else {
                groupParam?.let {groupParam ->
                    viewModel.changeTheme(groupParam, it)
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.fetchAllTheme(groupParam, isFromCreate)
    }
}

@Composable
private fun GroupSettingThemeView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    isLoading: Boolean,
    groupThemes: List<GroupTheme>,
    isFromCreate: Boolean,
    onThemeConfirmClick: (GroupTheme) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "主題色彩",
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
        ) {
            LazyColumn(
                state = rememberLazyListState(),
            ) {
                items(groupThemes) {
                    ThemeSettingItemScreen(
                        primary = it.theme.primary,
                        env_100 = it.theme.env_100,
                        env_80 = it.theme.env_80,
                        env_60 = it.theme.env_60,
                        name = it.name,
                        isSelected = it.isSelected,
                        onItemClick = {
                            navController.navigate(
                                GroupSettingThemePreviewScreenDestination(
                                    themeId = it.id,
                                    isFromCreate = isFromCreate
                                )
                            )
                        },
                        onConfirm = {
                            onThemeConfirmClick.invoke(it)
                        }
                    )
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(45.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

        }
    }

}

@Preview(showBackground = true)
@Composable
fun GroupSettingThemeScreenPreview() {
    FanciTheme {
        GroupSettingThemeView(
            navController = EmptyDestinationsNavigator,
            isLoading = false,
            groupThemes = emptyList(),
            isFromCreate = false,
            onThemeConfirmClick = {}
        )
    }
}