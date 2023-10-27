package com.cmoney.kolfanci.extension

import android.app.Activity
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.FileProvider
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.AttachmentType
import com.socks.library.KLog
import java.io.File

/**
 *  取得 顯示檔案大小 format
 */
fun Context.getDisplayFileSize(uri: Uri): String {
    val fileSize = getFileSize(this, uri) ?: 0

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

/**
 * 取得 Uri file name
 *
 * @param uri
 */
fun Context.getFileName(uri: Uri): String? {
    var fileName: String? = null
    val scheme = uri.scheme

    if (ContentResolver.SCHEME_CONTENT == scheme) {
        // If the URI scheme is content://, then query the content provider to get the file name.
        val projection = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                fileName = cursor.getString(columnIndex)
            }
        }
    } else if (ContentResolver.SCHEME_FILE == scheme) {
        // If the URI scheme is file://, then extract the file name from the URI.
        fileName = uri.lastPathSegment
    }
    return fileName
}

/**
 * 根據 Uri 區分檔案類型
 */
fun Context.getAttachmentType(uri: Uri): AttachmentType {
    val mimeType = getFileType(uri)
    val lowMimeType = mimeType.lowercase()
    return if (lowMimeType.startsWith("image")) {
        AttachmentType.Picture
    } else if (lowMimeType.startsWith("application")) {
        if (lowMimeType.contains("txt")) {
            AttachmentType.Txt
        } else if (lowMimeType.contains("pdf")) {
            AttachmentType.Pdf
        } else {
            AttachmentType.Unknown
        }
    } else if (lowMimeType.startsWith("audio")) {
        AttachmentType.Music
    } else {
        AttachmentType.Unknown
    }
}


/**
 * 取得檔案類型
 */
fun Context.getFileType(uri: Uri): String {
    val cr = contentResolver
    return cr.getType(uri).orEmpty()
    //    val mimeTypeMap = MimeTypeMap.getSingleton()
    //    return mimeTypeMap.getExtensionFromMimeType(r.getType(uri))
}

fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("", text)
    clipboard.setPrimaryClip(clip)
}

fun Context.share(shareMsg: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/分享給好友"
    intent.putExtra(Intent.EXTRA_TEXT, shareMsg)
    startActivity(Intent.createChooser(intent, "分享給好友"))
}

inline fun <reified T : Activity> Context.startActivity(block: Intent.() -> Unit = {}) {
    startActivity(Intent(this, T::class.java).apply(block))
}

inline fun <reified T : Any> Context.intent(body: Intent.() -> Unit): Intent {
    val intent = Intent(this, T::class.java)
    intent.body()
    return intent
}

inline fun <reified T : Any> Context.intent(): Intent {
    return Intent(this, T::class.java)
}

fun Context.goAppStore() {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
    } catch (e: android.content.ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
        )
    }
}

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

fun Context.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    startActivity(intent)
}

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.getCaptureUri(): Uri {
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//    if (storageDir?.isDirectory == true) {
//        storageDir.listFiles()?.forEach { file ->
//            file.delete()
//        }
//    }

    val file = File(storageDir, System.currentTimeMillis().toString() + ".jpg")

    return FileProvider.getUriForFile(
        this,
        "${this.packageName}.provider",
        file
    )
}

fun Context.openCustomTab(uri: Uri) {
    val customTabsIntent = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()
    customTabsIntent.launchUrl(this, uri)
}

/**
 * 是否開啟通知
 */
fun Context.isNotificationsEnabled(): Boolean {
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ?: return false
    return notificationManager.areNotificationsEnabled()
}

/**
 * 打開系統 推播設定頁面
 */
fun Context.openNotificationSetting() {
    Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        data = uri
        flags = FLAG_ACTIVITY_NEW_TASK
        startActivity(this)
    }
}

/**
 * 檢查該 activity 是否執行中
 */
fun Context.isActivityRunning(className: String): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    return activityManager.appTasks
        .filter { it.taskInfo != null }
        .filter { it.taskInfo.baseActivity != null }
        .any { it.taskInfo.baseActivity?.className == className }
}