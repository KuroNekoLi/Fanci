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

import com.squareup.moshi.Json
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 設定頻道功能區排序物件
 *
 * @param tabs 頻道功能區排序
 */
@Parcelize


data class ChannelTabsSortParam (

    /* 頻道功能區排序 */
    @Json(name = "tabs")
    val tabs: kotlin.collections.List<ChannelTabType>? = null

) : Parcelable

