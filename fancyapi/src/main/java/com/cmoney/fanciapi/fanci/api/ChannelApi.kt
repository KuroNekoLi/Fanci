package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.AccessorParam
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChannelAccessOptionModel
import com.cmoney.fanciapi.fanci.model.ChannelWhiteList
import com.cmoney.fanciapi.fanci.model.EditChannelParam
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.RoleIdsParam

interface ChannelApi {
    /**
     * 取得私密頻道權限設定文案  提供權限類型，以及權限描述的文案
     * 
     * Responses:
     *  - 200: Success
     *
     * @return [kotlin.collections.List<ChannelAccessOptionModel>]
     */
    @GET("api/v1/Channel/AccessType")
    suspend fun apiV1ChannelAccessTypeGet(): Response<kotlin.collections.List<ChannelAccessOptionModel>>

    /**
     * 刪除頻道 __________🔒 刪除頻道
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該頻道
     *
     * @param channelId 聊天室Id
     * @return [Unit]
     */
    @DELETE("api/v1/Channel/{channelId}")
    suspend fun apiV1ChannelChannelIdDelete(@Path("channelId") channelId: kotlin.String): Response<Unit>

    /**
     * 取得特定頻道 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該頻道
     *
     * @param channelId 頻道Id
     * @return [Channel]
     */
    @GET("api/v1/Channel/{channelId}")
    suspend fun apiV1ChannelChannelIdGet(@Path("channelId") channelId: kotlin.String): Response<Channel>

    /**
     * 編輯頻道 __________🔒 編輯頻道
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該頻道
     *
     * @param channelId 頻道Id
     * @param editChannelParam 頻道參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Channel/{channelId}")
    suspend fun apiV1ChannelChannelIdPut(@Path("channelId") channelId: kotlin.String, @Body editChannelParam: EditChannelParam? = null): Response<Unit>

    /**
     * 從頻道移除 多個 管理員角色 __________🔒 指派頻道管理員
     * 
     * Responses:
     *  - 404: 找不到該頻道
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 405: 要新增的角色不存在
     *
     * @param channelId 頻道Id
     * @param roleIdsParam 角色Id (optional)
     * @return [Unit]
     */
    @HTTP(method = "DELETE", path = "api/v1/Channel/{channelId}/Role", hasBody = true)
    suspend fun apiV1ChannelChannelIdRoleDelete(@Path("channelId") channelId: kotlin.String, @Body roleIdsParam: RoleIdsParam? = null): Response<Unit>

    /**
     * 取得角色清單
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 404: 找不到該頻道
     *  - 403: 沒有權限
     *
     * @param channelId 頻道Id
     * @return [kotlin.collections.List<FanciRole>]
     */
    @GET("api/v1/Channel/{channelId}/Role")
    suspend fun apiV1ChannelChannelIdRoleGet(@Path("channelId") channelId: kotlin.String): Response<kotlin.collections.List<FanciRole>>

    /**
     * 新增更新 多個管理員 到 頻道 __________🔒 指派頻道管理員
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該頻道
     *  - 405: 要新增的角色不存在
     *
     * @param channelId 頻道Id
     * @param roleIdsParam 新增的 role id 清單 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Channel/{channelId}/Role")
    suspend fun apiV1ChannelChannelIdRolePut(@Path("channelId") channelId: kotlin.String, @Body roleIdsParam: RoleIdsParam? = null): Response<Unit>

    /**
     * 設定私密頻道白名單 (Role/Users) __________🔒 編輯頻道
     * 
     * Responses:
     *  - 204: No Content
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 頻道ID
     * @param authType 指定加入成員的權限類型)
     * @param accessorParam  (optional)
     * @return [Unit]
     */
    @DELETE("api/v1/Channel/{channelId}/WhiteList/{authType}")
    suspend fun apiV1ChannelChannelIdWhiteListAuthTypeDelete(@Path("channelId") channelId: kotlin.String, @Path("authType") authType: kotlin.String, @Body accessorParam: AccessorParam? = null): Response<Unit>

    /**
     * 取得私密頻道白名單 __________🔒 編輯頻道
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 
     * @param authType 
     * @return [ChannelWhiteList]
     */
    @GET("api/v1/Channel/{channelId}/WhiteList/{authType}")
    suspend fun apiV1ChannelChannelIdWhiteListAuthTypeGet(@Path("channelId") channelId: kotlin.String, @Path("authType") authType: kotlin.String): Response<ChannelWhiteList>

    /**
     * 設定私密頻道白名單 (Role/Users) __________🔒 編輯頻道
     * 
     * Responses:
     *  - 204: No Content
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 頻道ID
     * @param authType 指定加入成員的權限類型)
     * @param accessorParam  (optional)
     * @return [Unit]
     */
    @PATCH("api/v1/Channel/{channelId}/WhiteList/{authType}")
    suspend fun apiV1ChannelChannelIdWhiteListAuthTypePatch(@Path("channelId") channelId: kotlin.String, @Path("authType") authType: kotlin.String, @Body accessorParam: AccessorParam? = null): Response<Unit>

    /**
     * 取得私密頻道白名單 __________🔒 編輯頻道
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 
     * @return [kotlin.collections.List<ChannelWhiteList>]
     */
    @GET("api/v1/Channel/{channelId}/WhiteList")
    suspend fun apiV1ChannelChannelIdWhiteListGet(@Path("channelId") channelId: kotlin.String): Response<kotlin.collections.List<ChannelWhiteList>>

}
