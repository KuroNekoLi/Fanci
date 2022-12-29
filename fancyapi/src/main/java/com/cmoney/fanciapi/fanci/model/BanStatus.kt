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
 * @param isBanned 
 * @param expireUnixDateTime 
 */
@Parcelize


data class BanStatus (

    @Json(name = "isBanned")
    val isBanned: kotlin.Boolean? = null,

    @Json(name = "expireUnixDateTime")
    val expireUnixDateTime: kotlin.Long? = null

) : Parcelable

