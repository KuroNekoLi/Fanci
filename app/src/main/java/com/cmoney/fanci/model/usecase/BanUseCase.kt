package com.cmoney.fanci.model.usecase

import com.cmoney.fanci.extension.checkResponseBody
import com.cmoney.fanciapi.fanci.api.BanApi
import com.cmoney.fanciapi.fanci.model.BanParam
import com.cmoney.fanciapi.fanci.model.BanPeriodOption
import com.cmoney.fanciapi.fanci.model.UseridsParam

class BanUseCase(
    private val banApi: BanApi
) {

    /**
     * 解除 禁言
     * @param groupId 社團id
     * @param userIds 解除禁言 user
     */
    suspend fun liftBanUser(groupId: String, userIds: List<String>) = kotlin.runCatching {
        banApi.apiV1BanGroupGroupIdDelete(
            groupId = groupId,
            useridsParam = UseridsParam(
                userIds = userIds
            )
        )
    }

    /**
     * 取得 社團 禁言清單
     * @param groupId 社團 id
     */
    suspend fun fetchBanList(groupId: String) = kotlin.runCatching {
        banApi.apiV1BanGroupGroupIdGet(groupId).checkResponseBody()
    }

    /**
     * 禁言使用者
     */
    suspend fun banUser(groupId: String, userId: String, banPeriodOption: BanPeriodOption) =
        kotlin.runCatching {
            banApi.apiV1BanGroupGroupIdPut(
                groupId = groupId,
                banParam = BanParam(
                    userid = userId,
                    periodOption = banPeriodOption
                )
            )
        }
}