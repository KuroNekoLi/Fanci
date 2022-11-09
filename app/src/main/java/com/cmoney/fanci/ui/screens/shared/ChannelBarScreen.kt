package com.cmoney.fanci.ui.screens.shared

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanci.R
import com.cmoney.fanci.model.GroupModel
import com.cmoney.fanci.ui.common.ChannelText
import com.cmoney.fanci.ui.theme.White_494D54

@Composable
fun ChannelBarScreen(channel: GroupModel.Channel, onClick: (GroupModel.Channel) -> Unit) {
    Button(
        onClick = {
            onClick.invoke(channel)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 0.dp, vertical = 7.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            Row {
                Icon(
                    painterResource(id = R.drawable.message),
                    contentDescription = null,
                    tint = White_494D54
                )
                Spacer(modifier = Modifier.width(14.dp))
                ChannelText(channel.name)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChannelBarScreenPreview() {
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