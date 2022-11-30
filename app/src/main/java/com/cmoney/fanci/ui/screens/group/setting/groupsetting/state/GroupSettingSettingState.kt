package com.cmoney.fanci.ui.screens.group.setting.groupsetting.state

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

class GroupSettingSettingState(
    val openCameraDialog: MutableState<Boolean>,
    val onAttachImage: MutableState<Uri?>
) {

    fun closeCameraDialog() {
        openCameraDialog.value = false
    }

    fun openCameraDialog() {
        openCameraDialog.value = true
    }

    fun attachImage(uri: Uri) {
        onAttachImage.value = uri
    }
}

@Composable
fun rememberGroupSettingSettingState(
    openCameraDialog: MutableState<Boolean> = mutableStateOf(false),
    onAttachImage: MutableState<Uri?> = mutableStateOf(null)
) = remember {
    GroupSettingSettingState(openCameraDialog, onAttachImage)
}