package com.cmoney.kolfanci.repository

import com.cmoney.kolfanci.extension.checkResponseBody
import com.cmoney.kolfanci.model.notification.NotificationHistory
import com.cmoney.kolfanci.repository.request.NotificationClick
import com.cmoney.kolfanci.repository.request.NotificationSeen
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NetworkImpl(
    private val notificationService: NotificationService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Network {

    override suspend fun getNotificationHistory(): Result<NotificationHistory> =
        withContext(dispatcher) {
            kotlin.runCatching {
                notificationService.getNotificationHistory().checkResponseBody()
            }
        }

    override suspend fun getNextPageNotificationHistory(nextPageUrl: String): Result<NotificationHistory> =
        withContext(dispatcher) {
            kotlin.runCatching {
                notificationService.getNextPageNotificationHistory(nextPageUrl).checkResponseBody()
            }
        }

    override suspend fun setNotificationHistoryClick(notificationClick: NotificationClick): Result<Unit> =
        withContext(dispatcher) {
            kotlin.runCatching {
                notificationService.setNotificationHistoryClick(notificationClick = notificationClick)
                    .checkResponseBody()
            }
        }

    override suspend fun getNotificationUnreadCount(): Result<Long> =
        withContext(dispatcher) {
            kotlin.runCatching {
                notificationService.getNotificationUnreadCount().checkResponseBody()
            }
        }

    override suspend fun setNotificationSeen(): Result<Unit> =
        withContext(dispatcher) {
            kotlin.runCatching {
                notificationService.setNotificationSeen(NotificationSeen()).checkResponseBody()
            }
        }
}