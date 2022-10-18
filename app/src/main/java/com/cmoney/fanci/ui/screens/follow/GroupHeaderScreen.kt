package com.cmoney.fanci.ui.screens.follow

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.common.GroupText
import com.cmoney.fanci.ui.theme.Black_14171C
import com.cmoney.fanci.ui.theme.Blue_4F70E5
import com.socks.library.KLog

data class FollowGroup(val groupName: String, val groupAvatar: String)

@Composable
fun GroupHeaderScreen(followGroup: FollowGroup, modifier: Modifier, visibleAvatar: Boolean) {
    val tag = "GroupHeaderScreen"

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Image(
                painter = painterResource(id = R.drawable.fanci),
                contentDescription = null,
            )

            GroupText(text = followGroup.groupName, Modifier.padding(top = 21.dp))
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(35.dp)
                    .clip(CircleShape)
                    .background(Black_14171C)
                    .clickable {
                        // TODO:
                        KLog.i(tag, "more click.")
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .size(3.8.dp)
                            .clip(CircleShape)
                            .background(Blue_4F70E5)
                    )
                    Box(
                        modifier = Modifier
                            .size(3.8.dp)
                            .clip(CircleShape)
                            .background(Blue_4F70E5)
                    )
                    Box(
                        modifier = Modifier
                            .size(3.8.dp)
                            .clip(CircleShape)
                            .background(Blue_4F70E5)
                    )
                }
            }
            AnimatedVisibility(
                visible = visibleAvatar
            ){
                AsyncImage(
                    model = "https://picsum.photos/400/400",
                    modifier = Modifier
                        .size(55.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    placeholder = painterResource(id = R.drawable.resource_default)
                )
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun GroupHeaderScreenPreview() {
    GroupHeaderScreen(
        FollowGroup(
            groupName = "社團名稱",
            groupAvatar = "https://i.pinimg.com/474x/60/5d/31/605d318d7f932e3ebd1d672e5bff9229.jpg"
        ),
        modifier = Modifier.padding(20.dp),
        true
    )
}