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

import com.cmoney.fanciapi.fanci.model.Color

import com.squareup.moshi.Json
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 
 *
 * @param displayName 
 * @param previewThumbnail 
 * @param previewImage 
 * @param categoryColors 
 */
@Parcelize


data class Theme (

    @Json(name = "displayName")
    val displayName: kotlin.String? = null,

    @Json(name = "previewThumbnail")
    val previewThumbnail: kotlin.String? = null,

    @Json(name = "previewImage")
    val previewImage: kotlin.collections.List<kotlin.String>? = null,

    @Json(name = "categoryColors")
    val categoryColors: kotlin.collections.Map<kotlin.String, kotlin.collections.List<Color>>? = null

) : Parcelable

