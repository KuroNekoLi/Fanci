package com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.globalGroupViewModel
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.viewmodel.GroupViewModel
import com.cmoney.kolfanci.ui.destinations.EditInputScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupSettingAvatarScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupSettingBackgroundScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupSettingDescScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupSettingLogoScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupSettingThemeScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.avatar.ImageChangeData
import com.cmoney.kolfanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.toolbar.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.item.NarrowItem
import com.cmoney.kolfanci.ui.screens.shared.item.NarrowItemDefaults
import com.cmoney.kolfanci.ui.screens.shared.item.WideItem
import com.cmoney.kolfanci.ui.screens.shared.item.WideItemDefaults
import com.cmoney.kolfanci.ui.screens.shared.theme.ThemeColorCardScreen
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
 * 社團設定 - 社團設定
 */
@Destination
@Composable
fun GroupSettingSettingScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    setNameResult: ResultRecipient<EditInputScreenDestination, String>,
    setDescResult: ResultRecipient<GroupSettingDescScreenDestination, String>,
    setAvatarResult: ResultRecipient<GroupSettingAvatarScreenDestination, ImageChangeData>,
    setBackgroundResult: ResultRecipient<GroupSettingBackgroundScreenDestination, ImageChangeData>,
    group: Group,
) {
    val globalGroupViewModel = globalGroupViewModel()
    val viewModel: GroupSettingViewModel = koinViewModel()
    val uiState = viewModel.uiState
    val currentGroup by globalGroupViewModel.currentGroup.collectAsState()

    SetCallbackHandle(
        setNameResult = setNameResult,
        setDescResult = setDescResult,
        setAvatarResult = setAvatarResult,
        setBackgroundResult = setBackgroundResult,
        groupViewModel = globalGroupViewModel
    )

    GroupSettingSettingView(
        modifier = modifier,
        navController = navController,
        group = uiState.settingGroup ?: group
    )

    LaunchedEffect(key1 = currentGroup) {
        currentGroup?.let { focusGroup ->
            viewModel.settingGroup(focusGroup)
        }
    }
}

/**
 * 處理 所有更改過的 callback
 */
@Composable
private fun SetCallbackHandle(
    setNameResult: ResultRecipient<EditInputScreenDestination, String>,
    setDescResult: ResultRecipient<GroupSettingDescScreenDestination, String>,
    groupViewModel: GroupViewModel,
    setAvatarResult: ResultRecipient<GroupSettingAvatarScreenDestination, ImageChangeData>,
    setBackgroundResult: ResultRecipient<GroupSettingBackgroundScreenDestination, ImageChangeData>
) {
    //更改名字 callback
    setNameResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val changeName = result.value
                groupViewModel.changeGroupName(name = changeName)
            }
        }
    }

    //更改簡介 callback
    setDescResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val desc = result.value
                groupViewModel.changeGroupDesc(desc = desc)
            }
        }
    }

    //更改頭貼
    setAvatarResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val uri = result.value
                groupViewModel.changeGroupAvatar(uri)
            }
        }
    }

    //更改背景
    setBackgroundResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val uri = result.value
                groupViewModel.changeGroupCover(uri)
            }
        }
    }
}

@Composable
fun GroupSettingSettingView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group
) {
    val TAG = "GroupSettingSettingScreen"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "社團設定",
                backClick = {
                    navController.popBackStack()
                }
            )
        }
    ) { innerPadding ->
        val context = LocalContext.current
        LazyColumn(
            modifier = Modifier
                .background(LocalColor.current.env_80)
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            //========== 名稱 ==========
            item {
                NarrowItem(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .background(color = LocalColor.current.background)
                        .padding(NarrowItemDefaults.paddingValues),
                    title = stringResource(id = R.string.group_name),
                    subTitle = group.name.orEmpty(),
                    actionContent = NarrowItemDefaults.nextIcon(),
                    onClick = {
                        KLog.i(TAG, "name click")
                        navController.navigate(
                            EditInputScreenDestination(
                                defaultText = group.name.orEmpty(),
                                toolbarTitle = context.getString(R.string.group_name),
                                placeholderText = context.getString(R.string.group_name_placeholder),
                                emptyAlertTitle = context.getString(R.string.group_name_empty),
                                emptyAlertSubTitle = context.getString(R.string.group_name_empty_desc),
                                from = From.GroupName,
                                textFieldClicked = Clicked.GroupNameName
                            )
                        )
                        with(AppUserLogger.getInstance()) {
                            log(Page.GroupSettingsGroupSettingsGroupName)
                            log(Clicked.GroupName)
                        }
                    }
                )
            }

            //========== 簡介 ==========
            item {
                val isNoDescription = group.description.isNullOrBlank()
                NarrowItem(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .background(color = LocalColor.current.background)
                        .padding(NarrowItemDefaults.paddingValues),
                    title = stringResource(id = R.string.group_description),
                    subTitle = if (isNoDescription) {
                        stringResource(id = R.string.group_description_placeholder)
                    } else {
                        group.description.orEmpty()
                    },
                    subTitleColor = if (isNoDescription) {
                        LocalColor.current.text.default_30
                    } else {
                        LocalColor.current.text.default_100
                    },
                    actionContent = NarrowItemDefaults.nextIcon(),
                    onClick = {
                        KLog.i(TAG, "description click")
                        AppUserLogger.getInstance()
                            .log(Clicked.GroupIntroduction)
                        navController.navigate(GroupSettingDescScreenDestination(group = group))
                    }
                )
            }
            //========== 社團Logo ==========
            item {
                WideItem(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .background(LocalColor.current.background)
                        .padding(WideItemDefaults.paddingValues),
                    title = stringResource(id = R.string.group_logo),
                    displayContent = WideItemDefaults.imageDisplay(
                        model = group.logoImageUrl,
                        modifier = Modifier
                            .size(width = 125.dp, height = 40.dp)
                    ),
                    onClick = {
                        KLog.i(TAG, "logo image click")
                        navController.navigate(GroupSettingLogoScreenDestination(group = group))
                    }
                )
            }
            //========== 社團圖示 ==========
            item {
                WideItem(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .background(LocalColor.current.background)
                        .padding(WideItemDefaults.paddingValues),
                    title = stringResource(id = R.string.group_avatar),
                    displayContent = WideItemDefaults.imageDisplay(model = group.thumbnailImageUrl),
                    onClick = {
                        KLog.i(TAG, "avatar image click")
                        AppUserLogger.getInstance()
                            .log(Clicked.GroupIcon)
                        navController.navigate(GroupSettingAvatarScreenDestination(group = group))
                    }
                )
            }

            //========== 首頁背景 ==========
            item {
                WideItem(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .background(LocalColor.current.background)
                        .padding(WideItemDefaults.paddingValues),
                    title = stringResource(id = R.string.group_homepage_background),
                    displayContent = WideItemDefaults.imageDisplay(model = group.coverImageUrl),
                    onClick = {
                        KLog.i(TAG, "background image click")
                        AppUserLogger.getInstance()
                            .log(Clicked.HomeBackground)
                        navController.navigate(
                            GroupSettingBackgroundScreenDestination(
                                group = group
                            )
                        )
                    }
                )
            }

            //========== 主題色彩 ==========
            item {
                WideItem(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .background(LocalColor.current.background)
                        .padding(WideItemDefaults.paddingValues),
                    title = stringResource(id = R.string.theme_color),
                    displayContent = {
                        ThemeColorCardScreen(modifier = Modifier.size(56.dp))
                    },
                    onClick = {
                        KLog.i(TAG, "theme click")
                        AppUserLogger.getInstance()
                            .log(Clicked.ThemeColor)
                        navController.navigate(
                            GroupSettingThemeScreenDestination(
                                group = group
                            )
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupSettingSettingView() {
    FanciTheme {
        GroupSettingSettingView(
            group = Group(
                name = "韓勾ㄟ金針菇討論區",
                description = "我愛金針菇\uD83D\uDC97這裡是一群超愛金針菇的人類！喜歡的人就趕快來參加吧吧啊！"
            ),
            navController = EmptyDestinationsNavigator
        )
    }
}