package com.cmoney.fanci.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.theme.Black_181C23
import com.cmoney.fanci.ui.theme.Blue_4F70E5
import com.cmoney.fanci.ui.theme.White_DDDEDF

@Deprecated("not used")
@Composable
fun MessageYTScreen(
    thumbnail: String,
    channel: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(9.dp))
            .background(Black_181C23)
    ) {
        Column(modifier = Modifier
            .width(230.dp)
            .padding(10.dp)) {
            AsyncImage(
                model = thumbnail,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.resource_default)
            )
            Text(
                modifier = Modifier.padding(top = 5.6.dp),
                text = "Youtube",
                fontSize = 12.sp,
                color = White_DDDEDF
            )

            Text(
                modifier = Modifier.padding(top = 5.dp),
                text = channel,
                fontSize = 12.sp,
                color = White_DDDEDF
            )

            Text(
                modifier = Modifier.padding(top = 5.dp),
                text = title,
                fontSize = 14.sp,
                color = Blue_4F70E5,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

    }

}

@Preview(showBackground = true)
@Composable
fun MessageYTScreenPreview() {
    MessageYTScreen(
        thumbnail = "https://img.youtube.com/vi/1p_GLULMNbw/0.jpg",
        channel = "蔡阿嘎Life",
        title = "【蔡阿嘎地獄廚房#16】廚佛Fred大戰阿煨師，幹話最多的兩個男人正面對決！"
    )
}