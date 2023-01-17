package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.PatchPermissionParam
import com.cmoney.fanciapi.fanci.model.Permission
import com.cmoney.fanciapi.fanci.model.PermissionCategory

interface PermissionApi {
    /**
     * 取得user在此頻道擁有的權限 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該頻道
     *
     * @param channelId 聊天室頻道Id
     * @return [kotlin.collections.List<Permission>]
     */
    @GET("api/v1/Permission/Channel/{channelId}")
    suspend fun apiV1PermissionChannelChannelIdGet(@Path("channelId") channelId: kotlin.String): Response<kotlin.collections.List<Permission>>

    /**
     * 取得管理權限表
     * 
     * Responses:
     *  - 200: 成功
     *
     * @return [kotlin.collections.List<PermissionCategory>]
     */
    @GET("api/v1/Permission")
    suspend fun apiV1PermissionGet(): Response<kotlin.collections.List<PermissionCategory>>

    /**
     * 取得user在此社團擁有的權限 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該社團
     *
     * @param groupId 社團Id
     * @return [kotlin.collections.List<Permission>]
     */
    @GET("api/v1/Permission/Group/{groupId}")
    suspend fun apiV1PermissionGroupGroupIdGet(@Path("groupId") groupId: kotlin.String): Response<kotlin.collections.List<Permission>>

    /**
     * 取得單一權限資訊
     * 
     * Responses:
     *  - 200: Success
     *
     * @param id 
     * @return [Permission]
     */
    @Deprecated("This api was deprecated")
    @GET("api/v1/Permission/{id}")
    suspend fun apiV1PermissionIdGet(@Path("id") id: kotlin.String): Response<Permission>

    /**
     * 
     * 
     * Responses:
     *  - 200: Success
     *
     * @param id 
     * @param passCode  (optional)
     * @param patchPermissionParam  (optional)
     * @return [Permission]
     */
    @Deprecated("This api was deprecated")
    @PATCH("api/v1/Permission/{id}")
    suspend fun apiV1PermissionIdPatch(@Path("id") id: kotlin.String, @Query("passCode") passCode: kotlin.String? = null, @Body patchPermissionParam: PatchPermissionParam? = null): Response<Permission>

}
