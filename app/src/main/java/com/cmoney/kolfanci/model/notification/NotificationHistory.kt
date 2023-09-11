package com.cmoney.kolfanci.model.notification


import com.cmoney.kolfanci.ui.screens.notification.NotificationCenterData
import com.cmoney.kolfanci.utils.Utils
import com.google.gson.annotations.SerializedName

data class NotificationHistory(
    @SerializedName("items")
    val items: List<Item>? = listOf(),
    @SerializedName("paging")
    val paging: Paging? = Paging()
) {
    data class Item(
        @SerializedName("body")
        val body: String? = null,
        @SerializedName("createTime")
        val createTime: Int = 0,
        @SerializedName("hasClicked")
        val hasClicked: Boolean = false,
        @SerializedName("iconUrl")
        val iconUrl: String? = null,
        @SerializedName("imageUrl")
        val imageUrl: String? = null,
        @SerializedName("link")
        val link: String? = null,
        @SerializedName("notificationId")
        val notificationId: String? = null
    )

    data class Paging(
        @SerializedName("next")
        val next: String? = null
    )
}

fun NotificationHistory.Item.toNotificationCenterData(): NotificationCenterData {
    val splitStr = body?.split("<br>").orEmpty()
    val title = splitStr.firstOrNull().orEmpty()
    val description = if (splitStr.size > 1) {
        splitStr[1]
    } else {
        ""
    }

    return NotificationCenterData(
        notificationId = notificationId.orEmpty(),
        image = imageUrl.orEmpty(),
        title = title,
        description = description,
        deepLink = link.orEmpty(),
        isRead = hasClicked,
        displayTime = Utils.timesMillisToDate(
            createTime.toLong().times(1000)
        )
    )
}