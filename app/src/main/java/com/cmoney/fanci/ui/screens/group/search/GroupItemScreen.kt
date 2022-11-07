package com.cmoney.fanci.ui.screens.group.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.theme.Black_2B313C
import com.cmoney.fanci.ui.theme.White_767A7F
import com.cmoney.fanci.ui.theme.White_BBBCBF
import com.socks.library.KLog

@Composable
fun GroupItemScreen(modifier: Modifier = Modifier, onGroupItemClick: () -> Unit) {
    val TAG = "GroupItemScreen"
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(Black_2B313C)
            .clickable {
                KLog.i(TAG, "click.")
                onGroupItemClick.invoke()
            }
            .padding(15.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier
            .weight(1f)
            .padding(end = 20.dp)) {
            Text(text = "韓勾ㄟ\uD83C\uDDF0\uD83C\uDDF7金針菇討論區", fontSize = 16.sp, color = Color.White)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "私密社團・成員 1345 ", fontSize = 12.sp, color = White_767A7F)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "我愛金針菇\uD83D\uDC97這裡是一群超愛",
                fontSize = 14.sp,
                color = White_BBBCBF,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        AsyncImage(
            model = "https://picsum.photos/${(100..400).random()}/${(100..400).random()}",
            modifier = Modifier
                .size(55.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(18.dp)),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.resource_default)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GroupItemScreenPreview() {
    GroupItemScreen(){}
}