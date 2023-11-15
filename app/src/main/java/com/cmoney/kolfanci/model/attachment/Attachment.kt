package com.cmoney.kolfanci.model.attachment

import android.content.Context
import android.net.Uri
import com.cmoney.fanciapi.fanci.model.AudioContent
import com.cmoney.fanciapi.fanci.model.ImageContent
import com.cmoney.fanciapi.fanci.model.Media
import com.cmoney.fanciapi.fanci.model.PdfContent
import com.cmoney.fanciapi.fanci.model.TxtContent
import com.cmoney.kolfanci.extension.getAudioDuration

/**
 * 將附加檔案 List 轉為, 上傳用的 Media List
 */
fun List<Pair<AttachmentType, AttachmentInfoItem>>.toUploadMedia(context: Context): List<Media> {
    val medias = mutableListOf<Media>()

    //處理 附加檔案
    this.forEach { item ->
        val attachmentType = item.first
        val attachmentInfo = item.second
        val file = attachmentInfo.uri
        val serverUrl = attachmentInfo.serverUrl
        val fileName = attachmentInfo.filename
        val fileSize = attachmentInfo.fileSize

        when (attachmentType) {
            AttachmentType.Audio -> Media(
                resourceLink = serverUrl,
                type = AttachmentType.Audio.name,
                audio = AudioContent(
                    fileName = fileName,
                    fileSize = fileSize,
                    duration = file.getAudioDuration(context)
                )
            )

            AttachmentType.Image -> Media(
                resourceLink = serverUrl,
                type = AttachmentType.Image.name,
                image = ImageContent()
            )

            AttachmentType.Pdf -> Media(
                resourceLink = serverUrl,
                type = AttachmentType.Pdf.name,
                pdf = PdfContent(
                    fileName = fileName,
                    fileSize = fileSize,
                )
            )

            AttachmentType.Txt -> Media(
                resourceLink = serverUrl,
                type = AttachmentType.Txt.name,
                txt = TxtContent(
                    fileName = fileName,
                    fileSize = fileSize,
                )
            )

            AttachmentType.Unknown -> {
                null
            }

            AttachmentType.Choice -> {
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
    abstract val name: String

    object Image : AttachmentType() {
        override val name: String
            get() = "Image"
    }

    object Audio : AttachmentType() {
        override val name: String
            get() = "Audio"
    }

    object Txt : AttachmentType() {
        override val name: String
            get() = "Txt"
    }

    object Pdf : AttachmentType() {
        override val name: String
            get() = "Pdf"
    }

    object Unknown : AttachmentType() {
        override val name: String
            get() = ""
    }

    /**
     * 選擇題
     */
    object Choice : AttachmentType() {
        override val name: String
            get() = ""
    }
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

    /**
     * 附加檔案 item, 是否可以點擊預覽
     */
    fun isAttachmentItemClickable(): Boolean =
        (status == Status.Undefined || status == Status.Success)
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