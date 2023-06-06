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

import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.User

import com.squareup.moshi.Json
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 
 *
 * @param authType 
 * @param roles 
 * @param users 
 */
@Parcelize


data class ChannelWhiteList (

    @Json(name = "authType")
    val authType: kotlin.String? = null,

    @Json(name = "roles")
    val roles: kotlin.collections.List<FanciRole>? = null,

    @Json(name = "users")
    val users: kotlin.collections.List<User>? = null

) : Parcelable

