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

import com.cmoney.fanciapi.fanci.model.NotificationSettingType
import com.cmoney.fanciapi.fanci.model.PushNotificationSettingType

import com.squareup.moshi.Json
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 設定類型清單內容
 *
 * @param settingType 
 * @param shortTitle 
 * @param title 
 * @param description 
 * @param notificationTypes 
 */
@Parcelize


data class PushNotificationSetting (

    @Json(name = "settingType")
    val settingType: PushNotificationSettingType? = null,

    @Json(name = "shortTitle")
    val shortTitle: kotlin.String? = null,

    @Json(name = "title")
    val title: kotlin.String? = null,

    @Json(name = "description")
    val description: kotlin.String? = null,

    @Json(name = "notificationTypes")
    val notificationTypes: kotlin.collections.List<NotificationSettingType>? = null

) : Parcelable

