package com.cmoney.kolfanci.extension

import com.cmoney.fanciapi.fanci.model.BanPeriodOption

fun BanPeriodOption.toDisplayDay(): String = when(this) {
    BanPeriodOption.oneDay -> "1日"
    BanPeriodOption.threeDay -> "3日"
    BanPeriodOption.oneWeek -> "7日"
    BanPeriodOption.oneMonth -> "30日"
    BanPeriodOption.forever -> "永久"
}