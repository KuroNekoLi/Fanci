package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.GroupMemberPaging
import com.cmoney.fanciapi.fanci.model.UseridsParam

interface GroupMemberApi {
    /**
     * 取得社團會員清單
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *
     * @param groupId 社團id
     * @param search 搜尋用戶暱稱的關鍵字 (optional, default to "")
     * @param skip 跳脫筆數 (optional, default to 0)
     * @param take 取得筆數 (optional, default to 20)
     * @return [GroupMemberPaging]
     */
    @GET("api/v1/GroupMember/group/{groupId}")
    suspend fun apiV1GroupMemberGroupGroupIdGet(@Path("groupId") groupId: kotlin.String, @Query("search") search: kotlin.String? = "", @Query("skip") skip: kotlin.Int? = 0, @Query("take") take: kotlin.Int? = 20): Response<GroupMemberPaging>

    /**
     * 離開社團
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
     * 加入社團
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

    /**
     * 踢除成員離開社團 __________🔒 踢除
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *  - 409: 社長不得離開社團
     *
     * @param groupId 社團Id
     * @param userId 要剔除的成員id
     * @return [Unit]
     */
    @DELETE("api/v1/GroupMember/group/{groupId}/{userId}")
    suspend fun apiV1GroupMemberGroupGroupIdUserIdDelete(@Path("groupId") groupId: kotlin.String, @Path("userId") userId: kotlin.String): Response<Unit>

    /**
     * 取得特定社團會員清單
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到社團
     *
     * @param groupId 社團id
     * @param useridsParam 會員ids (optional)
     * @return [kotlin.collections.List<GroupMember>]
     */
    @GET("api/v1/GroupMember/Group/{groupId}/Users")
    suspend fun apiV1GroupMemberGroupGroupIdUsersGet(@Path("groupId") groupId: kotlin.String, @Body useridsParam: UseridsParam? = null): Response<kotlin.collections.List<GroupMember>>

}
