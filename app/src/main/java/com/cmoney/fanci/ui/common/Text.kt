package com.cmoney.fanci.ui.common

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.ui.theme.*


@Composable
fun GroupText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 16.sp,
            color = LocalColor.current.text.default_100,
            fontWeight = FontWeight.Bold
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
            color = LocalColor.current.text.default_50
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
            color = LocalColor.current.text.default_100
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
            color = LocalColor.current.text.default_80
        ),
        modifier = modifier
    )
}

/**
 * 聊天 回覆 content title
 */
@Composable
fun ReplyTitleText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 12.sp,
            color = LocalColor.current.text.default_100
        ),
        modifier = modifier
    )
}

/**
 * 聊天 回覆 content
 */
@Composable
fun ReplyText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 14.sp,
            color = LocalColor.current.text.default_100
        ),
        modifier = modifier
    )
}

/**
 * 聊天 回覆 title
 */
@Composable
fun ReplyChatTitleText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = LocalColor.current.text.default_100,
        fontSize = 12.sp,
        modifier = modifier,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

/**
 * 聊天 回覆
 */
@Composable
fun ReplyChatText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = LocalColor.current.text.default_100,
        fontSize = 14.sp,
        modifier = modifier,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}