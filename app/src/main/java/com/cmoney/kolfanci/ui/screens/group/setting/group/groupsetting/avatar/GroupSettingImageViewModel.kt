package com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.avatar

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.socks.library.KLog

/**
 * 社團設定頁面的UI State
 * @param openCameraDialog 選擇照片彈窗是否開啟
 * @param image 中央圖示的uri
 * @param coverImageUrl 背景圖示的uri
 */
data class UiState(
    val openCameraDialog: Boolean = false,
    val image: Uri? = null,
    val coverImageUrl: Uri? = null
)

/**
 * 社團圖示設定的ViewModel
 */
class GroupSettingImageViewModel : ViewModel() {
    private val TAG = GroupSettingImageViewModel::class.java.simpleName

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
            image = uri
        )
    }

    fun resetCameraUri() {
        uiState = uiState.copy(
            image = null
        )
    }
}