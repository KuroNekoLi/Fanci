package com.cmoney.kolfanci.extension

import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel

fun FanciRole.toVipPlanModel(): VipPlanModel {
    return VipPlanModel(
        id = this.id.orEmpty(),
        name = this.name.orEmpty(),
        memberCount = this.userCount?.toInt() ?: 0,
        description = ""
    )
}