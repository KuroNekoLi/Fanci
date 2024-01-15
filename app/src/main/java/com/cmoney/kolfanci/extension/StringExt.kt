package com.cmoney.kolfanci.extension

import androidx.compose.ui.graphics.Color
import com.cmoney.kolfanci.model.attachment.AttachmentType


fun String.toColor(): Color = Color(this.toLong(16))


/**
 * 轉換成app 在用的 attachment type
 */
fun String.toAttachmentType(): AttachmentType =
    when (this.lowercase()) {
        "voicemessage" -> AttachmentType.VoiceMessage
        "image" -> AttachmentType.Image
        "video" -> AttachmentType.Unknown
        "audio" -> AttachmentType.Audio
        "txt" -> AttachmentType.Txt
        "pdf" -> AttachmentType.Pdf
        else -> AttachmentType.Unknown
    }