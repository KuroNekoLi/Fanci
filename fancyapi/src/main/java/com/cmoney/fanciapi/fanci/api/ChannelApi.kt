package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.fanci.model.AccessorTypes
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChannelAccessOptionModel
import com.cmoney.fanciapi.fanci.model.ChannelAccessOptionV2
import com.cmoney.fanciapi.fanci.model.ChannelAuthType
import com.cmoney.fanciapi.fanci.model.ChannelWhiteList
import com.cmoney.fanciapi.fanci.model.EditChannelParam
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.GetWhiteListCountParam
import com.cmoney.fanciapi.fanci.model.PutAuthTypeRequest
import com.cmoney.fanciapi.fanci.model.PutWhiteListRequest
import com.cmoney.fanciapi.fanci.model.RoleIdsParam
import com.cmoney.fanciapi.fanci.model.WhiteListCount
import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.Response
import retrofit2.http.*

interface ChannelApi {
    /**
     * 取得私密頻道權限設定文案  提供權限類型，以及權限描述的文案
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @return [kotlin.collections.List<ChannelAccessOptionModel>]
     */
    @Deprecated("This api was deprecated")
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
     * 取得特定頻道
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
     * 取得角色清單 (不包含VIP)
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該頻道
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
     * 取得VIP角色清單
     * 
     * Responses:
     *  - 204: No Content
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 200: 成功
     *  - 404: 找不到該頻道
     *
     * @param channelId 頻道Id
     * @return [Unit]
     */
    @GET("api/v1/Channel/{channelId}/VipRole")
    suspend fun apiV1ChannelChannelIdVipRoleGet(@Path("channelId") channelId: kotlin.String): Response<Unit>

    /**
     * 編輯指定使用者/角色 於頻道中的權限AuthType   使用此方法移動該角色權限後 會將該角色從其他權限清單中移除 __________🔒 管理VIP方案
     * 
     * Responses:
     *  - 204: No Content
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 頻道ID
     * @param accessorType 異動的成員類型 使用者/角色/VIP角色
     * @param accessorId 異動頻道成員ID
     * @param putAuthTypeRequest 指定加入成員的權限類型) (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Channel/{channelId}/WhiteList/{accessorType}/{accessorId}")
    suspend fun apiV1ChannelChannelIdWhiteListAccessorTypeAccessorIdPut(@Path("channelId") channelId: kotlin.String, @Path("accessorType") accessorType: AccessorTypes, @Path("accessorId") accessorId: kotlin.String, @Body putAuthTypeRequest: PutAuthTypeRequest? = null): Response<Unit>

    /**
     * 取得私密頻道白名單
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
    suspend fun apiV1ChannelChannelIdWhiteListAuthTypeGet(@Path("channelId") channelId: kotlin.String, @Path("authType") authType: ChannelAuthType): Response<ChannelWhiteList>

    /**
     * 設定私密頻道白名單 (Role/VipRole/Users)  把channel底下 對應的authType清單用戶角色VIP全部替換   (若提供的清單用戶或角色已存在於其他authType，會保持同時存在於多個authType的狀態，所以異動已設定過的用戶或角色，記得到另一個authType將提供的清單一併異動) __________🔒 編輯頻道
     * 
     * Responses:
     *  - 204: No Content
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 頻道ID
     * @param authType 指定加入成員的權限類型)
     * @param putWhiteListRequest  (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Channel/{channelId}/WhiteList/{authType}")
    suspend fun apiV1ChannelChannelIdWhiteListAuthTypePut(@Path("channelId") channelId: kotlin.String, @Path("authType") authType: ChannelAuthType, @Body putWhiteListRequest: PutWhiteListRequest? = null): Response<Unit>

    /**
     * 取得私密頻道白名單
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

    /**
     * 取得私密頻道白名單覆蓋人數
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param getWhiteListCountParam  (optional)
     * @return [WhiteListCount]
     */
    @POST("api/v1/Channel/WhiteList/Users/Count")
    suspend fun apiV1ChannelWhiteListUsersCountPost(@Body getWhiteListCountParam: GetWhiteListCountParam? = null): Response<WhiteListCount>

    /**
     * 取得私密頻道權限設定文案  提供權限類型，以及權限描述的文案
     *
     * Responses:
     *  - 200: Success
     *
     * @param isWithNoPermission 是否包含無權限文案 (optional)
     * @return [kotlin.collections.List<ChannelAccessOptionV2>]
     */
    @GET("api/v2/Channel/AccessType")
    suspend fun apiV2ChannelAccessTypeGet(@Query("IsWithNoPermission") isWithNoPermission: kotlin.Boolean? = null): Response<kotlin.collections.List<ChannelAccessOptionV2>>
}
