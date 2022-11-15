package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.OrderParam

interface OrderApi {
    /**
     * 調整群組/頻道排序
     * 
     * Responses:
     *  - 200: Success
     *
     * @param orderParam  (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Order")
    suspend fun apiV1OrderPut(@Body orderParam: OrderParam? = null): Response<Unit>

}
