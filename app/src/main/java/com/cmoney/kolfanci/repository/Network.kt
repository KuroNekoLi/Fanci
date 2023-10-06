package com.cmoney.kolfanci.repository

import com.cmoney.kolfanci.model.notification.NotificationHistory
import com.cmoney.kolfanci.repository.request.NotificationClick

interface Network {

    /**
     * 取得 推播歷史訊息
     */
    suspend fun getNotificationHistory(): Result<NotificationHistory>

    /**
     * 取得 下一頁 推播歷史訊息
     */
    suspend fun getNextPageNotificationHistory(nextPageUrl: String): Result<NotificationHistory>

    /**
     * 點擊 通知中心 item
     */
    suspend fun setNotificationHistoryClick(notificationClick: NotificationClick): Result<Unit>

    /**
     * 取得 通知中心 未讀數量
     */
    suspend fun getNotificationUnreadCount(): Result<Long>

    /**
     * 通知中心 已讀
     */
    suspend fun setNotificationSeen(): Result<Unit>

}