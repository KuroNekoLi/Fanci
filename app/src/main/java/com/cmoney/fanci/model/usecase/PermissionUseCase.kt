package com.cmoney.fanci.model.usecase

import com.cmoney.fanci.extension.checkResponseBody
import com.cmoney.fanciapi.fanci.api.PermissionApi

class PermissionUseCase(
    private val permissionApi: PermissionApi
) {

    /**
     * 取得在此群組下的權限
     */
    suspend fun getPermissionByGroup(groupId: String) = kotlin.runCatching {
        permissionApi.apiV1PermissionGroupGroupIdGet(
            groupId = groupId
        ).checkResponseBody()
    }

}