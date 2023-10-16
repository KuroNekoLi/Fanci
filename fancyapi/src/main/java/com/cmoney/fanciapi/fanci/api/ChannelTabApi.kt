package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.ChannelTabType
import com.cmoney.fanciapi.fanci.model.ChannelTabsStatus

interface ChannelTabApi {
    /**
     * 取得頻道功能區啟用狀態
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 頻道ID
     * @return [ChannelTabsStatus]
     */
    @GET("api/v1/ChannelTab/{channelId}/Tab")
    suspend fun apiV1ChannelTabChannelIdTabGet(@Path("channelId") channelId: kotlin.String): Response<ChannelTabsStatus>

    /**
     * 關閉頻道功能區 __________🔒 編輯頻道
     * 
     * Responses:
     *  - 204: No Content
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 頻道ID
     * @param type 
     * @return [Unit]
     */
    @DELETE("api/v1/ChannelTab/{channelId}/{type}")
    suspend fun apiV1ChannelTabChannelIdTypeDelete(@Path("channelId") channelId: kotlin.String, @Path("type") type: ChannelTabType): Response<Unit>

    /**
     * 啟用頻道功能區 __________🔒 編輯頻道
     * 
     * Responses:
     *  - 204: No Content
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 頻道ID
     * @param type 
     * @return [Unit]
     */
    @PUT("api/v1/ChannelTab/{channelId}/{type}")
    suspend fun apiV1ChannelTabChannelIdTypePut(@Path("channelId") channelId: kotlin.String, @Path("type") type: ChannelTabType): Response<Unit>

}
