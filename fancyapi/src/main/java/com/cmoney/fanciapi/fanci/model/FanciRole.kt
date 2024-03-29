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
 * 
 *
 * @param id 
 * @param name 
 * @param permissionIds 
 * @param color 
 * @param createUnixTime 
 * @param updateUnixTime 
 * @param userCount 
 * @param isVipRole 
 */
@Parcelize


data class FanciRole (

    @Json(name = "id")
    val id: kotlin.String? = null,

    @Json(name = "name")
    val name: kotlin.String? = null,

    @Json(name = "permissionIds")
    val permissionIds: kotlin.collections.List<kotlin.String>? = null,

    @Json(name = "color")
    val color: kotlin.String? = null,

    @Json(name = "createUnixTime")
    val createUnixTime: kotlin.Long? = null,

    @Json(name = "updateUnixTime")
    val updateUnixTime: kotlin.Long? = null,

    @Json(name = "userCount")
    val userCount: kotlin.Long? = null,

    @Json(name = "isVipRole")
    val isVipRole: kotlin.Boolean? = null

) : Parcelable

