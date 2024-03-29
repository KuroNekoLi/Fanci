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
 * @param chatRoom 聊天室
 * @param bulletinboard 貼文
 */
@Parcelize


data class ChannelTabsStatus (

    /* 聊天室 */
    @Json(name = "chatRoom")
    val chatRoom: kotlin.Boolean? = null,

    /* 貼文 */
    @Json(name = "bulletinboard")
    val bulletinboard: kotlin.Boolean? = null

) : Parcelable

