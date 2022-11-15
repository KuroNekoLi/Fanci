package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fanciapi.fanci.model.ChatMessagePaging
import com.cmoney.fanciapi.fanci.model.ChatMessageParam
import com.cmoney.fanciapi.fanci.model.MessageIdParam
import com.cmoney.fanciapi.fanci.model.ReadStatus
import com.cmoney.fanciapi.fanci.model.ReadStatusParam

interface ChatRoomApi {
    /**
     * 取得聊天室訊息列表
     * 
     * Responses:
     *  - 200: Success
     *  - 404: Not Found
     *
     * @param chatRoomChannelId 指定聊天室
     * @param take 取得比數 (optional, default to 20)
     * @param fromSerialNumber 從哪一個序列號開始往回找 (若為Null 則從最新開始拿) (optional)
     * @return [ChatMessagePaging]
     */
    @GET("api/v1/ChatRoom/{chatRoomChannelId}/Message")
    suspend fun apiV1ChatRoomChatRoomChannelIdMessageGet(@Path("chatRoomChannelId") chatRoomChannelId: kotlin.String, @Query("take") take: kotlin.Int? = 20, @Query("fromSerialNumber") fromSerialNumber: kotlin.Long? = null): Response<ChatMessagePaging>

    /**
     * 對聊天室新增/回應一則聊天訊息
     * 
     * Responses:
     *  - 200: Success
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param chatRoomChannelId 指定聊天室
     * @param chatMessageParam 訊息內容 (optional)
     * @return [ChatMessage]
     */
    @POST("api/v1/ChatRoom/{chatRoomChannelId}/Message")
    suspend fun apiV1ChatRoomChatRoomChannelIdMessagePost(@Path("chatRoomChannelId") chatRoomChannelId: kotlin.String, @Body chatMessageParam: ChatMessageParam? = null): Response<ChatMessage>

    /**
     * 取消置頂 聊天室的一則聊天訊息
     * 
     * Responses:
     *  - 204: No Content
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param chatRoomChannelId 指定聊天室
     * @return [Unit]
     */
    @DELETE("api/v1/ChatRoom/{chatRoomChannelId}/PinnedMessage")
    suspend fun apiV1ChatRoomChatRoomChannelIdPinnedMessageDelete(@Path("chatRoomChannelId") chatRoomChannelId: kotlin.String): Response<Unit>

    /**
     * 取得 聊天室的一則聊天訊息
     * 
     * Responses:
     *  - 204: No Content
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param chatRoomChannelId 指定聊天室
     * @return [Unit]
     */
    @GET("api/v1/ChatRoom/{chatRoomChannelId}/PinnedMessage")
    suspend fun apiV1ChatRoomChatRoomChannelIdPinnedMessageGet(@Path("chatRoomChannelId") chatRoomChannelId: kotlin.String): Response<Unit>

    /**
     * 置頂 聊天室的一則聊天訊息
     * 
     * Responses:
     *  - 204: No Content
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param chatRoomChannelId 指定聊天室
     * @param messageIdParam 要置頂的訊息id參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/ChatRoom/{chatRoomChannelId}/PinnedMessage")
    suspend fun apiV1ChatRoomChatRoomChannelIdPinnedMessagePut(@Path("chatRoomChannelId") chatRoomChannelId: kotlin.String, @Body messageIdParam: MessageIdParam? = null): Response<Unit>

    /**
     * 取得聊天室訊息已讀狀態
     * 
     * Responses:
     *  - 200: Success
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param chatRoomChannelId 指定聊天室
     * @return [ReadStatus]
     */
    @GET("api/v1/ChatRoom/{chatRoomChannelId}/ReadStatus")
    suspend fun apiV1ChatRoomChatRoomChannelIdReadStatusGet(@Path("chatRoomChannelId") chatRoomChannelId: kotlin.String): Response<ReadStatus>

    /**
     * 對聊天室訊息已讀
     * 
     * Responses:
     *  - 204: No Content
     *  - 403: Forbidden
     *  - 404: Not Found
     *
     * @param chatRoomChannelId 指定聊天室
     * @param readStatusParam 已讀參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/ChatRoom/{chatRoomChannelId}/ReadStatus")
    suspend fun apiV1ChatRoomChatRoomChannelIdReadStatusPut(@Path("chatRoomChannelId") chatRoomChannelId: kotlin.String, @Body readStatusParam: ReadStatusParam? = null): Response<Unit>

}
