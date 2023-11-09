package com.cmoney.kolfanci.ui.screens.shared.audio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 音樂播放 mini icon
 *
 * @param isPlaying 是否要播放動畫, true -> play, false -> pause
 * @param onClick click callback
 */
@Composable
fun AudioMiniPlayIconScreen(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(width = 61.dp, height = 50.dp)
            .clip(RoundedCornerShape(topStart = 7.dp, bottomStart = 7.dp))
            .background(LocalColor.current.primary)
            .clickable {
                onClick.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.Asset("audio_playing.lottie"))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            reverseOnRepeat = true,
            isPlaying = isPlaying
        )
        LottieAnimation(
            composition = composition,
            progress = { progress }
        )

    }
}

@Preview(showBackground = true)
@Composable
fun AudioMiniPlayIconScreenPreview() {
    FanciTheme {
        AudioMiniPlayIconScreen(
            onClick = {}
        )
    }
}