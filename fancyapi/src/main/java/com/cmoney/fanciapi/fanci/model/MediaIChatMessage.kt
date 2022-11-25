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

import com.cmoney.fanciapi.fanci.model.IEmojiCount
import com.cmoney.fanciapi.fanci.model.IUserMessageReaction
import com.cmoney.fanciapi.fanci.model.MediaIChatContent
import com.cmoney.fanciapi.fanci.model.MessageType

import com.squareup.moshi.Json
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 
 *
 * @param id 
 * @param authorId 
 * @param replyMessageId 
 * @param content 
 * @param messageReaction 
 * @param emojiCount 
 * @param channelId 
 * @param messageType 
 * @param isDeleted 
 * @param createUnixTime 
 * @param updateUnixTime 
 * @param serialNumber 
 */
@Parcelize


data class MediaIChatMessage (

    @Json(name = "id")
    val id: kotlin.String? = null,

    @Json(name = "authorId")
    val authorId: kotlin.String? = null,

    @Json(name = "replyMessageId")
    val replyMessageId: kotlin.String? = null,

    @Json(name = "content")
    val content: MediaIChatContent? = null,

    @Json(name = "messageReaction")
    val messageReaction: IUserMessageReaction? = null,

    @Json(name = "emojiCount")
    val emojiCount: IEmojiCount? = null,

    @Json(name = "channelId")
    val channelId: kotlin.String? = null,

    @Json(name = "messageType")
    val messageType: MessageType? = null,

    @Json(name = "isDeleted")
    val isDeleted: kotlin.Boolean? = null,

    @Json(name = "createUnixTime")
    val createUnixTime: kotlin.Long? = null,

    @Json(name = "updateUnixTime")
    val updateUnixTime: kotlin.Long? = null,

    @Json(name = "serialNumber")
    val serialNumber: kotlin.Long? = null

) : Parcelable

