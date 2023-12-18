package com.cmoney.fanciapi.fanci.api

import com.cmoney.fanciapi.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.cmoney.fanciapi.fanci.model.CastVoteParam
import com.cmoney.fanciapi.fanci.model.DeleteVotingsParam
import com.cmoney.fanciapi.fanci.model.IVotingOptionStatisticWithVoter
import com.cmoney.fanciapi.fanci.model.VotingIdParam
import com.cmoney.fanciapi.fanci.model.VotingParam

interface VotingApi {
    /**
     * 刪除投票活動    非建立者不給刪 當有一個活動為非建立者 全部不動 然後回無權限
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 頻道ID (驗證權限) (optional)
     * @param deleteVotingsParam 刪除投票參數 (optional)
     * @return [Unit]
     */
    @HTTP(method = "DELETE", path = "api/v1/Voting", hasBody = true)
    suspend fun apiV1VotingDelete(@Query("channelId") channelId: kotlin.String? = null, @Body deleteVotingsParam: DeleteVotingsParam? = null): Response<Unit>

    /**
     * 頻道創建投票    要有canPost權限
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param channelId 頻道ID (驗證權限) (optional)
     * @param votingParam 投票資訊 (optional)
     * @return [VotingIdParam]
     */
    @POST("api/v1/Voting")
    suspend fun apiV1VotingPost(@Query("channelId") channelId: kotlin.String? = null, @Body votingParam: VotingParam? = null): Response<VotingIdParam>

    /**
     * 頻道投票    要有canRead權限  投不存在的Option會直接失敗
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param votingId 
     * @param channelId 頻道ID (驗證權限) (optional)
     * @param castVoteParam 投票 (optional)
     * @return [Unit]
     */
    @POST("api/v1/Voting/{votingId}/CastVote")
    suspend fun apiV1VotingVotingIdCastVotePost(@Path("votingId") votingId: kotlin.String, @Query("channelId") channelId: kotlin.String? = null, @Body castVoteParam: CastVoteParam? = null): Response<Unit>

    /**
     * 結束頻道投票  需要是創建者
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param votingId 投票ID
     * @param channelId 頻道ID (驗證權限) (optional)
     * @return [Unit]
     */
    @PUT("api/v1/Voting/{votingId}/End")
    suspend fun apiV1VotingVotingIdEndPut(@Path("votingId") votingId: kotlin.String, @Query("channelId") channelId: kotlin.String? = null): Response<Unit>

    /**
     * 取得投票活動數據    非建立者不給看
     * 
     * Responses:
     *  - 200: Success
     *  - 401: Unauthorized
     *  - 403: Forbidden
     *
     * @param votingId 
     * @param channelId  (optional)
     * @return [kotlin.collections.List<IVotingOptionStatisticWithVoter>]
     */
    @GET("api/v1/Voting/{votingId}/Statistics")
    suspend fun apiV1VotingVotingIdStatisticsGet(@Path("votingId") votingId: kotlin.String, @Query("channelId") channelId: kotlin.String? = null): Response<kotlin.collections.List<IVotingOptionStatisticWithVoter>>

}
