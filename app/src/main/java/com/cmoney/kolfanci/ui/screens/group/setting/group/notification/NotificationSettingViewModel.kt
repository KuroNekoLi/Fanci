package com.cmoney.kolfanci.ui.screens.group.setting.group.notification

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.PushNotificationSetting
import com.cmoney.kolfanci.model.usecase.NotificationUseCase
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PushNotificationSettingWrap(
    val pushNotificationSetting: PushNotificationSetting,
    val isChecked: Boolean
)

class NotificationSettingViewModel(
    private val context: Application,
    private val notificationUseCase: NotificationUseCase
) : AndroidViewModel(context) {
    private val TAG = NotificationSettingViewModel::class.java.simpleName

    private val _notificationSetting: MutableStateFlow<List<PushNotificationSettingWrap>> =
        MutableStateFlow(
            emptyList()
        )

    val notificationSetting = _notificationSetting.asStateFlow()

    private val _saveSettingComplete: MutableStateFlow<PushNotificationSetting?> =
        MutableStateFlow(null)
    val saveSettingComplete = _saveSettingComplete.asStateFlow()

    /**
     * 儲存 通知設定
     */
    fun saveNotificationSetting(groupId: String) {
        KLog.i(TAG, "saveNotificationSetting:$groupId")
        viewModelScope.launch {
            val pushNotificationSetting = _notificationSetting.value.firstOrNull {
                it.isChecked
            }?.pushNotificationSetting

            pushNotificationSetting?.settingType?.let { settingType ->
                notificationUseCase.setNotificationSetting(
                    groupId = groupId,
                    pushNotificationSettingType = settingType
                ).onSuccess {
                    _saveSettingComplete.value = pushNotificationSetting
                }.onFailure {
                    KLog.e(TAG, it)
                }
            }
        }
    }

    /**
     *  server 抓取所有設定檔, 並比對目前所選的
     */
    fun fetchAllNotificationSetting(currentPushNotificationSetting: PushNotificationSetting) {
        KLog.i(TAG, "fetchAllNotificationSetting:$currentPushNotificationSetting")
        viewModelScope.launch {
            notificationUseCase.getAllNotificationSetting()
                .onSuccess { pushNotificationSettingList ->
                    _notificationSetting.value =
                        pushNotificationSettingList.map { pushNotificationSetting ->
                            PushNotificationSettingWrap(
                                pushNotificationSetting = pushNotificationSetting,
                                isChecked = (currentPushNotificationSetting.settingType == pushNotificationSetting.settingType)
                            )
                        }
                }
                .onFailure {
                    KLog.e(TAG, it)
                }
        }
    }

    /**
     * 點擊推播設定 item
     */
    fun onNotificationSettingItemClick(pushNotificationSettingWrap: PushNotificationSettingWrap) {
        KLog.i(TAG, "onNotificationSettingItemClick:$pushNotificationSettingWrap")
        _notificationSetting.value = _notificationSetting.value.map {
            if (it.pushNotificationSetting.notificationTypes == pushNotificationSettingWrap.pushNotificationSetting.notificationTypes) {
                it.copy(isChecked = true)
            } else {
                it.copy(isChecked = false)
            }
        }
    }

}