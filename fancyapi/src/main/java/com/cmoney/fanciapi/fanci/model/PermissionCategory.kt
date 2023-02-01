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

import com.cmoney.fanciapi.fanci.model.Permission

import com.squareup.moshi.Json
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 
 *
 * @param displayCategoryName 
 * @param permissions 
 */
@Parcelize


data class PermissionCategory (

    @Json(name = "displayCategoryName")
    val displayCategoryName: kotlin.String? = null,

    @Json(name = "permissions")
    val permissions: kotlin.collections.List<Permission>? = null

) : Parcelable

