package com.cmoney.fanci.ui.screens.shared.channel

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.ui.common.CategoryText
import com.cmoney.fanci.ui.screens.shared.ChannelBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.Channel

@Composable
fun ChannelEditScreen(
    modifier: Modifier = Modifier,
    category: Category,
    channelList: List<Channel>,
    onCategoryEdit: (Category) -> Unit,
    onChanelEdit: (Channel) -> Unit,
    onAddChannel: (Category) -> Unit
) {
    Column(
        modifier = modifier
            .background(LocalColor.current.background)
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        //分類
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryText(
                modifier = Modifier.weight(1f),
                text = category.name.orEmpty()
            )

            Box(
                modifier =
                Modifier
                    .size(55.dp)
                    .padding(end = 3.dp)
                    .clickable {
                        onCategoryEdit.invoke(category)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "編輯", fontSize = 14.sp, color = LocalColor.current.primary
                )
            }
        }

        //Channel List
        repeat(channelList.size) { pos ->
            ChannelBarScreen(
                channel = channelList[pos],
                horizontalPadding = 0.dp,
                isEditMode = true,
                onClick = {
                    onChanelEdit.invoke(it)
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        //Add chanel block
        val stroke = Stroke(
            width = 2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)
        )
        val borderColor = LocalColor.current.text.default_30

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable {
                    onAddChannel.invoke(category)
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                Modifier.fillMaxSize()
            ) {
                drawRoundRect(
                    color = borderColor, style = stroke,
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
            }

            Image(
                painter = painterResource(id = com.cmoney.fanci.R.drawable.plus_white),
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChannelEditScreenPreview() {
    FanciTheme {
        ChannelEditScreen(
            category = Category(name = "Welcome"),
            channelList = listOf(
                Channel(
                    id = "",
                    creatorId = "",
                    groupId = "",
                    name = "\uD83D\uDC4F｜歡迎新朋友"
                ),
                Channel(
                    id = "",
                    creatorId = "",
                    groupId = "",
                    name = "\uD83D\uDC4F｜歡迎新朋友"
                )
            ),
            onCategoryEdit = {},
            onChanelEdit = {},
            onAddChannel = {}
        )
    }
}