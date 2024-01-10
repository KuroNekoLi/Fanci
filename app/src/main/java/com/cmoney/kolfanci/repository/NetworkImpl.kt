package com.cmoney.kolfanci.repository

import android.app.Application
import android.net.Uri
import com.cmoney.kolfanci.extension.checkResponseBody
import com.cmoney.kolfanci.extension.getMimeType
import com.cmoney.kolfanci.extension.getUploadFileType
import com.cmoney.kolfanci.extension.uriToFile
import com.cmoney.kolfanci.model.notification.NotificationHistory
import com.cmoney.kolfanci.repository.request.NotificationClick
import com.cmoney.kolfanci.repository.request.NotificationSeen
import com.cmoney.kolfanci.repository.response.FileUploadResponse
import com.cmoney.kolfanci.repository.response.FileUploadStatusResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class NetworkImpl(
    private val context: Application,
    private val notificationService: NotificationService,
    private val centralFileService: CentralFileService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Network {

    override suspend fun getNotificationHistory(): Result<NotificationHistory> =
        withContext(dispatcher) {
            kotlin.runCatching {
                notificationService.getNotificationHistory().checkResponseBody()
            }
        }

    override suspend fun getNextPageNotificationHistory(nextPageUrl: String): Result<NotificationHistory> =
        withContext(dispatcher) {
            kotlin.runCatching {
                notificationService.getNextPageNotificationHistory(nextPageUrl).checkResponseBody()
            }
        }

    override suspend fun setNotificationHistoryClick(notificationClick: NotificationClick): Result<Unit> =
        withContext(dispatcher) {
            kotlin.runCatching {
                notificationService.setNotificationHistoryClick(notificationClick = notificationClick)
                    .checkResponseBody()
            }
        }

    override suspend fun getNotificationUnreadCount(): Result<Long> =
        withContext(dispatcher) {
            kotlin.runCatching {
                notificationService.getNotificationUnreadCount().checkResponseBody()
            }
        }

    override suspend fun setNotificationSeen(): Result<Unit> =
        withContext(dispatcher) {
            kotlin.runCatching {
                notificationService.setNotificationSeen(NotificationSeen()).checkResponseBody()
            }
        }

    override suspend fun uploadFile(uri: Uri): Result<FileUploadResponse> =
        withContext(dispatcher) {
            kotlin.runCatching {
                val mimeType = uri.getMimeType()
                val file = uri.uriToFile(context)

                val requestBody = file.asRequestBody(
                    contentType = mimeType?.toMediaType()
                )

                val filePart = MultipartBody.Part.createFormData("File", file.name, requestBody)
                val fileType = uri.getUploadFileType(context)

                centralFileService.uploadFile(
                    file = filePart,
                    fileType = fileType.toRequestBody()
                ).checkResponseBody()
            }
        }

    override suspend fun checkUploadFileStatus(
        externalId: String,
        fileType: String
    ): Result<FileUploadStatusResponse> =
        withContext(dispatcher) {
            kotlin.runCatching {
                centralFileService.checkUploadFileStatus(
                    fileType = fileType,
                    externalId = externalId
                ).checkResponseBody()
            }
        }

    override suspend fun getContent(url: String): Result<String> = withContext(dispatcher) {
        kotlin.runCatching {
            centralFileService.getContent(url).checkResponseBody()
        }
    }
}