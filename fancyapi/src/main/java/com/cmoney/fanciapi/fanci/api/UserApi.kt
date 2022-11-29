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
     * 取得使用者 __________🔒 cmoney token
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到使用者
     *
     * @param id 使用者Id
     * @return [User]
     */
    @GET("api/v1/User/{id}")
    suspend fun apiV1UserIdGet(@Path("id") id: kotlin.String): Response<User>

    /**
     * 取得我的個人資訊 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到使用者
     *
     * @return [User]
     */
    @GET("api/v1/User/me")
    suspend fun apiV1UserMeGet(): Response<User>

    /**
     * 向fanci註冊使用者 __________🔒 cmoney token
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *
     * @param userParam 註冊參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/User/me")
    suspend fun apiV1UserMePut(@Body userParam: UserParam? = null): Response<Unit>

}
