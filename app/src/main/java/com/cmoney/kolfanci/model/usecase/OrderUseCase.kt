package com.cmoney.kolfanci.model.usecase

import com.cmoney.kolfanci.extension.checkResponseBody
import com.cmoney.fanciapi.fanci.api.OrderApi
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.CategoryOrder
import com.cmoney.fanciapi.fanci.model.OrderParam
import com.cmoney.fanciapi.fanci.model.RoleOrderParam

class OrderUseCase(
    private val orderApi: OrderApi
) {

    /**
     * 排序 分類/頻道
     *
     * @param groupId 社團id
     * @param category 分類
     */
    suspend fun orderCategoryOrChannel(
        groupId: String,
        category: List<Category>
    ) = kotlin.runCatching {
        val orderParam = category.map {
            CategoryOrder(
                categoryId = it.id.orEmpty(),
                channelIds = it.channels?.map { channel ->
                    channel.id.orEmpty()
                }
            )
        }

        orderApi.apiV1OrderGroupGroupIdPut(
            groupId = groupId,
            orderParam = OrderParam(
                categoryOrders = orderParam
            )
        ).checkResponseBody()
    }

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