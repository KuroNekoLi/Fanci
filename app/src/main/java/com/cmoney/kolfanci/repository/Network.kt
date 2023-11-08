package com.cmoney.kolfanci.repository

import android.net.Uri
import com.cmoney.kolfanci.model.notification.NotificationHistory
import com.cmoney.kolfanci.repository.request.NotificationClick
import com.cmoney.kolfanci.repository.response.FileUploadResponse
import com.cmoney.kolfanci.repository.response.FileUploadStatusResponse

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

    /**
     * 上傳檔案 step 1.
     */
    suspend fun uploadFile(uri: Uri): Result<FileUploadResponse>

    /**
     * 上傳檔案 狀態檢查, 確認有上傳 完成
     */
    suspend fun checkUploadFileStatus(externalId: String, fileType: String): Result<FileUploadStatusResponse>

    /**
     * 取得 網址內容
     */
    suspend fun getContent(url: String): Result<String>
}