package com.cmoney.kolfanci.model.usecase

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.cmoney.backend2.centralizedimage.service.CentralizedImageWeb
import com.cmoney.backend2.centralizedimage.service.api.upload.GenreAndSubGenre
import com.cmoney.backend2.centralizedimage.service.api.upload.UploadResponseBody
import com.cmoney.compress_image.CompressSetting
import com.cmoney.compress_image.resizeAndCompressAndRotateImage
import com.socks.library.KLog
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

private const val IMAGE_SIZE = 1024 * 1024  //1MB

class UploadImageUseCase(
    val context: Context, private val centralizedImageWeb: CentralizedImageWeb
) {
    private val TAG = UploadImageUseCase::class.java.simpleName

    suspend fun uploadImage(uriLis: List<Uri>) = flow {
        uriLis.forEach { uri ->
            val uploadResponseBody = fetchImageUrl(uri)
            uploadResponseBody?.let { uploadResponse ->
                KLog.i(TAG, "uploadImage success:$uploadResponse")
                emit(Pair(uri, uploadResponse.url.orEmpty()))
            } ?: kotlin.run {
                val errMsg = "Upload error."
                throw Exception(errMsg)
            }
        }
    }

    private suspend fun fetchImageUrl(uri: Uri): UploadResponseBody? {
        val imageFile = createUploadFile(uri, context)
        imageFile?.let {
            val compressFile = resizeAndCompressAndRotateImage(
                setting = CompressSetting(
                    contentFile = imageFile,
                    compressedFolder = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!,
                    maxImageSize = IMAGE_SIZE
                )
            )

            return centralizedImageWeb.upload(
                GenreAndSubGenre.AttachmentBlog, compressFile
            ).getOrNull()
        } ?: kotlin.run {
            val errMsg = "File not exists error!"
            throw Exception(errMsg)
        }
    }

    /**
     * change uri to file.
     */
    private fun createUploadFile(uri: Uri, context: Context): File? {
        var tempFile: File? = null
        try {
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            tempFile = File.createTempFile("IMG_", ".jpg", storageDir)
            val outputStream = FileOutputStream(tempFile ?: return null)
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val buffer = ByteArray(inputStream?.available() ?: return null)
            inputStream.read(buffer)
            outputStream.write(buffer)
            inputStream.close()
            outputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            tempFile?.deleteOnExit()
        }
        return tempFile
    }
}