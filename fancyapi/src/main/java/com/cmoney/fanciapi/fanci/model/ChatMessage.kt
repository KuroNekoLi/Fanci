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

import com.cmoney.fanciapi.fanci.model.DeleteStatus
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.IEmojiCount
import com.cmoney.fanciapi.fanci.model.IReplyMessage
import com.cmoney.fanciapi.fanci.model.IUserMessageReaction
import com.cmoney.fanciapi.fanci.model.MediaIChatContent
import com.cmoney.fanciapi.fanci.model.MessageServiceType

import com.squareup.moshi.Json
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 
 *
 * @param author 
 * @param replyMessage 
 * @param content 
 * @param emojiCount 
 * @param id 
 * @param isDeleted 是否刪除
 * @param createUnixTime 
 * @param updateUnixTime 
 * @param serialNumber 
 * @param messageFromType 
 * @param messageReaction 
 * @param deleteStatus 
 * @param deleteFrom 
 * @param commentCount 
 */
@Parcelize


data class ChatMessage (

    @Json(name = "author")
    val author: GroupMember? = null,

    @Json(name = "replyMessage")
    val replyMessage: IReplyMessage? = null,

    @Json(name = "content")
    val content: MediaIChatContent? = null,

    @Json(name = "emojiCount")
    val emojiCount: IEmojiCount? = null,

    @Json(name = "id")
    val id: kotlin.String? = null,

    /* 是否刪除 */
    @Json(name = "isDeleted")
    val isDeleted: kotlin.Boolean? = null,

    @Json(name = "createUnixTime")
    val createUnixTime: kotlin.Long? = null,

    @Json(name = "updateUnixTime")
    val updateUnixTime: kotlin.Long? = null,

    @Json(name = "serialNumber")
    val serialNumber: kotlin.Long? = null,

    @Json(name = "messageFromType")
    val messageFromType: MessageServiceType? = null,

    @Json(name = "messageReaction")
    val messageReaction: IUserMessageReaction? = null,

    @Json(name = "deleteStatus")
    val deleteStatus: DeleteStatus? = null,

    @Json(name = "deleteFrom")
    val deleteFrom: GroupMember? = null,

    @Json(name = "commentCount")
    val commentCount: kotlin.Int? = null

) : Parcelable

