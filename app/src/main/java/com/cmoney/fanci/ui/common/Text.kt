package com.cmoney.fanci.ui.common

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.ui.theme.White_767A7F
import com.cmoney.fanci.ui.theme.White_C7C7C7
import com.cmoney.fanci.ui.theme.White_DDDEDF


@Composable
fun GroupText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 16.sp,
            color = Color.White
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@Composable
fun CategoryText(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 14.sp,
            color = MaterialTheme.colors.onSurface
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun ChannelText(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 16.sp,
            color = Color.White
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

/**
 * 聊天室 po文時間
 */
@Composable
fun ChatTimeText(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 12.sp,
            color = White_767A7F
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

/**
 * 聊天室 內文
 */
@Composable
fun ChatMessageText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 17.sp,
            color = White_DDDEDF
        ),
        modifier = modifier
    )
}

@Composable
fun EmojiText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 14.sp,
            color = White_C7C7C7
        ),
        modifier = modifier
    )
}