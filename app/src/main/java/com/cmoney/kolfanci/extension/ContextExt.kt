package com.cmoney.kolfanci.extension

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

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