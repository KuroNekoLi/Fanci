package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.ReportInformation
import com.cmoney.fanciapi.fanci.model.ReportParm
import com.cmoney.fanciapi.fanci.model.ReportStatusUpdateParam

interface UserReportApi {
    /**
     * 更新檢舉處理狀態 __________🔒 讀取被檢舉名單
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *  - 204: 成功
     *
     * @param channelId 
     * @param id 
     * @param reportStatusUpdateParam  (optional)
     * @return [Unit]
     */
    @PUT("api/v1/UserReport/Channel/{channelId}/{id}")
    suspend fun apiV1UserReportChannelChannelIdIdPut(@Path("channelId") channelId: kotlin.String, @Path("id") id: kotlin.String, @Body reportStatusUpdateParam: ReportStatusUpdateParam? = null): Response<Unit>

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
     * @return [ReportInformation]
     */
    @POST("api/v1/UserReport/Channel/{channelId}")
    suspend fun apiV1UserReportChannelChannelIdPost(@Path("channelId") channelId: kotlin.String, @Body reportParm: ReportParm? = null): Response<ReportInformation>

    /**
     * 取得社團的檢舉審核清單 __________🔒 讀取被檢舉名單
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 
     * @return [kotlin.collections.List<ReportInformation>]
     */
    @GET("api/v1/UserReport/Group/{groupId}")
    suspend fun apiV1UserReportGroupGroupIdGet(@Path("groupId") groupId: kotlin.String): Response<kotlin.collections.List<ReportInformation>>

}
