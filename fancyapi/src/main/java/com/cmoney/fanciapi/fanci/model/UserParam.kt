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
 * 使用者參數
 *
 * @param name 使用者名稱
 * @param thumbNail 使用者頭像
 */
@Parcelize


data class UserParam (

    /* 使用者名稱 */
    @Json(name = "name")
    val name: kotlin.String,

    /* 使用者頭像 */
    @Json(name = "thumbNail")
    val thumbNail: kotlin.String

) : Parcelable

