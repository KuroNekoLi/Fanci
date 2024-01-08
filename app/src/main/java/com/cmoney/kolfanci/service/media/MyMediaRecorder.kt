package com.cmoney.kolfanci.service.media

import kotlinx.coroutines.flow.StateFlow

interface MyMediaRecorder {
    fun startRecording()
    fun stopRecording()
    fun startPlaying()
    fun pausePlaying()
    fun resumePlaying()
    fun stopPlaying()
    fun dismiss()
    fun getPlayingDuration(): Int
    fun getRecordingCurrentMilliseconds(): StateFlow<Int>
    fun getRecordingDuration(): Int
}