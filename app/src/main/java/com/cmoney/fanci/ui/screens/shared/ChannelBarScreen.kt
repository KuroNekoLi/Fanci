package com.cmoney.fanci.ui.screens.shared

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanci.R
import com.cmoney.fanci.model.GroupModel
import com.cmoney.fanci.ui.common.ChannelText
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanci.ui.theme.White_494D54

@Composable
fun ChannelBarScreen(channel: GroupModel.Channel, onClick: (GroupModel.Channel) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(LocalColor.current.background)
            .clickable {
                onClick.invoke(channel)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp)
        ) {
            Icon(
                painterResource(id = R.drawable.message),
                contentDescription = null,
                tint = LocalColor.current.component.other
            )
            Spacer(modifier = Modifier.width(14.dp))
            ChannelText(channel.name)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChannelBarScreenPreview() {
    FanciTheme {
        ChannelBarScreen(
            GroupModel.Channel(
                channelId = "",
                creatorId = "",
                groupId = "",
                name = "\uD83D\uDC4F｜歡迎新朋友"
            )
        ) {

        }
    }
}