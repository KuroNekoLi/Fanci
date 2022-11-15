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
     * 取得分類下頻道(有分頁)
     * 
     * Responses:
     *  - 200: Success
     *  - 404: Not Found
     *
     * @param categoryId 
     * @param startWeight  (optional, default to 0L)
     * @param pageSize  (optional, default to 20)
     * @return [ChannelPaging]
     */
    @GET("api/v1/Category/{categoryId}/Channel")
    suspend fun apiV1CategoryCategoryIdChannelGet(@Path("categoryId") categoryId: kotlin.String, @Query("startWeight") startWeight: kotlin.Long? = 0L, @Query("pageSize") pageSize: kotlin.Int? = 20): Response<ChannelPaging>

    /**
     * 在分類下新增頻道
     * 
     * Responses:
     *  - 200: Success
     *  - 404: Not Found
     *
     * @param categoryId 
     * @param channelParam  (optional)
     * @return [Channel]
     */
    @POST("api/v1/Category/{categoryId}/Channel")
    suspend fun apiV1CategoryCategoryIdChannelPost(@Path("categoryId") categoryId: kotlin.String, @Body channelParam: ChannelParam? = null): Response<Channel>

    /**
     * 刪除分類
     * 
     * Responses:
     *  - 204: No Content
     *  - 404: Not Found
     *
     * @param categoryId 
     * @return [Unit]
     */
    @DELETE("api/v1/Category/{categoryId}")
    suspend fun apiV1CategoryCategoryIdDelete(@Path("categoryId") categoryId: kotlin.String): Response<Unit>

    /**
     * 取得特定分類
     * 
     * Responses:
     *  - 200: Success
     *  - 404: Not Found
     *
     * @param categoryId 
     * @return [Category]
     */
    @GET("api/v1/Category/{categoryId}")
    suspend fun apiV1CategoryCategoryIdGet(@Path("categoryId") categoryId: kotlin.String): Response<Category>

    /**
     * 重命名分類
     * 
     * Responses:
     *  - 200: Success
     *  - 404: Not Found
     *
     * @param categoryId 
     * @param categoryParam  (optional)
     * @return [Category]
     */
    @PUT("api/v1/Category/{categoryId}/Name")
    suspend fun apiV1CategoryCategoryIdNamePut(@Path("categoryId") categoryId: kotlin.String, @Body categoryParam: CategoryParam? = null): Response<Category>

}
