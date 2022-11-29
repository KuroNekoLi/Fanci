package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.OrderParam

interface OrderApi {
    /**
     * 調整群組/頻道排序 __________🔒 重新排列
     * 
     * Responses:
     *  - 200: Success
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 204: 成功
     *
     * @param groupId 社團Id
     * @param orderParam 排序參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Order/Group/{groupId}")
    suspend fun apiV1OrderGroupGroupIdPut(@Path("groupId") groupId: kotlin.String, @Body orderParam: OrderParam? = null): Response<Unit>

}
