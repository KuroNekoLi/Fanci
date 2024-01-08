package com.cmoney.kolfanci.ui.screens.media.audio

import android.util.Log
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

    /**
     * 錄音狀態
     */
    val recordingScreenState: State<RecordingScreenState> = _recordingScreenState

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
                        recorderAndPlayer.startPlaying()
                        _recordingScreenState.updateState {
                            copy(
                                progressIndicator = ProgressIndicator.PLAYING
                            )
                        }
                        playingJob()
                    }

                    ProgressIndicator.PLAYING -> {
                        recorderAndPlayer.pausePlaying()
                        _recordingScreenState.updateState {
                            copy(
                                progressIndicator = ProgressIndicator.PAUSE
                            )
                        }
                        playingJob().cancel()
                    }

                    ProgressIndicator.PAUSE -> {
                        recorderAndPlayer.resumePlaying()
                        _recordingScreenState.updateState {
                            copy(
                                progressIndicator = ProgressIndicator.PLAYING
                            )
                        }
                        playingJob()
                    }
                }
            }

            RecordingScreenEvent.OnDelete -> {
                playingJob().cancel()
                recorderAndPlayer.dismiss()
                _recordingScreenState.updateState {
                    RecordingScreenState.default
                }
            }

            RecordingScreenEvent.OnUpload -> {
                //TODO 實現上傳
                KLog.i(TAG, "upload...")
            }

            RecordingScreenEvent.OnDismiss -> {
                playingJob().cancel()
                recorderAndPlayer.dismiss()
                _recordingScreenState.updateState {
                    RecordingScreenState.default
                }
            }
        }
    }

    private fun playingJob(): Job {
        return viewModelScope.launch {
            val duration = recorderAndPlayer.getPlayingDuration()
            recorderAndPlayer.getPlayingCurrentMilliseconds().collect { passedTime ->
                Log.i(TAG, "currentPosition: $passedTime")
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
                            progressIndicator = ProgressIndicator.COMPLETE
                        )
                    }
                }
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
    private fun changeToTimeText(time: Int): String {
        val minutes = (time / 1000 / 60).toString().padStart(2, '0')
        val seconds = (time / 1000 % 60).toString().padStart(2, '0')
        return "$minutes:$seconds"
    }
}