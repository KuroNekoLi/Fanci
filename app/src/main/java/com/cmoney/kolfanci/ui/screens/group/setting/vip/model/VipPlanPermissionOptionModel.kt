package com.cmoney.kolfanci.ui.screens.group.setting.vip.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * VIP方案下權限選項
 *
 * @property name 權限名稱
 * @property description 權限功能描述
 * @property authType 權限類型
 */
@Parcelize
data class VipPlanPermissionOptionModel(
    val name: String,
    val description: String,
    val authType: String
): Parcelable
