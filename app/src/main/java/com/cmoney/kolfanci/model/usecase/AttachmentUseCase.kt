package com.cmoney.kolfanci.model.usecase

import android.app.Application
import android.net.Uri
import com.cmoney.kolfanci.extension.getUploadFileType
import com.cmoney.kolfanci.repository.Network
import com.socks.library.KLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class AttachmentUseCase(
    val context: Application,
    private val network: Network
) {
    private val TAG = AttachmentUseCase::class.java.simpleName

    suspend fun uploadFile(uri: Uri) = network.uploadFile(uri)

    suspend fun uploadFile(uris: List<Uri>): Flow<UploadStatus> = flow {
        KLog.i(TAG, "uploadFile")

        uris.forEachIndexed { index, uri ->
            network.uploadFile(uri)
                .onSuccess { fileUploadResponse ->
                    val externalId = fileUploadResponse.externalId
                    KLog.i(TAG, "uploadFile step1 success:$uri, id=$externalId")

                    //polling status
                    emit(
                        checkFileStatus(
                            externalId = externalId,
                            uri = uri,
                            index = index
                        )
                    )
                }
                .onFailure {
                    KLog.e(TAG, "uploadFile step1 fail:$it")
                    emit(
                        UploadStatus(
                            id = index.toString(),
                            uri = uri,
                            status = UploadStatus.Status.Failed("upload step1 failed.")
                        )
                    )
                }
        }
    }


    /**
     * 檢查 檔案上傳 狀態
     */
    private suspend fun checkFileStatus(externalId: String, uri: Uri, index: Int): UploadStatus {
        val uploadFileType = uri.getUploadFileType(context)

        val whileLimit = 10
        var whileCount = 0
        var delayTime = 0L
        var pre = 0L
        var current = 1L

        while (whileCount < whileLimit) {

            delayTime = pre + current
            pre = current
            current = delayTime
            whileCount += 1

            val fileUploadStatusResponse = network.checkUploadFileStatus(
                externalId = externalId,
                fileType = uploadFileType
            ).getOrNull()

            KLog.i(TAG, "fileUploadStatusResponse:$fileUploadStatusResponse")

            fileUploadStatusResponse?.let { statusResponse ->
                val status = when (statusResponse.status) {
                    "uploading" -> {
                        UploadStatus.Status.Uploading
                    }

                    "success" -> {
                        UploadStatus.Status.Success
                    }

                    else -> {
                        UploadStatus.Status.Failed("checkFileStatus fail")
                    }
                }

                if (whileCount > whileLimit || status == UploadStatus.Status.Success) {
                    return UploadStatus(
                        id = index.toString(),
                        uri = uri,
                        status = status
                    )
                }

            } ?: kotlin.run {

                if (whileCount > whileLimit) {
                    return UploadStatus(
                        id = index.toString(),
                        uri = uri,
                        status = UploadStatus.Status.Failed("checkFileStatus fail, is null response.")
                    )
                }
            }

            delay(delayTime * 1000)
        }

        return UploadStatus(
            id = "",
            uri = uri,
            status = UploadStatus.Status.Failed("upload failed.")
        )
    }
}

/**
 * 檔案 上傳狀態
 *
 * @param id 唯一識別碼
 * @param uri 上傳的檔案
 * @param status 上傳狀態
 */
data class UploadStatus(
    val id: String,
    val uri: Uri,
    val status: Status
) {
    /**
     * 檔案上傳狀態
     * @param description 說明
     */
    sealed class Status(description: String = "") {
        object Success : Status()

        object Uploading : Status()

        data class Failed(val reason: String) : Status(reason)
    }
}