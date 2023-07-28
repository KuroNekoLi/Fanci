package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.CategoryParam
import com.cmoney.fanciapi.fanci.model.EditGroupParam
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupPaging
import com.cmoney.fanciapi.fanci.model.GroupParam
import com.cmoney.fanciapi.fanci.model.OrderType
import com.cmoney.fanciapi.fanci.model.RoleChannelAuthType
import com.cmoney.fanciapi.fanci.model.RoleParam
import com.cmoney.fanciapi.fanci.model.UpdateIsNeedApprovalParam

interface GroupApi {
    /**
     * 取得社團列表
     * 
     * Responses:
     *  - 200: 成功
     *
     * @param startWeight 起始權重 (optional, default to 2147483647L)
     * @param pageSize 每頁筆數 (optional, default to 100)
     * @param orderType 排序類型 (optional)
     * @return [GroupPaging]
     */
    @GET("api/v1/Group")
    suspend fun apiV1GroupGet(@Query("startWeight") startWeight: kotlin.Long? = 2147483647L, @Query("pageSize") pageSize: kotlin.Int? = 100, @Query("orderType") orderType: OrderType? = null): Response<GroupPaging>

    /**
     * 新增社團 頻道分類 __________🔒 建立分類
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該社團
     *
     * @param groupId 社團Id
     * @param categoryParam 分類參數 (optional)
     * @return [Category]
     */
    @POST("api/v1/Group/{groupId}/Category")
    suspend fun apiV1GroupGroupIdCategoryPost(@Path("groupId") groupId: kotlin.String, @Body categoryParam: CategoryParam? = null): Response<Category>

    /**
     * 刪除/解散社團 __________🔒 社長
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該社團
     *
     * @param groupId 社團Id
     * @return [Unit]
     */
    @DELETE("api/v1/Group/{groupId}")
    suspend fun apiV1GroupGroupIdDelete(@Path("groupId") groupId: kotlin.String): Response<Unit>

    /**
     * 取得特定社團
     * 
     * Responses:
     *  - 200: 成功
     *  - 404: 找不到該社團
     *
     * @param groupId 社團Id
     * @return [Group]
     */
    @GET("api/v1/Group/{groupId}")
    suspend fun apiV1GroupGroupIdGet(@Path("groupId") groupId: kotlin.String): Response<Group>

    /**
     * 更新社團是否公開 __________🔒 設定社團公開私密
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該社團
     *
     * @param groupId 社團Id
     * @param updateIsNeedApprovalParam 更新參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Group/{groupId}/isNeedApproval")
    suspend fun apiV1GroupGroupIdIsNeedApprovalPut(@Path("groupId") groupId: kotlin.String, @Body updateIsNeedApprovalParam: UpdateIsNeedApprovalParam? = null): Response<Unit>

    /**
     * 編輯社團資訊 __________🔒 編輯社團
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該社團
     *
     * @param groupId 社團Id
     * @param editGroupParam 編輯社團參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Group/{groupId}")
    suspend fun apiV1GroupGroupIdPut(@Path("groupId") groupId: kotlin.String, @Body editGroupParam: EditGroupParam? = null): Response<Unit>

    /**
     * 取得角色列表 (不包含VIP)
     * 
     * Responses:
     *  - 200: 成功
     *  - 404: 找不到該社團
     *
     * @param groupId 社團Id
     * @return [kotlin.collections.List<FanciRole>]
     */
    @GET("api/v1/Group/{groupId}/Role")
    suspend fun apiV1GroupGroupIdRoleGet(@Path("groupId") groupId: kotlin.String): Response<kotlin.collections.List<FanciRole>>

    /**
     * 取得特定角色在Group中所有頻道權限
     * 
     * Responses:
     *  - 200: 成功
     *  - 404: 找不到該社團
     *
     * @param groupId 社團Id
     * @param roleId 
     * @return [kotlin.collections.List<RoleChannelAuthType>]
     */
    @GET("api/v1/Group/{groupId}/{roleId}/ChannelAuthType")
    suspend fun apiV1GroupGroupIdRoleIdChannelAuthTypeGet(@Path("groupId") groupId: kotlin.String, @Path("roleId") roleId: kotlin.String): Response<kotlin.collections.List<RoleChannelAuthType>>

    /**
     * 新增角色(管理員) __________🔒 新增角色
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 已有相同名稱的角色
     *
     * @param groupId 社團Id
     * @param roleParam 角色參數 (optional)
     * @return [FanciRole]
     */
    @POST("api/v1/Group/{groupId}/Role")
    suspend fun apiV1GroupGroupIdRolePost(@Path("groupId") groupId: kotlin.String, @Body roleParam: RoleParam? = null): Response<FanciRole>

    /**
     * 刪除角色 __________🔒 刪除角色
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 找不到要刪除的角色
     *
     * @param groupId 社團Id
     * @param roleId 角色Id
     * @return [Unit]
     */
    @DELETE("api/v1/Group/{groupId}/Role/{roleId}")
    suspend fun apiV1GroupGroupIdRoleRoleIdDelete(@Path("groupId") groupId: kotlin.String, @Path("roleId") roleId: kotlin.String): Response<Unit>

    /**
     * 編輯角色 __________🔒 編輯角色
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 已有相同名稱的角色
     *
     * @param groupId 社團Id
     * @param roleId 角色Id
     * @param roleParam 角色參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Group/{groupId}/Role/{roleId}")
    suspend fun apiV1GroupGroupIdRoleRoleIdPut(@Path("groupId") groupId: kotlin.String, @Path("roleId") roleId: kotlin.String, @Body roleParam: RoleParam? = null): Response<Unit>

    /**
     * 取得角色列表 (Vip)
     * 
     * Responses:
     *  - 200: 成功
     *  - 404: 找不到該社團
     *
     * @param groupId 社團Id
     * @return [kotlin.collections.List<FanciRole>]
     */
    @GET("api/v1/Group/{groupId}/VipRole")
    suspend fun apiV1GroupGroupIdVipRoleGet(@Path("groupId") groupId: kotlin.String): Response<kotlin.collections.List<FanciRole>>

    /**
     * 新增Vip角色(限定後台使用)
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 已有相同名稱的角色
     *
     * @param groupId 社團Id
     * @param roleParam 角色參數 (optional)
     * @return [FanciRole]
     */
    @POST("api/v1/Group/{groupId}/VipRole")
    suspend fun apiV1GroupGroupIdVipRolePost(@Path("groupId") groupId: kotlin.String, @Body roleParam: RoleParam? = null): Response<FanciRole>

    /**
     * 取得我加入的社團清單 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *
     * @param startWeight 起始權重 (optional, default to 0L)
     * @param pageSize 每頁筆數 (optional, default to 20)
     * @return [GroupPaging]
     */
    @GET("api/v1/Group/me")
    suspend fun apiV1GroupMeGet(@Query("startWeight") startWeight: kotlin.Long? = 0L, @Query("pageSize") pageSize: kotlin.Int? = 20): Response<GroupPaging>

    /**
     * 新增社團 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *
     * @param groupParam 新增社團參數 (optional)
     * @return [Group]
     */
    @POST("api/v1/Group")
    suspend fun apiV1GroupPost(@Body groupParam: GroupParam? = null): Response<Group>

}
