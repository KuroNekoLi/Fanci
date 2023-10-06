package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.BulletinboardMessagePaging
import com.cmoney.fanciapi.fanci.model.BulletingBoardMessageParam
import com.cmoney.fanciapi.fanci.model.MessageIdParam
import com.cmoney.fanciapi.fanci.model.OrderType
import com.cmoney.fanciapi.fanci.model.PinnedMessageInfo

interface BulletinBoardApi {
    /**
     * 取得貼文區貼文列表 __________🔒 可看
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該頻道
     *
     * @param channelId 貼文區頻道Id
     * @param take 取得筆數 (optional, default to 20)
     * @param order 排序依據(預設為Latest) &lt;br&gt;&lt;/br&gt;Latest:代表從SerialNumber往前找 SerialNumber預設0 代表從最新往舊找,&lt;br&gt;&lt;/br&gt;Oldest:代表從SerialNumber往後找 SerialNumber預設0 代表從舊往新找&lt;br&gt;&lt;/br&gt;每次回傳weight為當下已經取得的最底一筆SerialNumber&lt;br&gt;&lt;/br&gt;所以下輪帶入SerialNumber並不會再包含該筆資料 (optional)
     * @param fromSerialNumber 從哪一個序列號開始往回找 (optional)
     * @return [BulletinboardMessagePaging]
     */
    @GET("api/v1/BulletinBoard/{channelId}/Message")
    suspend fun apiV1BulletinBoardChannelIdMessageGet(@Path("channelId") channelId: kotlin.String, @Query("take") take: kotlin.Int? = 20, @Query("order") order: OrderType? = null, @Query("fromSerialNumber") fromSerialNumber: kotlin.Long? = null): Response<BulletinboardMessagePaging>

    /**
     * 對貼文新增一則 留言 / 回覆 __________🔒 CanReply
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 頻道
     * @param messageId 訊息
     * @param bulletingBoardMessageParam BulletingBoardMessageParam (optional)
     * @return [BulletinboardMessage]
     */
    @POST("api/v1/BulletinBoard/{channelId}/Message/{messageId}/Comment")
    suspend fun apiV1BulletinBoardChannelIdMessageMessageIdCommentPost(@Path("channelId") channelId: kotlin.String, @Path("messageId") messageId: kotlin.String, @Body bulletingBoardMessageParam: BulletingBoardMessageParam? = null): Response<BulletinboardMessage>

    /**
     * 取得貼文的 留言 / 回覆 __________🔒 可看
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 頻道ID
     * @param messageId 貼文ID
     * @param take 取得筆數 (optional, default to 20)
     * @param order 排序方式 (optional)
     * @param fromSerialNumber  (optional)
     * @return [BulletinboardMessagePaging]
     */
    @GET("api/v1/BulletinBoard/{channelId}/Message/{messageId}/Comments")
    suspend fun apiV1BulletinBoardChannelIdMessageMessageIdCommentsGet(@Path("channelId") channelId: kotlin.String, @Path("messageId") messageId: kotlin.String, @Query("take") take: kotlin.Int? = 20, @Query("order") order: OrderType? = null, @Query("fromSerialNumber") fromSerialNumber: kotlin.Long? = null): Response<BulletinboardMessagePaging>

    /**
     * 對貼文區新增一則貼文 __________🔒 可發文
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該頻道
     *
     * @param channelId 頻道Id
     * @param bulletingBoardMessageParam 訊息參數 (optional)
     * @return [BulletinboardMessage]
     */
    @POST("api/v1/BulletinBoard/{channelId}/Message")
    suspend fun apiV1BulletinBoardChannelIdMessagePost(@Path("channelId") channelId: kotlin.String, @Body bulletingBoardMessageParam: BulletingBoardMessageParam? = null): Response<BulletinboardMessage>

    /**
     * 取消貼文區公告 __________🔒 可管理
     * 
     * Responses:
     *  - 200: Success
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 204: 成功
     *  - 404: 找不到該頻道
     *
     * @param channelId 頻道Id
     * @return [Unit]
     */
    @DELETE("api/v1/BulletinBoard/{channelId}/PinnedMessage")
    suspend fun apiV1BulletinBoardChannelIdPinnedMessageDelete(@Path("channelId") channelId: kotlin.String): Response<Unit>

    /**
     * 取得貼文區的公告訊息 __________🔒 可看
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該頻道
     *
     * @param channelId 頻道Id
     * @return [PinnedMessageInfo]
     */
    @GET("api/v1/BulletinBoard/{channelId}/PinnedMessage")
    suspend fun apiV1BulletinBoardChannelIdPinnedMessageGet(@Path("channelId") channelId: kotlin.String): Response<PinnedMessageInfo>

    /**
     * 公告貼文區的一則訊息 __________🔒 可管理
     * 
     * Responses:
     *  - 200: Success
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 204: 成功
     *  - 404: 找不到該頻道
     *
     * @param channelId 貼文區頻道Id
     * @param messageIdParam 公告訊息參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/BulletinBoard/{channelId}/PinnedMessage")
    suspend fun apiV1BulletinBoardChannelIdPinnedMessagePut(@Path("channelId") channelId: kotlin.String, @Body messageIdParam: MessageIdParam? = null): Response<Unit>

    /**
     * 清空使用者貼文未讀數 __________🔒 可看
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 頻道ID
     * @return [Unit]
     */
    @PUT("api/v1/BulletinBoard/{channelId}/ResetUnreadCount")
    suspend fun apiV1BulletinBoardChannelIdResetUnreadCountPut(@Path("channelId") channelId: kotlin.String): Response<Unit>

}
