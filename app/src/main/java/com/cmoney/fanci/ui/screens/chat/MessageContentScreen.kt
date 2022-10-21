package com.cmoney.fanci.ui.screens.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cmoney.fanci.R
import com.cmoney.fanci.model.ChatMessageModel
import com.cmoney.fanci.ui.common.ChatMessageText
import com.cmoney.fanci.ui.common.ChatTimeText
import com.cmoney.fanci.ui.screens.shared.ChatUsrAvatarScreen
import com.cmoney.fanci.ui.screens.shared.EmojiCountScreen
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun MessageContentScreen(messageModel: ChatMessageModel) {
    val contentPaddingModifier = Modifier.padding(top = 10.dp, start = 40.dp, end = 40.dp)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            //大頭貼
            ChatUsrAvatarScreen(messageModel.poster)
            Spacer(modifier = Modifier.width(10.dp))
            //發文時間
            ChatTimeText(messageModel.displayTime)
        }
        //Reply
        messageModel.message.reply?.apply {
            MessageReplayScreen(this, modifier = contentPaddingModifier)
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
                    // TODO: 調整成N張圖片, 縮成一張
                    AsyncImage(
                        model = mediaContent.image.first(),
                        modifier = Modifier
                            .padding(start = 40.dp, top = 10.dp)
                            .size(205.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        alignment = Alignment.TopCenter,
                        placeholder = painterResource(id = R.drawable.resource_default)
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

@Preview(showBackground = false)
@Composable
fun MessageContentScreenPreview() {
    MessageContentScreen(
        ChatMessageModel(
            poster = ChatMessageModel.User(
                avatar = "https://picsum.photos/100/100",
                nickname = "Warren"
            ),
            publishTime = 1666334733,
            message = ChatMessageModel.Message(
                reply = ChatMessageModel.Reply(
                    replyUser = ChatMessageModel.User(avatar = "", nickname = "阿修羅"),
                    text = "內容內容內容內容內容內容"
                ),
                text = "房市利空齊發，不動產大老林正雄提出建言，認為現階段打房政策已奏效，政府不應在此時通過平均地權條例的修法，而內政部長徐國勇則也語帶保留的說，修法時將會考慮各時期的情景轉變。",
                media = listOf(
                    ChatMessageModel.Media.Article(
                        from = "Medium",
                        title = "12 BEST Apps for Productivity You Need To Use in 2022",
                        thumbnail = "https://miro.medium.com/max/1100/0*VLSb2Oc_WSxtqN6K"
                    ),
                    ChatMessageModel.Media.Youtube(
                        channel = "蔡阿嘎Life",
                        title = "【蔡阿嘎地獄廚房#16】廚佛Fred大戰阿煨師，幹話最多的兩個男人正面對決！",
                        thumbnail = "https://img.youtube.com/vi/1p_GLULMNbw/0.jpg"
                    ),
                    ChatMessageModel.Media.Instagram(
                        channel = "阿滴英文",
                        title = "有妹妹然後教英文的那個\uD83D\uDE4B\uD83C\uDFFB\u200D♂️@raydudaily 比較多日常廢文",
                        thumbnail = "https://okapi.books.com.tw/uploads/image/2018/03/source/22908-1520239183.jpg"
                    ),
                    ChatMessageModel.Media.Image(
                        image = listOf(
                            "https://picsum.photos/500/500",
                            "https://picsum.photos/400/400",
                            "https://picsum.photos/300/300"
                        )
                    )
                ),
                emoji = listOf(
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_haha,
                        count = 777
                    ),
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_cry,
                        count = 123
                    ),
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_like,
                        count = 12355
                    ),
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_happiness,
                        count = 9
                    ),
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_angry,
                        count = 12
                    ),
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_happy,
                        count = 12
                    ),
                    ChatMessageModel.Emoji(
                        resource = R.drawable.emoji_love,
                        count = 55
                    ),
                )
            )
        )
    )
}