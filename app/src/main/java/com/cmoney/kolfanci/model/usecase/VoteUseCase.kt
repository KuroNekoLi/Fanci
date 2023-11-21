package com.cmoney.kolfanci.model.usecase

import com.cmoney.fanciapi.fanci.api.VotingApi
import com.cmoney.fanciapi.fanci.model.CastVoteParam
import com.cmoney.fanciapi.fanci.model.IVotingOption
import com.cmoney.fanciapi.fanci.model.VotingParam
import com.cmoney.kolfanci.extension.checkResponseBody
import com.cmoney.kolfanci.model.vote.VoteModel
import com.socks.library.KLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 投票相關
 */
class VoteUseCase(
    private val votingApi: VotingApi
) {
    private val TAG = VoteUseCase::class.java.simpleName

    /**
     * 建立多筆 投票
     *
     * @param channelId 頻道 id
     * @param voteModels 投票 modes
     */
    suspend fun createVotes(channelId: String, voteModels: List<VoteModel>): Flow<VoteModel> =
        flow {
            voteModels.forEach { voteModel ->
                try {
                    val votingId = createVote(
                        channelId = channelId,
                        voteModel = voteModel
                    )
                    emit(
                        voteModel.copy(id = votingId.toString())
                    )
                } catch (e: Exception) {
                    KLog.e(TAG, e)
                }
            }
        }

    /**
     * 建立投票
     *
     * @param channelId 頻道id
     * @param voteModel 投票model
     */
    private suspend fun createVote(channelId: String, voteModel: VoteModel): Long {
        KLog.i(TAG, "createVote:$voteModel")

        val votingParam = VotingParam(
            title = voteModel.question,
            votingOptions = voteModel.choice.map {
                IVotingOption(text = it)
            },
            isMultipleChoice = !voteModel.isSingleChoice,
            isAnonymous = false
        )

        return votingApi.apiV1VotingChannelIdChannelIdPost(
            channelId = channelId,
            votingParam = votingParam
        ).checkResponseBody()
    }

    /**
     * 刪除投票
     * @param channelId 頻道id
     * @param voteId 投票id
     */
    suspend fun deleteVote(channelId: String, voteId: Long) =
        votingApi.apiV1VotingChannelIdChannelIdDelete(
            channelId = channelId,
            requestBody = listOf(voteId)
        ).checkResponseBody()

    /**
     * 頻道投票動作
     *
     * @param channelId 頻道 id
     * @param votingId 投票 id
     * @param choice 所選擇的項目
     */
    suspend fun choiceVote(
        channelId: String, votingId: String, choice: List<Int>
    ) =
        votingApi.apiV1VotingVotingIdChannelIdChannelIdCastVotePost(
            channelId = channelId,
            votingId = votingId,
            castVoteParam = CastVoteParam(
                choice
            )
        ).checkResponseBody()

    /**
     * 投票 統計
     *
     * @param channelId 頻道 id
     * @param votingId 投票 id
     */
    suspend fun summaryVote(channelId: String, votingId: Long) =
        votingApi.apiV1VotingVotingIdChannelIdChannelIdStatisticsGet(
            channelId = channelId,
            votingId = votingId
        ).checkResponseBody()
}