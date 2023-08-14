package com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.globalGroupViewModel
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.destinations.GroupSettingThemePreviewScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.theme.model.GroupTheme
import com.cmoney.kolfanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.theme.ThemeSettingItemScreen
import com.cmoney.kolfanci.ui.theme.CoffeeThemeColor
import com.cmoney.kolfanci.ui.theme.DefaultThemeColor
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import org.koin.androidx.compose.koinViewModel

/**
 * 主題色彩選擇畫面
 */
@Destination
@Composable
fun GroupSettingThemeScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    viewModel: GroupSettingViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<String>,
    themeResult: ResultRecipient<GroupSettingThemePreviewScreenDestination, String>
) {
    val TAG = "GroupSettingThemeScreen"

    val globalGroupViewModel = globalGroupViewModel()
    val currentGroup by globalGroupViewModel.currentGroup.collectAsState()
    val uiState = viewModel.uiState
    val isFromCreate = group.id.isNullOrBlank()
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
        groupThemes = uiState.groupThemeList,
        isFromCreate = isFromCreate
    )

    LaunchedEffect(Unit) {
        viewModel.fetchAllTheme(currentGroup)
    }
    LaunchedEffect(key1 = group) {
        if (!isFromCreate) {
            AppUserLogger.getInstance()
                .log(Page.GroupSettingsGroupSettingsThemeColor)
        }
    }
}

@Composable
private fun GroupSettingThemeView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    groupThemes: List<GroupTheme>,
    isFromCreate: Boolean
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = stringResource(id = R.string.theme_color),
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
                        isSelected = (!isFromCreate && it.isSelected),
                        onItemClick = {
                            if (isFromCreate) {
                                AppUserLogger.getInstance().log(Clicked.CreateGroupThemeColorTheme)
                            } else {
                                AppUserLogger.getInstance().log(Clicked.ThemeColorTheme)
                            }

                            navController.navigate(
                                GroupSettingThemePreviewScreenDestination(
                                    themeId = it.id,
                                    isFromCreate = isFromCreate
                                )
                            )
                        }
                    )
                }
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
            groupThemes = listOf(
                GroupTheme(
                    id = "",
                    isSelected = true,
                    theme = DefaultThemeColor,
                    name = "測試主題1",
                    preview = emptyList()
                ),
                GroupTheme(
                    id = "",
                    isSelected = true,
                    theme = CoffeeThemeColor,
                    name = "測試主題2",
                    preview = emptyList()
                )
            ),
            isFromCreate = false
        )
    }
}