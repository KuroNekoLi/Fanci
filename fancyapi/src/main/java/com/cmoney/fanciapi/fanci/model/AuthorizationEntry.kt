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
 * @param packId 
 * @param type 
 * @param subjectId 
 */
@Parcelize


data class AuthorizationEntry (

    @Json(name = "packId")
    val packId: kotlin.Long? = null,

    @Json(name = "type")
    val type: kotlin.String? = null,

    @Json(name = "subjectId")
    val subjectId: kotlin.Int? = null

) : Parcelable
