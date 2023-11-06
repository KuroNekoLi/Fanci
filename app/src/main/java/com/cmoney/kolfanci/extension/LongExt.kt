package com.cmoney.kolfanci.extension

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