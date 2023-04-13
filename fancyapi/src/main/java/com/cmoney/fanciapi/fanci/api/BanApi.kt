package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.BanParam
import com.cmoney.fanciapi.fanci.model.UserBanInformation
import com.cmoney.fanciapi.fanci.model.UseridsParam

interface BanApi {
    /**
     * 解除禁言 __________🔒 禁言
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 
     * @param useridsParam  (optional)
     * @return [Unit]
     */
    @HTTP(method = "DELETE", path = "api/v1/Ban/Group/{groupId}", hasBody = true)
    suspend fun apiV1BanGroupGroupIdDelete(@Path("groupId") groupId: kotlin.String, @Body useridsParam: UseridsParam? = null): Response<Unit>

    /**
     * 取得社團的禁言用戶清單 __________🔒 禁言
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 
     * @return [kotlin.collections.List<UserBanInformation>]
     */
    @GET("api/v1/Ban/Group/{groupId}")
    suspend fun apiV1BanGroupGroupIdGet(@Path("groupId") groupId: kotlin.String): Response<kotlin.collections.List<UserBanInformation>>

    /**
     * 取得自己在社團的禁言狀態 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 
     * @return [UserBanInformation]
     */
    @GET("api/v1/Ban/Group/{groupId}/me")
    suspend fun apiV1BanGroupGroupIdMeGet(@Path("groupId") groupId: kotlin.String): Response<UserBanInformation>

    /**
     * 新增或調整某個用戶的禁言狀態 __________🔒 禁言
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 
     * @param banParam  (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Ban/Group/{groupId}")
    suspend fun apiV1BanGroupGroupIdPut(@Path("groupId") groupId: kotlin.String, @Body banParam: BanParam? = null): Response<Unit>

    /**
     * 取得特定會員在社團的禁言狀態，沒有被ban會回傳404 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 
     * @param userId 
     * @return [UserBanInformation]
     */
    @GET("api/v1/Ban/Group/{groupId}/{userId}")
    suspend fun apiV1BanGroupGroupIdUserIdGet(@Path("groupId") groupId: kotlin.String, @Path("userId") userId: kotlin.String): Response<UserBanInformation>

}
