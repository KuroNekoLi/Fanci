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
 * 投票活動
 *
 * @param id 投票活動Id
 */
@Parcelize


data class VotingIdParam (

    /* 投票活動Id */
    @Json(name = "id")
    val id: kotlin.String? = null

) : Parcelable

