package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.PushNotificationSetting
import com.cmoney.fanciapi.fanci.model.PushNotificationSettingType

interface NotifyApi {
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
    @Deprecated("This api was deprecated")
    @GET("api/v1/Notify/SettingType")
    suspend fun apiV1NotifySettingTypeGet(): Response<kotlin.collections.List<PushNotificationSetting>>

    /**
     * 取得使用者社團推播設定
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 
     * @return [PushNotificationSetting]
     */
    @Deprecated("This api was deprecated")
    @GET("api/v1/Notify/UserSettingType/{groupId}")
    suspend fun apiV1NotifyUserSettingTypeGroupIdGet(@Path("groupId") groupId: kotlin.String): Response<PushNotificationSetting>

    /**
     * 設定使用者社團推播設定
     * 
     * Responses:
     *  - 204: No Content
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 
     * @param settingType 
     * @return [Unit]
     */
    @Deprecated("This api was deprecated")
    @PUT("api/v1/Notify/UserSettingType/{groupId}/{settingType}")
    suspend fun apiV1NotifyUserSettingTypeGroupIdSettingTypePut(@Path("groupId") groupId: kotlin.String, @Path("settingType") settingType: PushNotificationSettingType): Response<Unit>

}
