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
 * @param userId 
 * @param emoji 
 */
@Parcelize


data class IUserMessageReaction (

    @Json(name = "userId")
    val userId: kotlin.String? = null,

    @Json(name = "emoji")
    val emoji: kotlin.String? = null

) : Parcelable

