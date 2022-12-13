package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.GroupMemberRoleInfos
import com.cmoney.fanciapi.fanci.model.User
import com.cmoney.fanciapi.fanci.model.UseridsParam

interface RoleUserApi {
    /**
     * 取得用戶ID清單的腳色列表 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 
     * @param userIds Fanci 用戶 ID (optional)
     * @return [kotlin.collections.List<GroupMemberRoleInfos>]
     */
    @GET("api/v1/RoleUser/Group/{groupId}")
    suspend fun apiV1RoleUserGroupGroupIdGet(@Path("groupId") groupId: kotlin.String, @Query("UserIds") userIds: kotlin.collections.List<kotlin.String>? = null): Response<kotlin.collections.List<GroupMemberRoleInfos>>

    /**
     * 移除使用者的角色身分 __________🔒 指派身分
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 找不到指派的角色
     *
     * @param groupId 社團Id
     * @param roleId 角色Id
     * @param useridsParam 使用者清單 (optional)
     * @return [Unit]
     */
    @DELETE("api/v1/RoleUser/Group/{groupId}/Role/{roleId}")
    suspend fun apiV1RoleUserGroupGroupIdRoleRoleIdDelete(@Path("groupId") groupId: kotlin.String, @Path("roleId") roleId: kotlin.String, @Body useridsParam: UseridsParam? = null): Response<Unit>

    /**
     * 取得具有此角色身分的用戶清單 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 找不到指派的角色
     *
     * @param groupId 
     * @param roleId 
     * @return [kotlin.collections.List<User>]
     */
    @GET("api/v1/RoleUser/Group/{groupId}/Role/{roleId}")
    suspend fun apiV1RoleUserGroupGroupIdRoleRoleIdGet(@Path("groupId") groupId: kotlin.String, @Path("roleId") roleId: kotlin.String): Response<kotlin.collections.List<User>>

    /**
     * 指派使用者角色身分 __________🔒 指派身分
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 找不到指派的角色
     *
     * @param groupId 社團Id
     * @param roleId 角色Id
     * @param useridsParam 使用者清單 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/RoleUser/Group/{groupId}/Role/{roleId}")
    suspend fun apiV1RoleUserGroupGroupIdRoleRoleIdPut(@Path("groupId") groupId: kotlin.String, @Path("roleId") roleId: kotlin.String, @Body useridsParam: UseridsParam? = null): Response<Unit>

}
