package com.cmoney.fanci

import androidx.annotation.DrawableRes

sealed class MainTab(
    val title: String,
    @DrawableRes val icon: Int,
    val route: String
) {
    object FOLLOW : MainTab(
        title = "追蹤",
        icon = R.drawable.follow,
        "follow"
    )

    object NOTIFY : MainTab(
        title = "通知",
        icon = R.drawable.notify,
        "notify"
    )

    object EXPLORE : MainTab(
        title = "探索",
        icon = R.drawable.explore,
        "explore"
    )

    object MY : MainTab(
        title = "我的",
        icon = R.drawable.my,
        "my"
    )
}

val mainTabItems = listOf(
    MainTab.FOLLOW,
    MainTab.NOTIFY,
    MainTab.EXPLORE,
    MainTab.MY
)