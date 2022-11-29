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
 * 分類參數
 *
 * @param name 分類命名
 */
@Parcelize


data class CategoryParam (

    /* 分類命名 */
    @Json(name = "name")
    val name: kotlin.String? = null

) : Parcelable

