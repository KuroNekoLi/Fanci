package com.cmoney.kolfanci.extension

import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.User

/**
 * GroupUser as GroupMember
 * 有部分資訊無法提供
 */
fun User.asGroupMember(): GroupMember =
    GroupMember(
        id = id,
        name = name,
        thumbNail = thumbNail,
        serialNumber = serialNumber
        // TODO 跟 server 溝通中, 要增加欄位才有辦法轉換
        // isVip = this.isVip
    )