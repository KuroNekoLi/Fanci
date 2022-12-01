package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.Color

interface ThemeColorApi {
    /**
     * 取得所有主題色卡包
     * 
     * Responses:
     *  - 200: Success
     *
     * @return [kotlin.collections.Map<kotlin.String, kotlin.collections.List<Color>>]
     */
    @GET("api/v1/ThemeColor")
    suspend fun apiV1ThemeColorGet(): Response<kotlin.collections.Map<kotlin.String, kotlin.collections.List<Color>>>

}
