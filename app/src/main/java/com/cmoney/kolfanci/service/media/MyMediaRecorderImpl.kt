package com.cmoney.kolfanci.service.media

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import kotlin.coroutines.CoroutineContext

private const val TAG = "MyMediaRecorderImpl"

class MyMediaRecorderImpl(private val context: Context) : MyMediaRecorder {

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var uri: Uri? = null
    private var fileName: String? = null
    private var _currentSeconds = MutableStateFlow(0)

    private val _progress = MutableStateFlow(0f)
    private var timerJob: Job? = null
    private val maxRecordingDuration = 45000
    private var recordingDuration = 0

    val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    override fun startRecording() {
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }
        recorder?.apply {
            fileName =
                "${context.externalCacheDir?.absolutePath}/錄音_${System.currentTimeMillis()}.3gp"
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(TAG, "prepare() failed")
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
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(TAG, "prepare() failed")
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
    }


    override fun cancelRecording() {
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }

    override fun deleteRecording() {
        fileName?.let { File(it).delete() }
//        stopRecording()
//        uri?.let {
//            context.contentResolver.delete(it,null,null)
//        }
    }

    override fun getDuration(): Int {
        return player?.duration ?: 0
    }

    override fun getCurrentMilliseconds(): StateFlow<Int> = _currentSeconds

    private fun startRecordingTimer() {
        timerJob = CoroutineScope(coroutineContext).launch {
            while (_currentSeconds.value < maxRecordingDuration) {
                delay(200L)
                _currentSeconds.value += 200
                _progress.value = _currentSeconds.value / maxRecordingDuration.toFloat()
            }
        }
    }

    private fun stopRecordingTimer() {
        recordingDuration = _currentSeconds.value
        timerJob?.cancel()
        _currentSeconds.value = 0
    }

    override fun getRecordingDuration() = recordingDuration
}