package com.cmoney.kolfanci.model.usecase

import com.cmoney.fanciapi.fanci.api.BuffInformationApi
import com.cmoney.fanciapi.fanci.api.CategoryApi
import com.cmoney.fanciapi.fanci.api.ChannelApi
import com.cmoney.fanciapi.fanci.api.ChannelTabApi
import com.cmoney.fanciapi.fanci.api.GroupApi
import com.cmoney.fanciapi.fanci.model.AccessorParam
import com.cmoney.fanciapi.fanci.model.CategoryParam
import com.cmoney.fanciapi.fanci.model.ChannelAuthType
import com.cmoney.fanciapi.fanci.model.ChannelParam
import com.cmoney.fanciapi.fanci.model.ChannelPrivacy
import com.cmoney.fanciapi.fanci.model.ChannelTabType
import com.cmoney.fanciapi.fanci.model.ChannelTabsSortParam
import com.cmoney.fanciapi.fanci.model.EditChannelParam
import com.cmoney.fanciapi.fanci.model.GetWhiteListCountParam
import com.cmoney.fanciapi.fanci.model.PutWhiteListRequest
import com.cmoney.fanciapi.fanci.model.RoleIdsParam
import com.cmoney.kolfanci.extension.checkResponseBody

class ChannelUseCase(
    private val categoryApi: CategoryApi,
    private val groupApi: GroupApi,
    private val channelApi: ChannelApi,
    private val buffInformationApi: BuffInformationApi,
    private val channelTabApi: ChannelTabApi
) {

    /**
     * 取得頻道 tab order
     */
    suspend fun getChannelTabOrder(channelId: String) = kotlin.runCatching {
        channelTabApi.apiV1ChannelTabChannelIdTabsGet(
            channelId = channelId
        ).checkResponseBody()
    }

    /**
     * 設定 tab 順序
     *
     * @param channelId 頻道id
     * @param tabs tab 順序
     */
    suspend fun setChannelTabOrder(channelId: String, tabs: List<ChannelTabType>) =
        kotlin.runCatching {
            val channelTabsSortParam = ChannelTabsSortParam(
                tabs = tabs
            )

            channelTabApi.apiV1ChannelTabChannelIdTabsSortPut(
                channelId = channelId,
                channelTabsSortParam = channelTabsSortParam
            )
        }

    /**
     * 取得User在此頻道 的狀態
     */
    suspend fun getChannelBuffer(channelId: String) = kotlin.runCatching {
        buffInformationApi.apiV1BuffInformationChannelChannelIdMeGet(channelId = channelId)
            .checkResponseBody()
    }

    /**
     * 取得 私密頻道 不重複用戶總數
     */
    suspend fun getPrivateChannelUserCount(roleIds: List<String>, userIds: List<String>) =
        kotlin.runCatching {
            channelApi.apiV1ChannelWhiteListUsersCountPost(
                getWhiteListCountParam = GetWhiteListCountParam(
                    roleIds = roleIds,
                    userIds = userIds
                )
            ).checkResponseBody()
        }

    /**
     * 取得 私密頻道 白名單
     */
    suspend fun getPrivateChannelWhiteList(channelId: String) = kotlin.runCatching {
        channelApi.apiV1ChannelChannelIdWhiteListGet(
            channelId
        ).checkResponseBody()
    }

    /**
     * 設定 私密頻道 白名單 (用戶/角色)
     */
    suspend fun putPrivateChannelWhiteList(
        channelId: String,
        authType: ChannelAuthType,
        accessorList: List<AccessorParam>
    ) =
        kotlin.runCatching {
            channelApi.apiV1ChannelChannelIdWhiteListAuthTypePut(
                channelId = channelId,
                authType = authType,
                putWhiteListRequest = PutWhiteListRequest(
                    parameter = accessorList
                )
            ).checkResponseBody()
        }

    /**
     * 抓取 私密頻道 權限類型清單, 不包含 無權限
     */
    suspend fun getChanelAccessTypeWithoutNoPermission() = kotlin.runCatching {
        channelApi.apiV2ChannelAccessTypeGet(isWithNoPermission = false).checkResponseBody()
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
    suspend fun editChannelName(channelId: String, name: String, privacy: ChannelPrivacy) =
        kotlin.runCatching {
            channelApi.apiV1ChannelChannelIdPut(
                channelId = channelId,
                editChannelParam = EditChannelParam(
                    name,
                    privacy
                )
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
     * @param privacy 公開/私密
     * @param tabs tab 排序
     */
    suspend fun addChannel(
        categoryId: String,
        name: String,
        privacy: ChannelPrivacy,
        tabs: List<ChannelTabType>
    ) =
        kotlin.runCatching {
            categoryApi.apiV1CategoryCategoryIdChannelPost(
                categoryId = categoryId,
                channelParam = ChannelParam(
                    tabs = tabs,
                    name = name,
                    privacy = privacy
                )
            ).checkResponseBody()
        }

    /**
     * 查看 該 channel tab 狀態
     */
    suspend fun fetchChannelTabStatus(channelId: String) = kotlin.runCatching {
        channelTabApi.apiV1ChannelTabChannelIdTabGet(channelId = channelId).checkResponseBody()
    }

}