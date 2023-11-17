package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.CastVoteParam
import com.cmoney.fanciapi.fanci.model.IVotingOptionStatisticsWithVoter
import com.cmoney.fanciapi.fanci.model.VotingParam

interface VotingApi {
    /**
     *  __________🔒 可發文
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 
     * @param requestBody  (optional)
     * @return [Unit]
     */
    @DELETE("api/v1/Voting/ChannelId/{channelId}")
    suspend fun apiV1VotingChannelIdChannelIdDelete(@Path("channelId") channelId: kotlin.String, @Body requestBody: kotlin.collections.List<kotlin.Long>? = null): Response<Unit>

    /**
     * 頻道創建投票 __________🔒 可發文
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 頻道ID (驗證權限)
     * @param votingParam 投票資訊 (optional)
     * @return [kotlin.Long]
     */
    @POST("api/v1/Voting/ChannelId/{channelId}")
    suspend fun apiV1VotingChannelIdChannelIdPost(@Path("channelId") channelId: kotlin.String, @Body votingParam: VotingParam? = null): Response<kotlin.Long>

    /**
     * 頻道投票 __________🔒 可看
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 頻道ID (驗證權限)
     * @param votingId 
     * @param castVoteParam  (optional)
     * @return [Unit]
     */
    @POST("api/v1/Voting/{VotingId}/ChannelId/{channelId}/CastVote")
    suspend fun apiV1VotingVotingIdChannelIdChannelIdCastVotePost(@Path("channelId") channelId: kotlin.String, @Path("VotingId") votingId: kotlin.String, @Body castVoteParam: CastVoteParam? = null): Response<Unit>

    /**
     *  __________🔒 可看
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param votingId 
     * @param channelId 
     * @return [kotlin.collections.List<IVotingOptionStatisticsWithVoter>]
     */
    @GET("api/v1/Voting/{votingId}/ChannelId/{channelId}/Statistics")
    suspend fun apiV1VotingVotingIdChannelIdChannelIdStatisticsGet(@Path("votingId") votingId: kotlin.Long, @Path("channelId") channelId: kotlin.String): Response<kotlin.collections.List<IVotingOptionStatisticsWithVoter>>

}
