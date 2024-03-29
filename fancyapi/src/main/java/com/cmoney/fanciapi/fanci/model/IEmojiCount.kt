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
 * @param like 
 * @param dislike 
 * @param laugh 
 * @param money 
 * @param shock 
 * @param cry 
 * @param think 
 * @param angry 
 */
@Parcelize


data class IEmojiCount (

    @Json(name = "like")
    val like: kotlin.Int? = null,

    @Json(name = "dislike")
    val dislike: kotlin.Int? = null,

    @Json(name = "laugh")
    val laugh: kotlin.Int? = null,

    @Json(name = "money")
    val money: kotlin.Int? = null,

    @Json(name = "shock")
    val shock: kotlin.Int? = null,

    @Json(name = "cry")
    val cry: kotlin.Int? = null,

    @Json(name = "think")
    val think: kotlin.Int? = null,

    @Json(name = "angry")
    val angry: kotlin.Int? = null

) : Parcelable

