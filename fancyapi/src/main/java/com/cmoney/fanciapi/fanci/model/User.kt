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
 * 使用者
 *
 * @param id 使用者id
 * @param name 使用者名稱
 * @param thumbNail 使用者頭像
 * @param serialNumber 使用者序列號
 * @param createUnixTime 使用者創建時間
 * @param updateUnixTime 使用者更新時間
 */


data class User (

    /* 使用者id */
    @Json(name = "id")
    val id: kotlin.String? = null,

    /* 使用者名稱 */
    @Json(name = "name")
    val name: kotlin.String? = null,

    /* 使用者頭像 */
    @Json(name = "thumbNail")
    val thumbNail: kotlin.String? = null,

    /* 使用者序列號 */
    @Json(name = "serialNumber")
    val serialNumber: kotlin.Long? = null,

    /* 使用者創建時間 */
    @Json(name = "createUnixTime")
    val createUnixTime: kotlin.Long? = null,

    /* 使用者更新時間 */
    @Json(name = "updateUnixTime")
    val updateUnixTime: kotlin.Long? = null

)

