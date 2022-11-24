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
 * 社團會員
 *
 * @param id 使用者id
 * @param name 名稱
 * @param thumbNail 頭像
 * @param serialNumber 會員識別號
 */
@Parcelize


data class GroupMember (

    /* 使用者id */
    @Json(name = "id")
    val id: kotlin.String? = null,

    /* 名稱 */
    @Json(name = "name")
    val name: kotlin.String? = null,

    /* 頭像 */
    @Json(name = "thumbNail")
    val thumbNail: kotlin.String? = null,

    /* 會員識別號 */
    @Json(name = "serialNumber")
    val serialNumber: kotlin.Long? = null

) : Parcelable

