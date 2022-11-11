package com.cmoney.fanci.ui.screens.follow

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanci.model.GroupModel
import com.cmoney.fanci.ui.common.CategoryText
import com.cmoney.fanci.ui.screens.shared.ChannelBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme


@Composable
fun CategoryScreen(
    category: GroupModel.Category,
    onChannelClick: (channel: GroupModel.Channel) -> Unit
) {
    Column {
        CategoryText(text = category.name)
        Spacer(modifier = Modifier.height(10.dp))
        category.channels.forEach { channel ->
            ChannelBarScreen(channel = channel) {
                onChannelClick.invoke(it)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryScreenPreview() {
    FanciTheme {
        CategoryScreen(
            GroupModel.Category(
                categoryId = "",
                groupId = "",
                name = "分類1",
                channels = listOf(
                    GroupModel.Channel(
                        channelId = "",
                        creatorId = "",
                        groupId = "",
                        name = "\uD83D\uDC4F｜歡迎新朋友"
                    ),
                    GroupModel.Channel(
                        channelId = "",
                        creatorId = "",
                        groupId = "",
                        name = "\uD83D\uDC4F｜歡迎新朋友"
                    ),
                    GroupModel.Channel(
                        channelId = "",
                        creatorId = "",
                        groupId = "",
                        name = "\uD83D\uDC4F｜歡迎新朋友"
                    )
                )
            )
        ) {

        }
    }
}