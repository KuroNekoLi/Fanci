package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.ChannelTabType
import com.cmoney.fanciapi.fanci.model.ChannelTabsSortParam
import com.cmoney.fanciapi.fanci.model.ChannelTabsStatus

interface ChannelTabApi {
    /**
     * 取得所有種類頻道功能區
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @return [ChannelTabsSortParam]
     */
    @GET("api/v1/ChannelTab/All")
    suspend fun apiV1ChannelTabAllGet(): Response<ChannelTabsSortParam>

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
    @Deprecated("This api was deprecated")
    @GET("api/v1/ChannelTab/{channelId}/Tab")
    suspend fun apiV1ChannelTabChannelIdTabGet(@Path("channelId") channelId: kotlin.String): Response<ChannelTabsStatus>

    /**
     * 取得頻道功能區啟用狀態
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 頻道ID
     * @return [ChannelTabsSortParam]
     */
    @GET("api/v1/ChannelTab/{channelId}/Tabs")
    suspend fun apiV1ChannelTabChannelIdTabsGet(@Path("channelId") channelId: kotlin.String): Response<ChannelTabsSortParam>

    /**
     * 編輯頻道功能區順序 __________🔒 編輯頻道
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 400: 參數錯誤 功能區數量不符合
     *  - 404: 找不到該頻道
     *
     * @param channelId 頻道Id
     * @param channelTabsSortParam  (optional)
     * @return [Unit]
     */
    @PUT("api/v1/ChannelTab/{channelId}/Tabs/Sort")
    suspend fun apiV1ChannelTabChannelIdTabsSortPut(@Path("channelId") channelId: kotlin.String, @Body channelTabsSortParam: ChannelTabsSortParam? = null): Response<Unit>

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
