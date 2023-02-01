package com.cmoney.fanci.ui.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.R
import com.cmoney.fanci.model.Constant
import com.cmoney.fanci.ui.common.ChannelText
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Channel

@Composable
fun ChannelBarScreen(
    channel: Channel,
    horizontalPadding: Dp = 14.dp,
    isEditMode: Boolean = false,
    onClick: (Channel) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = horizontalPadding, vertical = 7.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(LocalColor.current.background)
            .clickable {
                if (Constant.MyGroupPermission.createOrEditChannel == true || Constant.MyGroupPermission.deleteChannel == true) {
                    onClick.invoke(channel)
                }
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
            ChannelText(
                modifier = Modifier.weight(1f),
                channel.name.orEmpty()
            )
            if (isEditMode && (Constant.MyGroupPermission.createOrEditChannel == true ||
                        Constant.MyGroupPermission.deleteChannel == true
                        )
            ) {
                Text(
                    text = "編輯", fontSize = 14.sp, color = LocalColor.current.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChannelBarScreenPreview() {
    FanciTheme {
        ChannelBarScreen(
            Channel(
                id = "",
                creatorId = "",
                groupId = "",
                name = "\uD83D\uDC4F｜歡迎新朋友"
            ),
            onClick = {}
        )
    }
}