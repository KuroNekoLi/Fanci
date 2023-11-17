/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package com.cmoney.fanciapi.fanci.model

import com.cmoney.fanciapi.fanci.model.IUserVoteInfo
import com.cmoney.fanciapi.fanci.model.IVotingOptionStatistics

import com.squareup.moshi.Json
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 
 *
 * @param id 投票活動Id
 * @param title 標題
 * @param votingOptionStatistics 選項
 * @param startUnixTime 開始投票時間
 * @param endUnixTime 投票截止時間
 * @param isMultipleChoice 是否能多選
 * @param maxChoiceCount 最大選擇數量
 * @param isAnonymous 是否匿名投票
 * @param isEnded 投票是否結束
 * @param votersCount 總投票人數
 * @param userVoteInfo 
 * @param isDeleted 是否已被刪除
 * @param createUnixTime 資料建立時間
 * @param updateUnixTime 資料更新時間
 */
@Parcelize


data class IVoting (

    /* 投票活動Id */
    @Json(name = "id")
    val id: kotlin.Long? = null,

    /* 標題 */
    @Json(name = "title")
    val title: kotlin.String? = null,

    /* 選項 */
    @Json(name = "votingOptionStatistics")
    val votingOptionStatistics: kotlin.collections.List<IVotingOptionStatistics>? = null,

    /* 開始投票時間 */
    @Json(name = "startUnixTime")
    val startUnixTime: kotlin.Long? = null,

    /* 投票截止時間 */
    @Json(name = "endUnixTime")
    val endUnixTime: kotlin.Long? = null,

    /* 是否能多選 */
    @Json(name = "isMultipleChoice")
    val isMultipleChoice: kotlin.Boolean? = null,

    /* 最大選擇數量 */
    @Json(name = "maxChoiceCount")
    val maxChoiceCount: kotlin.Int? = null,

    /* 是否匿名投票 */
    @Json(name = "isAnonymous")
    val isAnonymous: kotlin.Boolean? = null,

    /* 投票是否結束 */
    @Json(name = "isEnded")
    val isEnded: kotlin.Boolean? = null,

    /* 總投票人數 */
    @Json(name = "votersCount")
    val votersCount: kotlin.Int? = null,

    @Json(name = "userVoteInfo")
    val userVoteInfo: IUserVoteInfo? = null,

    /* 是否已被刪除 */
    @Json(name = "isDeleted")
    val isDeleted: kotlin.Boolean? = null,

    /* 資料建立時間 */
    @Json(name = "createUnixTime")
    val createUnixTime: kotlin.Long? = null,

    /* 資料更新時間 */
    @Json(name = "updateUnixTime")
    val updateUnixTime: kotlin.Long? = null

) : Parcelable

