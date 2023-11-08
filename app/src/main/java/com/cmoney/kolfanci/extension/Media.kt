package com.cmoney.kolfanci.extension

import android.net.Uri
import com.cmoney.fanciapi.fanci.model.Media
import com.cmoney.fanciapi.fanci.model.MediaType
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem

/**
 * 取得 檔案名稱
 */
fun Media.getFileName(): String {
    return when (this.type) {
        MediaType.image -> ""
        MediaType.video -> ""
        MediaType.audio -> audio?.fileName.orEmpty()
        MediaType.txt -> txt?.fileName.orEmpty()
        MediaType.pdf -> pdf?.fileName.orEmpty()
        null -> ""
    }
}

/**
 * 取得 檔案大小
 */
fun Media.getFleSize(): Long {
    return when (this.type) {
        MediaType.image -> 0
        MediaType.video -> 0
        MediaType.audio -> audio?.fileSize ?: 0L
        MediaType.txt -> txt?.fileSize ?: 0L
        MediaType.pdf -> pdf?.fileSize ?: 0L
        null -> 0
    }
}

/**
 * 取得 音檔長度
 */
fun Media.getDuration(): Long {
    return when (this.type) {
        MediaType.image -> 0
        MediaType.video -> 0
        MediaType.audio -> audio?.duration ?: 0L
        MediaType.txt -> 0L
        MediaType.pdf -> 0L
        null -> 0
    }
}

/**
 * 轉換成app 在用的 type
 */
fun MediaType.toAttachmentType(): AttachmentType =
    when (this) {
        MediaType.image -> AttachmentType.Image
        MediaType.video -> AttachmentType.Unknown
        MediaType.audio -> AttachmentType.Audio
        MediaType.txt -> AttachmentType.Txt
        MediaType.pdf -> AttachmentType.Pdf
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