package com.cmoney.kolfanci.model.notification


import com.cmoney.kolfanci.ui.screens.notification.NotificationCenterData
import com.cmoney.kolfanci.utils.Utils
import com.google.gson.annotations.SerializedName

data class NotificationHistory(
    @SerializedName("items")
    val items: List<Item> = listOf(),
    @SerializedName("paging")
    val paging: Paging = Paging()
) {
    data class Item(
        @SerializedName("body")
        val body: String = "",
        @SerializedName("createTime")
        val createTime: Int = 0,
        @SerializedName("hasClicked")
        val hasClicked: Boolean = false,
        @SerializedName("iconUrl")
        val iconUrl: String = "",
        @SerializedName("imageUrl")
        val imageUrl: String = "",
        @SerializedName("link")
        val link: String = "",
        @SerializedName("notificationId")
        val notificationId: String = ""
    )

    data class Paging(
        @SerializedName("next")
        val next: String = ""
    )
}

fun NotificationHistory.Item.toNotificationCenterData(): NotificationCenterData {
    val splitStr = body.split("<br>")
    val title = splitStr.firstOrNull().orEmpty()
    val description = if (splitStr.size > 1) {
        splitStr[1]
    } else {
        ""
    }

    return NotificationCenterData(
        notificationId = notificationId,
        image = imageUrl,
        title = title,
        description = description,
        deepLink = link,
        isRead = hasClicked,
        displayTime = Utils.timesMillisToDate(
            createTime.toLong().times(1000)
        )
    )
}