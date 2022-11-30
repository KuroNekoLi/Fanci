package com.cmoney.fanci.ui.screens.group.setting.groupsetting.state

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

class GroupSettingSettingState(
    val openCameraDialog: MutableState<Boolean>,
    val avatarImage: MutableState<Uri?>,
    val coverImageUrl: MutableState<Uri?>
) {

    fun closeCameraDialog() {
        openCameraDialog.value = false
    }

    fun openCameraDialog() {
        openCameraDialog.value = true
    }

    fun setAvatarImage(uri: Uri) {
        avatarImage.value = uri
    }

    fun setBackgroundImage(uri: Uri) {
        coverImageUrl.value = uri
    }
}

@Composable
fun rememberGroupSettingSettingState(
    openCameraDialog: MutableState<Boolean> = mutableStateOf(false),
    avatarImage: MutableState<Uri?> = mutableStateOf(null),
    coverImageUrl: MutableState<Uri?> = mutableStateOf(null)
) = remember {
    GroupSettingSettingState(openCameraDialog, avatarImage, coverImageUrl)
}