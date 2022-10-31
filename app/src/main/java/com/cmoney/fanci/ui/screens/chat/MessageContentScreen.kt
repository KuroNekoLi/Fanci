package com.cmoney.fanci.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import com.cmoney.fanci.ui.common.ChatMessageText
import com.cmoney.fanci.ui.common.ChatTimeText
import com.cmoney.fanci.ui.screens.shared.ChatUsrAvatarScreen
import com.cmoney.fanci.ui.screens.shared.EmojiCountScreen
import com.cmoney.fanci.ui.theme.Black_181C23
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.White_494D54
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 聊天內容
 */
@Composable
fun MessageContentScreen(
    messageModel: ChatMessageModel,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onMsgLongClick: (ChatMessageModel) -> Unit,
    onMsgDismissHide: (ChatMessageModel) -> Unit
) {
    val contentPaddingModifier = Modifier.padding(top = 10.dp, start = 40.dp, end = 40.dp)
    val defaultColor = MaterialTheme.colors.surface
    var longTap by remember { mutableStateOf(false) }
    var backgroundColor by remember { mutableStateOf(defaultColor) }

    Box(modifier = modifier
        .fillMaxWidth()
        .background(backgroundColor)
        .pointerInput(messageModel) {
            detectTapGestures(
                onPress = {
                    tryAwaitRelease()
                    longTap = false
                    backgroundColor = defaultColor
                },
                onLongPress = {
                    longTap = true
                    backgroundColor = White_494D54
                    coroutineScope.launch {
                        delay(300)
                        if (longTap && !messageModel.message.isRecycle) {
                            onMsgLongClick.invoke(messageModel)
                        }
                    }
                }
            )
        }
    ) {
        Column(
            modifier = modifier
                .padding(top = 10.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth()
        ) {
            //隱藏用戶
            if (messageModel.message.isHideUser) {
                MessageHideUserScreen(chatMessageModel = messageModel){
                    onMsgDismissHide.invoke(it)
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    //大頭貼
                    ChatUsrAvatarScreen(messageModel.poster)
                    Spacer(modifier = Modifier.width(10.dp))
                    //發文時間
                    ChatTimeText(messageModel.displayTime)
                }

                //收回
                if (messageModel.message.isRecycle) {
                    MessageRecycleScreen(modifier = contentPaddingModifier)
                } else {

                    //Reply
                    messageModel.message.reply?.apply {
                        MessageReplayScreen(
                            this, modifier = contentPaddingModifier
                                .clip(RoundedCornerShape(9.dp))
                                .background(Black_181C23)
                        )
                    }

                    //內文
                    ChatMessageText(
                        modifier = contentPaddingModifier,
                        text = messageModel.message.text
                    )

                    messageModel.message.media?.forEach { mediaContent ->
                        when (mediaContent) {
                            is ChatMessageModel.Media.Article -> {
                                MessageArticleScreen(
                                    modifier = contentPaddingModifier,
                                    thumbnail = mediaContent.thumbnail,
                                    channel = mediaContent.from,
                                    title = mediaContent.title
                                )
                            }
                            is ChatMessageModel.Media.Instagram -> {
                                MessageIGScreen(
                                    modifier = contentPaddingModifier,
                                    thumbnail = mediaContent.thumbnail,
                                    channel = mediaContent.channel,
                                    title = mediaContent.title
                                )
                            }
                            is ChatMessageModel.Media.Youtube -> {
                                MessageYTScreen(
                                    modifier = contentPaddingModifier,
                                    thumbnail = mediaContent.thumbnail,
                                    channel = mediaContent.channel,
                                    title = mediaContent.title
                                )
                            }
                            is ChatMessageModel.Media.Image -> {
                                MessageImageScreen(
                                    images = mediaContent.image,
                                    modifier = Modifier.padding(start = 40.dp, top = 10.dp)
                                )
                            }
                        }
                    }

                    //Emoji
                    messageModel.message.emoji?.apply {
                        FlowRow(
                            modifier = contentPaddingModifier.fillMaxWidth(),
                            crossAxisSpacing = 10.dp
                        ) {
                            this.forEach { emoji ->
                                EmojiCountScreen(
                                    modifier = Modifier
                                        .padding(end = 10.dp),
                                    emojiResource = emoji.resource,
                                    countText = emoji.count.toString()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun MessageContentScreenPreview() {
    FanciTheme {
        MessageContentScreen(
            coroutineScope = rememberCoroutineScope(),
            messageModel = ChatRoomUseCase.allMessageType,
            onMsgLongClick = {}
        ) {

        }
    }
}