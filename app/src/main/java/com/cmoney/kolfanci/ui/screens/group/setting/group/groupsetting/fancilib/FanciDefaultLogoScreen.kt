package com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.fancilib

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.cmoney.kolfanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

/**
 * 預設Logo圖庫
 */
@Destination
@Composable
fun FanciDefaultLogoScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    viewModel: GroupSettingViewModel = koinViewModel(),
    fanciResultNavigator: ResultBackNavigator<String>
) {
    //TODO 待做，以下為測試
    val TAG = "FanciDefaultLogoScreen"
    val state = viewModel.uiState

    FanciDefaultAvatarView(
        modifier = modifier,
        navController = navController,
        imageUrl = state.groupAvatarLib,
        isLoading = state.isLoading
    ) {
        KLog.i(TAG, "image click:$it")
        fanciResultNavigator.navigateBack(it)
    }

    LaunchedEffect(Unit) {
        viewModel.fetchFanciAvatarLib()
    }
}