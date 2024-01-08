package com.cmoney.kolfanci.ui.screens.media.audio

sealed class RecordingScreenEvent {
    object OnButtonClicked : RecordingScreenEvent()
    object OnDelete : RecordingScreenEvent()
    object OnUpload : RecordingScreenEvent()
    object OnDismiss : RecordingScreenEvent()
}