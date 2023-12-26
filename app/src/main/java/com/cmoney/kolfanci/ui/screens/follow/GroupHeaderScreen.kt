package com.cmoney.kolfanci.ui.screens.follow

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.common.CircleDot
import com.cmoney.kolfanci.ui.common.GroupText
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

@Composable
fun GroupHeaderScreen(
    followGroup: Group,
    modifier: Modifier = Modifier,
    visibleAvatar: Boolean,
    isShowBubbleTip: Boolean,
    onMoreClick: (Group) -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = followGroup.logoImageUrl,
                contentScale = ContentScale.Inside,
                alignment = Alignment.CenterStart,
                modifier = Modifier
                    .size(120.dp, 40.dp),
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.placeholder)
            )

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isShowBubbleTip) {
                    BubbleTip()
                }

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
                        placeholder = painterResource(id = R.drawable.placeholder)
                    )
                }
            }
        }

        GroupText(text = followGroup.name.orEmpty(), Modifier.padding(top = 21.dp))
    }
}

@Composable
private fun BubbleTip(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val offsetX = infiniteTransition.animateFloat(
        -10f,
        0.dp.value,
        infiniteRepeatable(
            tween(), RepeatMode.Reverse,
            initialStartOffset = StartOffset(offsetMillis = 500, StartOffsetType.FastForward)
        )
    )

    Row(
        modifier = modifier.offset(x = offsetX.value.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Card(
            border = BorderStroke(1.dp, LocalColor.current.primary),
            colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.color_2D2D2D)),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                modifier = Modifier.padding(
                    top = 5.dp,
                    bottom = 5.dp,
                    start = 10.dp,
                    end = 10.dp
                ),
                text = "✨繼續打造社團", style = TextStyle(
                    fontSize = 14.sp,
                    color = LocalColor.current.text.default_100,
                    textAlign = TextAlign.Center
                )
            )
        }

        val triangleFillColor = colorResource(id = R.color.color_2D2D2D)
        val triangleBorderColor = LocalColor.current.primary

        // triangle
        Canvas(
            modifier = Modifier
                .padding(start = 5.dp, end = 10.dp)
                .height(15.dp)
                .width((7.5 * 1.7).dp)
        ) {
            val path = Path()
            path.moveTo(0f, 0f)
            path.lineTo(size.width, size.height / 2f)
            path.lineTo(0f, size.height)
            path.close()

            drawPath(path = path, color = triangleFillColor, style = Fill)
            drawPath(path = path, color = triangleBorderColor, style = Stroke(width = 3f))
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
            true,
            isShowBubbleTip = true,
        ) {}
    }
}