package com.cmoney.kolfanci.extension

import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.User

fun User.toGroupMember(): GroupMember =
    GroupMember(
        id = id,
        name = name,
        thumbNail = thumbNail,
        serialNumber = serialNumber
    )