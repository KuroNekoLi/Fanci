package com.cmoney.kolfanci.ui.screens.media.audio

/**
 * 錄音Screen的狀態
 * @param isDeleteVisible 刪除按鈕是否可見
 * @param isUploadVisible 上傳按鈕是否可見
 * @param isRecordHintVisible 預設提示是否可見
 * @param progressIndicator 錄音按鈕的狀態
 * @param progress 播放的進度百分比
 * @param currentTime 顯示的已播放時間
 */
data class RecordingScreenState(
    val isDeleteVisible: Boolean,
    val isUploadVisible: Boolean,
    val isRecordHintVisible: Boolean,
    val progressIndicator: ProgressIndicator,
    val progress: Float,
    val currentTime: String
) {
    companion object {
        val default = RecordingScreenState(
            isDeleteVisible = false,
            isUploadVisible = false,
            isRecordHintVisible = true,
            progressIndicator = ProgressIndicator.DEFAULT,
            progress = 0f,
            currentTime = "00:00"
        )
    }
}

/**
 * 錄音按鈕的狀態
 */
sealed class ProgressIndicator {
    /**
     * 剛開啟錄音的狀態
     */
    object DEFAULT : ProgressIndicator()
    /**
     * 錄音中
     */
    object RECORDING : ProgressIndicator()
    /**
     * 錄音完成
     */
    object COMPLETE : ProgressIndicator()
    /**
     * 正在播放錄音
     */
    object PLAYING : ProgressIndicator()
    /**
     * 暫停時
     */
    object PAUSE : ProgressIndicator()
}