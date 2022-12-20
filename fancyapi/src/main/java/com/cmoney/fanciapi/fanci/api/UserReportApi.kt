package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.ReportParm

interface UserReportApi {
    /**
     * 檢舉某頻道的內容 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 204: 成功
     *
     * @param channelId 
     * @param reportParm  (optional)
     * @return [Unit]
     */
    @Deprecated("This api was deprecated")
    @PUT("api/v1/UserReport/Channel/{channelId}")
    suspend fun apiV1UserReportChannelChannelIdPut(@Path("channelId") channelId: kotlin.String, @Body reportParm: ReportParm? = null): Response<Unit>

    /**
     * 取得社團的檢舉審核清單 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 
     * @return [Unit]
     */
    @Deprecated("This api was deprecated")
    @GET("api/v1/UserReport/Group/{groupId}")
    suspend fun apiV1UserReportGroupGroupIdGet(@Path("groupId") groupId: kotlin.String): Response<Unit>

}
