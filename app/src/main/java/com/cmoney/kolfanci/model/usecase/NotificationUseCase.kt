package com.cmoney.kolfanci.model.usecase

import android.app.Application
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.repository.Network
import com.cmoney.kolfanci.ui.screens.group.setting.group.notification.NotificationSettingItem

class NotificationUseCase(private val context: Application, private val network: Network) {

    /**
     * 取得 推播中心 資料
     */
    suspend fun getNotificationCenter() = network.getNotificationHistory()

    /**
     * 取得 下一頁 推播歷史訊息
     */
    suspend fun getNextPageNotificationCenter(nextPageUrl: String) =
        network.getNextPageNotificationHistory(nextPageUrl = nextPageUrl)


    /**
     * 產生 提醒設定 清單
     */
    fun generateNotificationSettingItems(): List<NotificationSettingItem> = listOf(
        NotificationSettingItem(
            title = context.getString(R.string.notification_setting_all),
            description = context.getString(R.string.notification_setting_all_desc),
            isChecked = true,
            shortTitle = context.getString(R.string.notification_setting_short)
        ),
        NotificationSettingItem(
            title = context.getString(R.string.notification_setting_post),
            description = context.getString(R.string.notification_setting_post_desc),
            isChecked = false,
            shortTitle = context.getString(R.string.notification_setting_post_short)
        ),
        NotificationSettingItem(
            title = context.getString(R.string.notification_setting_silence),
            description = context.getString(R.string.notification_setting_silence_desc),
            isChecked = false,
            shortTitle = context.getString(R.string.notification_setting_silence_short)
        )
    )


}