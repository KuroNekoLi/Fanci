package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.CategoryParam
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChannelPaging
import com.cmoney.fanciapi.fanci.model.ChannelParam

interface CategoryApi {
    /**
     * 取得分類下的頻道
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該分類
     *
     * @param categoryId 分類id
     * @param startWeight 起始權重 (optional, default to 0L)
     * @param pageSize 分頁筆數 (optional, default to 20)
     * @return [ChannelPaging]
     */
    @GET("api/v1/Category/{categoryId}/Channel")
    suspend fun apiV1CategoryCategoryIdChannelGet(@Path("categoryId") categoryId: kotlin.String, @Query("startWeight") startWeight: kotlin.Long? = 0L, @Query("pageSize") pageSize: kotlin.Int? = 20): Response<ChannelPaging>

    /**
     * 在分類下新增頻道 __________🔒 建立頻道
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該分類
     *
     * @param categoryId 分類id
     * @param channelParam 新增頻道參數 (optional)
     * @return [Channel]
     */
    @POST("api/v1/Category/{categoryId}/Channel")
    suspend fun apiV1CategoryCategoryIdChannelPost(@Path("categoryId") categoryId: kotlin.String, @Body channelParam: ChannelParam? = null): Response<Channel>

    /**
     * 刪除分類 __________🔒 刪除分類
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該分類
     *
     * @param categoryId 分類Id
     * @return [Unit]
     */
    @DELETE("api/v1/Category/{categoryId}")
    suspend fun apiV1CategoryCategoryIdDelete(@Path("categoryId") categoryId: kotlin.String): Response<Unit>

    /**
     * 取得特定分類
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該分類
     *
     * @param categoryId 分類id
     * @return [Category]
     */
    @GET("api/v1/Category/{categoryId}")
    suspend fun apiV1CategoryCategoryIdGet(@Path("categoryId") categoryId: kotlin.String): Response<Category>

    /**
     * 重命名分類 __________🔒 編輯分類
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該分類
     *
     * @param categoryId 分類Id
     * @param categoryParam 分類參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Category/{categoryId}/Name")
    suspend fun apiV1CategoryCategoryIdNamePut(@Path("categoryId") categoryId: kotlin.String, @Body categoryParam: CategoryParam? = null): Response<Unit>

}
