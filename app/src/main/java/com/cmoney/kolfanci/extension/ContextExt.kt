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