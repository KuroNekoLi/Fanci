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
 * @property DEFAULT 剛開啟錄音的狀態
 * @property RECORDING 錄音中
 * @property COMPLETE 錄音完成
 * @property PLAYING 正在播放錄音
 * @property PAUSE 暫停時
 */
enum class ProgressIndicator {
    DEFAULT, RECORDING, COMPLETE, PLAYING, PAUSE
}