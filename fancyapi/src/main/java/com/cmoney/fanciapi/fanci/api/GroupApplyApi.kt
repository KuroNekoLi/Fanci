package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.ApplyStatus
import com.cmoney.fanciapi.fanci.model.CountResult
import com.cmoney.fanciapi.fanci.model.GroupApplyParam
import com.cmoney.fanciapi.fanci.model.GroupApplyStatusParam
import com.cmoney.fanciapi.fanci.model.GroupRequirementApplyInfo
import com.cmoney.fanciapi.fanci.model.GroupRequirementApplyPaging

interface GroupApplyApi {
    /**
     * 整批更新審核狀態(通過/拒絕) __________🔒 審核入社申請
     * 
     * Responses:
     *  - 200: Success
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 204: 成功
     *  - 404: 找不到該社團
     *
     * @param groupId 社團Id
     * @param groupApplyStatusParam 審核狀態參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/GroupApply/group/{groupId}/Approval")
    suspend fun apiV1GroupApplyGroupGroupIdApprovalPut(@Path("groupId") groupId: kotlin.String, @Body groupApplyStatusParam: GroupApplyStatusParam? = null): Response<Unit>

    /**
     * 查詢未處理的入社申請筆數 __________🔒 審核入社申請
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 
     * @param applyStauts  (optional)
     * @return [CountResult]
     */
    @GET("api/v1/GroupApply/group/{groupId}/Count")
    suspend fun apiV1GroupApplyGroupGroupIdCountGet(@Path("groupId") groupId: kotlin.String, @Query("applyStauts") applyStauts: ApplyStatus? = null): Response<CountResult>

    /**
     * 取得社團申請清單 __________🔒 審核入社申請
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該社團
     *  - 409: 社團id 不合法
     *
     * @param groupId 社團id
     * @param applyStauts 申請狀態 (optional)
     * @param startWeight 起始權重 (optional, default to 0L)
     * @param pageSize 每頁筆數 (optional, default to 20)
     * @return [GroupRequirementApplyPaging]
     */
    @GET("api/v1/GroupApply/group/{groupId}")
    suspend fun apiV1GroupApplyGroupGroupIdGet(@Path("groupId") groupId: kotlin.String, @Query("applyStauts") applyStauts: ApplyStatus? = null, @Query("startWeight") startWeight: kotlin.Long? = 0L, @Query("pageSize") pageSize: kotlin.Int? = 20): Response<GroupRequirementApplyPaging>

    /**
     * 取得我的社團申請 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該社團
     *
     * @param groupId 社團Id
     * @return [GroupRequirementApplyInfo]
     */
    @GET("api/v1/GroupApply/group/{groupId}/me")
    suspend fun apiV1GroupApplyGroupGroupIdMeGet(@Path("groupId") groupId: kotlin.String): Response<GroupRequirementApplyInfo>

    /**
     * 新增社團申請 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 409: Conflict
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 204: 成功
     *  - 404: 找不到該社團
     *
     * @param groupId 社團Id
     * @param groupApplyParam 社團申請參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/GroupApply/group/{groupId}")
    suspend fun apiV1GroupApplyGroupGroupIdPut(@Path("groupId") groupId: kotlin.String, @Body groupApplyParam: GroupApplyParam? = null): Response<Unit>

}
