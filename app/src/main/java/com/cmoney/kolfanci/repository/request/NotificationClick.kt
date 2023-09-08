package com.cmoney.kolfanci.repository.request

data class NotificationClick(
    val appId: Int = 176,
    val notificationIds: List<String>
)