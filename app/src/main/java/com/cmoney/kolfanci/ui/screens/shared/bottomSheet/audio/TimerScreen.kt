package com.cmoney.kolfanci.ui.screens.shared.bottomSheet.audio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.media.audio.ProgressIndicator
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun TimerScreen(
    time: String,
    isRecorderHintVisible: Boolean,
    isDeleteVisible: Boolean,
    isUploadVisible: Boolean,
    progress: Float,
    progressIndicator: ProgressIndicator,
    onPlayingButtonClick: () -> Unit,
    onDelete: () -> Unit,
    onUpload: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = CenterHorizontally
    ) {
        if (isRecorderHintVisible) DefaultText() else TimerText(time)
        Row(
            verticalAlignment = CenterVertically
        ) {
            if (isDeleteVisible) {
                OutlinedIconButton(
                    border = BorderStroke(width = 1.dp, color = Color.Gray),
                    onClick = { onDelete() }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_delete_record),
                        null,
                        tint = Color.Red
                    )
                }
            }
            when (progressIndicator) {
                ProgressIndicator.DEFAULT -> {
                    RecorderDefault(Modifier.clickable {
                        onPlayingButtonClick()
                    })
                }

                ProgressIndicator.RECORDING -> {
                    RecorderRecording(Modifier.clickable {
                        onPlayingButtonClick()
                    })
                }

                ProgressIndicator.COMPLETE -> {
                    RecorderComplete(Modifier.clickable {
                        onPlayingButtonClick()
                    })
                }

                ProgressIndicator.PLAYING -> {
                    RecorderPlaying(
                        progress = progress,
                        modifier = Modifier
                            .clickable {
                                onPlayingButtonClick()
                            }
                    )
                }

                ProgressIndicator.PAUSE -> {
                    RecorderPause(
                        progress = progress,
                        modifier = Modifier
                            .clickable {
                                onPlayingButtonClick()
                            }
                    )
                }
            }
            if (isUploadVisible) {
                FilledIconButton(
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = LocalColor.current.primary
                    ),
                    onClick = { /*TODO*/
                    }) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_arrow_up), null
                    )
                }
            }
        }
    }
}

@Composable
private fun TimerText(time: String) {
    Text(
        text = time,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = colorResource(id = R.color.color_FF6DC160)
    )
}

@Composable
private fun DefaultText() {
    Text(
        text = stringResource(id = R.string.record_hint),
        fontSize = 16.sp,
        color = Color.White
    )
}

@Composable
fun RecorderPlaying(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 5.dp,
    foregroundColor: Color = Color.Green,
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
        BackgroundCircularProgressIndicator(modifier = modifier)
        CircularProgressIndicator(
            progress = progress,
            color = foregroundColor,
            modifier = modifier.size(127.dp),
            strokeWidth = strokeWidth
        )
        Icon(
            painter = painterResource(id = R.drawable.icon_pause),
            contentDescription = null,
            tint = Color.Green
        )
    }
}

@Composable
fun BackgroundCircularProgressIndicator(
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 5.dp,
    backgroundColor: Color = Color.White.copy(alpha = 0.05f),
) {
    CircularProgressIndicator(
        progress = 1f, // Full circle
        color = backgroundColor, // The color for the background circle
        modifier = modifier,
        strokeWidth = strokeWidth
    )
}

@Composable
fun RecorderDefault(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(150.dp)
    ) {
        BackgroundCircularProgressIndicator(modifier = modifier.size(127.dp))
        Icon(
            painter = painterResource(id = R.drawable.icon_start_record),
            contentDescription = null,
            tint = Color.Red
        )
    }
}

@Composable
fun RecorderRecording(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(150.dp)
    ) {
        BackgroundCircularProgressIndicator(modifier = modifier)
        Icon(
            painter = painterResource(id = R.drawable.img_recording_background),
            contentDescription = null,
            modifier = Modifier.size(127.dp),
            tint = Color.Green,
        )
        Icon(
            painter = painterResource(id = R.drawable.img_recording_forground),
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
fun RecorderComplete(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(150.dp)
    ) {
        BackgroundCircularProgressIndicator(
            modifier = modifier.size(127.dp),
            backgroundColor = Color.Green
        )
        Icon(
            painter = painterResource(id = R.drawable.icon_play),
            contentDescription = null,
            tint = Color.Green
        )
    }
}

@Composable
fun RecorderPause(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 5.dp,
    foregroundColor: Color = Color.Green
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(150.dp)
    ) {
        BackgroundCircularProgressIndicator(modifier = modifier)
        CircularProgressIndicator(
            progress = progress,
            color = foregroundColor,
            modifier = modifier.size(127.dp),
            strokeWidth = strokeWidth
        )
        Icon(
            painter = painterResource(id = R.drawable.icon_play),
            contentDescription = null,
            tint = Color.Green
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecorderDefaultPreview() {
    RecorderDefault()
}

@Preview(showBackground = true)
@Composable
fun RecorderRecordingPreview() {
    RecorderRecording()
}

@Preview(showBackground = true)
@Composable
fun RecorderCompletePreview() {
    RecorderComplete()
}

@Preview(showBackground = true)
@Composable
fun RecorderPlayingPreview() {
    RecorderPlaying(0.6f, Modifier.size(127.dp))
}