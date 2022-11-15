package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.CategoryParam
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupPaging
import com.cmoney.fanciapi.fanci.model.GroupParam
import com.cmoney.fanciapi.fanci.model.RoleParam

interface GroupApi {
    /**
     * 取得社團列表
     * 
     * Responses:
     *  - 200: Success
     *  - 404: Not Found
     *
     * @param startWeight  (optional, default to 0L)
     * @param pageSize  (optional, default to 100)
     * @return [GroupPaging]
     */
    @GET("api/v1/Group")
    suspend fun apiV1GroupGet(@Query("startWeight") startWeight: kotlin.Long? = 0L, @Query("pageSize") pageSize: kotlin.Int? = 100): Response<GroupPaging>

    /**
     * 新增社團 頻道分類
     * 
     * Responses:
     *  - 200: Success
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param groupId 
     * @param categoryParam  (optional)
     * @return [Category]
     */
    @POST("api/v1/Group/{groupId}/Category")
    suspend fun apiV1GroupGroupIdCategoryPost(@Path("groupId") groupId: kotlin.String, @Body categoryParam: CategoryParam? = null): Response<Category>

    /**
     * 刪除/解散社團(只有社團管理員可執行)
     * 
     * Responses:
     *  - 204: No Content
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param groupId 
     * @return [Unit]
     */
    @DELETE("api/v1/Group/{groupId}")
    suspend fun apiV1GroupGroupIdDelete(@Path("groupId") groupId: kotlin.String): Response<Unit>

    /**
     * 根據社團id取得特定社團
     * 
     * Responses:
     *  - 200: Success
     *  - 404: Not Found
     *
     * @param groupId 
     * @return [Group]
     */
    @GET("api/v1/Group/{groupId}")
    suspend fun apiV1GroupGroupIdGet(@Path("groupId") groupId: kotlin.String): Response<Group>

    /**
     * 編輯社團資訊(重命名/簡介/是否公開)
     * 
     * Responses:
     *  - 204: No Content
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param groupId 
     * @param groupParam  (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Group/{groupId}")
    suspend fun apiV1GroupGroupIdPut(@Path("groupId") groupId: kotlin.String, @Body groupParam: GroupParam? = null): Response<Unit>

    /**
     * 取得頻道角色列表
     * 
     * Responses:
     *  - 200: Success
     *
     * @param groupId 
     * @return [Unit]
     */
    @GET("api/v1/Group/{groupId}/Role")
    suspend fun apiV1GroupGroupIdRoleGet(@Path("groupId") groupId: kotlin.String): Response<Unit>

    /**
     * 新增角色
     * 
     * Responses:
     *  - 200: Success
     *
     * @param groupId 
     * @param roleParam  (optional)
     * @return [FanciRole]
     */
    @POST("api/v1/Group/{groupId}/Role")
    suspend fun apiV1GroupGroupIdRolePost(@Path("groupId") groupId: kotlin.String, @Body roleParam: RoleParam? = null): Response<FanciRole>

    /**
     * 刪除角色
     * 
     * Responses:
     *  - 200: Success
     *
     * @param groupId 
     * @param roleId 
     * @return [Unit]
     */
    @DELETE("api/v1/Group/{groupId}/Role/{roleId}")
    suspend fun apiV1GroupGroupIdRoleRoleIdDelete(@Path("groupId") groupId: kotlin.String, @Path("roleId") roleId: kotlin.Int): Response<Unit>

    /**
     * 編輯角色
     * 
     * Responses:
     *  - 200: Success
     *
     * @param groupId 
     * @param roleId 
     * @param roleParam  (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Group/{groupId}/Role/{roleId}")
    suspend fun apiV1GroupGroupIdRoleRoleIdPut(@Path("groupId") groupId: kotlin.String, @Path("roleId") roleId: kotlin.Int, @Body roleParam: RoleParam? = null): Response<Unit>

    /**
     * 取得我加入的社團頻道清單
     * 
     * Responses:
     *  - 200: Success
     *
     * @param startWeight  (optional, default to 0L)
     * @param pageSize  (optional, default to 20)
     * @return [GroupPaging]
     */
    @POST("api/v1/Group/me")
    suspend fun apiV1GroupMePost(@Query("startWeight") startWeight: kotlin.Long? = 0L, @Query("pageSize") pageSize: kotlin.Int? = 20): Response<GroupPaging>

    /**
     * 新增社團
     * 
     * Responses:
     *  - 200: Success
     *
     * @param groupParam  (optional)
     * @return [Group]
     */
    @POST("api/v1/Group")
    suspend fun apiV1GroupPost(@Body groupParam: GroupParam? = null): Response<Group>

}
