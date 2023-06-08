package com.cmoney.kolfanci.ui.screens.group.setting.vip.model

import android.os.Parcelable
import com.cmoney.kolfanci.R
import kotlinx.parcelize.Parcelize

/**
 * Vip 方案 顯示 MODEL
 */
@Parcelize
data class VipPlanModel(
    val id: String,                             //方案id
    val name: String,                           //方案名稱
    val memberCount: Int,                       //成員人數
    val planIcon: Int = R.drawable.vip_diamond, //方案 icon
    val description: String                     //方案描述
): Parcelable
