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
import com.cmoney.fanciapi.fanci.model.OrderType
import com.cmoney.fanciapi.fanci.model.PinnedMessageInfo

interface ChatRoomApi {
    /**
     * 取得聊天室訊息列表 __________🔒 可看
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該頻道
     *
     * @param chatRoomChannelId 聊天室頻道Id
     * @param take 取得筆數 (optional, default to 20)
     * @param order 排序依據(預設為Latest) &lt;br&gt;&lt;/br&gt;Latest:代表從SerialNumber往前找 SerialNumber預設0 代表從最新往舊找,&lt;br&gt;&lt;/br&gt;Oldest:代表從SerialNumber往後找 SerialNumber預設0 代表從舊往新找&lt;br&gt;&lt;/br&gt;每次回傳weight為當下已經取得的最底一筆SerialNumber&lt;br&gt;&lt;/br&gt;所以下輪帶入SerialNumber並不會再包含該筆資料 (optional)
     * @param fromSerialNumber 從哪一個序列號開始往回找 (optional)
     * @return [ChatMessagePaging]
     */
    @GET("api/v1/ChatRoom/{chatRoomChannelId}/Message")
    suspend fun apiV1ChatRoomChatRoomChannelIdMessageGet(@Path("chatRoomChannelId") chatRoomChannelId: kotlin.String, @Query("take") take: kotlin.Int? = 20, @Query("order") order: OrderType? = null, @Query("fromSerialNumber") fromSerialNumber: kotlin.Long? = null): Response<ChatMessagePaging>

    /**
     * 對聊天室新增一則聊天訊息 __________🔒 可發文
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該頻道
     *
     * @param chatRoomChannelId 聊天室頻道Id
     * @param chatMessageParam 訊息參數 (optional)
     * @return [ChatMessage]
     */
    @POST("api/v1/ChatRoom/{chatRoomChannelId}/Message")
    suspend fun apiV1ChatRoomChatRoomChannelIdMessagePost(@Path("chatRoomChannelId") chatRoomChannelId: kotlin.String, @Body chatMessageParam: ChatMessageParam? = null): Response<ChatMessage>

    /**
     * 取消聊天室公告 __________🔒 可管理
     * 
     * Responses:
     *  - 200: Success
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 204: 成功
     *  - 404: 找不到該頻道
     *
     * @param chatRoomChannelId 聊天室頻道Id
     * @return [Unit]
     */
    @DELETE("api/v1/ChatRoom/{chatRoomChannelId}/PinnedMessage")
    suspend fun apiV1ChatRoomChatRoomChannelIdPinnedMessageDelete(@Path("chatRoomChannelId") chatRoomChannelId: kotlin.String): Response<Unit>

    /**
     * 取得聊天室的公告訊息 __________🔒 可看
     * 
     * Responses:
     *  - 200: 成功
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 404: 找不到該頻道
     *
     * @param chatRoomChannelId 聊天室頻道Id
     * @return [PinnedMessageInfo]
     */
    @GET("api/v1/ChatRoom/{chatRoomChannelId}/PinnedMessage")
    suspend fun apiV1ChatRoomChatRoomChannelIdPinnedMessageGet(@Path("chatRoomChannelId") chatRoomChannelId: kotlin.String): Response<PinnedMessageInfo>

    /**
     * 公告聊天室的一則聊天訊息 __________🔒 可管理
     * 
     * Responses:
     *  - 200: Success
     *  - 401: 未驗證
     *  - 403: 沒有權限
     *  - 204: 成功
     *  - 404: 找不到該頻道
     *
     * @param chatRoomChannelId 聊天室頻道Id
     * @param messageIdParam 公告訊息參數 (optional)
     * @return [Unit]
     */
    @PUT("api/v1/ChatRoom/{chatRoomChannelId}/PinnedMessage")
    suspend fun apiV1ChatRoomChatRoomChannelIdPinnedMessagePut(@Path("chatRoomChannelId") chatRoomChannelId: kotlin.String, @Body messageIdParam: MessageIdParam? = null): Response<Unit>

    /**
     * 清空使用者聊天室未讀數 __________🔒 可看
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param chatRoomChannelId 
     * @return [Unit]
     */
    @PUT("api/v1/ChatRoom/{chatRoomChannelId}/ResetUnreadCount")
    suspend fun apiV1ChatRoomChatRoomChannelIdResetUnreadCountPut(@Path("chatRoomChannelId") chatRoomChannelId: kotlin.String): Response<Unit>

}
