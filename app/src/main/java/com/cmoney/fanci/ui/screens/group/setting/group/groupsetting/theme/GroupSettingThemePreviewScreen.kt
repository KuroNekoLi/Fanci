package com.cmoney.fanci.ui.screens.group.setting.group.groupsetting.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cmoney.fanci.LocalDependencyContainer
import com.cmoney.fanci.MainViewModel
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.screens.group.setting.group.groupsetting.theme.model.GroupTheme
import com.cmoney.fanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.screens.shared.theme.ThemeSettingItemScreen
import com.cmoney.fanci.ui.theme.DefaultThemeColor
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.google.gson.Gson
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

/**
 * 主題預覽
 */
@Destination
@Composable
fun GroupSettingThemePreviewScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    isFromCreate: Boolean = false,
    themeId: String,
    viewModel: GroupSettingViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<String>
) {
    val TAG = "GroupSettingThemePreviewScreen"
    val globalViewModel = LocalDependencyContainer.current.globalViewModel
    var groupParam = globalViewModel.uiState.currentGroup!!
    viewModel.uiState.settingGroup?.let {
        groupParam = it
        globalViewModel.setCurrentGroup(it)
    }

    GroupSettingThemePreviewView(
        modifier,
        navController,
        groupTheme = viewModel.uiState.previewTheme,
    ) {
        KLog.i(TAG, "on theme click.")
        if (isFromCreate) {
            val gson = Gson()
            resultNavigator.navigateBack(gson.toJson(it))
        }
        else {
            viewModel.changeTheme(groupParam, it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchThemeInfo(themeId)
    }
}

@Composable
private fun GroupSettingThemePreviewView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    groupTheme: GroupTheme?,
    onThemeConfirmClick: (GroupTheme) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "主題預覽",
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
        Column {
            groupTheme?.let { groupTheme ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LocalColor.current.env_100)
                        .height(130.dp)
                ) {
                    ThemeSettingItemScreen(
                        primary = groupTheme.theme.primary,
                        env_100 = groupTheme.theme.env_100,
                        env_80 = groupTheme.theme.env_80,
                        env_60 = groupTheme.theme.env_60,
                        name = groupTheme.name,
                        isSelected = groupTheme.isSelected,
                        isShowArrow = false,
                        onItemClick = {
                        },
                        onConfirm = {
                            onThemeConfirmClick.invoke(groupTheme)
                        }
                    )
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(LocalColor.current.background),
                    contentPadding = PaddingValues(29.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    items(groupTheme.preview) {
                        AsyncImage(
                            modifier = Modifier
                                .clip(RoundedCornerShape(15.dp))
                                .size(width = 147.dp, height = 326.dp),
                            model = it,
                            contentScale = ContentScale.FillBounds,
                            contentDescription = null,
                            placeholder = painterResource(id = R.drawable.resource_default)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupSettingThemePreviewScreenPreview() {
    FanciTheme {
        GroupSettingThemePreviewView(
            navController = EmptyDestinationsNavigator,
            groupTheme = GroupTheme(
                id = "",
                isSelected = false,
                name = "Hello",
                preview = listOf("1", "2"),
                theme = DefaultThemeColor
            ),
            onThemeConfirmClick = {}
        )
    }

}