package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.ColorTheme
import com.cmoney.fanciapi.fanci.model.Theme

interface ThemeColorApi {
    /**
     * 取得特定主題色卡包
     * 
     * Responses:
     *  - 200: Success
     *
     * @param colorTheme 
     * @return [Theme]
     */
    @GET("api/v1/ThemeColor/{colorTheme}")
    suspend fun apiV1ThemeColorColorThemeGet(@Path("colorTheme") colorTheme: ColorTheme): Response<Theme>

    /**
     * 取得所有主題色卡包
     * 
     * Responses:
     *  - 200: Success
     *
     * @return [kotlin.collections.Map<kotlin.String, Theme>]
     */
    @GET("api/v1/ThemeColor")
    suspend fun apiV1ThemeColorGet(): Response<kotlin.collections.Map<kotlin.String, Theme>>

}
