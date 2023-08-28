package com.cmoney.kolfanci.ui.screens.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.kolfanci.model.usecase.NotificationUseCase
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * @param icon 圖示
 * @param title 標題
 * @param description 下標題
 * @param deepLink 點擊後要處理的深層連結, 跟推播一樣
 * @param isRead 是否已讀
 */
data class NotificationCenterData(
    val icon: String,
    val title: String,
    val description: String,
    val deepLink: String,
    val isRead: Boolean
)

class NotificationCenterViewModel(private val notificationUseCase: NotificationUseCase) :
    ViewModel() {
    private val TAG = NotificationCenterViewModel::class.java.simpleName

    private val _notificationCenter = MutableStateFlow<List<NotificationCenterData>>(emptyList())
    val notificationCenter = _notificationCenter.asStateFlow()

    init {
        viewModelScope.launch {
            notificationUseCase.getNotificationCenter()
                .onSuccess {
                    _notificationCenter.value = it
                }.onFailure {
                    KLog.e(TAG, it)
                }
        }
    }
}