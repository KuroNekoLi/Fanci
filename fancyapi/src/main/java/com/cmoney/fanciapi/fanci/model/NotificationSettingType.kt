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

/**
 * 通知與推播設定分類
 *
 * Values: fanciOfficial,fanciAgreeJoinGroup,fanciNewGroupApply,fanciGroupDelete,fanciNewBulletinMessage,fanciNewBulletinReplyMessage,fanciNewBulletinEmoji,fanciNewBulletinReplyMessageEmoji,fanciNewBulletinReplyMessageFollow,fanciNewChatMessage,fanciNewChatEmoji,fanciSendGroupApply
 */

enum class NotificationSettingType(val value: kotlin.String) {

    @Json(name = "FanciOfficial")
    fanciOfficial("FanciOfficial"),

    @Json(name = "FanciAgreeJoinGroup")
    fanciAgreeJoinGroup("FanciAgreeJoinGroup"),

    @Json(name = "FanciNewGroupApply")
    fanciNewGroupApply("FanciNewGroupApply"),

    @Json(name = "FanciGroupDelete")
    fanciGroupDelete("FanciGroupDelete"),

    @Json(name = "FanciNewBulletinMessage")
    fanciNewBulletinMessage("FanciNewBulletinMessage"),

    @Json(name = "FanciNewBulletinReplyMessage")
    fanciNewBulletinReplyMessage("FanciNewBulletinReplyMessage"),

    @Json(name = "FanciNewBulletinEmoji")
    fanciNewBulletinEmoji("FanciNewBulletinEmoji"),

    @Json(name = "FanciNewBulletinReplyMessageEmoji")
    fanciNewBulletinReplyMessageEmoji("FanciNewBulletinReplyMessageEmoji"),

    @Json(name = "FanciNewBulletinReplyMessageFollow")
    fanciNewBulletinReplyMessageFollow("FanciNewBulletinReplyMessageFollow"),

    @Json(name = "FanciNewChatMessage")
    fanciNewChatMessage("FanciNewChatMessage"),

    @Json(name = "FanciNewChatEmoji")
    fanciNewChatEmoji("FanciNewChatEmoji"),

    @Json(name = "FanciSendGroupApply")
    fanciSendGroupApply("FanciSendGroupApply");

    /**
     * Override toString() to avoid using the enum variable name as the value, and instead use
     * the actual value defined in the API spec file.
     *
     * This solves a problem when the variable name and its value are different, and ensures that
     * the client sends the correct enum values to the server always.
     */
    override fun toString(): String = value

    companion object {
        /**
         * Converts the provided [data] to a [String] on success, null otherwise.
         */
        fun encode(data: kotlin.Any?): kotlin.String? = if (data is NotificationSettingType) "$data" else null

        /**
         * Returns a valid [NotificationSettingType] for [data], null otherwise.
         */
        fun decode(data: kotlin.Any?): NotificationSettingType? = data?.let {
          val normalizedData = "$it".lowercase()
          values().firstOrNull { value ->
            it == value || normalizedData == "$value".lowercase()
          }
        }
    }
}

