package com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.avatar

import android.net.Uri
import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.common.TransparentButton
import com.cmoney.kolfanci.ui.destinations.FanciDefaultAvatarScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.dialog.GroupPhotoPickDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.SaveConfirmDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.toolbar.EditToolbarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel

@Parcelize
data class ImageChangeData(
    val uri: Uri?,
    val url: String?
) : Parcelable

@Destination
@Composable
fun GroupSettingAvatarScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    viewModel: GroupSettingViewModel = koinViewModel(),
    groupSettingImageViewModel: GroupSettingImageViewModel = koinViewModel(),
    resultNavigator: ResultBackNavigator<ImageChangeData>,
    fanciAvatarResult: ResultRecipient<FanciDefaultAvatarScreenDestination, String>
) {
    var settingGroup by remember {
        mutableStateOf(group)
    }

    val uiState = groupSettingImageViewModel.uiState

    var showSaveTip by remember {
        mutableStateOf(false)
    }

    //是否為建立社團開啟
    val isFromCreate = group.id.isNullOrEmpty()

    fanciAvatarResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val fanciUrl = result.value

                settingGroup = settingGroup.copy(
                    thumbnailImageUrl = fanciUrl
                )
                groupSettingImageViewModel.resetCameraUri()
            }
        }
    }

    GroupSettingAvatarView(
        modifier = modifier,
        group = settingGroup,
        isLoading = viewModel.uiState.isLoading,
        onImageChange = {
            if (isFromCreate) {
                AppUserLogger.getInstance().log(Clicked.Confirm, From.AddGroupIcon)
            } else {
                AppUserLogger.getInstance().log(Clicked.Confirm, From.EditGroupIcon)
            }
            resultNavigator.navigateBack(
                it
            )
        },
        avatarImage = uiState.image,
        openCameraDialog = {
            if (isFromCreate) {
                AppUserLogger.getInstance().log(Clicked.CreateGroupChangeGroupIconPicture)
            } else {
                AppUserLogger.getInstance().log(Clicked.GroupIconChangePicture)
            }
            groupSettingImageViewModel.openCameraDialog()
        }
    ) {
        showSaveTip = true
    }

    if (uiState.openCameraDialog) {
        GroupPhotoPickDialogScreen(
            quantityLimit = 1,
            onDismiss = {
                groupSettingImageViewModel.closeCameraDialog()
            },
            onAttach = { photoUris ->
                photoUris.firstOrNull()?.let {
                    groupSettingImageViewModel.setImage(it)
                }
            },
            onFanciClick = {
                groupSettingImageViewModel.closeCameraDialog()
                navController.navigate(FanciDefaultAvatarScreenDestination)
            }
        )
    }

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

    LaunchedEffect(key1 = group) {
        AppUserLogger.getInstance()
            .log(Page.GroupSettingsGroupSettingsGroupIcon)
    }
}

@Composable
fun GroupSettingAvatarView(
    modifier: Modifier = Modifier,
    group: Group,
    isLoading: Boolean,
    onImageChange: (ImageChangeData) -> Unit,
    avatarImage: Uri?,
    openCameraDialog: () -> Unit,
    onBack: () -> Unit
) {
    val TAG = "GroupSettingAvatarView"
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            EditToolbarScreen(
                title = stringResource(id = R.string.group_avatar),
                saveClick = {
                    KLog.i(TAG, "on image save click.")
                    avatarImage?.let {
                        onImageChange.invoke(
                            ImageChangeData(
                                uri = it,
                                url = null
                            )
                        )
                    } ?: run {
                        onImageChange.invoke(
                            ImageChangeData(
                                uri = null,
                                url = group.thumbnailImageUrl.orEmpty()
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
                .background(LocalColor.current.env_80)
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
                val imageModel = avatarImage ?: group.thumbnailImageUrl

                imageModel?.let {
                    AsyncImage(
                        model = imageModel,
                        modifier = Modifier
                            .size(182.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(60.dp)),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        placeholder = painterResource(id = R.drawable.placeholder)
                    )
                } ?: kotlin.run {
                    //Empty View
                    Box(
                        modifier = Modifier
                            .size(182.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(60.dp))
                            .background(colorResource(id = R.color.color_D9D9D9))
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

            //========== 儲存 ==========
            TransparentButton(
                text = "更換圖片"
            ) {
                KLog.i(TAG, "button click.")
                openCameraDialog.invoke()
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GroupSettingAvatarScreenPreview() {
    FanciTheme {
        GroupSettingAvatarView(
            group = Group(),
            isLoading = true,
            onImageChange = {},
            avatarImage = null,
            openCameraDialog = {}
        ) {}
    }
}