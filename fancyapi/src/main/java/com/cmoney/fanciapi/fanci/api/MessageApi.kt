package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.ChatMessageParam
import com.cmoney.fanciapi.fanci.model.EmojiParam
import com.cmoney.fanciapi.fanci.model.MessageServiceType
import com.cmoney.fanciapi.fanci.model.OrderType
import com.cmoney.fanciapi.fanci.model.SearchMessageParam
import com.cmoney.fanciapi.fanci.model.User

interface MessageApi {
    /**
     * 收回我的訊息 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: 未驗證
     *  - 403: 沒有權限(代表不是自己發的訊息)
     *  - 204: 成功
     *  - 404: 找不到訊息
     *
     * @param messageId 訊息Id
     * @return [Unit]
     */
    @DELETE("api/v2/Message/me/{messageType}/{messageId}")
    suspend fun apiV2MessageMeMessageTypeMessageIdDelete(@Path("messageType") messageType: MessageServiceType, @Path("messageId") messageId: kotlin.String): Response<Unit>

    /**
     * 收回表情符號 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 204: 成功
     *  - 404: 找不到訊息
     *
     * @param messageType
     * @param messageId
     * @return [Unit]
     */
    @DELETE("api/v2/Message/{messageType}/{messageId}/Emoji")
    suspend fun apiV2MessageMessageTypeMessageIdEmojiDelete(@Path("messageType") messageType: MessageServiceType, @Path("messageId") messageId: kotlin.String): Response<Unit>

    /**
     * 取得訊息表情符號來自誰 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 204: 成功
     *  - 404: 找不到訊息
     *
     * @param messageType
     * @param messageId 訊息Id
     * @return [kotlin.collections.Map<kotlin.String, kotlin.collections.List<User>>]
     */
    @GET("api/v2/Message/{messageType}/{messageId}/Emoji")
    suspend fun apiV2MessageMessageTypeMessageIdEmojiGet(@Path("messageType") messageType: MessageServiceType, @Path("messageId") messageId: kotlin.String): Response<kotlin.collections.Map<kotlin.String, kotlin.collections.List<User>>>

    /**
     * 對訊息新增表情符號 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 204: 成功
     *  - 404: 找不到訊息
     *
     * @param messageType
     * @param messageId 訊息Id
     * @param emojiParam 表情符號參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v2/Message/{messageType}/{messageId}/Emoji")
    suspend fun apiV2MessageMessageTypeMessageIdEmojiPut(@Path("messageType") messageType: MessageServiceType, @Path("messageId") messageId: kotlin.String, @Body emojiParam: EmojiParam? = null): Response<Unit>

    /**
     * 取得單一訊息 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到訊息
     *
     * @param messageType
     * @param messageId 訊息id
     * @return [ChatMessage]
     */
    @GET("api/v2/Message/{messageType}/{messageId}")
    suspend fun apiV2MessageMessageTypeMessageIdGet(@Path("messageType") messageType: MessageServiceType, @Path("messageId") messageId: kotlin.String): Response<ChatMessage>

    /**
     * 編輯訊息內容 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: 未驗證
     *  - 403: 不是發文者
     *  - 204: 成功
     *  - 404: 找不到訊息
     *
     * @param messageType
     * @param messageId 訊息Id
     * @param chatMessageParam 異動訊息參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v2/Message/{messageType}/{messageId}")
    suspend fun apiV2MessageMessageTypeMessageIdPut(@Path("messageType") messageType: MessageServiceType, @Path("messageId") messageId: kotlin.String, @Body chatMessageParam: ChatMessageParam? = null): Response<Unit>

    /**
     * 角色刪除他人訊息 __________🔒 可管理
     * 
     * Responses:
     *  - 200: Success
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 204: 成功
     *  - 404: 找不到訊息
     *
     * @param messageType
     * @param messageId 訊息Id
     * @return [Unit]
     */
    @DELETE("api/v2/Message/role/{messageType}/{messageId}")
    suspend fun apiV2MessageRoleMessageTypeMessageIdDelete(@Path("messageType") messageType: MessageServiceType, @Path("messageId") messageId: kotlin.String): Response<Unit>

    /**
     * 搜尋功能  代入自訂條件搜尋訊息 全部條件取\&quot;交集\&quot; 不需要的條件請移除   有範圍小的條件可以不用代範圍大的條件 (如 有Channel可以不用Group)  TODO:貼文回覆先不要 等前端確定 (聊天回復不影響) __________🔒 已註冊的fanci使用者
     *
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param order  (optional)
     * @param offset 從第幾筆搜尋結果開始取 (optional, default to 0)
     * @param fetch 共取幾筆搜尋結果 (optional, default to 10)
     * @param searchMessageParam  (optional)
     * @return [kotlin.collections.List<ChatMessage>]
     */
    @POST("api/v2/Message/Search")
    suspend fun apiV2MessageSearchPost(@Query("order") order: OrderType? = null, @Query("offset") offset: kotlin.Int? = 0, @Query("fetch") fetch: kotlin.Int? = 10, @Body searchMessageParam: SearchMessageParam? = null): Response<kotlin.collections.List<ChatMessage>>

}
