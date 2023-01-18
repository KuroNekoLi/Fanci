package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.Relation
import com.cmoney.fanciapi.fanci.model.User
import com.cmoney.fanciapi.fanci.model.UserPaging

interface RelationApi {
    /**
     * 解除與用戶的關係 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param relation 
     * @param blockUserId 
     * @return [Unit]
     */
    @HTTP(method = "DELETE", path = "api/v1/Relation/{relation}/me/{blockUserId}", hasBody = true)
    suspend fun apiV1RelationRelationMeBlockUserIdDelete(@Path("relation") relation: Relation, @Path("blockUserId") blockUserId: kotlin.String): Response<Unit>

    /**
     * 增加與用戶的關係 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param relation 
     * @param blockUserId 
     * @return [User]
     */
    @PUT("api/v1/Relation/{relation}/me/{blockUserId}")
    suspend fun apiV1RelationRelationMeBlockUserIdPut(@Path("relation") relation: Relation, @Path("blockUserId") blockUserId: kotlin.String): Response<User>

    /**
     * 取得有此關係的用戶清單 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param relation 
     * @param skip  (optional, default to 0)
     * @param take  (optional, default to 20)
     * @return [UserPaging]
     */
    @GET("api/v1/Relation/{relation}/me")
    suspend fun apiV1RelationRelationMeGet(@Path("relation") relation: Relation, @Query("skip") skip: kotlin.Int? = 0, @Query("take") take: kotlin.Int? = 20): Response<UserPaging>

}
