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