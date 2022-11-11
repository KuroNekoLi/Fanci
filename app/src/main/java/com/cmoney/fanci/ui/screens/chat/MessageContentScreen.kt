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
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.model.usecase.ChatRoomUseCase
import com.cmoney.fanci.ui.common.ChatTimeText
import com.cmoney.fanci.ui.common.AutoLinkText
import com.cmoney.fanci.ui.screens.shared.ChatUsrAvatarScreen
import com.cmoney.fanci.ui.screens.shared.EmojiCountScreen
import com.cmoney.fanci.ui.theme.*
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanci.utils.Utils
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class MessageContentCallback {
    data class LongClick(val message: ChatMessageModel) : MessageContentCallback()
    data class MsgDismissHideClick(val message: ChatMessageModel) : MessageContentCallback()
    data class EmojiClick(val message: ChatMessageModel, val resourceId: Int) :
        MessageContentCallback()
}

/**
 * 聊天內容
 */
@Composable
fun MessageContentScreen(
    messageModel: ChatMessageModel,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onMessageContentCallback: (MessageContentCallback) -> Unit
) {
    val contentPaddingModifier = Modifier.padding(top = 10.dp, start = 40.dp, end = 10.dp)
    val defaultColor = LocalColor.current.env_80
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
                    backgroundColor = White_767A7F
                    coroutineScope.launch {
                        delay(300)
                        if (longTap && !messageModel.message.isRecycle) {
                            onMessageContentCallback.invoke(
                                MessageContentCallback.LongClick(messageModel)
                            )
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
                MessageHideUserScreen(chatMessageModel = messageModel) {
                    onMessageContentCallback.invoke(MessageContentCallback.MsgDismissHideClick(it))
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
                                .background(LocalColor.current.background)
                        )
                    }

                    //內文
                    AutoLinkText(
                        modifier = contentPaddingModifier,
                        text = messageModel.message.text,
                        fontSize = 17.sp,
                        color = LocalColor.current.text.default_100
                    )

                    //OG
                    Utils.extractLinks(messageModel.message.text).forEach { url ->
                        MessageOGScreen(modifier = contentPaddingModifier, url = url)
                    }

                    messageModel.message.media.forEach { mediaContent ->
                        MediaContent(contentPaddingModifier, mediaContent)
                    }

                    //Emoji
                    messageModel.message.emoji.apply {
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
                                ) {
                                    onMessageContentCallback.invoke(
                                        MessageContentCallback.EmojiClick(
                                            messageModel,
                                            it
                                        )
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}

/**
 * 多媒體 型態
 */
@Composable
private fun MediaContent(modifier: Modifier, media: ChatMessageModel.Media) {
    when (media) {
        is ChatMessageModel.Media.Image -> {
            MessageImageScreen(
                images = media.image,
                modifier = modifier
            )
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
        ) {

        }
    }
}