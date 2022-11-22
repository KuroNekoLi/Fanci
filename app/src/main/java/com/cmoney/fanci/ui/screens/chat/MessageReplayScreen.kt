package com.cmoney.fanci.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.ui.common.ReplyText
import com.cmoney.fanci.ui.common.ReplyTitleText
import com.cmoney.fanci.ui.theme.Black_181C23
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.MediaIChatContent

@Composable
fun MessageReplayScreen(reply: ChatMessage, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(
                top = 10.dp,
                bottom = 10.dp,
                start = 16.dp,
                end = 16.dp
            )
        ) {
            ReplyTitleText(text = "回覆・" + reply.author?.name)
            Spacer(modifier = Modifier.height(10.dp))
            ReplyText(text = reply.content?.text.orEmpty())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageReplayScreenPreview() {
    FanciTheme {
        MessageReplayScreen(
            ChatMessage(
                author = GroupMember(name = "阿修羅"),
                content = MediaIChatContent(
                    text = "內容內容內容內容1234"
                )
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(9.dp))
                .background(Black_181C23)
        )
    }
}