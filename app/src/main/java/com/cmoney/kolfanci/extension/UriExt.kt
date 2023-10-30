package com.cmoney.kolfanci.extension

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.loader.content.CursorLoader
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.AttachmentType
import java.io.File

/**
 *  取得 顯示檔案大小 format
 */
fun Uri.getDisplayFileSize(context: Context): String {
    val fileSize = getFileSize(context, this) ?: 0

    val sizeInKB = fileSize.toDouble() / 1024.0
    val sizeInMB = sizeInKB / 1024.0
    val sizeInGB = sizeInMB / 1024.0

    return when {
        fileSize < 1024 -> "$fileSize bytes"
        sizeInKB < 1024 -> String.format("%.2f KB", sizeInKB)
        sizeInMB < 1024 -> String.format("%.2f MB", sizeInMB)
        else -> String.format("%.2f GB", sizeInGB)
    }
}

private fun getFileSize(context: Context, uri: Uri): Long? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
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
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursorLoader = CursorLoader(context, this, projection, null, null, null)
    val cursor: Cursor? = cursorLoader.loadInBackground()

    cursor?.use {
        val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        it.moveToFirst()
        val filePath = it.getString(columnIndex)
        return File(filePath)
    }

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
    val mimeType = getFileType(context)
    val lowMimeType = mimeType.lowercase()
    return if (lowMimeType.startsWith("image")) {
        AttachmentType.Picture
    } else if (lowMimeType.startsWith("application")) {
        if (lowMimeType.contains("pdf")) {
            AttachmentType.Pdf
        } else {
            AttachmentType.Unknown
        }
    } else if (lowMimeType.startsWith("text")) {
        AttachmentType.Txt
    } else if (lowMimeType.startsWith("audio")) {
        AttachmentType.Music
    } else {
        AttachmentType.Unknown
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
 * 取得音檔長度 字串
 *
 * @param context
 * @return 字串 -> 00:00:00
 */
fun Uri.getAudioDuration(context: Context): String {
    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(context, this)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val durationMillis = durationStr?.toLong() ?: 0L
        return formatDuration(durationMillis)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        retriever.release()
    }
    return "00:00:00"
}

private fun formatDuration(milliseconds: Long): String {
    val hours = (milliseconds / 3600000).toInt()
    val minutes = ((milliseconds % 3600000) / 60000).toInt()
    val seconds = ((milliseconds % 60000) / 1000).toInt()

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}