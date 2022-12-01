package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.DefaultImageResource

interface DefaultImageApi {
    /**
     * 取得預設圖片
     * 
     * Responses:
     *  - 200: Success
     *
     * @return [DefaultImageResource]
     */
    @GET("api/v1/DefaultImage")
    suspend fun apiV1DefaultImageGet(): Response<DefaultImageResource>

}
