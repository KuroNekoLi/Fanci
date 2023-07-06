package com.cmoney.kolfanci.ui.screens.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.kolfanci.R
/**
 * 聊天室 公告 訊息
 */
@Composable
fun ChatRoomAnnounceScreen(chatMessageModel: ChatMessage, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(LocalColor.current.background),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(modifier = Modifier.padding(start = 17.dp, end = 17.dp)) {
            Image(
                painter = painterResource(id = R.drawable.pin),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = LocalColor.current.primary)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text(
                text = chatMessageModel.content?.text.orEmpty(),
                fontSize = 16.sp,
                color = LocalColor.current.text.default_100,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatRoomAnnounceScreenPreview() {
    FanciTheme {
        ChatRoomAnnounceScreen(ChatRoomUseCase.mockMessage)
    }
}