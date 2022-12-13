package com.cmoney.fanci.model.usecase

import com.cmoney.fanci.extension.checkResponseBody
import com.cmoney.fanciapi.fanci.api.CategoryApi
import com.cmoney.fanciapi.fanci.api.ChannelApi
import com.cmoney.fanciapi.fanci.api.GroupApi
import com.cmoney.fanciapi.fanci.model.CategoryParam
import com.cmoney.fanciapi.fanci.model.ChannelParam
import com.cmoney.fanciapi.fanci.model.ChannelType
import com.cmoney.fanciapi.fanci.model.EditChannelParam

class ChannelUseCase(
    private val categoryApi: CategoryApi,
    private val groupApi: GroupApi,
    private val channelApi: ChannelApi
) {

    /**
     * 刪除 分類
     */
    suspend fun deleteCategory(categoryId: String) = kotlin.runCatching {
        categoryApi.apiV1CategoryCategoryIdDelete(categoryId = categoryId).checkResponseBody()
    }

    /**
     * 編輯 分類名稱
     */
    suspend fun editCategoryName(categoryId: String, name: String) = kotlin.runCatching {
        categoryApi.apiV1CategoryCategoryIdNamePut(
            categoryId = categoryId,
            categoryParam = CategoryParam(
                name
            )
        ).checkResponseBody()
    }

    /**
     * 刪除 頻道
     */
    suspend fun deleteChannel(channelId: String) = kotlin.runCatching {
        channelApi.apiV1ChannelChannelIdDelete(channelId = channelId).checkResponseBody()
    }

    /**
     * 編輯 頻道名稱
     */
    suspend fun editChannelName(channelId: String, name: String) = kotlin.runCatching {
        channelApi.apiV1ChannelChannelIdPut(
            channelId = channelId,
            editChannelParam = EditChannelParam(name)
        ).checkResponseBody()
    }


    /**
     * 取得 頻道角色清單
     */
    suspend fun getChannelRole(channelId: String) = kotlin.runCatching {
        channelApi.apiV1ChannelChannelIdRoleGet(channelId).checkResponseBody()
    }

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