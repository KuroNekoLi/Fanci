package com.cmoney.kolfanci.model.usecase

import android.app.Application
import com.cmoney.fanciapi.fanci.api.PushNotificationApi
import com.cmoney.fanciapi.fanci.model.PushNotificationSettingType
import com.cmoney.kolfanci.extension.checkResponseBody
import com.cmoney.kolfanci.model.persistence.SettingsDataStore
import com.cmoney.kolfanci.repository.Network
import com.cmoney.kolfanci.repository.request.NotificationClick
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationUseCase(
    private val context: Application,
    private val network: Network,
    private val settingsDataStore: SettingsDataStore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val pushNotificationApi: PushNotificationApi
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

    /**
     * 取得該社團的推播設定
     */
    suspend fun getNotificationSetting(groupId: String) = withContext(dispatcher) {
        kotlin.runCatching {
            pushNotificationApi.apiV1PushNotificationUserGroupIdSettingTypeGet(groupId = groupId)
                .checkResponseBody()
        }
    }

    /**
     * 設定指定社團的推播設定值
     */
    suspend fun setNotificationSetting(
        groupId: String,
        pushNotificationSettingType: PushNotificationSettingType
    ) = withContext(dispatcher) {
        kotlin.runCatching {
            pushNotificationApi.apiV1PushNotificationUserGroupIdSettingTypeSettingTypePut(
                groupId = groupId,
                settingType = pushNotificationSettingType
            ).checkResponseBody()
        }
    }

    /**
     * 取得所有推播設定檔
     */
    suspend fun getAllNotificationSetting() = withContext(dispatcher) {
        kotlin.runCatching {
            pushNotificationApi.apiV1PushNotificationSettingTypeAllGet().checkResponseBody()
        }
    }

}