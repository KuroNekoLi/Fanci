package com.cmoney.kolfanci.ui.screens.chat.attachment

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun AttachmentAudioScreen(
    modifier: Modifier = Modifier,
    itemModifier: Modifier = Modifier,
    audioList: List<Uri>,
    onClick: (Uri) -> Unit
) {
    val listState = rememberLazyListState()

    LazyRow(
        modifier = modifier.padding(start = 10.dp, end = 10.dp),
        state = listState, horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(audioList) { audio ->
            AttachmentAudioItem(
                modifier = itemModifier,
                audio = audio,
                onClick = onClick
            )
        }
    }
}

@Composable
fun AttachmentAudioItem(
    modifier: Modifier = Modifier,
    audio: Uri,
    onClick: (Uri) -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(LocalColor.current.background)
            .clickable {
                onClick.invoke(audio)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.TopEnd)
        ) {

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    modifier = Modifier
                        .clickable {

                        }
                        .padding(15.dp),
                    painter = painterResource(id = R.drawable.play),
                    contentDescription = "play"
                )

                Text(
                    text = "上課教材 001.mp3",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = LocalColor.current.text.default_100,
                    )
                )
            }

            //Close btn
            Image(
                modifier = Modifier
                    .padding(10.dp)
                    .clickable {

                    },
                painter = painterResource(id = R.drawable.close), contentDescription = null
            )
        }
    }
}

@Preview
@Composable
fun AttachmentAudioItemPreview() {
    FanciTheme {
        AttachmentAudioItem(
            modifier = Modifier
                .width(270.dp)
                .height(75.dp),
            audio = Uri.EMPTY,
            onClick = {}
        )
    }
}

@Preview
@Composable
fun AttachmentAudioScreenPreview() {
    FanciTheme {
        AttachmentAudioScreen(
            itemModifier = Modifier
                .width(270.dp)
                .height(75.dp),
            audioList = listOf(
                Uri.EMPTY,
                Uri.EMPTY,
                Uri.EMPTY
            ),
            onClick = {}
        )
    }
}