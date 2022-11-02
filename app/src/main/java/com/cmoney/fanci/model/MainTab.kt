package com.cmoney.fanci.model

import androidx.annotation.DrawableRes
import com.cmoney.fanci.R

sealed class MainTab(
    val title: String,
    @DrawableRes val icon: Int,
    val route: String
) {
    object FOLLOW : MainTab(
        title = "社團",
        icon = R.drawable.follow,
        "follow"
    )

    object NOTIFY : MainTab(
        title = "聊天",
        icon = R.drawable.notify,
        "notify"
    )

    object ACTIVITY : MainTab(
        title = "活動",
        icon = R.drawable.explore,
        "explore"
    )

    object MARKET : MainTab(
        title = "商城",
        icon = R.drawable.market,
        "market"
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
    MainTab.ACTIVITY,
    MainTab.MARKET,
    MainTab.MY
)