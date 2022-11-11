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
fun MessageIGScreen(
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
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.resource_default)
            )
            Text(
                modifier = Modifier.padding(top = 5.6.dp),
                text = "Instagram",
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
                color = White_DDDEDF,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

    }

}

@Preview(showBackground = true)
@Composable
fun MessageIGScreenPreview() {
    MessageIGScreen(
        thumbnail = "https://okapi.books.com.tw/uploads/image/2018/03/source/22908-1520239183.jpg",
        channel = "阿滴英文",
        title = "有妹妹然後教英文的那個\uD83D\uDE4B\uD83C\uDFFB\u200D♂️@raydudaily 比較多日常廢文"
    )
}