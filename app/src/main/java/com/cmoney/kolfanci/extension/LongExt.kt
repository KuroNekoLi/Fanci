package com.cmoney.kolfanci.extension

import java.util.Calendar
import java.util.TimeZone

fun Long.getDisplayFileSize(): String {
    val sizeInKB = this.toDouble() / 1024.0
    val sizeInMB = sizeInKB / 1024.0
    val sizeInGB = sizeInMB / 1024.0

    return when {
        this < 1024 -> "$this bytes"
        sizeInKB < 1024 -> String.format("%.2f KB", sizeInKB)
        sizeInMB < 1024 -> String.format("%.2f MB", sizeInMB)
        else -> String.format("%.2f GB", sizeInGB)
    }
}

fun Long.formatDuration(): String {
    val milliseconds = this
    val hours = (milliseconds / 3600000).toInt()
    val minutes = ((milliseconds % 3600000) / 60000).toInt()
    val seconds = ((milliseconds % 60000) / 1000).toInt()

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

/**
 * 獲取當天的 0 點 0 分 0 秒
 */
fun Long.startOfDayFromTimestamp(): Long {
    // 創建一個 Calendar 實例
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Taipei"))
    // 使用提供的時間戳轉換成毫秒，並設置 Calendar 的時間
    calendar.timeInMillis = this * 1000
    // 將小時、分、秒和毫秒設置為 0，以獲得當天的 0 點 0 分 0 秒
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    // 返回更新後的時間戳，並將毫秒轉換成秒
    return calendar.timeInMillis / 1000
}