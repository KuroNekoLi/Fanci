package com.cmoney.kolfanci.ui.screens.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanciapi.fanci.model.IReplyMessage
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.common.ReplyChatText
import com.cmoney.kolfanci.ui.common.ReplyChatTitleText
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 回覆 某人 訊息
 */
@Composable
fun ChatReplyScreen(reply: IReplyMessage, onDelete: (IReplyMessage) -> Unit) {
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
            val replyContent = if (reply.replyVotings?.isNotEmpty() == true) {
                reply.replyVotings?.let { iReplyVoting ->
                    val firstVote = iReplyVoting.first()
                    firstVote.title
                }
            } else {
                reply.content?.text
            }

            ReplyChatTitleText(text = "回覆・" + reply.author?.name)
            Spacer(modifier = Modifier.height(10.dp))
            ReplyChatText(text = replyContent.orEmpty())
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
            IReplyMessage()
        ) {

        }
    }
}