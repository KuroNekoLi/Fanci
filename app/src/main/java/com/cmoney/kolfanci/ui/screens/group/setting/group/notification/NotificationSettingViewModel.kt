package com.cmoney.kolfanci.ui.screens.group.setting.group.notification

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.kolfanci.extension.isNotificationsEnabled
import com.cmoney.kolfanci.extension.openNotificationSetting
import com.cmoney.kolfanci.model.usecase.NotificationUseCase
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationSettingItem(
    val title: String,
    val description: String,
    val isChecked: Boolean = false,
    val shortTitle: String
) : Parcelable

class NotificationSettingViewModel(
    val context: Application,
    notificationUseCase: NotificationUseCase
) : AndroidViewModel(context) {
    private val TAG = NotificationSettingViewModel::class.java.simpleName

    private val _notificationSetting: MutableStateFlow<List<NotificationSettingItem>> =
        MutableStateFlow(
            emptyList()
        )

    val notificationSetting = _notificationSetting.asStateFlow()

    private val _showOpenNotificationSettingAlert: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val showOpenNotificationSettingAlert = _showOpenNotificationSettingAlert.asStateFlow()

    private val _saveSettingComplete: MutableStateFlow<NotificationSettingItem?> =
        MutableStateFlow(null)
    val saveSettingComplete = _saveSettingComplete.asStateFlow()

    init {
        _notificationSetting.value = notificationUseCase.generateNotificationSettingItems()

        //檢查是否開啟 推播
        if (!context.isNotificationsEnabled()) {
            _showOpenNotificationSettingAlert.value = true
        }
    }

    /**
     * 點擊 提醒設定
     */
    fun onSettingItemClick(notificationSettingItem: NotificationSettingItem) {
        KLog.i(TAG, "onSettingItemClick:$notificationSettingItem")
        _notificationSetting.value = _notificationSetting.value.map {
            if (it.title == notificationSettingItem.title) {
                it.copy(isChecked = true)
            } else {
                it.copy(isChecked = false)
            }
        }
    }

    /**
     * 關閉 彈窗
     */
    fun dismissNotificationOpenAlert() {
        _showOpenNotificationSettingAlert.value = false
    }

    fun openSystemNotificationSetting() {
        KLog.i(TAG, "openSystemNotificationSetting")
        context.openNotificationSetting()
    }

    /**
     * 儲存 通知設定
     */
    fun saveNotificationSetting() {
        KLog.i(TAG, "saveNotificationSetting")
        viewModelScope.launch {
            // TODO: call api
            _saveSettingComplete.value = _notificationSetting.value.firstOrNull {
                it.isChecked
            }
        }
    }
}