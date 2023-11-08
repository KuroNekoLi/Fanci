package com.cmoney.kolfanci.model.attachment

import android.content.Context
import android.net.Uri
import com.cmoney.fanciapi.fanci.model.AudioContent
import com.cmoney.fanciapi.fanci.model.ImageContent
import com.cmoney.fanciapi.fanci.model.Media
import com.cmoney.fanciapi.fanci.model.MediaType
import com.cmoney.fanciapi.fanci.model.PdfContent
import com.cmoney.fanciapi.fanci.model.TxtContent
import com.cmoney.kolfanci.extension.getAudioDuration
import com.cmoney.kolfanci.extension.getFileName
import com.cmoney.kolfanci.extension.getFileSize

/**
 * 將附加檔案 List 轉為, 上傳用的 Media List
 */
fun List<Pair<AttachmentType, AttachmentInfoItem>>.toUploadMedia(context: Context): List<Media> {
    val medias = mutableListOf<Media>()

    //處理 附加檔案
    this.forEach { item ->
        val attachmentType = item.first
        val file = item.second.uri
        val serverUrl = item.second.serverUrl
        val fileName = file.getFileName(context).orEmpty()
        val fileSize = file.getFileSize(context) ?: 0L

        when (attachmentType) {
            AttachmentType.Audio -> Media(
                resourceLink = serverUrl,
                type = MediaType.audio,
                audio = AudioContent(
                    fileName = fileName,
                    fileSize = fileSize,
                    duration = file.getAudioDuration(context)
                )
            )

            AttachmentType.Image -> Media(
                resourceLink = serverUrl,
                type = MediaType.image,
                image = ImageContent()
            )

            AttachmentType.Pdf -> Media(
                resourceLink = serverUrl,
                type = MediaType.pdf,
                pdf = PdfContent(
                    fileName = fileName,
                    fileSize = fileSize,
                )
            )

            AttachmentType.Txt -> Media(
                resourceLink = serverUrl,
                type = MediaType.txt,
                txt = TxtContent(
                    fileName = fileName,
                    fileSize = fileSize,
                )
            )

            AttachmentType.Unknown -> {
                null
            }
        }?.apply {
            medias.add(this)
        }
    }
    return medias
}

/**
 * 附加檔案 fanci 支援類型
 */
sealed class AttachmentType {
    object Image : AttachmentType()

    object Audio : AttachmentType()

    object Txt : AttachmentType()

    object Pdf : AttachmentType()

    object Unknown : AttachmentType()
}

/**
 * 檔案 上傳狀態
 *
 * @param uri 上傳的檔案
 * @param status 上傳狀態
 * @param serverUrl 上傳成功後 拿到的 url
 * @param filename 檔案名稱
 * @param fileSize 檔案大小
 * @param duration 音檔長度 (option)
 */
data class AttachmentInfoItem(
    val uri: Uri = Uri.EMPTY,
    val status: Status = Status.Undefined,
    val serverUrl: String = "",
    val filename: String = "",
    val fileSize: Long = 0,
    val duration: Long? = 0
) {
    /**
     * 檔案上傳狀態
     * @param description 說明
     */
    sealed class Status(description: String = "") {
        //還未確定要上傳
        object Undefined : Status()

        //等待上傳中
        object Pending : Status()

        //上傳成功
        object Success : Status()

        //上傳中
        object Uploading : Status()

        //上傳失敗
        data class Failed(val reason: String) : Status(reason)
    }
}

/**
 * 重新上傳檔案 info
 *
 * @param type 檔案類型
 * @param file 檔案
 * @param title dialog title
 * @param description dialog content
 */
data class ReSendFile(
    val type: AttachmentType,
    val file: Uri,
    val title: String,
    val description: String
)