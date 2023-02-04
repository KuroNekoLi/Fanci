package com.cmoney.kolfanci.ui.screens.shared.setting

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class UserInfoSettingState(
    val navController: NavHostController,
    val inputText: MutableState<TextFieldValue>,
    val openCameraDialog: MutableState<Boolean>,
    val onAttachImage: MutableState<Uri?>
) {
    private val maxInputChar = 10

    fun getCurrentInputLength(): String {
        return "%d / %d".format(inputText.value.text.length, maxInputChar)
    }

    fun onValueChange(textFieldValue: TextFieldValue) {
        if (textFieldValue.text.length <= maxInputChar) inputText.value = textFieldValue
    }

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
fun rememberUserInfoSettingState(
    navController: NavHostController = rememberNavController(),
    inputText: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue("")),
    openCameraDialog: MutableState<Boolean> = mutableStateOf(false),
    onAttachImage: MutableState<Uri?> = mutableStateOf(Uri.parse("https://picsum.photos/300/300"))
) = remember {
    UserInfoSettingState(navController, inputText, openCameraDialog, onAttachImage)
}