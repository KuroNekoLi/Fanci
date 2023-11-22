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


import com.squareup.moshi.Json
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 
 *
 * @param voterIds 投該選項用戶
 * @param optionId 選項ID
 * @param voteCount 得票數
 * @param text 選項描述
 */
@Parcelize


data class IVotingOptionStatisticsWithVoter (

    /* 投該選項用戶 */
    @Json(name = "voterIds")
    val voterIds: kotlin.collections.List<kotlin.String>? = null,

    /* 選項ID */
    @Json(name = "optionId")
    val optionId: kotlin.Int? = null,

    /* 得票數 */
    @Json(name = "voteCount")
    val voteCount: kotlin.Int? = null,

    /* 選項描述 */
    @Json(name = "text")
    val text: kotlin.String? = null

) : Parcelable
