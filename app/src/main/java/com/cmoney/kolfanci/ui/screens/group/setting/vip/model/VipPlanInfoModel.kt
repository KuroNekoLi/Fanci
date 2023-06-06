package com.cmoney.kolfanci.ui.screens.group.setting.vip.model

import android.os.Parcelable
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.kolfanci.R
import kotlinx.parcelize.Parcelize

/**
 * Vip 方案 詳細資訊 Model
 */
@Parcelize
data class VipPlanInfoModel(
    val name: String,                            //方案名稱
    val memberCount: Int,                        //成員人數
    val planIcon: Int = R.drawable.vip_diamond,  //方案 icon
    val planSourceDescList: List<String>,        //購買該方案的來源管道
    val members: List<GroupMember>               //購買該方案的成員清單
): Parcelable
