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
 * 題組回應
 *
 * @param question 題目
 * @param answer 回應
 */


data class GroupRequirementAnswer (

    /* 題目 */
    @Json(name = "question")
    val question: kotlin.String? = null,

    /* 回應 */
    @Json(name = "answer")
    val answer: kotlin.String? = null

)

