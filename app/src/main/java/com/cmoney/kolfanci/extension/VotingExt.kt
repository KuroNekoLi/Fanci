package com.cmoney.kolfanci.extension

import com.cmoney.fanciapi.fanci.model.IVotingOptionStatistics
import com.cmoney.fanciapi.fanci.model.Voting

/**
 * 是否已經投過票
 */
fun Voting.isVoted(): Boolean = this.userVoteInfo?.selectedOptions?.isEmpty() == false

/**
 * 將 投票結果 轉換成 UI model
 */
fun List<IVotingOptionStatistics>.toPercentageList(): List<Pair<String, Float>> {
    val totalCount = this.sumOf {
        it.voteCount ?: 0
    }
    return this.map {
        it.text.orEmpty() to (it.voteCount?.div(totalCount.toFloat()) ?: 0f)
    }

}