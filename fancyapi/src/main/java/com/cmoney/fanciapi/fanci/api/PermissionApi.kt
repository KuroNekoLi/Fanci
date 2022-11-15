package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.FanciPermission

interface PermissionApi {
    /**
     * 取得管理權限表
     * 
     * Responses:
     *  - 200: Success
     *
     * @return [kotlin.collections.List<FanciPermission>]
     */
    @GET("api/v1/Permission")
    suspend fun apiV1PermissionGet(): Response<kotlin.collections.List<FanciPermission>>

}
