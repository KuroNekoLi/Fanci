package com.cmoney.kolfanci.service.media

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.MutableLiveData

/**
 * 相當於媒體播放器的 遙控器
 */
class MusicServiceConnection(val context: Context, serviceComponent: ComponentName) {
    private val TAG = MusicServiceConnection::class.java.simpleName

    val isConnected = MutableLiveData<Boolean>()
        .apply { postValue(false) }

    val playbackState = MutableLiveData<PlaybackStateCompat>()
        .apply { postValue(EMPTY_PLAYBACK_STATE) }

    val nowPlaying = MutableLiveData<MediaMetadataCompat>()
        .apply { postValue(NOTHING_PLAYING) }

    val shuffleModeState = MutableLiveData<Int>()
        .apply { postValue(PlaybackStateCompat.SHUFFLE_MODE_NONE) }

    val repeatModeState = MutableLiveData<Int>()
        .apply { postValue(PlaybackStateCompat.REPEAT_MODE_NONE) }

    lateinit var mediaController: MediaControllerCompat

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(
        context,
        serviceComponent,
        mediaBrowserConnectionCallback, null
    ).apply { connect() }

    private inner class MediaBrowserConnectionCallback(private val context: Context) :
        MediaBrowserCompat.ConnectionCallback() {
        /**
         * Invoked after [MediaBrowserCompat.connect] when the request has successfully
         * completed.
         */
        override fun onConnected() {
            Log.d(TAG, "MediaBrowserConnectionCallback onConnected")
            // Get a MediaController for the MediaSession.
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }

            isConnected.postValue(true)
        }

        /**
         * Invoked when the client is disconnected from the media browser.
         */
        override fun onConnectionSuspended() {
            Log.d(TAG, "MediaBrowserConnectionCallback onConnectionSuspended")
            isConnected.postValue(false)
        }

        /**
         * Invoked when the connection to the media browser failed.
         */
        override fun onConnectionFailed() {
            Log.d(TAG, "MediaBrowserConnectionCallback onConnectionFailed")
            isConnected.postValue(false)
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            Log.d(TAG, "MediaControllerCallback onPlaybackStateChanged:$state")
            val playbackStateCompat = state ?: EMPTY_PLAYBACK_STATE
            playbackState.postValue(state ?: EMPTY_PLAYBACK_STATE)
            sendWidgetPlaybackStateIntent(playbackStateCompat)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            Log.d(TAG, "MediaControllerCallback onMetadataChanged:$metadata")
            nowPlaying.postValue(
                if (metadata?.id == null) {
                    NOTHING_PLAYING
                } else {
                    sendWidgetMetadataIntent(metadata)
                    metadata
                }
            )
        }

        override fun onShuffleModeChanged(shuffleMode: Int) {
            super.onShuffleModeChanged(shuffleMode)
            Log.d(TAG, "MediaControllerCallback onShuffleModeChanged:$shuffleMode")
            shuffleModeState.postValue(shuffleMode)
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            Log.d(TAG, "MediaControllerCallback onRepeatModeChanged:$repeatMode")
            repeatModeState.postValue(repeatMode)
        }
    }

    //Widget 交互處理 - 播放狀態
    private fun sendWidgetPlaybackStateIntent(playbackStateCompat: PlaybackStateCompat) {
    }

    //Widget 交互處理 - 播放媒體資訊
    private fun sendWidgetMetadataIntent(metadata: MediaMetadataCompat) {
    }

    companion object {
        // For Singleton instantiation.
        @Volatile
        private var instance: MusicServiceConnection? = null

        fun getInstance(context: Context, serviceComponent: ComponentName) =
            instance ?: synchronized(this) {
                instance ?: MusicServiceConnection(context, serviceComponent)
                    .also { instance = it }
            }
    }
}

@Suppress("PropertyName")
val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()

@Suppress("PropertyName")
val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()