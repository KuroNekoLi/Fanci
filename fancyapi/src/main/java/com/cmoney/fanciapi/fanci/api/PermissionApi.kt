package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.Permission

interface PermissionApi {
    /**
     * 取得user在此頻道擁有的權限
     * 
     * Responses:
     *  - 200: Success
     *  - 404: Not Found
     *
     * @param channelId 指定聊天室
     * @return [Channel]
     */
    @GET("api/v1/Permission/Channel/{channelId}")
    suspend fun apiV1PermissionChannelChannelIdGet(@Path("channelId") channelId: kotlin.String): Response<Channel>

    /**
     * 取得管理權限表
     * 
     * Responses:
     *  - 200: Success
     *
     * @return [kotlin.collections.List<Permission>]
     */
    @GET("api/v1/Permission")
    suspend fun apiV1PermissionGet(): Response<kotlin.collections.List<Permission>>

    /**
     * 取得user在此社團擁有的權限
     * 
     * Responses:
     *  - 200: Success
     *  - 404: Not Found
     *
     * @param groupId 
     * @return [Group]
     */
    @GET("api/v1/Permission/Group/{groupId}")
    suspend fun apiV1PermissionGroupGroupIdGet(@Path("groupId") groupId: kotlin.String): Response<Group>

}
