package com.cmoney.kolfanci.service.media

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import com.cmoney.kolfanci.model.Constant
import com.socks.library.KLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import kotlin.coroutines.CoroutineContext

private const val TAG = "MyMediaRecorderImpl"

/**
 * 錄音與播放介面的實現
 */
class RecorderAndPlayerImpl(private val context: Context) : RecorderAndPlayer {

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var filePath: String? = null

    //目前錄製的秒數
    private var _currentRecordSeconds = MutableStateFlow(0)
    private var _playingCurrentMilliseconds = MutableStateFlow(0)
    private val _progress = MutableStateFlow(0f)
    private var recordJob: Job? = null
    private var playingJob: Job? = null

    //最大錄音秒數
    private val maxRecordingDuration = 45000

    //錄音時長
    private var recordingDuration = 0

    private val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + Job()
    @Suppress("DEPRECATION")
    override fun startRecording() {
        _playingCurrentMilliseconds.value = 0
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }
        recorder?.apply {
            filePath =
                "${Constant.absoluteCachePath}/錄音_${System.currentTimeMillis()}.aac"
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setOutputFile(filePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

            try {
                prepare()
            } catch (e: IOException) {
                KLog.e(TAG, "prepare() failed")
            }

            start()
        }
        startRecordingTimer()
    }


    override fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        stopRecordingTimer()
    }


    override fun startPlaying() {
        _playingCurrentMilliseconds.value = 0
        player = MediaPlayer().apply {
            try {
                setDataSource(filePath)
                prepare()
                start()
            } catch (e: IOException) {
                KLog.e(TAG, "prepare() failed")
            }
        }
    }

    override fun pausePlaying() {
        if (player?.isPlaying == true) {
            player!!.pause()
        }
    }

    override fun resumePlaying() {
        if (!player?.isPlaying!!) {
            player?.start()
        }
    }

    override fun stopPlaying() {
        player?.release()
        player = null
        _playingCurrentMilliseconds.value = 0
    }


    override fun dismiss() {
        recordJob?.cancel()
        _currentRecordSeconds.value = 0
        recordingDuration = 0
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }

    override fun getPlayingDuration(): Int {
        return player?.duration ?: 0
    }

    override fun getPlayingCurrentMilliseconds(): StateFlow<Int> {
        playingJob?.cancel()
        playingJob = CoroutineScope(coroutineContext).launch {
            player?.let { mediaPlayer ->
                try {
                    while (mediaPlayer.isPlaying) {
                        val currentPosition = mediaPlayer.currentPosition
                        _playingCurrentMilliseconds.emit(currentPosition)
                        delay(200)
                    }
                    if (mediaPlayer.currentPosition == mediaPlayer.duration) {
                        _playingCurrentMilliseconds.emit(mediaPlayer.duration)
                    }
                } catch (e: IllegalStateException) {
                    KLog.e(TAG, "MediaPlayer is in an invalid state", e)
                }
            }
        }
        return _playingCurrentMilliseconds.asStateFlow()
    }

    override fun getRecordingCurrentMilliseconds(): StateFlow<Int> = _currentRecordSeconds

    private fun startRecordingTimer() {
        recordJob = CoroutineScope(coroutineContext).launch {
            while (_currentRecordSeconds.value < maxRecordingDuration) {
                delay(200L)
                _currentRecordSeconds.value += 200
                _progress.value = _currentRecordSeconds.value / maxRecordingDuration.toFloat()
            }
            if (_currentRecordSeconds.value >= maxRecordingDuration) {
                stopRecording()
            }
        }
    }

    private fun stopRecordingTimer() {
        recordingDuration = _currentRecordSeconds.value
        recordJob?.cancel()
    }

    override fun getRecordingDuration() = recordingDuration
    override fun getFileUri(): Uri? {
        KLog.i(TAG, "fileName: $filePath, Uri: ${ Uri.fromFile(filePath?.let { File(it) })}")
        return Uri.fromFile(filePath?.let { File(it) })
    }

    override fun deleteFile() {
        filePath?.let { File(it).delete() }
    }
}