package com.cmoney.kolfanci.model.usecase

import com.cmoney.kolfanci.model.mock.MockData

class NotificationUseCase {

    /**
     * 取得 推播中心 資料
     */
    fun getNotificationCenter() = MockData.mockNotificationCenter

}