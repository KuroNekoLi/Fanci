package com.cmoney.fanci.ui.screens.my

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanci.ui.theme.White_767A7F

@Composable
fun MyInfoScreen(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            model = "https://picsum.photos/300/300",
            placeholder = painterResource(id = R.drawable.resource_default),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(text = "Emily Chen", fontSize = 17.sp, color = LocalColor.current.text.default_100)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "此頭像與暱稱為基本設定，你在不同社團的頭像與暱稱，皆可以自由更換。", fontSize = 12.sp, color = LocalColor.current.text.default_100)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyInfoScreenPreview() {
    FanciTheme {
        MyInfoScreen()
    }
}