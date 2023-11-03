package com.cmoney.kolfanci.model.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.kolfanci.extension.getAttachmentType
import com.cmoney.kolfanci.model.usecase.AttachmentUseCase
import com.cmoney.kolfanci.model.usecase.UploadFileItem
import com.cmoney.kolfanci.model.usecase.UploadImageUseCase
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.AttachmentType
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AttachmentViewModel(
    val context: Application,
    val attachmentUseCase: AttachmentUseCase,
    val uploadImageUseCase: UploadImageUseCase
) : AndroidViewModel(context) {
    private val TAG = AttachmentViewModel::class.java.simpleName

    //附加檔案 (有分類聚合)
    private val _attachment =
        MutableStateFlow<Map<AttachmentType, List<UploadFileItem>>>(emptyMap())
    val attachment = _attachment.asStateFlow()

    //附加檔案 (依照加入順序)
    private val _attachmentList =
        MutableStateFlow<List<Pair<AttachmentType, UploadFileItem>>>(emptyList())
    val attachmentList = _attachmentList.asStateFlow()

    //是否全部檔案 上傳完成
    private val _uploadComplete = MutableStateFlow<Pair<Boolean, Any?>>(false to null)
    val uploadComplete = _uploadComplete.asStateFlow()

    //有上傳失敗的檔案
    private val _uploadFailed = MutableStateFlow(false)
    val uploadFailed = _uploadFailed.asStateFlow()

    /**
     * 附加檔案, 區分 類型
     */
    fun attachment(uris: List<Uri>) {
        val oldList = _attachmentList.value.toMutableList()
        oldList.addAll(
            uris.map { uri ->
                val attachmentType = uri.getAttachmentType(context)
                attachmentType to UploadFileItem(uri = uri)
            }
        )

        _attachmentList.update {
            oldList
        }

        val attachmentMap = uris.map { uri ->
            val attachmentType = uri.getAttachmentType(context)
            attachmentType to UploadFileItem(uri = uri)
        }.groupBy {
            it.first
        }.mapValues { entry ->
            entry.value.map { it.second }
        }

        val unionList = (_attachment.value.asSequence() + attachmentMap.asSequence())
            .distinct()
            .groupBy({ it.key }, { it.value })
            .mapValues { entry ->
                entry.value.flatten()
            }

        _attachment.update {
            unionList
        }
    }

    /**
     * 移除 附加 檔案
     * @param uri
     */
    fun removeAttach(uri: Uri) {
        KLog.i(TAG, "removeAttach:$uri")
        val attachmentType = uri.getAttachmentType(context)

        _attachmentList.update {
            _attachmentList.value.filter {
                it.second.uri != uri
            }
        }

        val newAttachment = _attachment.value[attachmentType]?.filter { existsItem ->
            existsItem.uri != uri
        }

        if (newAttachment.isNullOrEmpty()) {
            _attachment.update {
                emptyMap()
            }
        } else {
            _attachment.update { oldAttachment ->
                oldAttachment.toMutableMap().apply {
                    set(attachmentType, newAttachment)
                }
            }
        }
    }

    /**
     * 執行上傳動作
     *
     * @param other 檔案之外的東西, ex: text 內文
     */
    fun upload(other: Any? = null) {
        KLog.i(TAG, "upload")
        viewModelScope.launch {
            statusToPending()

            val uploadList = _attachmentList.value

            //圖片處理
            val imageFiles = uploadList.filter {
                it.first == AttachmentType.Image
            }.map { it.second.uri }

            var allImages = uploadImageUseCase.uploadImage2(imageFiles).toList()

            //todo ----- for test start -----
            val testItem = allImages.first().copy(
                status = UploadFileItem.Status.Failed("")
            )
            allImages.toMutableList().apply {
                this[0] = testItem
                allImages = this
            }
            //todo ----- for test end -----

            //圖片之外的檔案
            val otherFiles = uploadList.filter {
                it.first != AttachmentType.Image
            }.map { it.second.uri }

            val allOtherFiles = attachmentUseCase.uploadFile(otherFiles).toList()

            val newList = _attachmentList.value.map {
                val key = it.first
                val oldItem = it.second

                val newStatusItem = allImages.firstOrNull { newItem ->
                    newItem.uri == oldItem.uri
                } ?: kotlin.run {
                    allOtherFiles.firstOrNull { newItem ->
                        newItem.uri == oldItem.uri
                    }
                }

                if (newStatusItem != null) {
                    key to newStatusItem
                } else {
                    key to oldItem
                }
            }

            _attachmentList.update {
                newList
            }

            val hasFailed = _attachmentList.value.any { pairItem ->
                val item = pairItem.second
                item.status is UploadFileItem.Status.Failed
            }

            if (hasFailed) {
                _uploadFailed.value = true
            } else {
                _uploadComplete.value = (true to other)
            }
        }
    }

    /**
     * 將所有檔案狀態改為 等待上傳中
     */
    private fun statusToPending() {
        KLog.i(TAG, "statusToPending")

        val newList = _attachmentList.value.map {
            val key = it.first
            val uploadItem = it.second.copy(
                status = UploadFileItem.Status.Pending
            )
            key to uploadItem
        }

        _attachmentList.update {
            newList
        }
    }

    fun finishPost() {
        _uploadComplete.update {
            false to null
        }
    }

    fun clearUploadFailed() {
        _uploadFailed.update {
            false
        }
    }
}