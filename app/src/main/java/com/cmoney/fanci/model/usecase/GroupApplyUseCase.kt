package com.cmoney.fanci.model.usecase

import com.cmoney.fanci.extension.checkResponseBody
import com.cmoney.fanciapi.fanci.api.GroupApplyApi
import com.cmoney.fanciapi.fanci.model.ApplyStatus
import com.cmoney.fanciapi.fanci.model.GroupApplyParam
import com.cmoney.fanciapi.fanci.model.GroupRequirementAnswer

class GroupApplyUseCase(private val groupApplyApi: GroupApplyApi) {

    /**
     * 申請加入審核社團
     *
     * @param groupId 社團id
     * @param groupRequirementAnswer 問題及答案
     */
    suspend fun joinGroupWithQuestion(
        groupId: String,
        groupRequirementAnswer: List<GroupRequirementAnswer>
    ) = kotlin.runCatching {
        groupApplyApi.apiV1GroupApplyGroupGroupIdPut(
            groupId = groupId,
            groupApplyParam = GroupApplyParam(
                groupRequirementAnswer
            )
        ).checkResponseBody()
    }


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