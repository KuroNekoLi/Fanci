package com.cmoney.fanci.ui.screens.group.setting.groupsetting.theme

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cmoney.fanci.MainStateHolder
import com.cmoney.fanci.MainViewModel
import com.cmoney.fanci.ui.screens.group.setting.groupsetting.theme.model.GroupTheme
import com.cmoney.fanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.screens.shared.theme.ThemeSettingItemScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.socks.library.KLog

/**
 * 主題色彩選擇畫面
 */
@Composable
fun GroupSettingThemeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    group: Group,
    viewModel: GroupSettingViewModel,
    globalViewModel: MainViewModel,
    route: (MainStateHolder.Route) -> Unit
) {
    val TAG = "GroupSettingThemeScreen"
    val state = viewModel.uiState
    val fetchAllTheme = remember {
        mutableStateOf(true)
    }

    var groupParam = group
    viewModel.uiState.settingGroup?.let {
        groupParam = it
        globalViewModel.setCurrentGroup(it)
    }

    GroupSettingThemeView(
        modifier,
        navController,
        isLoading = viewModel.uiState.isLoading,
        groupThemes = state.groupThemeList,
        group = groupParam,
        route = route
    ) {
        KLog.i(TAG, "on theme click.")
        viewModel.changeTheme(groupParam, it)
    }

    LaunchedEffect(fetchAllTheme) {
        viewModel.fetchAllTheme(groupParam)
    }
}

@Composable
private fun GroupSettingThemeView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    isLoading: Boolean,
    groupThemes: List<GroupTheme>,
    group: Group,
    route: (MainStateHolder.Route) -> Unit,
    onThemeConfirmClick: (GroupTheme) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                navController,
                title = "主題色彩",
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
                            route.invoke(MainStateHolder.GroupRoute.GroupSettingSettingThemePreview(
                                group = group,
                                themeId = it.id
                            ))
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
            navController = rememberNavController(),
            isLoading = false,
            groupThemes = emptyList(),
            group = Group(),
            route = {},
            onThemeConfirmClick = {}
        )
    }
}