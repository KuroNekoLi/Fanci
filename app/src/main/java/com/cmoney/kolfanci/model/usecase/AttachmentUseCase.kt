package com.cmoney.kolfanci.model.usecase

import android.app.Application
import android.content.Context
import android.net.Uri
import com.bumptech.glide.Glide
import com.cmoney.kolfanci.BuildConfig
import com.cmoney.kolfanci.extension.getAttachmentType
import com.cmoney.kolfanci.extension.toUploadFileItem
import com.cmoney.kolfanci.extension.getUploadFileType
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.repository.Network
import com.cmoney.kolfanci.ui.destinations.AudioPreviewScreenDestination
import com.cmoney.kolfanci.ui.destinations.PdfPreviewScreenDestination
import com.cmoney.kolfanci.ui.destinations.TextPreviewScreenDestination
import com.cmoney.kolfanci.ui.screens.media.audio.AudioViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.socks.library.KLog
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class AttachmentUseCase(
    val context: Application,
    private val network: Network
) {
    private val TAG = AttachmentUseCase::class.java.simpleName

    suspend fun uploadFile(uri: Uri) = network.uploadFile(uri)

    suspend fun uploadFile(uris: List<Uri>): Flow<AttachmentInfoItem> = flow {
        KLog.i(TAG, "uploadFile")

        uris.forEach { uri ->
            emit(
                uri.toUploadFileItem(
                    context = context,
                    status = AttachmentInfoItem.Status.Uploading
                )
            )

            network.uploadFile(uri)
                .onSuccess { fileUploadResponse ->
                    val externalId = fileUploadResponse.externalId
                    KLog.i(TAG, "uploadFile step1 success:$uri, id=$externalId")

                    //polling status
                    emit(
                        checkFileStatus(
                            externalId = externalId,
                            uri = uri
                        )
                    )
                }
                .onFailure {
                    KLog.e(TAG, "uploadFile step1 fail:$it")
                    emit(
                        AttachmentInfoItem(
                            uri = uri,
                            status = AttachmentInfoItem.Status.Failed("upload step1 failed.")
                        )
                    )
                }
        }
    }


    /**
     * 檢查 檔案上傳 狀態
     */
    private suspend fun checkFileStatus(externalId: String, uri: Uri): AttachmentInfoItem {
        val uploadFileType = uri.getUploadFileType(context)

        val whileLimit = 10
        var whileCount = 0
        var delayTime: Long
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
                        AttachmentInfoItem.Status.Uploading
                    }

                    "success" -> {
                        AttachmentInfoItem.Status.Success
                    }

                    else -> {
                        AttachmentInfoItem.Status.Failed("checkFileStatus fail")
                    }
                }

                if (whileCount > whileLimit || status == AttachmentInfoItem.Status.Success) {
                    return uri.toUploadFileItem(
                        context = context,
                        status = status,
                        serverUrl = if (status == AttachmentInfoItem.Status.Success) {
                            BuildConfig.CM_COMMON_URL + "/files/%s/%s".format(
                                uploadFileType,
                                externalId
                            )
                        } else {
                            ""
                        }
                    )
                }

            } ?: kotlin.run {

                if (whileCount > whileLimit) {
                    return AttachmentInfoItem(
                        uri = uri,
                        status = AttachmentInfoItem.Status.Failed("checkFileStatus fail, is null response.")
                    )
                }
            }

            delay(delayTime * 1000)
        }

        return AttachmentInfoItem(
            uri = uri,
            status = AttachmentInfoItem.Status.Failed("upload failed.")
        )
    }


    /**
     * 取得 Url 內容
     */
    suspend fun getUrlContent(url: String) = network.getContent(url)
}

object AttachmentController {
    private val TAG = "AttachmentController"

    /**
     * 點擊附加檔案預覽
     */
    fun onAttachmentClick(
        navController: DestinationsNavigator,
        uri: Uri,
        context: Context,
        attachmentType: AttachmentType? = null,
        fileName: String = "",
        duration: Long = 0,
        audioViewModel: AudioViewModel? = null
    ) {
        val type = attachmentType ?: uri.getAttachmentType(context)
        KLog.i(TAG, "onAttachmentClick:$uri type:$type")

        when (type) {
            AttachmentType.Audio -> {
                audioViewModel?.apply {
                    playSilence(
                        uri = uri,
                        duration = duration,
                        title = fileName
                    )

                    openBottomPlayer()
                } ?: kotlin.run {
                    navController.navigate(
                        AudioPreviewScreenDestination(
                            uri = uri,
                            duration = duration,
                            title = fileName
                        )
                    )
                }
            }

            AttachmentType.Image -> {
                StfalconImageViewer
                    .Builder(
                        context, listOf(uri)
                    ) { imageView, image ->
                        Glide
                            .with(context)
                            .load(image)
                            .into(imageView)
                    }
                    .show()
            }

            AttachmentType.Pdf -> {
                navController.navigate(
                    PdfPreviewScreenDestination(
                        uri = uri,
                        title = fileName
                    )
                )
            }

            AttachmentType.Txt -> {
                navController.navigate(
                    TextPreviewScreenDestination(
                        uri = uri,
                        fileName = fileName
                    )
                )
            }

            AttachmentType.Unknown -> {

            }
        }
    }
}