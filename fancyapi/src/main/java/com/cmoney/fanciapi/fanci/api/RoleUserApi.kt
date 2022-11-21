package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.UseridsParam

interface RoleUserApi {
    /**
     * 移除 清單中的用戶 roleId 角色身分
     * 
     * Responses:
     *  - 200: Success
     *
     * @param groupId 
     * @param roleId 
     * @param useridsParam  (optional)
     * @return [Unit]
     */
    @DELETE("api/v1/RoleUser/Group/{groupId}/Role/{roleId}")
    suspend fun apiV1RoleUserGroupGroupIdRoleRoleIdDelete(@Path("groupId") groupId: kotlin.String, @Path("roleId") roleId: kotlin.String, @Body useridsParam: UseridsParam? = null): Response<Unit>

    /**
     * 給予 清單中的用戶 roleId 角色身分
     * 
     * Responses:
     *  - 200: Success
     *
     * @param groupId 
     * @param roleId 
     * @param useridsParam  (optional)
     * @return [Unit]
     */
    @PUT("api/v1/RoleUser/Group/{groupId}/Role/{roleId}")
    suspend fun apiV1RoleUserGroupGroupIdRoleRoleIdPut(@Path("groupId") groupId: kotlin.String, @Path("roleId") roleId: kotlin.String, @Body useridsParam: UseridsParam? = null): Response<Unit>

}
