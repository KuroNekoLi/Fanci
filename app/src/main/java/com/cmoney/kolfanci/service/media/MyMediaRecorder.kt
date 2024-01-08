package com.cmoney.kolfanci.service.media

import kotlinx.coroutines.flow.StateFlow

interface MyMediaRecorder {
    fun startRecording()
    fun stopRecording()
    fun startPlaying()
    fun pausePlaying()
    fun resumePlaying()
    fun stopPlaying()
    fun cancelRecording()
    fun deleteRecording()
    fun getDuration(): Int
    fun getCurrentMilliseconds(): StateFlow<Int>
    fun getRecordingDuration(): Int
}