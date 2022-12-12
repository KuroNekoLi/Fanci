package com.cmoney.fanci.ui.screens.follow

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanci.ui.common.CategoryText
import com.cmoney.fanci.ui.screens.shared.ChannelBarScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.Channel


@Composable
fun CategoryScreen(
    category: Category,
    onChannelClick: (channel: Channel) -> Unit
) {
    Column {
        CategoryText(text = category.name.orEmpty())
        Spacer(modifier = Modifier.height(10.dp))
        category.channels?.forEach { channel ->
            ChannelBarScreen(channel = channel,
                onClick = {
                    onChannelClick.invoke(it)
                })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryScreenPreview() {
    FanciTheme {
        CategoryScreen(
            Category(
                id = "",
                groupId = "",
                name = "分類1",
                channels = listOf(
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
                    ),
                    Channel(
                        id = "",
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