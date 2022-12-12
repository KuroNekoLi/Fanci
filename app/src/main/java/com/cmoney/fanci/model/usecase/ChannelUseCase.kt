package com.cmoney.fanci.model.usecase

import com.cmoney.fanci.extension.checkResponseBody
import com.cmoney.fanciapi.fanci.api.CategoryApi
import com.cmoney.fanciapi.fanci.api.GroupApi
import com.cmoney.fanciapi.fanci.model.CategoryParam
import com.cmoney.fanciapi.fanci.model.ChannelParam
import com.cmoney.fanciapi.fanci.model.ChannelType

class ChannelUseCase(
    private val categoryApi: CategoryApi,
    private val groupApi: GroupApi
) {

    /**
     * 新增 分類
     * @param groupId 群組id, 要在此群組下建立
     * @param name 分類名稱
     */
    suspend fun addCategory(groupId: String, name: String) = kotlin.runCatching {
        groupApi.apiV1GroupGroupIdCategoryPost(
            groupId = groupId,
            categoryParam = CategoryParam(
                name = name
            )
        ).checkResponseBody()
    }

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