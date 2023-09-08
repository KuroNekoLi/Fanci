package com.cmoney.kolfanci.ui.screens.notification

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.kolfanci.model.notification.CustomNotification
import com.cmoney.kolfanci.model.notification.NotificationHelper
import com.cmoney.kolfanci.model.notification.NotificationHistory
import com.cmoney.kolfanci.model.notification.Payload
import com.cmoney.kolfanci.model.notification.toNotificationCenterData
import com.cmoney.kolfanci.model.usecase.NotificationUseCase
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * @param image 圖示
 * @param title 標題
 * @param description 下標題
 * @param deepLink 點擊後要處理的深層連結, 跟推播一樣
 * @param isRead 是否已讀
 */
data class NotificationCenterData(
    val notificationId: String,
    val image: String,
    val title: String,
    val description: String,
    val deepLink: String,
    val isRead: Boolean,
    val displayTime: String
)

class NotificationCenterViewModel(
    private val notificationUseCase: NotificationUseCase,
    private val notificationHelper: NotificationHelper
) :
    ViewModel() {

    private val TAG = NotificationCenterViewModel::class.java.simpleName

    private val _notificationCenter = MutableStateFlow<List<NotificationCenterData>>(emptyList())
    val notificationCenter = _notificationCenter.asStateFlow()

    private val _payload = MutableStateFlow<Payload?>(null)
    val payload = _payload.asStateFlow()

    //推播原始資料
    private var notificationHistory: NotificationHistory? = null

    init {
        viewModelScope.launch {
            notificationUseCase.getNotificationCenter()
                .onSuccess {
                    notificationHistory = it
                    _notificationCenter.value = it.items.map { item ->
                        item.toNotificationCenterData()
                    }.distinctBy { distinctItem -> distinctItem.notificationId }
                }.onFailure {
                    KLog.e(TAG, it)
                }
        }
    }

    fun onLoadMore() {
        KLog.i(TAG, "onLoadMore")
        notificationHistory?.let {
            val nextPageUrl = it.paging.next
            KLog.i(TAG, "onLoadMore nextPageUrl:$nextPageUrl")

            viewModelScope.launch {
                notificationUseCase.getNextPageNotificationCenter(nextPageUrl)
                    .fold({ notificationHistoryResponse ->
                        notificationHistory = notificationHistoryResponse
                        val oldData = _notificationCenter.value.toMutableList()
                        oldData.addAll(notificationHistoryResponse.items.map { item -> item.toNotificationCenterData() })
                        _notificationCenter.value =
                            oldData.distinctBy { distinctItem -> distinctItem.notificationId }
                    }, { e ->
                        KLog.e(TAG, e)
                    })
            }

        }
    }

    /**
     *  點擊 推播 item
     */
    fun onNotificationClick(notificationCenterData: NotificationCenterData) {
        KLog.i(TAG, "onNotificationClick:$notificationCenterData")
        val deepLink = notificationCenterData.deepLink
        if (deepLink.isNotEmpty()) {
            val intent = Intent().apply {
                putExtra(CustomNotification.CUSTOM_TARGET_TYPE, 0)
                putExtra(CustomNotification.DEEPLINK, deepLink)
            }
            _payload.value = notificationHelper.getPayloadFromBackground(intent)
        }
    }

    fun clickPayloadDone() {
        _payload.value = null
    }
}