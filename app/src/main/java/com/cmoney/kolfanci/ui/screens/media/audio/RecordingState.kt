package com.cmoney.kolfanci.ui.screens.media.audio

/**
 * @param progress 播放的進度百分比
 * @param currentTime 顯示的已播放時間
 */
data class RecordingState(
    val isDeleteVisible: Boolean,
    val isUploadVisible: Boolean,
    val isTimerVisible: Boolean,
    val isRecordHintVisible: Boolean,
    val progressIndicator: ProgressIndicator,
    val progress: Float,
    val currentTime: String
)

enum class ProgressIndicator {
    DEFAULT, RECORDING, COMPLETE, PLAYING, PAUSE
}