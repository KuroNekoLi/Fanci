package com.cmoney.kolfanci.ui.screens.media.audio

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
}