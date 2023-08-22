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
    val analyticsId: Long?,
    val deeplink: String?
): Parcelable {
    companion object{
        //type enum
        const val TYPE_1 = 1        //社團邀請

        const val TYPE_2 = 2        //進入聊天區, 並跳往該訊息
        const val TYPE_3 = 3        //打開貼文詳細頁面
        const val TYPE_4 = 4        //社團解散
    }
}