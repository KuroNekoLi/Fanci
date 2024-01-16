package com.cmoney.kolfanci.ui.screens.media.audio

import android.net.Uri

/**
 * 錄音Screen的狀態
 * @param isDeleteVisible 刪除按鈕是否可見
 * @param isUploadVisible 上傳按鈕是否可見
 * @param isRecordHintVisible 預設提示是否可見
 * @param progressIndicator 錄音按鈕的狀態
 * @param progress 播放的進度百分比
 * @param currentTime 顯示的已播放時間
 * @param recordFileUri 錄音檔案的Uri
 */
data class RecordingScreenState(
    val isDeleteVisible: Boolean,
    val isUploadVisible: Boolean,
    val isRecordHintVisible: Boolean,
    val progressIndicator: ProgressIndicator,
    val progress: Float,
    val currentTime: String,
    val recordFileUri: Uri?
) {
    companion object {
        val default = RecordingScreenState(
            isDeleteVisible = false,
            isUploadVisible = false,
            isRecordHintVisible = true,
            progressIndicator = ProgressIndicator.DEFAULT,
            progress = 0f,
            currentTime = "00:00",
            recordFileUri = null
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

/**
 * 判斷當前的 [RecordingScreenState] 是否為 playOnly 狀態。
 * playOnly 狀態定義為：isDeleteVisible 為 false, isUploadVisible 為 false,
 * isRecordHintVisible 為 false 且 progressIndicator 不是 ProgressIndicator.DEFAULT。
 *
 * @return 如果當前狀態符合 playOnly 條件，則返回 true，否則返回 false。
 */
fun RecordingScreenState.isPlayOnly(): Boolean {
    return !isDeleteVisible &&
            !isUploadVisible &&
            !isRecordHintVisible &&
            progressIndicator != ProgressIndicator.DEFAULT
}