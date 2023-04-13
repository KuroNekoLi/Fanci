package com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.avatar

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.socks.library.KLog

data class UiState(
    val openCameraDialog: Boolean = false,
    val avatarImage: Uri? = null,
    val coverImageUrl: Uri? = null
)

class GroupSettingAvatarViewModel : ViewModel() {
    private val TAG = GroupSettingAvatarViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    /**
     * 打開 挑選照片 彈窗
     */
    fun openCameraDialog() {
        KLog.i(TAG, "openCameraDialog")
        uiState = uiState.copy(
            openCameraDialog = true
        )
    }

    /**
     * 關閉 挑選照片 彈窗
     */
    fun closeCameraDialog() {
        KLog.i(TAG, "closeCameraDialog")
        uiState = uiState.copy(
            openCameraDialog = false
        )
    }

    /**
     * 設定照片
     */
    fun setAvatarImage(uri: Uri) {
        KLog.i(TAG, "setAvatarImage:$uri")
//        uiState = uiState.copy(
//            avatarImage = Uri.parse(uri.toString())
//        )

        uiState = uiState.copy(
            avatarImage = uri
        )
    }

    fun resetCameraUri() {
        uiState = uiState.copy(
            avatarImage = null
        )
    }
}