package com.cmoney.fanci.model.usecase

import com.cmoney.fanci.extension.checkResponseBody
import com.cmoney.fanciapi.fanci.api.CategoryApi
import com.cmoney.fanciapi.fanci.model.ChannelParam
import com.cmoney.fanciapi.fanci.model.ChannelType

class ChannelUseCase(
    private val categoryApi: CategoryApi
) {

    /**
     * 新增 頻道
     * @param categoryId 分類id, 在此分類下建立
     * @param name 頻道名稱
     */
    suspend fun addChannel(categoryId: String, name: String) = kotlin.runCatching {
        categoryApi.apiV1CategoryCategoryIdChannelPost(
            categoryId = categoryId,
            channelParam = ChannelParam(
                channelType = ChannelType.chatRoom,
                name = name
            )
        ).checkResponseBody()
    }

}