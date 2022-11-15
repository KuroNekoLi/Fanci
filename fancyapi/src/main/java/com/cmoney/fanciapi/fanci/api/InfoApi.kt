package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json


interface InfoApi {
    /**
     * 密碼是 xlabxlab
     * 
     * Responses:
     *  - 200: Success
     *
     * @param passcode  (optional, default to "1234")
     * @return [Unit]
     */
    @GET("api/v1/Info")
    suspend fun apiV1InfoGet(@Query("passcode") passcode: kotlin.String? = "1234"): Response<Unit>

}
