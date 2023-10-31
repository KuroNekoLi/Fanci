package com.cmoney.kolfanci.ui.screens.shared.bottomSheet.audio

import android.net.Uri
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.media.audio.AudioViewModel
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.item.AudioSpeedItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.kolfanci.utils.Utils
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * 底部 音樂控制器
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AudioBottomPlayerScreen(
    modifier: Modifier = Modifier,
    state: ModalBottomSheetState,
    viewModel: AudioViewModel = koinViewModel(
        parameters = {
            parametersOf(Uri.EMPTY)
        }
    )
) {
    val coroutineScope = rememberCoroutineScope()

    fun hideBottomSheet() {
        coroutineScope.launch {
            state.hide()
        }
    }

    //正在播的音檔title
    val title by viewModel.title.collectAsState()

    //正在播的音檔長度
    val duration by viewModel.audioDuration.collectAsState()

    //正在播放的位置
    val mediaPosition by viewModel.mediaPosition.collectAsState()

    //播放按鈕icon
    val playBtnResource by viewModel.playButtonRes.collectAsState()

    //播放速度 彈窗
    var showSpeedSetting by remember {
        mutableStateOf(false)
    }

    val speedTitle by viewModel.speedTitle.collectAsState()

    ModalBottomSheetLayout(
        sheetState = state,
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        sheetContent = {
            AudioBottomPlayerScreenView(
                modifier = modifier,
                audioTitle = title,
                speedTitle = speedTitle,
                playBtnResource = playBtnResource,
                onPlayClick = {
                    viewModel.pauseOrPlay()
                },
                audioDuration = duration,
                mediaPosition = mediaPosition,
                onSeekTo = {
                    viewModel.seekTo(it)
                },
                onStopUpdatePosition = {
                    viewModel.stopUpdatePosition()
                },
                onSpeedSettingClick = {
                    showSpeedSetting = true
                },
                onClose = {
                    viewModel.stopPlay()
                    hideBottomSheet()
                },
                onCollapseClick = {
                    hideBottomSheet()
                }
            )
        }
    ) {}

    LaunchedEffect(key1 = Unit) {
        viewModel.fetchCurrentPlayInfo()
    }

    //================== Dialog ==================
    if (showSpeedSetting) {
        DialogScreen(
            title = stringResource(id = R.string.play_speed),
            subTitle = stringResource(id = R.string.play_speed_desc),
            onDismiss = {
                showSpeedSetting = false
            }
        ) {
            AudioSpeedItemScreen(
                onClick = {
                    showSpeedSetting = false
                    viewModel.changeSpeed(it)
                },
                onDismiss = {
                    showSpeedSetting = false
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AudioBottomPlayerScreenView(
    modifier: Modifier = Modifier,
    audioTitle: String,
    speedTitle: String,
    playBtnResource: Int,
    onPlayClick: () -> Unit,
    audioDuration: Long,
    mediaPosition: Long,
    onSeekTo: (Float) -> Unit,
    onStopUpdatePosition: () -> Unit,
    onSpeedSettingClick: () -> Unit,
    onClose: () -> Unit,
    onCollapseClick: () -> Unit
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
                top = 21.dp, start = 24.dp, end = 24.dp, bottom = 30.dp
            )
    ) {

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .basicMarquee(
                        iterations = Int.MAX_VALUE
                    ),
                text = audioTitle,
                color = Color.White,
                fontSize = 16.sp,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.width(10.dp))

            //Speed setting
            Box(
                modifier = Modifier
                    .size(width = 55.dp, height = 25.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(LocalColor.current.background)
                    .clickable {
                        onSpeedSettingClick.invoke()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = speedTitle, fontSize = 16.sp, color = LocalColor.current.text.default_100)
            }
        }

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
                        onClose.invoke()
                    },
                painter = painterResource(id = R.drawable.player_close),
                contentDescription = "close"
            )

            Spacer(modifier = Modifier.width(40.dp))

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

            Spacer(modifier = Modifier.width(40.dp))

            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        onCollapseClick.invoke()
                    },
                painter = painterResource(id = R.drawable.player_collapse),
                contentDescription = "collapse"
            )
        }

    }

}

@Preview
@Composable
fun AudioBottomPlayerScreenPreview() {
    FanciTheme {
        AudioBottomPlayerScreenView(
            audioTitle = "跑馬燈,跑馬燈,跑馬燈,跑馬燈,跑馬燈,跑馬燈,跑馬燈,跑馬燈,跑馬燈,跑馬燈,跑馬燈,跑馬燈,",
            playBtnResource = R.drawable.play,
            onPlayClick = {},
            audioDuration = 100,
            mediaPosition = 0,
            onSeekTo = {},
            onStopUpdatePosition = {},
            onSpeedSettingClick = {},
            onClose = {},
            onCollapseClick = {},
            speedTitle = "1x"
        )
    }
}