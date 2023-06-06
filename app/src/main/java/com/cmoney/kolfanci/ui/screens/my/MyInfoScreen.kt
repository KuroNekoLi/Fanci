package com.cmoney.kolfanci.ui.screens.my

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 個人資訊 - 大頭貼/名稱
 */
@Composable
fun MyInfoScreen(
    modifier: Modifier = Modifier,
    avatarUrl: String = "https://picsum.photos/300/300",
    name: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            model = avatarUrl,
            placeholder = painterResource(id = R.drawable.placeholder),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = name, fontSize = 17.sp, color = LocalColor.current.text.default_100)
    }
}

@Preview(showBackground = true)
@Composable
fun MyInfoScreenPreview() {
    FanciTheme {
        MyInfoScreen(
            name = "Test Name"
        )
    }
}