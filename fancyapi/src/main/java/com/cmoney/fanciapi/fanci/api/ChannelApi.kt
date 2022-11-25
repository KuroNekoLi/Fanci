package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.EditChannelParam
import com.cmoney.fanciapi.fanci.model.FanciRole

interface ChannelApi {
    /**
     * 刪除頻道
     * 
     * Responses:
     *  - 403: Forbidden
     *  - 404: Not Found
     *  - 401: Unauthorized
     *
     * @param channelId 指定聊天室
     * @return [Unit]
     */
    @DELETE("api/v1/Channel/{channelId}")
    suspend fun apiV1ChannelChannelIdDelete(@Path("channelId") channelId: kotlin.String): Response<Unit>

    /**
     * 取得特定頻道
     * 
     * Responses:
     *  - 200: Success
     *  - 404: Not Found
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 指定聊天室
     * @return [Channel]
     */
    @GET("api/v1/Channel/{channelId}")
    suspend fun apiV1ChannelChannelIdGet(@Path("channelId") channelId: kotlin.String): Response<Channel>

    /**
     * 編輯頻道
     * 
     * Responses:
     *  - 204: No Content
     *  - 404: Not Found
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 指定聊天室
     * @param editChannelParam  (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Channel/{channelId}")
    suspend fun apiV1ChannelChannelIdPut(@Path("channelId") channelId: kotlin.String, @Body editChannelParam: EditChannelParam? = null): Response<Unit>

    /**
     * 取得角色清單
     * 
     * Responses:
     *  - 404: Not Found
     *  - 204: No Content
     *  - 200: Success
     *
     * @param channelId 
     * @return [kotlin.collections.List<FanciRole>]
     */
    @GET("api/v1/Channel/{channelId}/Role")
    suspend fun apiV1ChannelChannelIdRoleGet(@Path("channelId") channelId: kotlin.String): Response<kotlin.collections.List<FanciRole>>

    /**
     * 從頻道移除角色
     * 
     * Responses:
     *  - 404: Not Found
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 
     * @param roleId 
     * @return [Unit]
     */
    @DELETE("api/v1/Channel/{channelId}/Role/{roleId}")
    suspend fun apiV1ChannelChannelIdRoleRoleIdDelete(@Path("channelId") channelId: kotlin.String, @Path("roleId") roleId: kotlin.String): Response<Unit>

    /**
     * 新增角色到頻道/編輯可存取頻道角色權限
     * 
     * Responses:
     *  - 404: Not Found
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 
     * @param roleId 
     * @return [Unit]
     */
    @PUT("api/v1/Channel/{channelId}/Role/{roleId}")
    suspend fun apiV1ChannelChannelIdRoleRoleIdPut(@Path("channelId") channelId: kotlin.String, @Path("roleId") roleId: kotlin.String): Response<Unit>

}
