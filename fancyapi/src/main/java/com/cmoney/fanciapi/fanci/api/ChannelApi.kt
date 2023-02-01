package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.EditChannelParam
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.RoleIdsParam

interface ChannelApi {
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
     * 從頻道移除 多個 角色 __________🔒 指派頻道管理員
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
     * 新增更新 多個 頻道角色 __________🔒 指派頻道管理員
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

}
