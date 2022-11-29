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
     * 從頻道移除角色 __________🔒 指派頻道管理員
     * 
     * Responses:
     *  - 404: 找不到該頻道
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 405: 要新增的角色不存在
     *
     * @param channelId 頻道Id
     * @param roleId 角色Id
     * @return [Unit]
     */
    @DELETE("api/v1/Channel/{channelId}/Role/{roleId}")
    suspend fun apiV1ChannelChannelIdRoleRoleIdDelete(@Path("channelId") channelId: kotlin.String, @Path("roleId") roleId: kotlin.String): Response<Unit>

    /**
     * 新增更新頻道角色 __________🔒 指派頻道管理員
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該頻道
     *  - 405: 要新增的角色不存在
     *
     * @param channelId 頻道Id
     * @param roleId 角色Id
     * @return [Unit]
     */
    @PUT("api/v1/Channel/{channelId}/Role/{roleId}")
    suspend fun apiV1ChannelChannelIdRoleRoleIdPut(@Path("channelId") channelId: kotlin.String, @Path("roleId") roleId: kotlin.String): Response<Unit>

}
