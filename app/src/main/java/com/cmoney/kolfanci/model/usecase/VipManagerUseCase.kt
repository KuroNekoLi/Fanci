package com.cmoney.kolfanci.model.usecase

import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.ChannelPrivacy
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanInfoModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanPermissionModel

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

    /**
     * 取得某社團下該 vip 方案的詳細資訊
     *
     * @param group 指定社團
     * @param vipPlanModel 選擇的方案
     * @return 社團所有頻道此方案下的權限設定
     */
    fun getPermissions(group: Group, vipPlanModel: VipPlanModel): Result<List<VipPlanPermissionModel>> {
        return kotlin.runCatching {
            group.categories?.fold(mutableListOf<Channel>()) { acc, category ->
                acc.addAll(category.channels.orEmpty())
                acc
            }?.map { channel ->
                // TODO 取得每個頻道目前方案下設定的權限
                VipPlanPermissionModel(
                    name = channel.name.orEmpty(),
                    canEdit = when (channel.privacy) {
                        ChannelPrivacy.public -> false
                        ChannelPrivacy.private -> true
                        null -> false
                    },
                    permissionTitle = "假資料",
                    authType = "basic"
                )
            }.orEmpty()
        }
    }

}