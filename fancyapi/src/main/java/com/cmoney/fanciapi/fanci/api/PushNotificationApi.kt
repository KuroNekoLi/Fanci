package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.PushNotificationSetting
import com.cmoney.fanciapi.fanci.model.PushNotificationSettingType

interface PushNotificationApi {
    /**
     * 取得推播開關設定種類
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @return [kotlin.collections.List<PushNotificationSetting>]
     */
    @GET("api/v1/PushNotification/SettingType/All")
    suspend fun apiV1PushNotificationSettingTypeAllGet(): Response<kotlin.collections.List<PushNotificationSetting>>

    /**
     * 取得使用者社團推播設定
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 社團Id
     * @return [PushNotificationSetting]
     */
    @GET("api/v1/PushNotification/User/{groupId}/SettingType")
    suspend fun apiV1PushNotificationUserGroupIdSettingTypeGet(@Path("groupId") groupId: kotlin.String): Response<PushNotificationSetting>

    /**
     * 設定使用者社團推播設定
     * 
     * Responses:
     *  - 204: No Content
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 社團Id
     * @param settingType 設定種類
     * @return [Unit]
     */
    @PUT("api/v1/PushNotification/User/{groupId}/SettingType/{settingType}")
    suspend fun apiV1PushNotificationUserGroupIdSettingTypeSettingTypePut(@Path("groupId") groupId: kotlin.String, @Path("settingType") settingType: PushNotificationSettingType): Response<Unit>

}
