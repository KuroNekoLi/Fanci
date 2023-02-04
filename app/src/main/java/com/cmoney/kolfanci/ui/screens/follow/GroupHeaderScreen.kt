package com.cmoney.kolfanci.ui.screens.follow

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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cmoney.kolfanci.ui.common.CircleDot
import com.cmoney.kolfanci.ui.common.GroupText
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
@Composable
fun GroupHeaderScreen(
    followGroup: Group,
    modifier: Modifier = Modifier,
    visibleAvatar: Boolean,
    onMoreClick: (Group) -> Unit
) {
    val tag = "GroupHeaderScreen"
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Image(
                painter = painterResource(id = R.drawable.fanci),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    color = LocalColor.current.primary
                )
            )

            GroupText(text = followGroup.name.orEmpty(), Modifier.padding(top = 21.dp))
        }

        Row(
            modifier = Modifier.weight(0.5f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(35.dp)
                    .clip(CircleShape)
                    .background(LocalColor.current.background)
                    .clickable {
                        onMoreClick.invoke(followGroup)
                    },
                contentAlignment = Alignment.Center
            ) {
                CircleDot()
            }
            AnimatedVisibility(
                visible = visibleAvatar
            ) {
                AsyncImage(
                    model = followGroup.thumbnailImageUrl,
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
    FanciTheme {
        GroupHeaderScreen(
            Group(
                name = "社團名稱社團名稱社團名稱社團名稱社團名稱社團名稱",
                thumbnailImageUrl = "https://i.pinimg.com/474x/60/5d/31/605d318d7f932e3ebd1d672e5bff9229.jpg"
            ),
            modifier = Modifier.padding(20.dp),
            true
        ){}
    }
}