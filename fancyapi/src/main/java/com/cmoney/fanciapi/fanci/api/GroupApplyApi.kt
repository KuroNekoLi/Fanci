package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.ApplyStatus
import com.cmoney.fanciapi.fanci.model.GroupApplyParam
import com.cmoney.fanciapi.fanci.model.GroupApplyStatusParam

interface GroupApplyApi {
    /**
     * 取得社團申請清單
     * 
     * Responses:
     *  - 409: Conflict
     *
     * @param groupId 社團id
     * @param applyStauts 申請狀態 (optional)
     * @param startWeight  (optional)
     * @param pageSize  (optional, default to 20)
     * @return [Unit]
     */
    @GET("api/v1/GroupApply/group/{groupId}")
    suspend fun apiV1GroupApplyGroupGroupIdGet(@Path("groupId") groupId: kotlin.String, @Query("applyStauts") applyStauts: ApplyStatus? = null, @Query("startWeight") startWeight: kotlin.Long? = null, @Query("pageSize") pageSize: kotlin.Int? = 20): Response<Unit>

    /**
     * 取得我的社團申請
     * 
     * Responses:
     *  - 409: Conflict
     *  - 404: Not Found
     *
     * @param groupId 社團id
     * @return [Unit]
     */
    @GET("api/v1/GroupApply/group/{groupId}/me")
    suspend fun apiV1GroupApplyGroupGroupIdMeGet(@Path("groupId") groupId: kotlin.String): Response<Unit>

    /**
     * 新增社團申請
     * 
     * Responses:
     *  - 409: Conflict
     *
     * @param groupId 社團id
     * @param groupApplyParam 社團申請參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/GroupApply/group/{groupId}")
    suspend fun apiV1GroupApplyGroupGroupIdPut(@Path("groupId") groupId: kotlin.String, @Body groupApplyParam: GroupApplyParam? = null): Response<Unit>

    /**
     * 更新審核狀態
     * 
     * Responses:
     *  - 409: Conflict
     *
     * @param id 申請id
     * @param groupApplyStatusParam 審核狀態參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/GroupApply/{id}")
    suspend fun apiV1GroupApplyIdPut(@Path("id") id: kotlin.String, @Body groupApplyStatusParam: GroupApplyStatusParam? = null): Response<Unit>

}
