package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.User
import com.cmoney.fanciapi.fanci.model.UserParam

interface UserApi {
    /**
     * 取得使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 404: Not Found
     *
     * @param id 
     * @return [User]
     */
    @GET("api/v1/User/{id}")
    suspend fun apiV1UserIdGet(@Path("id") id: kotlin.String): Response<User>

    /**
     * 取得我的個人資訊
     * 
     * Responses:
     *  - 200: Success
     *  - 404: Not Found
     *
     * @return [User]
     */
    @GET("api/v1/User/me")
    suspend fun apiV1UserMeGet(): Response<User>

    /**
     * 更新或新增使用者
     * 
     * Responses:
     *  - 204: No Content
     *
     * @param userParam  (optional)
     * @return [Unit]
     */
    @PUT("api/v1/User/me")
    suspend fun apiV1UserMePut(@Body userParam: UserParam? = null): Response<Unit>

}
