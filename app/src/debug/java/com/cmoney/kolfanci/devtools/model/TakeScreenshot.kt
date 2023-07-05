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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

private const val ROOT_FOLDER_NAME = "test_screenshot"

/**
 * 螢幕截圖
 *
 * @param context android context
 * @param relativePath 儲存的相對路徑 "分類/子分類..."
 * @param name 儲存的原始檔案名稱(會加上時間戳記: xxx-millisTime.jpg)
 */
fun CoroutineScope.takeScreenshot(context: Context, relativePath: String, name: String) {
    val activity = context as? Activity ?: return
    val fileName = "$name-${System.currentTimeMillis()}.jpg"
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            takeScreenshotAboveO(activity = activity) { bitmap ->
                launch(Dispatchers.IO) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        context.contentResolver.storeImageAboveQ(
                            folderName = if (relativePath.isNotEmpty()) {
                                "$ROOT_FOLDER_NAME/$relativePath"
                            } else {
                                ROOT_FOLDER_NAME
                            },
                            fileName = fileName,
                            outputBitmap = bitmap
                        )
                    } else {
                        context.storeImageBelowQ(
                            relativePath = relativePath,
                            fileName = fileName,
                            bitmap = bitmap
                        )
                    }
                }
            }
        } else {
            val bitmap = takeScreenshotBelowO(activity)
            launch(Dispatchers.IO) {
                context.storeImageBelowQ(
                    relativePath = relativePath,
                    fileName = fileName,
                    bitmap = bitmap
                )
            }
        }
    } catch (e: Exception) {
        // TODO screenshot flow failed
    }
}

private fun Context.storeImageBelowQ(
    relativePath: String,
    fileName: String,
    bitmap: Bitmap
) {
    val picturesFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    picturesFolder.mkdir()
    val testFolder = File(picturesFolder, ROOT_FOLDER_NAME)
    testFolder.mkdir()
    val saveFolder = if (relativePath.isNotEmpty()) {
        File(testFolder, relativePath).apply {
            mkdirs()
        }
    } else {
        testFolder
    }
    val saveFile = File(saveFolder, fileName)
    val isSuccess = saveFile.outputStream().use { outputStream ->
        val saveSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        saveSuccess
    }
    if (isSuccess) {
        notifyGalleryAddPic(saveFile.absolutePath)
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
