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

import com.cmoney.fanciapi.fanci.model.PurchasedSale

import com.squareup.moshi.Json
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 
 *
 * @param cmoneyMemberId 
 * @param name 
 * @param purchasedSales 
 */
@Parcelize


data class UserConsumeInfo (

    @Json(name = "cmoneyMemberId")
    val cmoneyMemberId: kotlin.Int? = null,

    @Json(name = "name")
    val name: kotlin.String? = null,

    @Json(name = "purchasedSales")
    val purchasedSales: kotlin.collections.List<PurchasedSale>? = null

) : Parcelable
