package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.GroupMemberPaging

interface GroupMemberApi {
    /**
     * 取得社團會員清單
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 社團id
     * @param skip 跳脫筆數 (optional, default to 0)
     * @param take 取得筆數 (optional, default to 20)
     * @return [GroupMemberPaging]
     */
    @GET("api/v1/GroupMember/group/{groupId}")
    suspend fun apiV1GroupMemberGroupGroupIdGet(@Path("groupId") groupId: kotlin.String, @Query("skip") skip: kotlin.Int? = 0, @Query("take") take: kotlin.Int? = 20): Response<GroupMemberPaging>

    /**
     * 離開社團
     * 
     * Responses:
     *  - 204: No Content
     *  - 404: Not Found
     *  - 409: Conflict
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 
     * @return [Unit]
     */
    @DELETE("api/v1/GroupMember/group/{groupId}/me")
    suspend fun apiV1GroupMemberGroupGroupIdMeDelete(@Path("groupId") groupId: kotlin.String): Response<Unit>

    /**
     * 加入社團
     * 
     * Responses:
     *  - 204: No Content
     *  - 404: Not Found
     *  - 409: Conflict
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 
     * @return [Unit]
     */
    @PUT("api/v1/GroupMember/group/{groupId}/me")
    suspend fun apiV1GroupMemberGroupGroupIdMePut(@Path("groupId") groupId: kotlin.String): Response<Unit>

}
