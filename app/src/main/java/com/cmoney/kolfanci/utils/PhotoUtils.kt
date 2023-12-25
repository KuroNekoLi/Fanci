package com.cmoney.kolfanci.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.socks.library.KLog
import okhttp3.internal.closeQuietly
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object PhotoUtils {
    suspend fun createUploadImage(uri: Uri, context: Context): String? {
        var tempFile: File? = null
        var outputStream: OutputStream? = null
        var inputStream: InputStream? = null
        try {
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            tempFile = File(storageDir, "uploadedImage.jpg")
            outputStream = FileOutputStream(tempFile)
            inputStream = context.contentResolver?.openInputStream(uri) ?: return null
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            outputStream.write(buffer)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.closeQuietly()
            outputStream?.closeQuietly()
            tempFile?.deleteOnExit()
        }
        return compress(tempFile!!.path, context)
    }

    /**
     * 根據圖片路徑壓縮圖片，並返回壓縮後圖片路徑
     * @param path
     * @return
     */
    private suspend fun compress(path: String, context: Context): String? {
        // 壓縮照片
        var bm = getImage(path) ?: return null
        val degree = readPictureDegree(path) // 讀取照片旋轉角度
        if (degree > 0) {
            bm = rotateBitmap(bm, degree) // 旋轉照片
        }
        val file: String
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        file = File(storageDir, "uploadedImage.jpg").absolutePath
        val f = File(file)
        if (!f.exists()) {
            f.mkdirs()
        }
        val picName = ("compressedImage.jpg")
        val resultFilePath = file + picName
        var out: OutputStream? = null
        try {
            out = FileOutputStream(resultFilePath)
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            out?.closeQuietly()
        }

        return resultFilePath
    }

    /**
     * 第二：圖片按比例大小壓縮方法（根據路徑獲取圖片並壓縮）：
     * @param srcPath
     * @return
     */
    private suspend fun getImage(srcPath: String): Bitmap? {
        val newOpts = BitmapFactory.Options()
        // 開始讀入圖片，此時把options.inJustDecodeBounds設回true了
        newOpts.inJustDecodeBounds = true
        val bitmap: Bitmap // 此時返回bm為空

        newOpts.inJustDecodeBounds = false
        val w = newOpts.outWidth
        val h = newOpts.outHeight
        val hh = 800f // 這裡設置高度為800f
        val ww = 480f // 這裡設置高度為480f
        // 縮放比。由於是固定比例缩放，只用高或者寬其中一個數據進行計算即可
        var be = 1 // be = 1表示不缩縮放
        if (w > h && w > ww) { // 如果寬度大的話根據寬度固定大小缩放
            be = (newOpts.outWidth / ww).toInt()
        } else if (w < h && h > hh) { // 如果高度高的話根據高度固定大小缩放
            be = (newOpts.outHeight / hh).toInt()
        }
        if (be <= 0)
            be = 1
        newOpts.inSampleSize = be // 設置縮放比例
        // 重新讀入圖片，注意此時已經把options.inJustDecodeBounds設回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts)
        return compressImage(bitmap) // 壓縮好比例大小後再進行質量壓縮
    }

    /**
     * 質量壓縮
     * @param image
     * @return
     */
    private suspend fun compressImage(image: Bitmap): Bitmap? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            byteArrayOutputStream
        ) // 質量壓縮方法，這裡100表示不壓縮，把壓縮後的數據存放到byteArrayOutputStream中
        var options = 99 // 這個只能是0-100之間不包括0和100不然會報異常
        while (byteArrayOutputStream.toByteArray().size / 1024 > 200 && options > 0) {    // 循環判斷壓縮後圖片是否大於200kb，大於則繼續壓縮
            byteArrayOutputStream.reset() // 重置byteArrayOutputStream及清空
            image.compress(
                Bitmap.CompressFormat.JPEG,
                options,
                byteArrayOutputStream
            ) // 這裡壓縮options%，把壓縮後的數據存放到byteArrayOutputStream中
            options -= 10 // 每次都减少10
        }
        val isBm =
            ByteArrayInputStream(byteArrayOutputStream.toByteArray()) // 把壓縮後的數據byteArrayOutputStream存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null)
    }

    /**
     * 讀取照片的旋轉角度
     * @param path
     * @return
     */
    private suspend fun readPictureDegree(path: String): Float {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            when (exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )) {
                ExifInterface.ORIENTATION_ROTATE_90 -> {
                    degree = 90
                }

                ExifInterface.ORIENTATION_ROTATE_180 -> {
                    degree = 180
                }

                ExifInterface.ORIENTATION_ROTATE_270 -> {
                    degree = 270
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return degree.toFloat()
    }

    /**
     * 旋轉照片
     * @param bitmap
     * @param rotate
     * @return
     */
    private suspend fun rotateBitmap(bitmap: Bitmap, rotate: Float): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val matrix = Matrix()
        matrix.postRotate(rotate)
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true)
    }

    /**
     * 將ImageBitmap寫入IO
     * @param imageBitmap 圖片的ImageBitmap
     * @return 檔案的Uri，可能為空
     */
    fun saveBitmapAndGetUri(context: Context, imageBitmap: ImageBitmap): Uri? {
        val bitmap = imageBitmap.asAndroidBitmap()
        val directory = context.cacheDir
        val fileName = System.currentTimeMillis().toString() + ".png"
        val file = File(directory, fileName)

        return try {
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: Exception) {
            KLog.e("saveBitmapAndGetUri: ${e.printStackTrace()}", e)
            null
        }
    }
}