package com.cmoney.fanci.model.usecase

import com.cmoney.fanci.extension.checkResponseBody
import com.cmoney.fanciapi.fanci.api.OrderApi
import com.cmoney.fanciapi.fanci.model.RoleOrderParam

class OrderUseCase(
    private val orderApi: OrderApi
) {

    /**
     * 重新排序 角色清單
     */
    suspend fun orderRole(
        groupId: String,
        roleIds: List<String>
    ) = kotlin.runCatching {
        orderApi.apiV1OrderGroupGroupIdRoleOrderPut(
            groupId = groupId,
            roleOrderParam = RoleOrderParam(
                roleIds = roleIds
            )
        ).checkResponseBody()
    }

}