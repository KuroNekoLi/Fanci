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
     *  - 200: 成功
     *  - 404: 找不到社團
     *
     * @param groupId 社團id
     * @return [GroupRequirement]
     */
    @GET("api/v1/GroupRequirement/group/{groupId}")
    suspend fun apiV1GroupRequirementGroupGroupIdGet(@Path("groupId") groupId: kotlin.String): Response<GroupRequirement>

    /**
     * 新增或更新社團題目 __________🔒 建立與編輯入社問題
     * 
     * Responses:
     *  - 409: Conflict
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *
     * @param groupId 社團id
     * @param groupRequirementParam 題目參數 (optional)
     * @return [GroupRequirement]
     */
    @PUT("api/v1/GroupRequirement/group/{groupId}")
    suspend fun apiV1GroupRequirementGroupGroupIdPut(@Path("groupId") groupId: kotlin.String, @Body groupRequirementParam: GroupRequirementParam? = null): Response<GroupRequirement>

}
