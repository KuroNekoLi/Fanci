package com.cmoney.kolfanci.service.media

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import android.support.v4.media.session.PlaybackStateCompat.STATE_STOPPED
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
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

private const val TAG = "MyMediaRecorderImpl2"

/**
 * 錄音與播放介面的實現
 */
class RecorderAndPlayerImpl2(private val context: Context,private val musicServiceConnection:MusicServiceConnection) : RecorderAndPlayer {


    private var playbackState: PlaybackStateCompat = EMPTY_PLAYBACK_STATE
    private val playbackStateObserver = Observer<PlaybackStateCompat> { playbackState->
        playingJob?.cancel()
        playingJob = CoroutineScope(coroutineContext).launch {
            val nowPlaying = musicServiceConnection.nowPlaying.value
            val duration = nowPlaying?.duration ?: 0L
            while (playbackState.state == STATE_PLAYING){
                val currentPosition = playbackState.currentPlayBackPosition
                _playingCurrentMilliseconds.emit(currentPosition)
                delay(200)
            }
            if(playbackState.state == STATE_STOPPED){
                Log.i("LinLi", "emit,duration: $duration")

                _playingCurrentMilliseconds.emit(getDurationFromRecordFile())
            }
        }
//        val metadata = musicServiceConnection.nowPlaying.value ?: NOTHING_PLAYING
//        when(it.state){
//            STATE_PLAYING -> {
//                Log.i(TAG, "STATE_PLAYING:")
//            }
//            STATE_PAUSED -> {
//                Log.i(TAG, "STATE_PAUSED:")
//            }
//            STATE_STOPPED -> {
//                Log.i(TAG, "STATE_STOPPED:")
//            }
//            else -> {}
//        }
    }

    private var recorder: MediaRecorder? = null
    private var filePath: String? = null

    //目前錄製的秒數
    private var _currentRecordSeconds = MutableStateFlow(0L)
    private var _playingCurrentMilliseconds = MutableStateFlow(0L)
    private val _progress = MutableStateFlow(0f)
    private var recordJob: Job? = null
    private var playingJob: Job? = null
    //最大錄音秒數
    private val maxRecordingDuration = 45000L

    //錄音時長
    private var recordingDuration = 0L

    private val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + Job()

    init {
        musicServiceConnection.also {
            it.playbackState.observeForever(playbackStateObserver)
        }
    }

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

    override fun startPlaying(uri: Uri) {
        _playingCurrentMilliseconds.value = 0

        if (musicServiceConnection.isConnected.value == true) {

            val transportControls = musicServiceConnection.transportControls
            transportControls.playFromUri(uri, null)

        } else {
            KLog.e(TAG, "musicServiceConnection is not connect.")
        }
    }

    override fun startPlaying() {
        startPlaying(Uri.parse(filePath))
    }

    override fun pausePlaying() {
        if (musicServiceConnection.isConnected.value == true) {
            val transportControls = musicServiceConnection.transportControls
            musicServiceConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        KLog.w(
                            TAG,
                            "Playable item clicked but neither play nor pause are enabled!"
                        )
                    }
                }
            }
        }
    }

    override fun resumePlaying() {
        if (musicServiceConnection.isConnected.value == true) {
            val transportControls = musicServiceConnection.transportControls
            musicServiceConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        KLog.w(
                            TAG,
                            "Playable item clicked but neither play nor pause are enabled!"
                        )
                    }
                }
            }
        }
    }

    override fun stopPlaying() {
        musicServiceConnection.transportControls.stop()
        _playingCurrentMilliseconds.value = 0
    }


    override fun dismiss() {
        recordJob?.cancel()
        _currentRecordSeconds.value = 0
        recordingDuration = 0
        recorder?.release()
        recorder = null
        musicServiceConnection.transportControls.stop()
        musicServiceConnection.playbackState.removeObserver(playbackStateObserver)
    }

    override fun getDurationFromRecordFile(): Long {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(filePath)
        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val millSecond = durationStr?.toLong() ?: 0L

        return millSecond
    }

    override fun getPlayingCurrentMilliseconds(): StateFlow<Long> {
        return _playingCurrentMilliseconds.asStateFlow()
    }

    override fun getRecordingCurrentMilliseconds(): StateFlow<Long> = _currentRecordSeconds

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
        KLog.i(TAG, "fileName: $filePath, Uri: ${Uri.fromFile(filePath?.let { File(it) })}")
        return Uri.fromFile(filePath?.let { File(it) })
    }

    override fun deleteFile() {
        filePath?.let { File(it).delete() }
    }

    override fun deleteFile(uri: Uri) {
        filePath?.let { File(uri.toString()).delete() }
    }
}