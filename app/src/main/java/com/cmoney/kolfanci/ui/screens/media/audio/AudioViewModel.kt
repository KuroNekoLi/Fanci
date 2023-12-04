package com.cmoney.kolfanci.ui.screens.media.audio

import android.app.Application
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.isURL
import com.cmoney.kolfanci.service.media.EMPTY_PLAYBACK_STATE
import com.cmoney.kolfanci.service.media.MusicServiceConnection
import com.cmoney.kolfanci.service.media.NOTHING_PLAYING
import com.cmoney.kolfanci.service.media.currentPlayBackPosition
import com.cmoney.kolfanci.service.media.duration
import com.cmoney.kolfanci.service.media.isPlayEnabled
import com.cmoney.kolfanci.service.media.isPlaying
import com.cmoney.kolfanci.service.media.title
import com.cmoney.kolfanci.service.media.toUri
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


/**
 * 處理 音樂播放相關
 */
class AudioViewModel(
    private val context: Application,
    private val musicServiceConnection: MusicServiceConnection,
    private val uri: Uri
) : AndroidViewModel(context) {
    private val TAG = AudioViewModel::class.java.simpleName

    //Play Button Res
    private val _playButtonRes = MutableStateFlow(R.drawable.play)
    val playButtonRes = _playButtonRes.asStateFlow()

    //Audio duration
    private val _audioDuration = MutableStateFlow(0L)
    val audioDuration = _audioDuration.asStateFlow()

    //Audio Position
    private val _mediaPosition = MutableStateFlow<Long>(0)
    val mediaPosition = _mediaPosition.asStateFlow()

    //Title
    private val _title = MutableStateFlow<String>("")
    val title = _title.asStateFlow()

    //Speed display
    private val _speedTitle = MutableStateFlow<String>("1x")
    val speedTitle = _speedTitle.asStateFlow()

    //是否要顯示mini play icon
    private val _isShowMiniIcon = MutableStateFlow(false)
    val isShowMiniIcon = _isShowMiniIcon.asStateFlow()

    //是否顯示 底部 播放器
    private val _isShowBottomPlayer = MutableStateFlow(false)
    val isShowBottomPlayer = _isShowBottomPlayer.asStateFlow()

    //是否正在播放中
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private var playbackState: PlaybackStateCompat = EMPTY_PLAYBACK_STATE
    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        playbackState = it
        val metadata = musicServiceConnection.nowPlaying.value ?: NOTHING_PLAYING
        updateState(playbackState, metadata)
    }

    private val handler = Handler(Looper.getMainLooper())
    private var updatePosition = true
    private val POSITION_UPDATE_INTERVAL_MILLIS = 100L
    private var isStopUpdatePosition = false

    init {
        if (uri != Uri.EMPTY && !uri.isURL()) {
            getAudioFileDuration(uri)
        }

        musicServiceConnection.also {
            it.playbackState.observeForever(playbackStateObserver)
            checkPlaybackPosition()
        }

        fetchIsShowMiniIcon()
    }

    fun fetchIsShowMiniIcon() {
        val playState = musicServiceConnection.playbackState.value?.state
        _isShowMiniIcon.value = (musicServiceConnection.isConnected.value == true
                && (playState != PlaybackStateCompat.STATE_STOPPED && playState != PlaybackStateCompat.STATE_NONE))

        _isPlaying.update {
            (musicServiceConnection.isConnected.value == true && playState == PlaybackStateCompat.STATE_PLAYING)
        }
    }

    /**
     * 取得 正在播放中的音樂資訊
     * title, length
     */
    fun fetchCurrentPlayInfo() {
        if (musicServiceConnection.isConnected.value == true) {
            //正在播的歌曲
            val nowPlaying = musicServiceConnection.nowPlaying.value

            _title.value = nowPlaying?.title.orEmpty()

            val duration = nowPlaying?.duration ?: 0L
            if (duration > 0) {
                _audioDuration.update {
                    duration
                }
            }
        }
    }

    /**
     * 取得 檔案音檔 長度
     */
    private fun getAudioFileDuration(uri: Uri) {
        KLog.i(TAG, "getAudioFileDuration")
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, uri)
        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val millSecond = durationStr?.toLong() ?: 0L
        _audioDuration.update {
            millSecond
        }
    }

    /**
     * 刷新 播放狀態
     *
     * @param playbackState 播放器狀態
     * @param mediaMetadata 正在播放的歌曲
     */
    private fun updateState(
        playbackState: PlaybackStateCompat,
        mediaMetadata: MediaMetadataCompat
    ) {
        viewModelScope.launch {
            _playButtonRes.value = when (playbackState.isPlaying) {
                //Set pause
                true -> {
                    _isPlaying.update { true }
                    com.google.android.exoplayer2.R.drawable.exo_controls_pause
                }
                //Set play
                else -> {
                    _isPlaying.update { false }
                    com.google.android.exoplayer2.ui.R.drawable.exo_controls_play
                }
            }


        }
    }

    /**
     *  檢查播放秒數
     */
    private fun checkPlaybackPosition(): Boolean = handler.postDelayed({
        val currPosition = playbackState.currentPlayBackPosition
        if (isStopUpdatePosition.not() && _isPlaying.value) {
            viewModelScope.launch {
                _mediaPosition.update {
                    currPosition
                }

                fetchIsShowMiniIcon()

                if (_audioDuration.value <= 0) {
                    fetchCurrentPlayInfo()
                }
            }
        }

        if (updatePosition)
            checkPlaybackPosition()

    }, POSITION_UPDATE_INTERVAL_MILLIS)

    /**
     * 播放
     *
     * @param uri 檔案uri
     * @param title 檔案 title
     */
    fun play(uri: Uri, title: String? = null) {
        KLog.i(TAG, "play:$uri")
        if (musicServiceConnection.isConnected.value == true) {
            //正在播的歌曲
            val nowPlaying = musicServiceConnection.nowPlaying.value
            val nowPlayUri =
                nowPlaying?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)?.toUri()

            //取得控制器
            val transportControls = musicServiceConnection.transportControls

            //目前背景正在播放的音檔是否一樣
            if (uri == nowPlayUri) {
                KLog.i(TAG, "play the same file.")
                pauseOrPlay()
            }
            //開啟新播放
            else {
                KLog.i(TAG, "playFromUri:$uri")
                transportControls.playFromUri(uri, bundleOf("title" to title))
            }
        } else {
            KLog.e(TAG, "musicServiceConnection is not connect.")
        }
    }

    /**
     * Remove all observers
     */
    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.playbackState.removeObserver(playbackStateObserver)

        // Stop updating the position
        updatePosition = false
    }

    /**
     * 前往指定播放位置
     */
    fun seekTo(duration: Float) {
        KLog.i(TAG, "seekTo:$duration")
        isStopUpdatePosition = false
        musicServiceConnection.transportControls.seekTo(duration.toLong())
    }

    /**
     * 停止播放
     */
    fun stopPlay() {
        KLog.i(TAG, "stopPlay")
        musicServiceConnection.transportControls.stop()
        _isShowMiniIcon.value = false
    }

    /**
     * 暫停更新 media position
     */
    fun stopUpdatePosition() {
        KLog.i(TAG, "stopUpdatePosition")
        isStopUpdatePosition = true
    }

    /**
     * 暫停 或是 繼續播放
     */
    fun pauseOrPlay() {
        KLog.i(TAG, "pauseOrPlay")
        if (musicServiceConnection.isConnected.value == true) {
            val transportControls = musicServiceConnection.transportControls
            musicServiceConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        KLog.w(
                            TAG,
                            "Playable item clicked but neither play nor pause are enabled!" +
                                    " (mediaId=$uri)"
                        )
                    }
                }
            }
        }
    }

    /**
     * 更換 逼放速度
     */
    fun changeSpeed(speed: Float) {
        KLog.i(TAG, "changeSpeed:$speed")
        if (musicServiceConnection.isConnected.value == true) {
            val transportControls = musicServiceConnection.transportControls
            transportControls.setPlaybackSpeed(speed)
            _speedTitle.value = speed.toString() + "x"
        }
    }

    /**
     * 設定音檔長度
     */
    fun setDuration(duration: Long) {
        KLog.i(TAG, "setDuration:$duration")
        _audioDuration.update {
            duration
        }
    }

    /**
     * 直接背景播放
     */
    fun playSilence(
        uri: Uri,
        duration: Long,
        title: String? = null
    ) {

        KLog.i(TAG, "playSilence.")

        if (uri != Uri.EMPTY && !uri.isURL()) {
            getAudioFileDuration(uri)
        } else {
            setDuration(duration)
        }

        play(
            uri = uri,
            title = title
        )
    }

    fun openBottomPlayer() {
        _isShowBottomPlayer.update { true }
    }

    fun closeBottomPlayer() {
        _isShowBottomPlayer.update { false }
    }
}