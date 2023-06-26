package com.cmoney.kolfanci.ui.screens.group.setting.vip.model

import android.os.Parcelable
import com.cmoney.fanciapi.fanci.model.ChannelAuthType
import kotlinx.parcelize.Parcelize

/**
 * VIP方案權限管理顯示資料
 *
 * @property id 頻道編號
 * @property name 名稱
 * @property canEdit 是否可以編輯
 * @property permissionTitle 權限顯示文字
 * @property authType 權限類型
 */
@Parcelize
data class VipPlanPermissionModel(
    val id: String,
    val name: String,
    val canEdit: Boolean,
    val permissionTitle: String,
    val authType: ChannelAuthType
): Parcelable
