package com.cmoney.kolfanci.ui.screens.media.audio

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.kolfanci.service.media.RecorderAndPlayer
import com.socks.library.KLog
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "RecordingViewModel"

/**
 * 錄音的ViewModel
 * @param recorderAndPlayer 錄音與播放介面
 */
class RecordingViewModel(private val recorderAndPlayer: RecorderAndPlayer) : ViewModel() {
    private val _recordingScreenState = mutableStateOf(RecordingScreenState.default)
    private var durationFromAttachmentRecordInfo: Long? = null

    /**
     * 錄音狀態
     */
    val recordingScreenState: State<RecordingScreenState> = _recordingScreenState

    // 定義 var playingProgressJob，初始為 null
    private var playingProgressJob: Job? = null

    /**
     * 使用者事件
     * @param event RecordingScreen 的事件
     */
    fun onEvent(event: RecordingScreenEvent) {
        when (event) {
            RecordingScreenEvent.OnButtonClicked -> {
                when (_recordingScreenState.value.progressIndicator) {
                    ProgressIndicator.DEFAULT -> {
                        viewModelScope.launch {
                            recorderAndPlayer.getRecordingCurrentMilliseconds().collect {
                                _recordingScreenState.updateState {
                                    copy(
                                        currentTime = changeToTimeText(it)
                                    )
                                }
                                if (it >= 45000) {
                                    _recordingScreenState.updateState {
                                        copy(
                                            progressIndicator = ProgressIndicator.COMPLETE
                                        )
                                    }
                                }
                            }
                        }

                        recorderAndPlayer.startRecording()
                        _recordingScreenState.updateState {
                            copy(
                                progressIndicator = ProgressIndicator.RECORDING,
                                isDeleteVisible = true,
                                isUploadVisible = true,
                                isRecordHintVisible = false,
                            )
                        }
                    }

                    ProgressIndicator.RECORDING -> {
                        recorderAndPlayer.stopRecording()
                        _recordingScreenState.updateState {
                            copy(
                                progressIndicator = ProgressIndicator.COMPLETE,
                                currentTime = changeToTimeText(recorderAndPlayer.getRecordingDuration())
                            )
                        }
                    }

                    ProgressIndicator.COMPLETE -> {
                        if (_recordingScreenState.value.isPlayOnly()) {
                            val uri = _recordingScreenState.value.recordFileUri
                            uri?.let { recorderAndPlayer.startPlaying(it) }
                            startCollectingPlayingProgressJob(false)
                        } else {
                            recorderAndPlayer.startPlaying()
                            startCollectingPlayingProgressJob()
                        }

                        _recordingScreenState.updateState {
                            copy(
                                progressIndicator = ProgressIndicator.PLAYING
                            )
                        }
                    }

                    ProgressIndicator.PLAYING -> {
                        recorderAndPlayer.pausePlaying()
                        _recordingScreenState.updateState {
                            copy(
                                progressIndicator = ProgressIndicator.PAUSE
                            )
                        }
                        stopCollectingPlayingProgressJob()
                    }

                    ProgressIndicator.PAUSE -> {
                        recorderAndPlayer.resumePlaying()
                        _recordingScreenState.updateState {
                            copy(
                                progressIndicator = ProgressIndicator.PLAYING
                            )
                        }
                        startCollectingPlayingProgressJob()
                    }
                }
            }

            RecordingScreenEvent.OnDelete -> {
                stopCollectingPlayingProgressJob()
                recorderAndPlayer.dismiss()
                recorderAndPlayer.deleteFile()
                _recordingScreenState.updateState {
                    RecordingScreenState.default
                }
            }

            RecordingScreenEvent.OnUpload -> {
                stopCollectingPlayingProgressJob()
                recorderAndPlayer.stopRecording()
                recorderAndPlayer.stopPlaying()
                _recordingScreenState.updateState {
                    copy(
                        isDeleteVisible = false,
                        isUploadVisible = false,
                        recordFileUri = recorderAndPlayer.getFileUri()
                    )
                }

                KLog.i(TAG, "recordFileUri: ${recorderAndPlayer.getFileUri()}")
            }

            RecordingScreenEvent.OnDismiss -> {
                stopCollectingPlayingProgressJob()
                if (_recordingScreenState.value.isPlayOnly()) {
                    recorderAndPlayer.stopPlaying()
                    _recordingScreenState.updateState {
                        copy(
                            currentTime = changeToTimeText(recorderAndPlayer.getDurationFromRecordFile()),
                            progressIndicator = ProgressIndicator.COMPLETE
                        )
                    }
                } else {
                    initStateAndPlayer()
                    recorderAndPlayer.deleteFile()
                }
            }

            is RecordingScreenEvent.OnPreviewItemClicked -> {
                durationFromAttachmentRecordInfo = event.duration ?: 0
                _recordingScreenState.updateState {
                    copy(
                        isRecordHintVisible = false,
                        progressIndicator = ProgressIndicator.COMPLETE,
                        progress = 0f,
                        currentTime = changeToTimeText(durationFromAttachmentRecordInfo!!),
                        recordFileUri = event.uri
                    )
                }
            }

            is RecordingScreenEvent.OnPreviewItemDeleted -> {
                recorderAndPlayer.deleteFile(event.uri)
            }

            RecordingScreenEvent.OnOpenSheet -> {
                initStateAndPlayer()
            }
        }
    }

    private fun <T> MutableState<T>.updateState(updateFunc: T.() -> T) {
        value = value.updateFunc()
    }

    /**
     * 將時間轉換為mm:ss
     * @param time 毫秒
     */
    private fun changeToTimeText(time: Long): String {
        val minutes = (time / 1000 / 60).toString().padStart(2, '0')
        val seconds = (time / 1000 % 60).toString().padStart(2, '0')
        return "$minutes:$seconds"
    }

    // 一個收集正在播放時間的流，並設定State的任務
    private fun startCollectingPlayingProgressJob(isFromRecordFile: Boolean = true) {
        playingProgressJob?.cancel() // 取消先前的任務（如果存在）
        playingProgressJob = viewModelScope.launch {
            val duration =
                if (isFromRecordFile) recorderAndPlayer.getDurationFromRecordFile() else durationFromAttachmentRecordInfo
                    ?: 0
            recorderAndPlayer.getPlayingCurrentMilliseconds().collect { passedTime ->
                _recordingScreenState.updateState {
                    copy(
                        //(passedTime/duration)如果都是整數不會動
                        progress = (passedTime.toDouble() / duration.toDouble()).toFloat(),
                        currentTime = changeToTimeText(passedTime),
                    )
                }
                if (passedTime >= duration) {
                    _recordingScreenState.updateState {
                        copy(
                            progressIndicator = ProgressIndicator.COMPLETE,
                            currentTime = changeToTimeText(recorderAndPlayer.getDurationFromRecordFile()),
                        )
                    }
                }
            }
        }
    }

    private fun stopCollectingPlayingProgressJob() {
        playingProgressJob?.cancel()
        playingProgressJob = null
    }

    override fun onCleared() {
        super.onCleared()
        recorderAndPlayer.dismiss()
        recorderAndPlayer.deleteFile()
    }

    fun initStateAndPlayer() {
        recorderAndPlayer.dismiss()
        _recordingScreenState.updateState {
            RecordingScreenState.default
        }
    }
}