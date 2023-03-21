package com.cmoney.kolfanci.model.usecase

import com.cmoney.kolfanci.extension.checkResponseBody
import com.cmoney.fanciapi.fanci.api.CategoryApi
import com.cmoney.fanciapi.fanci.api.ChannelApi
import com.cmoney.fanciapi.fanci.api.GroupApi
import com.cmoney.fanciapi.fanci.model.*

class ChannelUseCase(
    private val categoryApi: CategoryApi,
    private val groupApi: GroupApi,
    private val channelApi: ChannelApi
) {

    /**
     * 抓取 私密頻道 權限類型清單
     */
    suspend fun getChanelAccessType() = kotlin.runCatching {
        channelApi.apiV1ChannelAccessTypeGet().checkResponseBody()
    }

    /**
     * 頻道 移除多個角色
     * @param channelId 頻道id
     * @param roleIds 角色id清單
     */
    suspend fun deleteRoleFromChannel(channelId: String, roleIds: List<String>) =
        kotlin.runCatching {
            channelApi.apiV1ChannelChannelIdRoleDelete(
                channelId = channelId,
                roleIdsParam = RoleIdsParam(
                    roleIds = roleIds
                )
            ).checkResponseBody()
        }

    /**
     * 頻道 新增多個角色
     * @param channelId 頻道id
     * @param roleIds 角色id清單
     */
    suspend fun addRoleToChannel(channelId: String, roleIds: List<String>) = kotlin.runCatching {
        channelApi.apiV1ChannelChannelIdRolePut(
            channelId = channelId,
            roleIdsParam = RoleIdsParam(
                roleIds = roleIds
            )
        ).checkResponseBody()
    }

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