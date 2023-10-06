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

import com.cmoney.fanciapi.fanci.model.ChannelTabType
import com.cmoney.fanciapi.fanci.model.IUserContext

import com.squareup.moshi.Json
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 
 *
 * @param channelId 
 * @param type 
 * @param userContext 
 */
@Parcelize


data class IChannelTab (

    @Json(name = "channelId")
    val channelId: kotlin.String? = null,

    @Json(name = "type")
    val type: ChannelTabType? = null,

    @Json(name = "userContext")
    val userContext: IUserContext? = null

) : Parcelable

