package com.cmoney.kolfanci.model.usecase

import com.cmoney.fanciapi.fanci.api.BuffInformationApi
import com.cmoney.kolfanci.extension.checkResponseBody
import com.cmoney.fanciapi.fanci.api.PermissionApi
import com.cmoney.kolfanci.model.Constant

class PermissionUseCase(
    private val permissionApi: PermissionApi,
    private val buffInformationApi: BuffInformationApi
) {

    /**
     * 更新 在此channel的permission 以及 身上的 buff
     */
    suspend fun updateChannelPermissionAndBuff(channelId: String) = kotlin.runCatching {
        Constant.MyChannelPermission =
            permissionApi.apiV1PermissionChannelChannelIdGet(channelId = channelId)
                .checkResponseBody()

        Constant.MyChannelBuff =
            buffInformationApi.apiV1BuffInformationChannelChannelIdMeGet(channelId = channelId)
                .checkResponseBody()
    }

    suspend fun getPermissionByChannel(channelId: String) = kotlin.runCatching {
        permissionApi.apiV1PermissionChannelChannelIdGet(channelId = channelId).checkResponseBody()
    }

    /**
     * 取得在此群組下的權限
     */
    suspend fun getPermissionByGroup(groupId: String) = kotlin.runCatching {
        permissionApi.apiV1PermissionGroupGroupIdGet(
            groupId = groupId
        ).checkResponseBody()
    }

}