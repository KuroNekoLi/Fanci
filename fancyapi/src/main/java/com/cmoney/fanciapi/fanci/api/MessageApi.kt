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
     * 收回表情符號 __________🔒 可看
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到訊息
     *
     * @param messageId 
     * @return [Unit]
     */
    @DELETE("api/v1/Message/{messageId}/Emoji")
    suspend fun apiV1MessageMessageIdEmojiDelete(@Path("messageId") messageId: kotlin.String): Response<Unit>

    /**
     * 對訊息新增表情符號 __________🔒 可看
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到訊息
     *
     * @param messageId 訊息Id
     * @param emojiParam 表情符號參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Message/{messageId}/Emoji")
    suspend fun apiV1MessageMessageIdEmojiPut(@Path("messageId") messageId: kotlin.String, @Body emojiParam: EmojiParam? = null): Response<Unit>

    /**
     * 取得單一訊息 __________🔒 可看
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到訊息
     *
     * @param messageId 訊息id
     * @return [ChatMessage]
     */
    @GET("api/v1/Message/{messageId}")
    suspend fun apiV1MessageMessageIdGet(@Path("messageId") messageId: kotlin.String): Response<ChatMessage>

    /**
     * 編輯訊息內容 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 204: 成功
     *  - 401: 未驗證
     *  - 403: 不是發文者
     *  - 404: 找不到訊息
     *
     * @param messageId 訊息Id
     * @param chatMessageParam 異動訊息參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Message/{messageId}")
    suspend fun apiV1MessageMessageIdPut(@Path("messageId") messageId: kotlin.String, @Body chatMessageParam: ChatMessageParam? = null): Response<Unit>

}
