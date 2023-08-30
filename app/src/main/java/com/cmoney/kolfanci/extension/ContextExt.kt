package com.cmoney.kolfanci.extension

import android.app.Activity
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.FileProvider
import java.io.File


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