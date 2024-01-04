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
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import kotlinx.coroutines.delay

@Preview
@Composable
fun TimerScreen(modifier: Modifier = Modifier) {
    var currentTime by remember { mutableStateOf(0.0f) } // Start time
    var recordTime by remember { mutableStateOf(0.0f) } // Record time
    var isRunning by remember { mutableStateOf(false) }
    val maxTime = 45.0f // Maximum time in seconds
    // 計算分鐘與秒數
    val minutes = (currentTime.toInt() / 60).toString().padStart(2, '0')
    val seconds = (currentTime.toInt() % 60).toString().padStart(2, '0')

    // Timer logic
    LaunchedEffect(key1 = isRunning) {
        while (isRunning && currentTime < maxTime) {
            delay(200L)
            currentTime += 0.2f
        }
    }

    // Calculate progress as a percentage, put this after LaunchedEffect
    val progress = (currentTime / maxTime)

    Column(
        modifier = modifier,
        horizontalAlignment = CenterHorizontally,
    ) {
        Text(
            text = "$minutes:$seconds",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color.Green,
        )
        Row(
            verticalAlignment = CenterVertically,
        ) {
            OutlinedIconButton(
                border = BorderStroke(width = 1.dp, color = Color.Gray),
                onClick = { /*TODO*/ }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_delete_record),
                    null,
                    tint = Color.Red
                )
            }
            TimeProgress(
                progress = progress,
                modifier = Modifier
                    .size(127.dp)
                    .clickable {
                        if (!isRunning && currentTime < maxTime) {
                            isRunning = true
                        } else if (isRunning) {
                            isRunning = false
                            recordTime = currentTime
                            currentTime = 0f
                        }
                    },
            )
            FilledIconButton(
                onClick = { /*TODO*/
                }) {
                Icon(painter = painterResource(id = R.drawable.icon_arrow_up), null)
            }
        }
    }
}

@Composable
fun TimeProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 5.dp,
    backgroundColor: Color = Color.White.copy(alpha = 0.05f),
    foregroundColor: Color = Color.Green,
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
        CircularProgressIndicator(
            progress = 1f, // Full circle
            color = backgroundColor, // The color for the background circle
            modifier = modifier,
            strokeWidth = strokeWidth
        )
        CircularProgressIndicator(
            progress = progress,
            color = foregroundColor,
            modifier = modifier,
            strokeWidth = strokeWidth
        )
    }
}
