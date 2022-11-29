package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.GroupMemberPaging

interface GroupMemberApi {
    /**
     * 取得社團會員清單 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *
     * @param groupId 社團id
     * @param skip 跳脫筆數 (optional, default to 0)
     * @param take 取得筆數 (optional, default to 20)
     * @return [GroupMemberPaging]
     */
    @GET("api/v1/GroupMember/group/{groupId}")
    suspend fun apiV1GroupMemberGroupGroupIdGet(@Path("groupId") groupId: kotlin.String, @Query("skip") skip: kotlin.Int? = 0, @Query("take") take: kotlin.Int? = 20): Response<GroupMemberPaging>

    /**
     * 離開社團 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 社長不得離開社團
     *
     * @param groupId 社團Id
     * @return [Unit]
     */
    @DELETE("api/v1/GroupMember/group/{groupId}/me")
    suspend fun apiV1GroupMemberGroupGroupIdMeDelete(@Path("groupId") groupId: kotlin.String): Response<Unit>

    /**
     * 加入社團 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 該社團須申請審核
     *
     * @param groupId 社團Id
     * @return [Unit]
     */
    @PUT("api/v1/GroupMember/group/{groupId}/me")
    suspend fun apiV1GroupMemberGroupGroupIdMePut(@Path("groupId") groupId: kotlin.String): Response<Unit>

}
