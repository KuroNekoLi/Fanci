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

import com.cmoney.fanciapi.fanci.model.ChatMessage

import com.squareup.moshi.Json
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 公告訊息資訊
 *
 * @param isAnnounced 是否有公告訊息
 * @param message 
 */
@Parcelize


data class PinnedMessageInfo (

    /* 是否有公告訊息 */
    @Json(name = "isAnnounced")
    val isAnnounced: kotlin.Boolean? = null,

    @Json(name = "message")
    val message: ChatMessage? = null

) : Parcelable

