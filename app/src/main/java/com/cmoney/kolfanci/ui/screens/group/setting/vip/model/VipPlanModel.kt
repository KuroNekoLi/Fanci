package com.cmoney.kolfanci.ui.screens.group.setting.vip.model

import com.cmoney.kolfanci.R

/**
 * Vip 方案 顯示 MODEL
 */
data class VipPlanModel(
    val name: String,           //方案名稱
    val memberCount: Int,       //成員人數
    val planIcon: Int = R.drawable.vip_diamond  //方案 icon
)
