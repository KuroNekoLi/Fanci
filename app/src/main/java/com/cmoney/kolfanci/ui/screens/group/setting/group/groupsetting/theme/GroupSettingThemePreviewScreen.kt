package com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.main.LocalDependencyContainer
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.theme.model.GroupTheme
import com.cmoney.kolfanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.dialog.ChangeThemeDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.theme.ThemeSettingItemScreen
import com.cmoney.kolfanci.ui.screens.shared.toolbar.EditToolbarScreen
import com.cmoney.kolfanci.ui.theme.DefaultThemeColor
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.google.gson.Gson
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.socks.library.KLog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * 主題預覽, 社團背景
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
    val globalGroupViewModel = LocalDependencyContainer.current.globalGroupViewModel
    val currentGroup by globalGroupViewModel.currentGroup.collectAsState()
    var groupParam = currentGroup

    var showConfirmDialog: GroupTheme? by remember {
        mutableStateOf(null)
    }

    viewModel.uiState.settingGroup?.let {
        groupParam = it
        globalGroupViewModel.setCurrentGroup(it)
    }

    GroupSettingThemePreviewView(
        modifier,
        navController,
        groupTheme = viewModel.uiState.previewTheme,
    ) {
        KLog.i(TAG, "on theme click.")
        showConfirmDialog = it
    }

    showConfirmDialog?.let {
        //如果是建立 直接確定並返回
        if (isFromCreate) {
            val gson = Gson()
            resultNavigator.navigateBack(gson.toJson(it))
        } else {
            ChangeThemeDialogScreen(
                groupTheme = it,
                onDismiss = {
                    showConfirmDialog = null
                },
                onConfirm = {
                    showConfirmDialog = null
                    viewModel.changeTheme(groupParam, it)
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        launch {
            viewModel.fetchThemeInfo(themeId)
        }
        launch {
            viewModel.confirmNewThemeEvent
                .onEach { groupThemeId ->
                    println("confirm")
                    resultNavigator.navigateBack(groupThemeId)
                }
                .collect()
        }
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
            EditToolbarScreen(
                title = stringResource(id = R.string.theme_preview),
                confirmText = stringResource(id = R.string.apply),
                backClick = {
                    navController.popBackStack()
                },
                saveClick = {
                    groupTheme?.let {
                        onThemeConfirmClick.invoke(groupTheme)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier.padding(innerPadding)
        ) {
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
                        }
                    )
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(LocalColor.current.env_40),
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
                            placeholder = painterResource(id = R.drawable.placeholder)
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