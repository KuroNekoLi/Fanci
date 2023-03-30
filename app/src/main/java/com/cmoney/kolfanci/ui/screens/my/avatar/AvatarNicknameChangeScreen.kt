package com.cmoney.kolfanci.ui.screens.my.avatar

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.kolfanci.LocalDependencyContainer
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.viewmodel.UserViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.avatar.GroupSettingAvatarViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.GroupPhotoPickDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.setting.BottomButtonScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.xlogin.XLoginHelper
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun AvatarNicknameChangeScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    viewModel: GroupSettingAvatarViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel()
) {
    val TAG = "AvatarNicknameChangeScreen"
    val maxNickNameSize = 10
    var textState by remember { mutableStateOf(XLoginHelper.nickName) }

    val isLoading = userViewModel.isLoading.collectAsState(false)
    val isSaveComplete = userViewModel.isComplete.collectAsState(false)

    if (isSaveComplete.value) {
        navController.popBackStack()
    }

    AvatarNicknameChangeScreenView(
        modifier = modifier,
        defaultText = textState,
        avatarImage = viewModel.uiState.avatarImage,
        maxLength = maxNickNameSize,
        input = {
            textState = it
        },
        isLoading = isLoading.value,
        onChangeAvatar = {
            viewModel.openCameraDialog()
        },
        onSave = {
            userViewModel.changeNicknameAndAvatar(
                nickName = textState,
                avatarUri = viewModel.uiState.avatarImage
            )
        }
    )

    //相片選擇 彈窗
    if (viewModel.uiState.openCameraDialog) {
        GroupPhotoPickDialogScreen(
            isShowFanciPic = false,
            onDismiss = {
                viewModel.closeCameraDialog()
            },
            onAttach = {
                viewModel.setAvatarImage(it)
            },
            onFanciClick = {
            }
        )
    }
}

@Composable
private fun AvatarNicknameChangeScreenView(
    modifier: Modifier = Modifier,
    avatarImage: Uri?,
    onChangeAvatar: () -> Unit,
    onSave: () -> Unit,
    defaultText: String,
    maxLength: Int,
    input: (String) -> Unit,
    isLoading: Boolean
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopBarScreen(
                title = "頭像與暱稱",
                leadingEnable = true,
                trailingEnable = false,
                moreEnable = false,
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
                    text = "我在此社團的頭像",
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
                        text = "我在此社團的暱稱",
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

            BottomButtonScreen(text = "儲存", onClick = onSave)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun AvatarNicknameChangeScreenPreview() {
    FanciTheme {
        AvatarNicknameChangeScreenView(
            avatarImage = null,
            onChangeAvatar = {},
            onSave = {},
            defaultText = "",
            maxLength = 10,
            input = {},
            isLoading = false
        )
    }
}