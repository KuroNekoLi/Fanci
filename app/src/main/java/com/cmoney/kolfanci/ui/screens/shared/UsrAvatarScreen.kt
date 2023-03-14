package com.cmoney.kolfanci.ui.screens.shared

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.theme.Color_B3FB9304
import com.cmoney.fanciapi.fanci.model.GroupMember

@Composable
fun ChatUsrAvatarScreen(
    user: GroupMember,
    nickNameColor: Color = Color_B3FB9304,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = user.thumbNail,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.placeholder)
        )

        Text(
            text = user.name.orEmpty(),
            modifier = Modifier.padding(start = 10.dp),
            fontSize = 14.sp,
            color = nickNameColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatUsrAvatarScreenPreview() {
    ChatUsrAvatarScreen(user = GroupMember(
        thumbNail = "https://pickaface.net/gallery/avatar/unr_sample_161118_2054_ynlrg.png",
        name = "Hello"
    ))
}