package com.cmoney.kolfanci.devtools.model

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.view.PixelCopy
import androidx.core.os.HandlerCompat
import com.cmoney.tools_android.util.notifyGalleryAddPic
import com.cmoney.tools_android.util.storeImageAboveQ
import com.cmoney.tools_android.util.storeImageBelowQ
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

/**
 * 螢幕截圖
 *
 * @param context android context
 */
fun CoroutineScope.takeScreenshot(context: Context) {
    val activity = context as? Activity ?: return
    val picturesFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    if (!picturesFolder.exists()) {
        picturesFolder.mkdir()
    }
    val testFolder = File(picturesFolder, "test_screenshot")
    if (!testFolder.exists()) {
        testFolder.mkdir()
    }
    val fileName = "screenshot-${System.currentTimeMillis()}.jpg"
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            takeScreenshotAboveO(activity = activity) { bitmap ->
                launch(Dispatchers.IO) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.contentResolver.storeImageAboveQ(
                            folderName = testFolder.name,
                            fileName = fileName,
                            outputBitmap = bitmap
                        )
                    } else {
                        context.storeImageBelowQ(
                            folderName = testFolder.name,
                            fileName = fileName,
                            outputBitmap = bitmap,
                            format = Bitmap.CompressFormat.JPEG
                        ).onSuccess { absolutePath ->
                            context.notifyGalleryAddPic(absolutePath)
                        }
                    }
                }
            }
        } else {
            val bitmap = takeScreenshotBelowO(activity)
            launch(Dispatchers.IO) {
                context.storeImageBelowQ(
                    folderName = testFolder.name,
                    fileName = fileName,
                    outputBitmap = bitmap,
                    format = Bitmap.CompressFormat.JPEG
                ).onSuccess { absolutePath ->
                    context.notifyGalleryAddPic(absolutePath)
                }
            }
        }
    } catch (e: Exception) {
        // TODO screenshot flow failed
        println("e: $e")
    }
}

@TargetApi(Build.VERSION_CODES.O)
private fun takeScreenshotAboveO(activity: Activity, onScreenshot: (Bitmap) -> Unit) {
    val window = activity.window
    val rootView = window.decorView.rootView
    val bitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
    val locationOfViewInWindow = IntArray(2)
    rootView.getLocationInWindow(locationOfViewInWindow)
    PixelCopy.request(
        window,
        Rect(
            locationOfViewInWindow[0],
            locationOfViewInWindow[1],
            locationOfViewInWindow[0] + rootView.width,
            locationOfViewInWindow[1] + rootView.height
        ),
        bitmap,
        { copyResult ->
            if (copyResult == PixelCopy.SUCCESS) {
                onScreenshot(bitmap)
            }
        },
        HandlerCompat.createAsync(Looper.getMainLooper())
    )
}

private fun takeScreenshotBelowO(activity: Activity): Bitmap {
    val window = activity.window
    val rootView = window.decorView.rootView
    rootView.isDrawingCacheEnabled = true
    val bitmap = Bitmap.createBitmap(rootView.drawingCache)
    rootView.isDrawingCacheEnabled = false
    return bitmap
}
