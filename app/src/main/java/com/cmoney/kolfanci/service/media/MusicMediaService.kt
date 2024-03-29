package com.cmoney.kolfanci.service.media

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media.MediaBrowserServiceCompat
import com.cmoney.kolfanci.extension.getFileName
import com.cmoney.kolfanci.extension.isURL
import com.cmoney.xlogin.XLoginHelper
import com.google.android.exoplayer2.BuildConfig
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector.ALL_PLAYBACK_ACTIONS
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.socks.library.KLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

const val BROWSABLE_ROOT = "/"
const val EMPTY_ROOT = "@empty@"
private const val MUSIC_USER_AGENT = "music.agent"
const val BUNDLE_STORIES = "stories"
const val BUNDLE_START_PLAY_POSITION = "start_position"
const val PLAY_ERROR_RESPONSE_CODE_KEY = "PLAY_ERROR_CODE_KEY"
const val PLAY_ERROR_STORY_ID_KEY = "PLAY_ERROR_STORY_ID_KEY"

class MusicMediaService : MediaBrowserServiceCompat(), CoroutineScope by MainScope() {

    private val TAG = MusicMediaService::class.java.simpleName

    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var currentPlayer: Player
    private lateinit var notificationManager: MusicNotificationManager
    private val playerListener = PlayerEventListener()
    private var isForegroundService = false
    private var mediaSession: MediaSessionCompat? = null
    private var currentPlaylistItems: List<MediaMetadataCompat> = emptyList()

    //record playlist
//    private val lastListenStoryDatabase by inject<LastListenStoryDatabase>()
//    private val lastListenStoryDao: LastListenStoryDao by lazy {
//        lastListenStoryDatabase.lastListenStoryDao()
//    }

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(musicAudioAttributes, true)
            setHandleAudioBecomingNoisy(true)
            addListener(playerListener)
        }
    }

    private val musicAudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private fun getDataSourceFactor(): DefaultHttpDataSource.Factory {
        return DefaultHttpDataSource.Factory()
            .setUserAgent("exoplayer-codelab")
            .setTransferListener(null)
            .setConnectTimeoutMs(30000)
            .setReadTimeoutMs(30000)
            .setDefaultRequestProperties(HashMap<String, String>().apply {
                if (XLoginHelper.isLogin) {
                    put("Authorization", "Bearer ${XLoginHelper.accessToken}")
                }
            })
    }

    override fun onCreate() {
        super.onCreate()
        //Notification 使用
        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(this, 0, sessionIntent, PendingIntent.FLAG_IMMUTABLE)
            }

        mediaSession = MediaSessionCompat(this, TAG)
            .apply {
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
                setSessionToken(sessionToken)
                // ExoPlayer 套件
                mediaSessionConnector = MediaSessionConnector(this)
                mediaSessionConnector.setPlaybackPreparer(MusicPlaybackPreparer())
                mediaSessionConnector.setQueueNavigator(MusicQueueNavigator(this))
                mediaSessionConnector.setEnabledPlaybackActions(ALL_PLAYBACK_ACTIONS)
            }

        switchToPlayer(previousPlayer = null, newPlayer = exoPlayer)

        notificationManager = MusicNotificationManager(
            this,
            mediaSession!!.sessionToken,
            PlayerNotificationListener()
        )

        notificationManager.showNotificationForPlayer(currentPlayer)
    }

    private inner class PlayerNotificationListener :
        PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    applicationContext,
                    Intent(applicationContext, this@MusicMediaService.javaClass)
                )

                startForeground(notificationId, notification)
                isForegroundService = true
            }
        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        val isKnownCaller = allowBrowsing(clientPackageName)
        return if (isKnownCaller) {
            BrowserRoot(BROWSABLE_ROOT, null)
        } else {
            if (BuildConfig.DEBUG) {
                BrowserRoot(EMPTY_ROOT, null)
            } else {
                null
            }
        }
    }

    private fun allowBrowsing(clientPackageName: String): Boolean {
        return clientPackageName == packageName
    }

    /**
     * 屬於瀏覽歌曲相關，可以看到會傳入 parentId，
     * 然後再將 result 傳出，在 uamp 專案內點擊專輯後，
     * 會顯示專輯內的歌曲，收到 Id 後，去 local 的 repository 尋找相關的歌曲，
     * 然後將結果透過 result.sendResult(items) 回傳
     */
    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        // TODO:
    }

    private fun switchToPlayer(previousPlayer: Player?, newPlayer: Player) {
        if (previousPlayer == newPlayer) {
            return
        }
        currentPlayer = newPlayer
        if (previousPlayer != null) {
            val playbackState = previousPlayer.playbackState
            if (currentPlaylistItems.isEmpty()) {
                // We are joining a playback session. Loading the session from the new player is
                // not supported, so we stop playback.
                currentPlayer.stop(/* reset= */true)
            } else if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
                preparePlaylist(
                    metadataList = currentPlaylistItems,
                    itemToPlay = currentPlaylistItems[previousPlayer.currentWindowIndex],
                    playWhenReady = previousPlayer.playWhenReady,
                    playbackStartPositionMs = previousPlayer.currentPosition
                )
            }
        }
        mediaSessionConnector.setPlayer(newPlayer)
        previousPlayer?.stop(/* reset= */true)
    }

    private fun preparePlaylist(
        metadataList: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playWhenReady: Boolean,
        playbackStartPositionMs: Long
    ) {
        KLog.i(TAG, "preparePlaylist")

        val initialWindowIndex = if (itemToPlay == null) 0 else metadataList.indexOfFirst {
            it.id == itemToPlay?.id
        }

        currentPlaylistItems = metadataList

        currentPlayer.playWhenReady = playWhenReady
        currentPlayer.stop(/* reset= */ true)
        if (currentPlayer == exoPlayer) {
            itemToPlay?.mediaUri?.let { mediaUri ->
                if (mediaUri.isURL()) {
                    val mediaSource = metadataList.toMediaSource(getDataSourceFactor())
                    exoPlayer.setMediaSource(mediaSource)
                } else {
                    exoPlayer.setMediaItem(MediaItem.fromUri(mediaUri))
                }
            }

            exoPlayer.prepare()
            exoPlayer.seekTo(initialWindowIndex, playbackStartPositionMs)
        }
    }

    /**
     * 播放器 相關狀態事件處理
     */
    private inner class PlayerEventListener : Player.Listener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            KLog.i(TAG, "onPlayerStateChanged:$playbackState")
            when (playbackState) {
                Player.STATE_BUFFERING,
                Player.STATE_READY -> {
                    //TODO: show notification
                    notificationManager.showNotificationForPlayer(currentPlayer)
                    // If playback is paused we remove the foreground state which allows the
                    // notification to be dismissed. An alternative would be to provide a "close"
                    // button in the notification which stops playback and clears the notification.
                    if (playbackState == Player.STATE_READY) {
                        if (!playWhenReady) stopForeground(false)
                    }
                }

                else -> {
                    //TODO: hide notification
                    notificationManager.hideNotification()
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            KLog.e(TAG, "onPlayerError:${error.errorCode}")
            KLog.e(TAG, "onPlayerError:${error.errorCodeName}")
            KLog.e(TAG, "onPlayerError:${error.cause}")
            val currentPlayItem = currentPlaylistItems[currentPlayer.currentMediaItemIndex]
            KLog.e(TAG, "onPlayerError:${currentPlayItem.displayTitle}")
            KLog.e(TAG, "onPlayerError:${currentPlayItem.mediaUri}")

            val intent = Intent(com.cmoney.kolfanci.BuildConfig.APPLICATION_ID)
            val responseCode =
                (error.cause as? HttpDataSource.InvalidResponseCodeException)?.responseCode
                    ?: -1
            intent.putExtra(PLAY_ERROR_RESPONSE_CODE_KEY, responseCode)
            intent.putExtra(PLAY_ERROR_STORY_ID_KEY, currentPlayItem.id)
            LocalBroadcastManager.getInstance(this@MusicMediaService).sendBroadcast(intent)
        }
    }

    /**
     * 設定準備要播放的音樂
     */
    private inner class MusicPlaybackPreparer : MediaSessionConnector.PlaybackPreparer {
        override fun onCommand(
            player: Player,
            command: String,
            extras: Bundle?,
            cb: ResultReceiver?
        ): Boolean = false

        override fun getSupportedPrepareActions(): Long =
            PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH or
                    PlaybackStateCompat.ACTION_PLAY_FROM_URI

        override fun onPrepare(playWhenReady: Boolean) {
            Log.i(TAG, "onPrepare")
        }

        /**
         * @param mediaId 準備要播放的id
         * @param extras binder List music 進來
         */
        override fun onPrepareFromMediaId(
            mediaId: String,
            playWhenReady: Boolean,
            extras: Bundle?
        ) {
            Log.i(TAG, "onPrepareFromMediaId:$mediaId")
        }

        override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) {
        }

        override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) {
            val playItem =
                MediaMetadataCompat.Builder().also {
                    it.id = uri.toString()
                    it.mediaUri = uri.toString()
                    it.displaySubtitle = " "
                    if (uri.isURL()) {
                        val title = extras?.getString("title").orEmpty()
                        it.displayTitle = title
                    } else {
                        it.displayTitle = uri.getFileName(applicationContext)
                    }
                }.build()

            val metadataList = listOf(playItem)

            preparePlaylist(
                metadataList = metadataList,
                itemToPlay = playItem,
                playWhenReady = true,
                playbackStartPositionMs = 0L
            )
        }
    }

    private inner class MusicQueueNavigator(
        mediaSession: MediaSessionCompat
    ) : TimelineQueueNavigator(mediaSession) {

        /**
         * 現在播放歌曲的描述，讓 Notification 顯示使用
         * MediaMetadataCompat 為 Android 所內建的資料結構
         */
        override fun getMediaDescription(
            player: Player,
            windowIndex: Int
        ): MediaDescriptionCompat {
            return currentPlaylistItems[windowIndex].description
        }
    }
}

/**
 * Extension method for building a [ConcatenatingMediaSource] given a [List]
 * of [MediaMetadataCompat] objects.
 */
fun List<MediaMetadataCompat>.toMediaSource(
    dataSourceFactory: DataSource.Factory
): ConcatenatingMediaSource {

    val concatenatingMediaSource = ConcatenatingMediaSource()
    forEach {
        concatenatingMediaSource.addMediaSource(
            it.toMediaSource(dataSourceFactory, it)
        )
    }
    return concatenatingMediaSource
}

/**
 * Extension method for building an [ExtractorMediaSource] from a [MediaMetadataCompat] object.
 *
 * For convenience, place the [MediaDescriptionCompat] into the tag so it can be retrieved later.
 */
private fun MediaMetadataCompat.toMediaSource(
    dataSourceFactory: DataSource.Factory,
    mediaMetadataCompat: MediaMetadataCompat
) =
    ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(mediaUri))

inline val MediaMetadataCompat.mediaUri: Uri
    get() = this.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri()