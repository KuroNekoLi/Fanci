package com.cmoney.kolfanci.repository

import com.cmoney.kolfanci.model.notification.NotificationHistory

interface Network {

    /**
     * 取得 推播歷史訊息
     */
    suspend fun getNotificationHistory(): Result<NotificationHistory>

    /**
     * 取得 下一頁 推播歷史訊息
     */
    suspend fun getNextPageNotificationHistory(nextPageUrl: String): Result<NotificationHistory>

}