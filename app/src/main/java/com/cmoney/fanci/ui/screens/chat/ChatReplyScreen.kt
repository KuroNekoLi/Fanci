package com.cmoney.fanci.ui.screens.chat

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
import com.cmoney.fanci.R
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.ui.common.ReplyChatText
import com.cmoney.fanci.ui.common.ReplyChatTitleText
import com.cmoney.fanci.ui.theme.Black_181C23
import com.cmoney.fanci.ui.theme.FanciTheme

/**
 * 回覆 某人 訊息
 */
@Composable
fun ChatReplyScreen(reply: ChatMessageModel.Reply, onDelete: (ChatMessageModel.Reply) -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Black_181C23),
        contentAlignment = Alignment.TopEnd
    ) {

        Column(
            modifier = Modifier.padding(
                top = 10.dp,
                bottom = 10.dp,
                start = 16.dp,
                end = 16.dp
            ).fillMaxWidth()
        ) {
            ReplyChatTitleText(text = "回覆・" + reply.replyUser.nickname)
            Spacer(modifier = Modifier.height(10.dp))
            ReplyChatText(text = reply.text)
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
            ChatMessageModel.Reply(
                replyUser = ChatMessageModel.User(avatar = "", nickname = "阿修羅"),
                text = "內容內容內容內容內容內容"
            )
        ){

        }
    }
}