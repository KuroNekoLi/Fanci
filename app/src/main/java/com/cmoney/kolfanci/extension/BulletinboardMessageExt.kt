package com.cmoney.kolfanci.extension

import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.User
import com.cmoney.kolfanci.utils.Utils

fun BulletinboardMessage.displayPostTime(): String {
    this.createUnixTime?.apply {
        return Utils.timesMillisToDate(this.times(1000))
    }
    return ""
}

fun BulletinboardMessage.isMyPost(myInfo: User?): Boolean = author?.id == myInfo?.id