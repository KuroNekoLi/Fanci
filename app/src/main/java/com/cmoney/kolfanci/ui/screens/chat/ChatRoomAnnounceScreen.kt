package com.cmoney.kolfanci.ui.screens.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.getPinMessage
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 聊天室 公告 訊息
 */
@Composable
fun ChatRoomAnnounceScreen(
    chatMessageModel: ChatMessage,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(LocalColor.current.background)
            .clickable {
                onClick.invoke()
            },
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
                text = chatMessageModel.getPinMessage(),
                fontSize = 16.sp,
                color = LocalColor.current.text.default_100,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun ChatRoomAnnounceScreenPreview() {
    FanciTheme {
        ChatRoomAnnounceScreen(
            MockData.mockMessage,
            onClick = {}
        )
    }
}