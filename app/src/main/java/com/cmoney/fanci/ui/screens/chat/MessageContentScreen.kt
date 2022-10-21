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
import com.cmoney.fanci.ui.common.ChatMessageText
import com.cmoney.fanci.ui.common.ChatTimeText
import com.cmoney.fanci.ui.screens.shared.EmojiCountScreen
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun MessageContentScreen() {
    val contentPaddingModifier = Modifier.padding(top = 10.dp, start = 40.dp, end = 40.dp)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ChatUsrAvatarScreen()
            Spacer(modifier = Modifier.width(10.dp))
            ChatTimeText("現在")
        }
        //內文
        ChatMessageText(
            modifier = Modifier.padding(start = 40.dp, end = 40.dp),
            text = "以圖片搜尋都找不到想詢問版上的大家有沒有知道\uD83E\uDD7A謝謝大家\uD83D\uDC97～"
        )

        //Article
        MessageArticleScreen(
            modifier = contentPaddingModifier,
            thumbnail = "https://miro.medium.com/max/1100/0*KcA3ptDox2uP1ZAN",
            channel = "Medium",
            title = "I found the perfect Architecture for Flutter apps."
        )

        //YT
        MessageYTScreen(
            modifier = contentPaddingModifier,
            thumbnail = "https://img.youtube.com/vi/1p_GLULMNbw/0.jpg",
            channel = "蔡阿嘎Life",
            title = "【蔡阿嘎地獄廚房#16】廚佛Fred大戰阿煨師，幹話最多的兩個男人正面對決！"
        )

        //IG
        MessageIGScreen(
            modifier = contentPaddingModifier,
            thumbnail = "https://okapi.books.com.tw/uploads/image/2018/03/source/22908-1520239183.jpg",
            channel = "阿滴英文",
            title = "有妹妹然後教英文的那個\uD83D\uDE4B\uD83C\uDFFB\u200D♂️@raydudaily 比較多日常廢文"
        )

        // TODO: 調整成N張圖片, 縮成一張
        AsyncImage(
            model = "https://picsum.photos/500/500",
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

        //Emoji
        FlowRow(
            modifier = contentPaddingModifier .fillMaxWidth(),
            crossAxisSpacing = 10.dp
        ) {
            EmojiCountScreen(
                modifier = Modifier
                    .padding(end = 10.dp),
                emojiResource = R.drawable.emoji_love,
                countText = "234"
            )
            EmojiCountScreen(
                modifier = Modifier
                    .padding(end = 10.dp),
                emojiResource = R.drawable.emoji_happy,
                countText = "567"
            )
            EmojiCountScreen(
                modifier = Modifier
                    .padding(end = 10.dp),
                emojiResource = R.drawable.emoji_angry,
                countText = "33"
            )
            EmojiCountScreen(
                modifier = Modifier
                    .padding(end = 10.dp),
                emojiResource = R.drawable.emoji_happiness,
                countText = "5671234123"
            )
            EmojiCountScreen(
                modifier = Modifier
                    .padding(end = 10.dp),
                emojiResource = R.drawable.emoji_like,
                countText = "123"
            )
            EmojiCountScreen(
                modifier = Modifier
                    .padding(end = 10.dp),
                emojiResource = R.drawable.emoji_cry,
                countText = "777"
            )
            EmojiCountScreen(
                modifier = Modifier
                    .padding(end = 10.dp),
                emojiResource = R.drawable.emoji_haha,
                countText = "799"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MessageContentScreenPreview() {
    MessageContentScreen()
}