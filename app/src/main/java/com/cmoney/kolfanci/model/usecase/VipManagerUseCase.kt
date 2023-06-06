package com.cmoney.kolfanci.model.usecase

import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanInfoModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel

class VipManagerUseCase {

    companion object {
        fun getVipPlanMockData() = listOf(
            VipPlanModel(
                name = "高級學員",
                memberCount = 10
            ),
            VipPlanModel(
                name = "進階學員",
                memberCount = 5
            ),
            VipPlanModel(
                name = "初階學員",
                memberCount = 0
            )
        )

        fun getVipPlanInfoMockData() = VipPlanInfoModel(
            name = "高級學員",
            memberCount = 10,
            planSourceDescList = listOf(
                "・30元訂閱促銷方案",
                "・99元月訂閱方案"
            ),
            members = emptyList()
        )
    }

    /**
     * 取得 vip 方案清單
     *
     * @param group 社團
     */
    fun getVipPlan(group: Group) = kotlin.runCatching {
//            emptyList<VipPlanModel>()
        //TODO wait server api
        getVipPlanMockData()
    }


    /**
     * 取得該 vip 方案 詳細資訊
     *
     * @param vipPlanModel 選擇的方案
     */
    fun getVipPlanInfo(vipPlanModel: VipPlanModel) = kotlin.runCatching {
        getVipPlanInfoMockData()
    }

}