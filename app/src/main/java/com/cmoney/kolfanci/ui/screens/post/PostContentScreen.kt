package com.cmoney.kolfanci.ui.screens.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.ui.common.AutoLinkPostText
import com.cmoney.kolfanci.ui.common.CircleDot
import com.cmoney.kolfanci.ui.screens.chat.MessageImageScreen
import com.cmoney.kolfanci.ui.screens.shared.ChatUsrAvatarScreen
import com.cmoney.kolfanci.ui.screens.shared.EmojiCountScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.kolfanci.utils.Utils
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun PostContentScreen(
    modifier: Modifier = Modifier,
    post: ChatMessage,
    defaultDisplayLine: Int = 4,
    contentModifier: Modifier = Modifier,
    bottomContent: @Composable ColumnScope.() -> Unit
) {
    //最多顯示幾行
    var maxDisplayLine by remember {
        mutableStateOf(defaultDisplayLine)
    }

    //是否要顯示 顯示更多文案
    var showMoreLine by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .background(LocalColor.current.background)
            .padding(20.dp)
    ) {
        //Avatar
        Row(verticalAlignment = Alignment.CenterVertically) {
            //大頭貼
            post.author?.let {
                ChatUsrAvatarScreen(it)
            }

            Spacer(modifier = Modifier.weight(1f))

            //More
            Box(
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(25.dp)
                    .clickable {

                    },
                contentAlignment = Alignment.Center
            ) {
                CircleDot()
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Column(
            modifier = contentModifier
        ) {
            //Message text
            post.content?.text?.apply {
                if (this.isNotEmpty()) {
                    AutoLinkPostText(
                        text = this,
                        fontSize = 17.sp,
                        color = LocalColor.current.text.default_100,
                        maxLine = maxDisplayLine,
                        onLineCount = {
                            showMoreLine = it > maxDisplayLine
                        },
                        onClick = {
                            maxDisplayLine = if (maxDisplayLine == Int.MAX_VALUE) {
                                defaultDisplayLine
                            } else {
                                Int.MAX_VALUE
                            }
                        }
                    )
                }
            }


            //顯示更多
            if (showMoreLine) {
                Text(
                    modifier = Modifier.clickable {
                        maxDisplayLine = if (maxDisplayLine == Int.MAX_VALUE) {
                            defaultDisplayLine
                        } else {
                            Int.MAX_VALUE
                        }
                    },
                    text = "⋯⋯ 顯示更多",
                    fontSize = 17.sp,
                    color = LocalColor.current.text.default_100
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            //Image attach
            if (post.content?.medias?.isNotEmpty() == true) {
                MessageImageScreen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    images = post.content?.medias?.map {
                        it.resourceLink.orEmpty()
                    }.orEmpty()
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            //Emoji
            FlowRow(
                crossAxisSpacing = 10.dp
            ) {
                post.emojiCount?.apply {
                    Utils.emojiMapping(this).forEach { emoji ->
                        if (emoji.second != 0) {
                            EmojiCountScreen(
                                modifier = Modifier
                                    .padding(end = 10.dp),
                                emojiResource = emoji.first,
                                countText = emoji.second.toString()
                            ) {
                            }
                        }
                    }
                }

                //Add Emoji
                EmojiCountScreen(
                    modifier = Modifier.height(30.dp),
                    emojiResource = R.drawable.empty_emoji,
                    emojiIconSize = 23.dp,
                    countText = ""
                ) {

                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            bottomContent()
        }

        Spacer(modifier = Modifier.height(15.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun PostContentScreenPreview() {
    FanciTheme {
        PostContentScreen(
            post = ChatRoomUseCase.mockMessage,
            bottomContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "1天以前", fontSize = 14.sp, color = LocalColor.current.text.default_100)

                    Box(
                        modifier = Modifier
                            .padding(start = 5.dp, end = 5.dp)
                            .size(3.8.dp)
                            .clip(CircleShape)
                            .background(LocalColor.current.text.default_100)
                    )

                    Text(text = "留言 142", fontSize = 14.sp, color = LocalColor.current.text.default_100)
                }
            }
        )
    }
}