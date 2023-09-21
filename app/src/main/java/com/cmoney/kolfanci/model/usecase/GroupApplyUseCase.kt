package com.cmoney.kolfanci.model.usecase

import com.cmoney.fanciapi.fanci.api.GroupApi
import com.cmoney.fanciapi.fanci.api.GroupApplyApi
import com.cmoney.fanciapi.fanci.model.ApplyStatus
import com.cmoney.fanciapi.fanci.model.GroupApplyParam
import com.cmoney.fanciapi.fanci.model.GroupApplyStatusParam
import com.cmoney.fanciapi.fanci.model.GroupRequirementAnswer
import com.cmoney.kolfanci.extension.checkResponseBody

class GroupApplyUseCase(private val groupApplyApi: GroupApplyApi, private val groupApi: GroupApi) {

    /**
     * 審核加入社團
     *
     * @param groupId 社團id
     * @param applyId 要處理的申請單id
     * @param applyStatus 審核狀態, 允許 or 拒絕
     */
    suspend fun approval(groupId: String, applyId: List<String>, applyStatus: ApplyStatus) =
        kotlin.runCatching {
            groupApplyApi.apiV1GroupApplyGroupGroupIdApprovalPut(
                groupId = groupId,
                groupApplyStatusParam = GroupApplyStatusParam(
                    status = applyStatus,
                    applyIds = applyId
                )
            ).checkResponseBody()
        }

    /**
     * 查詢未處理的入社申請筆數
     *
     * @param groupId 社團id
     * @param applyStauts 查詢審核狀態, 預設 未審核
     */
    suspend fun getUnApplyCount(
        groupId: String,
        applyStauts: ApplyStatus = ApplyStatus.unConfirmed
    ) = kotlin.runCatching {
        groupApplyApi.apiV1GroupApplyGroupGroupIdCountGet(groupId = groupId).checkResponseBody()
    }

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
     * @param applyStatus 審核狀態, 預設為未審核
     */
    suspend fun fetchGroupApplyList(
        groupId: String,
        startWeight: Long = 0,
        applyStatus: ApplyStatus = ApplyStatus.unConfirmed
    ) = kotlin.runCatching {
        groupApplyApi.apiV1GroupApplyGroupGroupIdGet(
            groupId = groupId,
            applyStatus = applyStatus,
            startWeight = startWeight
        ).checkResponseBody()
    }

    /**
     * 取得 我申請的社團-審核中
     */
    suspend fun fetchAllMyGroupApplyUnConfirmed() = kotlin.runCatching {
        groupApplyApi.apiV1GroupApplyGroupAllMeGet().checkResponseBody().filter {
            it.apply?.status == ApplyStatus.unConfirmed
        }.mapNotNull {
            val groupId = it.apply?.groupId.orEmpty()
            groupApi.apiV1GroupGroupIdGet(groupId = groupId).checkResponseBody()
        }
    }

    /**
     * 取得 我的社團申請狀態
     * @param groupId 社團 id
     */
    suspend fun fetchMyApply(groupId: String) = kotlin.runCatching {
        groupApplyApi.apiV1GroupApplyGroupGroupIdMeGet(
            groupId = groupId
        ).checkResponseBody()
        }
}