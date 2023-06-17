package com.cmoney.kolfanci.extension

import com.cmoney.fanciapi.fanci.model.GroupMember

fun GroupMember.isVip() = this.roleInfos?.isNotEmpty() == true