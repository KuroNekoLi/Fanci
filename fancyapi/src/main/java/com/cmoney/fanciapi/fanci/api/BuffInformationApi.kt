package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.UserBuffInformation

interface BuffInformationApi {
    /**
     * 取得自己在頻道的Buff/Debuff狀態 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 
     * @return [UserBuffInformation]
     */
    @GET("api/v1/BuffInformation/Channel/{channelId}/me")
    suspend fun apiV1BuffInformationChannelChannelIdMeGet(@Path("channelId") channelId: kotlin.String): Response<UserBuffInformation>

    /**
     * 取得自己在社團的Buff/Debuff狀態 __________🔒 已註冊的fanci使用者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param groupId 
     * @return [UserBuffInformation]
     */
    @GET("api/v1/BuffInformation/Group/{groupId}/me")
    suspend fun apiV1BuffInformationGroupGroupIdMeGet(@Path("groupId") groupId: kotlin.String): Response<UserBuffInformation>

}
