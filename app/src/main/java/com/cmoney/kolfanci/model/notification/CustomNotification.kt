package com.cmoney.kolfanci.model.notification

import android.content.Intent
import com.google.firebase.messaging.RemoteMessage

/**
 *  將 推播訊息 以及 dynamic link 資料 轉為統一物件使用
 *
 *  @property title 標題
 *  @property message 訊息內容
 *  @property sound 音效
 *  @property targetType 目標類型
 *  @property commonTargetType 通用目標類型
 *  @property commonParameter 通用參數
 *  @property sn 推播序號
 *  @property analyticsId 統計用編號
 *  @property deeplink 跳轉連結
 */
class CustomNotification {
    val title: String
    val message: String
    val sound: String
    val targetType: Int
    val parameter: String
    val commonTargetType: Int
    val commonParameter: String
    val sn: Long?
    val analyticsId: Long?
    val deeplink: String?

    constructor(intent: Intent?) {
        val customTargetType = intent?.extras?.get(CUSTOM_TARGET_TYPE) as? Int? ?: -1
        message = intent?.getStringExtra(MESSAGE).orEmpty()
        title = intent?.getStringExtra(TITLE).orEmpty()
        sound = intent?.getStringExtra(SOUND).orEmpty()
        targetType = if (customTargetType == -1) {
            intent?.getStringExtra(TARGET_TYPE)?.toIntOrNull() ?: 0
        } else {
            customTargetType
        }
        parameter = intent?.getStringExtra(PARAMETER).orEmpty()
        commonTargetType = intent?.getStringExtra(COMMON_TARGET_TYPE)?.toIntOrNull() ?: 0
        commonParameter = intent?.getStringExtra(COMMON_PARAMETER).orEmpty()
        sn = intent?.getStringExtra(SN)?.toLongOrNull()
        analyticsId = intent?.getStringExtra(ANALYTICS_ID)?.toLongOrNull()
        deeplink = intent?.getStringExtra(DEEPLINK).orEmpty()
    }

    constructor(remoteMessage: RemoteMessage?) {
        val data = remoteMessage?.data
        val customTargetType = data?.get(CUSTOM_TARGET_TYPE)?.toIntOrNull() ?: -1
        message = data?.get(MESSAGE).orEmpty()
        title = data?.get(TITLE).orEmpty()
        sound = data?.get(SOUND).orEmpty()
        targetType = if (customTargetType == -1) {
            data?.get(TARGET_TYPE)?.toIntOrNull() ?: 0
        } else {
            customTargetType
        }
        parameter = data?.get(PARAMETER).orEmpty()
        commonTargetType = data?.get(COMMON_TARGET_TYPE)?.toIntOrNull() ?: 0
        commonParameter = data?.get(COMMON_PARAMETER).orEmpty()
        sn = data?.get(SN)?.toLongOrNull()
        analyticsId = data?.get(ANALYTICS_ID)?.toLongOrNull()
        deeplink = data?.get(DEEPLINK).orEmpty()
    }

    companion object {
        const val SN = "sn"
        const val TITLE = "title"
        const val SOUND = "sound"
        const val MESSAGE = "msg"
        const val TARGET_TYPE = "targetType"
        const val PARAMETER = "parameter"
        const val COMMON_TARGET_TYPE = "commonTargetType"
        const val COMMON_PARAMETER = "commonParameter"
        const val ANALYTICS_ID = "analyticsId"
        const val CUSTOM_TARGET_TYPE = "targetType"
        const val DEEPLINK = "deeplink"
    }

}