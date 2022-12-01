package com.cmoney.fanci.ui.screens.group.setting.groupsetting

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.cmoney.fanci.MainStateHolder
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.common.TransparentButton
import com.cmoney.fanci.ui.screens.group.setting.groupsetting.state.GroupSettingSettingState
import com.cmoney.fanci.ui.screens.group.setting.groupsetting.state.rememberGroupSettingSettingState
import com.cmoney.fanci.ui.screens.group.setting.viewmodel.GroupSettingViewModel
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.screens.shared.dialog.GroupPhotoPickDialogScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.socks.library.KLog

@Composable
fun GroupSettingBackgroundScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    group: Group,
    viewModel: GroupSettingViewModel,
    route: (MainStateHolder.Route) -> Unit
) {
    val state = viewModel.uiState

    GroupSettingBackgroundView(
        modifier,
        navController,
        isLoading = viewModel.uiState.isLoading,
        group = state.settingGroup ?: group,
        route = route
    ) {
        viewModel.changeGroupCover(it, group)
    }

    LaunchedEffect(viewModel.uiState.isGroupSettingPop) {
        if (viewModel.uiState.isGroupSettingPop) {
            navController.popBackStack()
            viewModel.changeGroupInfoScreenDone()
        }
    }
}

@Composable
fun GroupSettingBackgroundView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    group: Group,
    state: GroupSettingSettingState = rememberGroupSettingSettingState(),
    isLoading: Boolean,
    route: (MainStateHolder.Route) -> Unit,
    onImageChange: (Any) -> Unit
) {
    val TAG = "GroupSettingAvatarView"
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                navController,
                title = "社團背景",
                leadingEnable = true,
                moreEnable = false
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
                        placeholder = painterResource(id = R.drawable.resource_default)
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

            //========== 儲存 ==========
            TransparentButton(
                text = "更換圖片"
            ) {
                KLog.i(TAG, "button click.")
                state.openCameraDialog()
            }

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
                        KLog.i(TAG, "on save click.")
                        state.coverImageUrl.value?.let {
                            onImageChange.invoke(it)
                        } ?: kotlin.run {
                            onImageChange.invoke(group.coverImageUrl.orEmpty())
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
                    state.setBackgroundImage(it)
                },
                onFanciClick = {
                    route.invoke(
                        MainStateHolder.GroupRoute.GroupSettingSettingCoverFanci(
                            group = group
                        )
                    )
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
            navController = rememberNavController(),
            group = Group(),
            isLoading = true,
            route = {}
        ) {}
    }
}