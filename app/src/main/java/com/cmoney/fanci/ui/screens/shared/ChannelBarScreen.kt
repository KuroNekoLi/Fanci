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
import com.cmoney.fanci.ui.common.ChannelText
import com.cmoney.fanci.ui.theme.White_494D54

data class ChannelBar(
    @DrawableRes val icon: Int,
    val channelTitle: String
)

@Composable
fun ChannelBarScreen(channelBar: ChannelBar) {
    Button(
        onClick = { /*TODO*/ },
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
        ){
            Row{
                Icon(painterResource(id = channelBar.icon), contentDescription = null, tint = White_494D54)
                Spacer(modifier = Modifier.width(14.dp))
                ChannelText(channelBar.channelTitle)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChannelBarScreenPreview() {
    ChannelBarScreen(
        ChannelBar(
            icon = R.drawable.message,
            channelTitle = "\uD83D\uDC4F｜歡迎新朋友"
        )
    )
}