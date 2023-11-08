package com.cmoney.kolfanci.ui.screens.media.audio

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.getFileName
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.kolfanci.utils.Utils
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * 音檔 預覽頁面
 */
@Destination
@Composable
fun AudioPreviewScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    uri: Uri,
    viewModel: AudioViewModel = koinViewModel(
        parameters = {
            parametersOf(uri)
        }
    ),
    duration: Long = 0L,
    title: String = ""
) {

    //播放按鈕 顯示樣式(播放中, 暫停)
    val playBtnResource by viewModel.playButtonRes.collectAsState()

    //總共秒數(millisecond)
    val audioDuration by viewModel.audioDuration.collectAsState()

    val mediaPosition by viewModel.mediaPosition.collectAsState()

    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current
        ?.onBackPressedDispatcher

    LaunchedEffect(key1 = Unit) {
        if (duration != 0L) {
            viewModel.setDuration(duration)
        }
    }

    AudioPreviewScreenView(
        modifier = modifier,
        uri = uri,
        audioTitle = title,
        playBtnResource = playBtnResource,
        audioDuration = audioDuration,
        mediaPosition = mediaPosition,
        onPlayClick = {
            viewModel.play(uri, title)
        },
        onSeekTo = {
            viewModel.seekTo(it)
        },
        onBack = {
            onBackPressedDispatcher?.onBackPressed()
        },
        onStopUpdatePosition = {
            viewModel.stopUpdatePosition()
        }
    )

    BackHandler {
        viewModel.stopPlay()
        navController.popBackStack()
    }
}

@Composable
fun AudioPreviewScreenView(
    modifier: Modifier = Modifier,
    uri: Uri,
    audioTitle: String,
    onPlayClick: () -> Unit,
    playBtnResource: Int,
    audioDuration: Long,
    mediaPosition: Long,
    onSeekTo: (Float) -> Unit,
    onBack: () -> Unit,
    onStopUpdatePosition: () -> Unit
) {
    val newAudioTitle = audioTitle.ifEmpty {
        uri.getFileName(LocalContext.current)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopBarScreen(
                title = newAudioTitle.orEmpty(),
                backClick = onBack
            )
        },
        backgroundColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            SimplePlayer(
                modifier = modifier.padding(innerPadding),
                audioTitle = audioTitle.orEmpty(),
                onPlayClick = onPlayClick,
                playBtnResource = playBtnResource,
                audioDuration = audioDuration,
                mediaPosition = mediaPosition,
                onSeekTo = onSeekTo,
                onStopUpdatePosition = onStopUpdatePosition
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SimplePlayer(
    modifier: Modifier = Modifier,
    audioTitle: String,
    playBtnResource: Int,
    onPlayClick: () -> Unit,
    audioDuration: Long,
    mediaPosition: Long,
    onSeekTo: (Float) -> Unit,
    onStopUpdatePosition: () -> Unit
) {
    //是否正在 拖曳 slider
    var isSliding by remember {
        mutableStateOf(false)
    }

    //拖曳 position
    var seekPosition by remember {
        mutableFloatStateOf(0f)
    }

    Column(
        modifier = modifier
            .background(
                color = colorResource(id = R.color.color_20262F)
            )
            .padding(
                top = 21.dp, start = 24.dp, end = 24.dp, bottom = 21.dp
            )
    ) {
        Text(
            modifier = Modifier.basicMarquee(
                iterations = Int.MAX_VALUE
            ),
            text = audioTitle,
            color = Color.White,
            fontSize = 16.sp,
            maxLines = 1,
        )

        Spacer(modifier = Modifier.height(21.dp))

        //Seekbar
        Slider(
            value = if (isSliding) {
                seekPosition
            } else {
                mediaPosition.toFloat()
            },
            valueRange = 0f..audioDuration.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = LocalColor.current.primary,
                activeTrackColor = LocalColor.current.primary,
                inactiveTrackColor = LocalColor.current.background
            ),
            onValueChange = {
                isSliding = true
                seekPosition = it
                onStopUpdatePosition.invoke()
            },
            onValueChangeFinished = {
                isSliding = false
                onSeekTo.invoke(seekPosition)
                seekPosition = 0f
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        //Time
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = Utils.timeMillisToMinutesSeconds(mediaPosition),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.White,
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = Utils.timeMillisToMinutesSeconds((audioDuration - mediaPosition)),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.White,
                )
            )
        }

        //Controller
        Row(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        onSeekTo(
                            (mediaPosition.minus(10.times(1000))).toFloat()
                        )
                    },
                painter = painterResource(id = R.drawable.quickplay_back),
                contentDescription = "play back"
            )

            Spacer(modifier = Modifier.width(24.dp))

            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        onPlayClick.invoke()
                    },
                painter = painterResource(id = playBtnResource),
                contentDescription = "play back"
            )

            Spacer(modifier = Modifier.width(24.dp))

            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        onSeekTo(
                            (mediaPosition.plus(10.times(1000))).toFloat()
                        )
                    },
                painter = painterResource(id = R.drawable.quickplay_forward),
                contentDescription = "play forward"
            )
        }

    }

}

@Preview
@Composable
fun SimplePlayerPreview() {
    FanciTheme {
        SimplePlayer(
            audioTitle = "跑馬燈跑馬燈跑馬燈跑馬燈跑馬燈跑馬燈跑馬燈",
            playBtnResource = R.drawable.play,
            onPlayClick = {},
            audioDuration = 100,
            mediaPosition = 0,
            onSeekTo = {},
            onStopUpdatePosition = {}
        )
    }
}


@Preview
@Composable
fun AudioPreviewScreenPreview() {
    FanciTheme {
        AudioPreviewScreenView(
            uri = Uri.EMPTY,
            audioTitle = "",
            onPlayClick = {},
            playBtnResource = R.drawable.play,
            audioDuration = 100,
            mediaPosition = 0,
            onSeekTo = {},
            onBack = {},
            onStopUpdatePosition = {}
        )
    }
}