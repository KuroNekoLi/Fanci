package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.GroupRequirement
import com.cmoney.fanciapi.fanci.model.GroupRequirementParam

interface GroupRequirementApi {
    /**
     * 取得加入社團的要求題目
     * 
     * Responses:
     *  - 409: Conflict
     *
     * @param groupId 社團id
     * @return [Unit]
     */
    @GET("api/v1/GroupRequirement/group/{groupId}")
    suspend fun apiV1GroupRequirementGroupGroupIdGet(@Path("groupId") groupId: kotlin.String): Response<Unit>

    /**
     * 新增或更新社團題目
     * 
     * Responses:
     *  - 409: Conflict
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 社團id
     * @param groupRequirementParam 題目參數 (optional)
     * @return [GroupRequirement]
     */
    @PUT("api/v1/GroupRequirement/group/{groupId}")
    suspend fun apiV1GroupRequirementGroupGroupIdPut(@Path("groupId") groupId: kotlin.String, @Body groupRequirementParam: GroupRequirementParam? = null): Response<GroupRequirement>

}
