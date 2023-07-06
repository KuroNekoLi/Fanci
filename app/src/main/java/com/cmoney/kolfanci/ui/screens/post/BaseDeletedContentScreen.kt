package com.cmoney.kolfanci.ui.screens.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun BaseDeletedContentScreen(
    modifier: Modifier = Modifier,
    title: String = "這則留言已被本人刪除",
    content: String = "已經刪除的留言，你是看不到的！"
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                painter = painterResource(id = R.drawable.profile),
                contentDescription = null
            )

            Text(
                text = title,
                modifier = Modifier.padding(start = 10.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = LocalColor.current.text.default_100
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            modifier = Modifier.padding(start = 40.dp),
            text = content,
            fontSize = 16.sp,
            color = LocalColor.current.text.default_100
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BaseDeletedContentScreenPreview() {
    FanciTheme {
        BaseDeletedContentScreen()
    }
}