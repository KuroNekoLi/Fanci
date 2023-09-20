package com.cmoney.kolfanci.model

/**
 * User 對於該社團狀態
 */
sealed class GroupJoinStatus {
    /**
     * 已加入
     */
    object Joined : GroupJoinStatus()

    /**
     * 未加入
     */
    object NotJoin : GroupJoinStatus()

    /**
     * 審核中
     */
    object InReview : GroupJoinStatus()
}