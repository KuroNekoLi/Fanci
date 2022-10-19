package com.cmoney.fanci.ui.screens.follow

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanci.R
import com.cmoney.fanci.ui.common.CategoryText
import com.cmoney.fanci.ui.screens.shared.ChannelBar
import com.cmoney.fanci.ui.screens.shared.ChannelBarScreen

data class FollowCategory(
    val categoryTitle: String,
    val channelList: List<ChannelBar>
)

@Composable
fun CategoryScreen(
    followCategory: FollowCategory,
    onChannelClick: (channelBar: ChannelBar) -> Unit
) {
    Column {
        CategoryText(text = followCategory.categoryTitle)
        Spacer(modifier = Modifier.height(10.dp))
        followCategory.channelList.forEach { channelBar ->
            ChannelBarScreen(channelBar = channelBar) {
                onChannelClick.invoke(it)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryScreenPreview() {
    CategoryScreen(
        FollowCategory(
            categoryTitle = "分類1",
            listOf(
                ChannelBar(
                    icon = R.drawable.message,
                    channelTitle = "\uD83D\uDC4F｜歡迎新朋友"
                ),
                ChannelBar(
                    icon = R.drawable.message,
                    channelTitle = "\uD83D\uDC4F｜歡迎新朋友"
                ),
                ChannelBar(
                    icon = R.drawable.message,
                    channelTitle = "\uD83D\uDC4F｜歡迎新朋友"
                )
            )
        )
    ){

    }
}