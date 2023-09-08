package com.cmoney.kolfanci.repository

import com.cmoney.kolfanci.model.notification.NotificationHistory
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface NotificationService {

    @GET("notification/History")
    suspend fun getNotificationHistory(): Response<NotificationHistory>

    @GET
    suspend fun getNextPageNotificationHistory(
        @Url url: String
    ): Response<NotificationHistory>

}