package com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting

import android.net.Uri
import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.destinations.FanciDefaultAvatarScreenDestination
import com.cmoney.kolfanci.ui.common.TransparentButton
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.state.GroupSettingSettingState
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.state.rememberGroupSettingSettingState
import com.cmoney.kolfanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.GroupPhotoPickDialogScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import kotlinx.android.parcel.Parcelize
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
    resultNavigator: ResultBackNavigator<ImageChangeData>,
    fanciAvatarResult: ResultRecipient<FanciDefaultAvatarScreenDestination, String>
) {
    val state = viewModel.uiState

    fanciAvatarResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                val fanciUrl = result.value
                viewModel.onGroupAvatarSelect(fanciUrl, group)
            }
        }
    }

    GroupSettingAvatarView(
        modifier,
        navController,
        isLoading = viewModel.uiState.isLoading,
        group = state.settingGroup ?: group,
        onImageChange = {
            resultNavigator.navigateBack(
                it
            )
        }
    )
}

@Composable
fun GroupSettingAvatarView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    state: GroupSettingSettingState = rememberGroupSettingSettingState(),
    isLoading: Boolean,
    onImageChange: (ImageChangeData) -> Unit
) {
    val TAG = "GroupSettingAvatarView"
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "社團圖示",
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
                AsyncImage(
                    model = state.avatarImage.value ?: group.thumbnailImageUrl,
                    modifier = Modifier
                        .size(182.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(60.dp)),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    placeholder = painterResource(id = R.drawable.resource_default)
                )
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
                state.openCameraDialog()
            }

            Spacer(modifier = Modifier.height(50.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .background(LocalColor.current.env_100),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    modifier = Modifier
                        .padding(25.dp)
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = LocalColor.current.primary),
                    onClick = {
                        KLog.i(TAG, "on image save click.")
                        state.avatarImage.value?.let {
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
                                    url = group.thumbnailImageUrl.orEmpty()
                                )
                            )
                        }
                    }) {
                    Text(
                        text = "儲存",
                        color = LocalColor.current.text.other,
                        fontSize = 16.sp
                    )
                }
            }
        }

        if (state.openCameraDialog.value) {
            GroupPhotoPickDialogScreen(
                onDismiss = {
                    state.closeCameraDialog()
                },
                onAttach = {
                    KLog.i(TAG, "onAttach click.")
                    state.setAvatarImage(it)
                },
                onFanciClick = {
                    KLog.i(TAG, "onFanciClick")
                    state.closeCameraDialog()
                    navController.navigate(FanciDefaultAvatarScreenDestination)
                }
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
fun GroupSettingAvatarScreenPreview() {
    FanciTheme {
        GroupSettingAvatarView(
            navController = EmptyDestinationsNavigator,
            group = Group(),
            isLoading = true,
            onImageChange = {}
        )
    }
}