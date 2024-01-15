package com.cmoney.kolfanci.extension

import android.content.ContentResolver
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.model.attachment.AttachmentType
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 *  取得 顯示檔案大小 format
 */
fun Uri.getDisplayFileSize(context: Context): String {
    val fileSize = getFileSize(context) ?: 0
    return fileSize.getDisplayFileSize()
}

fun Uri.getFileSize(context: Context): Long? {
    val cursor = context.contentResolver.query(this, null, null, null, null)
    val filePath = cursor?.use {
        if (it.moveToFirst()) {
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            it.getLong(sizeIndex)
        } else {
            null
        }
    }
    return filePath
}

fun Uri.uriToFile(context: Context): File {
    if (this.scheme.equals(ContentResolver.SCHEME_FILE)) {
        return File(this.path.orEmpty())
    } else if (this.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
        val contentResolver = context.contentResolver
        val displayName = getFileName(context).orEmpty()

        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            inputStream = contentResolver.openInputStream(this)
            val cache = File(context.cacheDir.absolutePath, displayName)
            outputStream = FileOutputStream(cache)

            val buffer = ByteArray(4 * 1024)
            var read: Int

            while (inputStream!!.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }
            outputStream.flush()

            return cache

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


//    val projection = arrayOf(MediaStore.Images.Media.DATA)
//    val cursorLoader = CursorLoader(context, this, projection, null, null, null)
//    val cursor: Cursor? = cursorLoader.loadInBackground()
//
//    cursor?.use {
//        val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//        it.moveToFirst()
//        val filePath = it.getString(columnIndex)
//        return File(filePath)
//    }

    throw IllegalArgumentException("Failed to convert URI to File")
}

/**
 * 取得 Uri file name
 *
 * @param context
 */
fun Uri.getFileName(context: Context): String? {
    var fileName: String? = null
    val scheme = this.scheme

    if (ContentResolver.SCHEME_CONTENT == scheme) {
        // If the URI scheme is content://, then query the content provider to get the file name.
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        context.contentResolver.query(this, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                fileName = cursor.getString(columnIndex)
            }
        }
    } else if (ContentResolver.SCHEME_FILE == scheme) {
        // If the URI scheme is file://, then extract the file name from the URI.
        fileName = this.lastPathSegment
    }
    return fileName
}

/**
 * 根據 Uri 區分檔案類型
 */
fun Uri.getAttachmentType(context: Context): AttachmentType {
    val mimeType = getMimeType(context) ?: ""
    val lowMimeType = mimeType.lowercase()
    return if (lowMimeType.startsWith("image")) {
        AttachmentType.Image
    } else if (lowMimeType.startsWith("application")) {
        if (lowMimeType.contains("pdf")) {
            AttachmentType.Pdf
        } else {
            AttachmentType.Unknown
        }
    } else if (lowMimeType.startsWith("text")) {
        AttachmentType.Txt
    } else if (lowMimeType.startsWith("audio")) {
        if (isRecordFile()) {
            AttachmentType.VoiceMessage
        } else {
            AttachmentType.Audio
        }

    } else {
        AttachmentType.Unknown
    }
}

/**
 * 檔案 上傳時 要傳類型給後端知道
 */
fun Uri.getUploadFileType(context: Context): String {
    val mimeType = getMimeType(context)
    val lowMimeType = mimeType?.lowercase()
    return if (lowMimeType != null) {
        if (lowMimeType.startsWith("audio")) {
            "audio"
        } else if (lowMimeType.startsWith("video")) {
            "video"
        } else {
            "document"
        }
    } else {
        ""
    }
}


/**
 * 取得檔案類型
 */
fun Uri.getFileType(context: Context): String {
    val cr = context.contentResolver
    return cr.getType(this).orEmpty()
    //    val mimeTypeMap = MimeTypeMap.getSingleton()
    //    return mimeTypeMap.getExtensionFromMimeType(r.getType(uri))
}

/**
 * 取得音檔類型
 */
fun Uri.getMimeType(context: Context): String? {
    val type: String?
    val extension = MimeTypeMap.getFileExtensionFromUrl(this.toString())
    type = if (!extension.isNullOrEmpty()) {
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    } else {
        getFileType(context)
    }
    return type
}

/**
 * 取得音檔長度 字串
 *
 * @param context
 * @return 字串 -> 00:00:00
 */
fun Uri.getAudioDisplayDuration(context: Context): String {
    try {
        val durationMillis = getAudioDuration(context)
        return durationMillis.formatDuration()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return "00:00:00"
}

/**
 * 取得音檔長度
 */
fun Uri.getAudioDuration(context: Context): Long {
    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(context, this)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        return durationStr?.toLong() ?: 0L
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        retriever.release()
    }
    return 0L
}

/**
 * 判斷 Uri 是否為網址
 */
fun Uri.isURL(): Boolean = this.scheme?.startsWith("http") == true

/**
 * 轉換成上傳物件
 */
fun Uri.toUploadFileItem(
    context: Context,
    status: AttachmentInfoItem.Status = AttachmentInfoItem.Status.Undefined,
    serverUrl: String = ""
): AttachmentInfoItem =
    AttachmentInfoItem(
        uri = this,
        status = status,
        serverUrl = serverUrl,
        filename = this.getFileName(context).orEmpty(),
        fileSize = this.getFileSize(context) ?: 0L,
        duration = this.getAudioDuration(context),
        attachmentType = this.getAttachmentType(context)
    )

/**
 * 判斷是否為錄音檔
 */
fun Uri.isRecordFile(): Boolean {
    val uriString = this.toString()
    val isAacFile = uriString.endsWith(".aac")
    val isInCmoneyPath = uriString.contains("/Android/data/com.cmoney.kolfanci.debug/cache")
    return isAacFile && isInCmoneyPath
}