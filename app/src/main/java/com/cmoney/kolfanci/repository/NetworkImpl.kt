package com.cmoney.kolfanci.repository

import com.cmoney.kolfanci.extension.checkResponseBody
import com.cmoney.kolfanci.model.notification.NotificationHistory
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
}