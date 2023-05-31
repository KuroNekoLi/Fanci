package com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.common.TransparentButton
import com.cmoney.kolfanci.ui.destinations.FanciDefaultCoverScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.avatar.ImageChangeData
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.state.GroupSettingSettingState
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.state.rememberGroupSettingSettingState
import com.cmoney.kolfanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.GroupPhotoPickDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.SaveConfirmDialogScreen
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

@Destination
@Composable
fun GroupSettingBackgroundScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    viewModel: GroupSettingViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<ImageChangeData>,
    fanciCoverResult: ResultRecipient<FanciDefaultCoverScreenDestination, String>
) {
    val state = viewModel.uiState

    var showSaveTip by remember {
        mutableStateOf(false)
    }

    fanciCoverResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                val fanciUrl = result.value
                viewModel.onGroupCoverSelect(fanciUrl, group)
            }
        }
    }

    GroupSettingBackgroundView(
        modifier,
        navController,
        isLoading = viewModel.uiState.isLoading,
        group = state.settingGroup ?: group,
        onImageChange = {
            resultNavigator.navigateBack(it)
        },
        onBack = {
            showSaveTip = true
        }
    )

    SaveConfirmDialogScreen(
        isShow = showSaveTip,
        onContinue = {
            showSaveTip = false
        },
        onGiveUp = {
            showSaveTip = false
            navController.popBackStack()
        }
    )

//    LaunchedEffect(viewModel.uiState.isGroupSettingPop) {
//        if (viewModel.uiState.isGroupSettingPop) {
//            navController.popBackStack()
//            viewModel.changeGroupInfoScreenDone()
//        }
//    }
}

@Composable
fun GroupSettingBackgroundView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    state: GroupSettingSettingState = rememberGroupSettingSettingState(),
    isLoading: Boolean,
    onImageChange: (ImageChangeData) -> Unit,
    onBack: () -> Unit
) {
    val TAG = "GroupSettingAvatarView"
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            EditToolbarScreen(
                title = stringResource(id = R.string.group_background),
                saveClick = {
                    KLog.i(TAG, "on save click.")
                    state.coverImageUrl.value?.let {
                        onImageChange.invoke(
                            ImageChangeData(
                                uri = it,
                                url = null
                            )
                        )
                    } ?: kotlin.run {
                        onImageChange.invoke(
                            ImageChangeData(
                                uri = null,
                                url = group.coverImageUrl.orEmpty()
                            )
                        )
                    }
                },
                backClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Bottom
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    AsyncImage(
                        model = state.coverImageUrl.value ?: group.coverImageUrl,
                        modifier = Modifier
                            .size(screenWidth)
                            .aspectRatio(1f),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        placeholder = painterResource(id = R.drawable.placeholder)
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .size(width = 340.dp, height = 185.dp)
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .background(
                                LocalColor.current.env_80
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "面板覆蓋處",
                            fontSize = 30.sp,
                            color = LocalColor.current.text.default_30
                        )
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(45.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            TransparentButton(
                text = "更換圖片"
            ) {
                KLog.i(TAG, "button click.")
                state.openCameraDialog()
            }
        }

        if (state.openCameraDialog.value) {
            GroupPhotoPickDialogScreen(
                onDismiss = {
                    state.closeCameraDialog()
                },
                onAttach = {
                    state.setBackgroundImage(it)
                },
                onFanciClick = {
                    navController.navigate(FanciDefaultCoverScreenDestination)
                }
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
fun GroupSettingBackgroundPreview() {
    FanciTheme {
        GroupSettingBackgroundView(
            navController = EmptyDestinationsNavigator,
            group = Group(),
            isLoading = true,
            onImageChange = {},
            onBack = {}
        )
    }
}