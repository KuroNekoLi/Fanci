package com.cmoney.kolfanci.extension

import android.net.Uri
import com.cmoney.fanciapi.fanci.model.IMedia
import com.cmoney.fanciapi.fanci.model.Media
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem

/**
 * 取得 檔案名稱
 */
fun Media.getFileName(): String {
    return when (this.type?.toAttachmentType()) {
        AttachmentType.Audio -> audio?.fileName.orEmpty()
        AttachmentType.Image -> ""
        AttachmentType.Pdf -> pdf?.fileName.orEmpty()
        AttachmentType.Txt -> txt?.fileName.orEmpty()
        AttachmentType.Unknown -> ""
        else -> ""
    }
}

/**
 * 取得 檔案大小
 */
fun Media.getFleSize(): Long {
    return when (this.type?.toAttachmentType()) {
        AttachmentType.Audio -> audio?.fileSize ?: 0L
        AttachmentType.Image -> 0
        AttachmentType.Pdf -> pdf?.fileSize ?: 0L
        AttachmentType.Txt -> txt?.fileSize ?: 0L
        AttachmentType.Unknown -> 0
        else -> 0
    }
}

/**
 * 取得 音檔長度
 */
fun Media.getDuration(): Long {
    return when (this.type?.toAttachmentType()) {
        AttachmentType.Audio -> audio?.duration ?: 0L
        AttachmentType.Image -> 0L
        AttachmentType.Pdf -> 0L
        AttachmentType.Txt -> 0L
        AttachmentType.Unknown -> 0L
        else -> 0L
    }
}

/**
 * 將 server 給的 List media 轉換成 map
 */
fun List<Media>.toAttachmentTypeMap() =
    this.map {
        (it.type?.toAttachmentType() ?: AttachmentType.Unknown) to it
    }.groupBy({
        it.first
    }, {
        it.second
    })

/**
 * 將 server 給的 List media 轉換成 UploadFileItem map
 */
fun List<Media>.toUploadFileItemMap() =
    this.map {
        (it.type?.toAttachmentType() ?: AttachmentType.Unknown) to AttachmentInfoItem(
            uri = Uri.parse(it.resourceLink),
            status = AttachmentInfoItem.Status.Success,
            serverUrl = it.resourceLink.orEmpty(),
            filename = it.getFileName(),
            fileSize = it.getFleSize(),
            duration = it.getDuration()
        )
    }.groupBy({
        it.first
    }, {
        it.second
    })

fun Media.getDisplayType() = when (type?.toAttachmentType()) {
    AttachmentType.Audio -> "(音檔)"
    AttachmentType.Image -> "(圖片)"
    AttachmentType.Pdf -> "(檔案)"
    AttachmentType.Txt -> "(檔案)"
    AttachmentType.Unknown -> "(未知)"
    null -> ""
}

fun IMedia.getDisplayType() = when (type?.toAttachmentType()) {
    AttachmentType.Audio -> "(音檔)"
    AttachmentType.Image -> "(圖片)"
    AttachmentType.Pdf -> "(檔案)"
    AttachmentType.Txt -> "(檔案)"
    AttachmentType.Unknown -> "(未知)"
    null -> ""
}

