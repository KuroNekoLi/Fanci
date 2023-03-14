package com.cmoney.kolfanci.ui.screens.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.ui.common.ReplyChatText
import com.cmoney.kolfanci.ui.common.ReplyChatTitleText
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.kolfanci.R

/**
 * 回覆 某人 訊息
 */
@Composable
fun ChatReplyScreen(reply: ChatMessage, onDelete: (ChatMessage) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalColor.current.background),
        contentAlignment = Alignment.TopEnd
    ) {

        Column(
            modifier = Modifier
                .padding(
                    top = 10.dp,
                    bottom = 10.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .fillMaxWidth()
        ) {
            ReplyChatTitleText(text = "回覆・" + reply.author?.name)
            Spacer(modifier = Modifier.height(10.dp))
            ReplyChatText(text = reply.content?.text.orEmpty())
        }

        Image(
            modifier = Modifier
                .padding(10.dp)
                .clickable {
                    onDelete.invoke(reply)
                },
            painter = painterResource(id = R.drawable.reply_close),
            contentDescription = null
        )
    }

}

@Preview(showBackground = true)
@Composable
fun ChatReplyScreenPreview() {
    FanciTheme {
        ChatReplyScreen(
            ChatRoomUseCase.mockMessage
        ) {

        }
    }
}