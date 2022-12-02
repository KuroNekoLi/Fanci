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
 * 角色排序參數
 *
 * @param roleIds 
 */
@Parcelize


data class RoleOrderParam (

    @Json(name = "roleIds")
    val roleIds: kotlin.collections.List<kotlin.String>? = null

) : Parcelable

