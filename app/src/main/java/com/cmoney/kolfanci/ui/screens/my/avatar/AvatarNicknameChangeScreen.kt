package com.cmoney.kolfanci.ui.screens.my.avatar

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.viewmodel.UserViewModel
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.avatar.GroupSettingImageViewModel
import com.cmoney.kolfanci.ui.screens.shared.toolbar.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.GroupPhotoPickDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.SaveConfirmDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.setting.BottomButtonScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.xlogin.XLoginHelper
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import org.koin.androidx.compose.koinViewModel

/**
 * 頭像與暱稱
 */
@Destination
@Composable
fun AvatarNicknameChangeScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    viewModel: GroupSettingImageViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel()
) {
    val TAG = "AvatarNicknameChangeScreen"
    val maxNickNameSize = 10
    var textState by remember { mutableStateOf(XLoginHelper.nickName) }

    val isLoading = userViewModel.isLoading.collectAsState(false)
    val isSaveComplete = userViewModel.isComplete.collectAsState(false)

    val showEmptyNameDialog by userViewModel.showEmptyNameDialog.collectAsState()

    var showSaveTip by remember {
        mutableStateOf(false)
    }

    if (isSaveComplete.value) {
        navController.popBackStack()
    }

    AvatarNicknameChangeScreenView(
        modifier = modifier,
        navController = navController,
        defaultText = textState,
        avatarImage = viewModel.uiState.image,
        maxLength = maxNickNameSize,
        input = {
            textState = it
        },
        isLoading = isLoading.value,
        onChangeAvatar = {
            AppUserLogger.getInstance().log(Clicked.AvatarAndNicknameAvatar)
            viewModel.openCameraDialog()
        },
        onSave = {
            userViewModel.changeNicknameAndAvatar(
                nickName = textState,
                avatarUri = viewModel.uiState.image
            )
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

    //名稱為空 彈窗
    if (showEmptyNameDialog) {
        DialogScreen(
            title = stringResource(id = R.string.user_name_empty),
            subTitle = stringResource(id = R.string.user_name_empty_desc),
            onDismiss = {
                userViewModel.dismissEmptyNameDialog()
            }
        ) {
            BlueButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = stringResource(id = R.string.confirm)
            ) {
                userViewModel.dismissEmptyNameDialog()
            }
        }
    }

    //相片選擇 彈窗
    if (viewModel.uiState.openCameraDialog) {
        GroupPhotoPickDialogScreen(
            isShowFanciPic = false,
            quantityLimit = 1,
            onDismiss = {
                viewModel.closeCameraDialog()
            },
            onAttach = { photoUris ->
                photoUris.firstOrNull()?.let {
                    viewModel.setAvatarImage(it)
                }
            },
            onFanciClick = {
            }
        )
    }
    LaunchedEffect(key1 = Unit) {
        AppUserLogger.getInstance()
            .log(page = Page.MemberPageAvatarAndNickname)
    }
}

@Composable
private fun AvatarNicknameChangeScreenView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    avatarImage: Uri?,
    onChangeAvatar: () -> Unit,
    onSave: () -> Unit,
    defaultText: String,
    maxLength: Int,
    input: (String) -> Unit,
    isLoading: Boolean,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBarScreen(
                title = stringResource(id = R.string.avatar_nickname),
                backClick = {
                    onBack.invoke()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(LocalColor.current.env_80)
                    .padding(start = 24.dp, end = 24.dp)
            ) {

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = stringResource(id = R.string.my_avatar_in_group),
                    fontSize = 14.sp,
                    color = LocalColor.current.text.default_100
                )

                Spacer(modifier = Modifier.height(24.dp))

                //Avatar
                Box {
                    AsyncImage(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(CircleShape)
                            .clickable {
                                onChangeAvatar.invoke()
                            },
                        model = avatarImage ?: XLoginHelper.headImagePath,
                        placeholder = painterResource(id = R.drawable.placeholder),
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )

                    //Camera
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .align(Alignment.BottomEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.size(26.dp),
                            painter = painterResource(id = R.drawable.camera_setting),
                            contentDescription = null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.my_nickname_in_group),
                        fontSize = 14.sp,
                        color = LocalColor.current.text.default_100
                    )

                    Text(
                        text = "%d/%d".format(defaultText.length, maxLength),
                        fontSize = 14.sp,
                        color = LocalColor.current.text.default_50
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = defaultText,
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = LocalColor.current.text.default_100,
                        backgroundColor = LocalColor.current.background,
                        cursorColor = LocalColor.current.primary,
                        disabledLabelColor = LocalColor.current.text.default_30,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    onValueChange = {
                        if (it.length <= maxLength) {
                            input.invoke(it)
                        }
                    },
                    shape = RoundedCornerShape(4.dp),
                    maxLines = 1,
                    textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                    placeholder = {
                        Text(
                            text = "我的暱稱",
                            fontSize = 16.sp,
                            color = LocalColor.current.text.default_30
                        )
                    },
                    interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                if (it is PressInteraction.Release) {
                                    AppUserLogger.getInstance()
                                        .log(Clicked.AvatarAndNicknameNickname)
                                }
                            }
                        }
                    }
                )

                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(size = 32.dp),
                            color = LocalColor.current.primary
                        )
                    }
                }
            }

            BottomButtonScreen(text = stringResource(id = R.string.save), onClick = onSave)
        }

    }
}

@Preview
@Composable
fun AvatarNicknameChangeScreenPreview() {
    FanciTheme {
        AvatarNicknameChangeScreenView(
            avatarImage = null,
            navController = EmptyDestinationsNavigator,
            onChangeAvatar = {},
            onSave = {},
            defaultText = "",
            maxLength = 10,
            input = {},
            isLoading = false,
            onBack = {}
        )
    }
}