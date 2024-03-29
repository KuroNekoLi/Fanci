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
 * 圖檔內容
 *
 * @param width 
 * @param height 
 */
@Parcelize


data class ImageContent (

    @Json(name = "width")
    val width: kotlin.Int? = null,

    @Json(name = "height")
    val height: kotlin.Int? = null

) : Parcelable

