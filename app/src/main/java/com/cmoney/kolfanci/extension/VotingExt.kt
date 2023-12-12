package com.cmoney.kolfanci.extension

import com.cmoney.fanciapi.fanci.model.IReplyVoting
import com.cmoney.fanciapi.fanci.model.IVotingOptionStatistic
import com.cmoney.fanciapi.fanci.model.Voting
import com.cmoney.kolfanci.model.vote.VoteModel

/**
 * 是否已經投過票
 */
fun Voting.isVoted(): Boolean = this.userVote?.selectedOptions?.isEmpty() == false

/**
 * 將 投票結果 轉換成 UI model
 */
fun List<IVotingOptionStatistic>.toPercentageList(): List<Pair<String, Float>> {
    val totalCount = this.sumOf {
        it.voteCount ?: 0
    }
    return this.map {
        it.text.orEmpty() to (it.voteCount?.div(totalCount.toFloat()) ?: 0f)
    }
}

fun List<Voting>.toVoteModelList(): List<VoteModel> {
    return this.map { voting ->
        VoteModel(
            id = voting.id.toString(),
            question = voting.title.orEmpty(),
            choice = voting.votingOptionStatistics?.map {
                it.text.orEmpty()
            } ?: emptyList(),
            isSingleChoice = (voting.isMultipleChoice == false)
        )
    }
}

fun List<VoteModel>.toVotingList(): List<Voting> {
    return this.map { voteModel ->
        Voting(
            id = voteModel.id,
            title = voteModel.question,
            votingOptionStatistics = voteModel.choice.map { choice ->
                IVotingOptionStatistic(
                    text = choice
                )
            },
            isMultipleChoice = !voteModel.isSingleChoice
        )
    }
}

/**
 * 轉為 回復用 model
 */
fun List<Voting>.toIReplyVotingList(): List<IReplyVoting> = this.map {
    IReplyVoting(
        id = it.id,
        title = it.title
    )
}