package com.cmoney.kolfanci.model.notification

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Payload (
    val sn: Long?,
    val title: String,
    val message: String,
    val commonTargetType: Int,
    val commonParameter: String,
    val targetType: Int,
    val parameter: String,
    val analyticsId: Long?
): Parcelable {
    companion object{
        //type enum
        const val TYPE_1 = 1        //社團邀請
    }
}