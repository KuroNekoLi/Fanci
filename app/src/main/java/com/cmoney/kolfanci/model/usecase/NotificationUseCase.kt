package com.cmoney.kolfanci.model.usecase

import android.app.Application
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.persistence.SettingsDataStore
import com.cmoney.kolfanci.repository.Network
import com.cmoney.kolfanci.repository.request.NotificationClick
import com.cmoney.kolfanci.ui.screens.group.setting.group.notification.NotificationSettingItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationUseCase(
    private val context: Application,
    private val network: Network,
    private val settingsDataStore: SettingsDataStore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

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
     * 點擊 通知中心 item
     */
    suspend fun setNotificationClick(notificationId: String) = network.setNotificationHistoryClick(
        NotificationClick(
            notificationIds = listOf(notificationId)
        )
    )

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

    /**
     * 是否通知過使用者允許通知權限
     *
     * @return Result.success true 表示已通知, false 未通知, Result.failure 有例外發生
     */
    suspend fun hasNotifyAllowNotificationPermission(): Result<Boolean> = withContext(dispatcher) {
        kotlin.runCatching {
            settingsDataStore.hasNotifyAllowNotificationPermission()
        }
    }

    /**
     * 已通知過使用者允許通知權限
     */
    suspend fun alreadyNotifyAllowNotificationPermission() = withContext(dispatcher) {
        kotlin.runCatching {
            settingsDataStore.alreadyNotifyAllowNotificationPermission()
        }
    }
}