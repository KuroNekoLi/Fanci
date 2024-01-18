package com.cmoney.kolfanci.ui.screens.media.audio

import android.net.Uri

/**
 * RecordingScreen 的事件
 */
sealed class RecordingScreenEvent {
    /**
     * 按下錄音按紐
     */
    object OnButtonClicked : RecordingScreenEvent()

    /**
     * 刪除
     */
    object OnDelete : RecordingScreenEvent()

    /**
     * 上傳
     */
    object OnUpload : RecordingScreenEvent()

    /**
     * 取消BottomSheet
     */
    object OnDismiss : RecordingScreenEvent()

    /**
     * 開啟錄音介面
     */
    object OnOpenSheet : RecordingScreenEvent()

    /**
     * 點擊預覽檔案
     */
    data class OnPreviewItemClicked(val uri: Uri,val duration: Long?) : RecordingScreenEvent()

    /**
     * 刪除預覽檔案
     */
    data class OnPreviewItemDeleted(val uri: Uri) : RecordingScreenEvent()
}