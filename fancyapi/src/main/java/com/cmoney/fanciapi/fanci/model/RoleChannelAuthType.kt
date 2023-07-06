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
 * @param channelId 頻道Id
 * @param channelName 頻道命名
 * @param isPublic 是否為公開頻道
 * @param authType 該角色頻道權限
 */
@Parcelize


data class RoleChannelAuthType (

    /* 頻道Id */
    @Json(name = "channelId")
    val channelId: kotlin.String? = null,

    /* 頻道命名 */
    @Json(name = "channelName")
    val channelName: kotlin.String? = null,

    /* 是否為公開頻道 */
    @Json(name = "isPublic")
    val isPublic: kotlin.Boolean? = null,

    /* 該角色頻道權限 */
    @Json(name = "authType")
    val authType: ChannelAuthType? = null

) : Parcelable

