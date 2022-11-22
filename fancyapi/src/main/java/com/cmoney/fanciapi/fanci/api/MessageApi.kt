package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.ChatMessageParam
import com.cmoney.fanciapi.fanci.model.EmojiParam

interface MessageApi {
    /**
     * 刪除/收回 訊息內容
     * 
     * Responses:
     *  - 204: No Content
     *  - 403: Forbidden
     *  - 404: Not Found
     *  - 401: Unauthorized
     *
     * @param messageId 
     * @return [Unit]
     */
    @DELETE("api/v1/Message/{messageId}")
    suspend fun apiV1MessageMessageIdDelete(@Path("messageId") messageId: kotlin.String): Response<Unit>

    /**
     * 收回表情符號
     * 
     * Responses:
     *  - 204: No Content
     *  - 403: Forbidden
     *  - 404: Not Found
     *  - 401: Unauthorized
     *
     * @param messageId 
     * @return [Unit]
     */
    @DELETE("api/v1/Message/{messageId}/Emoji")
    suspend fun apiV1MessageMessageIdEmojiDelete(@Path("messageId") messageId: kotlin.String): Response<Unit>

    /**
     * 對訊息新增表情符號
     * 
     * Responses:
     *  - 204: No Content
     *  - 403: Forbidden
     *  - 404: Not Found
     *  - 401: Unauthorized
     *
     * @param messageId 
     * @param emojiParam 表情符號參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Message/{messageId}/Emoji")
    suspend fun apiV1MessageMessageIdEmojiPut(@Path("messageId") messageId: kotlin.String, @Body emojiParam: EmojiParam? = null): Response<Unit>

    /**
     * 取得單一訊息
     * 
     * Responses:
     *  - 200: Success
     *  - 404: Not Found
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param messageId 訊息id
     * @return [ChatMessage]
     */
    @GET("api/v1/Message/{messageId}")
    suspend fun apiV1MessageMessageIdGet(@Path("messageId") messageId: kotlin.String): Response<ChatMessage>

    /**
     * 編輯訊息內容
     * 
     * Responses:
     *  - 204: No Content
     *  - 403: Forbidden
     *  - 404: Not Found
     *  - 401: Unauthorized
     *
     * @param messageId 編輯的訊息ID
     * @param chatMessageParam 異動訊息參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Message/{messageId}")
    suspend fun apiV1MessageMessageIdPut(@Path("messageId") messageId: kotlin.String, @Body chatMessageParam: ChatMessageParam? = null): Response<Unit>

}
