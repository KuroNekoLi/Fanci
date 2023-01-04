package com.cmoney.fanci.model.usecase

import com.cmoney.fanci.extension.checkResponseBody
import com.cmoney.fanciapi.fanci.api.GroupApplyApi
import com.cmoney.fanciapi.fanci.model.ApplyStatus

class GroupApplyUseCase(private val groupApplyApi: GroupApplyApi) {

    /**
     * 抓取社團申請 清單,
     * @param groupId 社團 id
     * @param startWeight 起始權重 (分頁錨點用)
     * @param applyStauts 審核狀態, 預設為未審核
     */
    suspend fun fetchGroupApplyList(
        groupId: String,
        startWeight: Long = 0,
        applyStauts: ApplyStatus = ApplyStatus.unConfirmed
    ) = kotlin.runCatching {
        groupApplyApi.apiV1GroupApplyGroupGroupIdGet(
            groupId = groupId,
            applyStauts = applyStauts,
            startWeight = startWeight
        ).checkResponseBody()
    }
}