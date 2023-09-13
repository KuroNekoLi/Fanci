package com.cmoney.kolfanci.repository

import com.cmoney.kolfanci.model.notification.NotificationHistory
import com.cmoney.kolfanci.repository.request.NotificationClick
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Url

interface NotificationService {

    @GET("notification/History")
    suspend fun getNotificationHistory(): Response<NotificationHistory>

    @GET
    suspend fun getNextPageNotificationHistory(
        @Url url: String
    ): Response<NotificationHistory>

    @PUT("notification/History/clicked")
    suspend fun setNotificationHistoryClick(
        @Body notificationClick: NotificationClick
    ): Response<Unit>

}