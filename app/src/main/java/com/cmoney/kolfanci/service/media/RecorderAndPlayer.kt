package com.cmoney.kolfanci.service.media

import android.net.Uri
import kotlinx.coroutines.flow.StateFlow

/**
 * 錄音與播放的介面
 */
interface RecorderAndPlayer {
    /**
     * 開始錄音
     */
    fun startRecording()

    /**
     * 停止錄音
     */
    fun stopRecording()

    /**
     * 開始播放
     */
    fun startPlaying()

    /**
     * 暫停播放
     */
    fun pausePlaying()

    /**
     * 恢復播放
     */
    fun resumePlaying()

    /**
     * 停止播放
     */
    fun stopPlaying()

    /**
     * 停止錄音與播放
     */
    fun dismiss()

    /**
     * 獲得播放的總時長
     * @return 播放時長（毫秒）
     */
    fun getPlayingDuration(): Int

    /**
     * 獲得當前播放的秒數
     * @return 播放當前毫秒數的狀態流
     */
    fun getPlayingCurrentMilliseconds(): StateFlow<Int>

    /**
     * 獲得當前錄音的秒數
     * @return 錄音當前毫秒數的狀態流
     */
    fun getRecordingCurrentMilliseconds(): StateFlow<Int>

    /**
     * 獲得錄音的總時長
     * @return 錄音時長（毫秒）
     */
    fun getRecordingDuration(): Int
    fun getFileUri(): Uri?

    /**
     * 刪除錄音檔案
     */
    fun deleteFile()

    /**
     * 播放給定的錄音檔
     * @param uri 音檔的uri
     */
    fun startPlaying(uri: Uri)

    /**
     * 刪除給定的錄音檔
     * @param uri 音檔的uri
     */
    fun deleteFile(uri: Uri)
}