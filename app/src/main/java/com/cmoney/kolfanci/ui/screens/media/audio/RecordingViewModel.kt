package com.cmoney.kolfanci.ui.screens.media.audio

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.kolfanci.service.media.MyMediaRecorder
import com.socks.library.KLog
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "RecordingViewModel"

class RecordingViewModel(private val myMediaRecorder: MyMediaRecorder) : ViewModel() {
    private val _recordingScreenState = mutableStateOf(
        RecordingState.default
    )
    val recordingScreenState: State<RecordingState> = _recordingScreenState
    var passedTime = 0
    fun onEvent(event: RecordingScreenEvent) {
        when (event) {
            RecordingScreenEvent.OnButtonClicked -> {
                val progressIndicator = _recordingScreenState.value.progressIndicator
                when (progressIndicator) {
                    ProgressIndicator.DEFAULT -> {
                        viewModelScope.launch {
                            myMediaRecorder.getRecordingCurrentMilliseconds().collect {
                                _recordingScreenState.updateState {
                                    copy(
                                        currentTime = changeToTimeText(it)
                                    )
                                }
                            }
                        }

                        myMediaRecorder.startRecording()
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
                        myMediaRecorder.stopRecording()
                        _recordingScreenState.updateState {
                            copy(
                                progressIndicator = ProgressIndicator.COMPLETE,
                                currentTime = changeToTimeText(myMediaRecorder.getRecordingDuration())
                            )
                        }
                    }

                    ProgressIndicator.COMPLETE -> {
                        myMediaRecorder.startPlaying()
                        _recordingScreenState.updateState {
                            copy(
                                progressIndicator = ProgressIndicator.PLAYING
                            )
                        }
                        playingJob()
                    }

                    ProgressIndicator.PLAYING -> {
                        myMediaRecorder.pausePlaying()
                        _recordingScreenState.updateState {
                            copy(
                                progressIndicator = ProgressIndicator.PAUSE
                            )
                        }
                        playingJob().cancel()
                    }

                    ProgressIndicator.PAUSE -> {
                        myMediaRecorder.resumePlaying()
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
                passedTime = 0
                myMediaRecorder.dismiss()
                _recordingScreenState.updateState {
                    RecordingState.default
                }
            }

            RecordingScreenEvent.OnUpload -> {
                //TODO 實現上傳
                KLog.i(TAG, "upload...")
            }

            RecordingScreenEvent.OnDismiss -> {
                playingJob().cancel()
                passedTime = 0
                myMediaRecorder.dismiss()
                _recordingScreenState.updateState {
                    RecordingState.default
                }
            }
        }
    }

    private fun playingJob(): Job {
        return viewModelScope.launch {
            val duration = myMediaRecorder.getPlayingDuration()
            if (duration == 0) return@launch
            while (recordingScreenState.value.progressIndicator == ProgressIndicator.PLAYING && passedTime < duration) {
                delay(200L)
                passedTime += 200
                _recordingScreenState.updateState {
                    copy(
                        //(passedTime/duration)如果都是整數不會動
                        progress = (passedTime.toDouble() / duration.toDouble()).toFloat(),
                        currentTime = changeToTimeText(passedTime),
                    )
                }
                Log.i(
                    TAG,
                    "passedTime: $passedTime,duration: ${myMediaRecorder.getPlayingDuration()},equal? ${passedTime == myMediaRecorder.getPlayingDuration()},progress: ${recordingScreenState.value.progress}"
                )
            }
            //這邊要大於
            if (passedTime >= duration) {
                _recordingScreenState.updateState { copy(progressIndicator = ProgressIndicator.COMPLETE) }
                passedTime = 0
                Log.i(
                    TAG,
                    "passedTime: $passedTime,state: ${_recordingScreenState.value.progressIndicator.name}"
                )
            }
        }
    }
}

fun <T> MutableState<T>.updateState(updateFunc: T.() -> T) {
    value = value.updateFunc()
}

/**
 * 將時間轉換為mm:ss
 * @param time 秒數
 */
private fun changeToTimeText(time: Int): String {
    val minutes = (time / 1000 / 60).toString().padStart(2, '0')
    val seconds = (time / 1000 % 60).toString().padStart(2, '0')
    return "$minutes:$seconds"
}