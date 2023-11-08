package com.cmoney.kolfanci.model.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.Media
import com.cmoney.kolfanci.extension.getAttachmentType
import com.cmoney.kolfanci.extension.toUploadFileItem
import com.cmoney.kolfanci.extension.toUploadFileItemMap
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.ReSendFile
import com.cmoney.kolfanci.model.usecase.AttachmentUseCase
import com.cmoney.kolfanci.model.usecase.UploadImageUseCase
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

    //附加檔案 (依照加入順序)
    private val _attachmentList =
        MutableStateFlow<List<Pair<AttachmentType, AttachmentInfoItem>>>(emptyList())
    val attachmentList = _attachmentList.asStateFlow()

    //TODO: 統一一份資料
    //附加檔案 (有分類聚合)
    private val _attachment =
        MutableStateFlow<Map<AttachmentType, List<AttachmentInfoItem>>>(emptyMap())
    val attachment = _attachment.asStateFlow()

    //是否全部檔案 上傳完成
    private val _uploadComplete = MutableStateFlow<Pair<Boolean, Any?>>(false to null)
    val uploadComplete = _uploadComplete.asStateFlow()

    //有上傳失敗的檔案
    private val _uploadFailed = MutableStateFlow(false)
    val uploadFailed = _uploadFailed.asStateFlow()

    //附加檔案,只有image類型
    private val _isOnlyPhotoSelector = MutableStateFlow<Boolean>(false)
    val isOnlyPhotoSelector = _isOnlyPhotoSelector.asStateFlow()

    /**
     * 附加檔案, 區分 類型
     */
    fun attachment(uris: List<Uri>) {
        val oldList = _attachmentList.value.toMutableList()
        oldList.addAll(
            uris.map { uri ->
                val attachmentType = uri.getAttachmentType(context)
                attachmentType to uri.toUploadFileItem(context = context)
            }
        )

        _attachmentList.update {
            oldList
        }

        val attachmentMap = uris.map { uri ->
            val attachmentType = uri.getAttachmentType(context)
            attachmentType to uri.toUploadFileItem(context = context)
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
     * 執行上傳動作, 將未處理檔案改為 pending, 並執行上傳
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
                it.first == AttachmentType.Image && it.second.status == AttachmentInfoItem.Status.Pending
            }.map { it.second.uri }

            val allImages = uploadImageUseCase.uploadImage2(imageFiles).toList()

            //todo ----- for test start -----
            //為了測試 上傳失敗
//            val testItem = allImages.first().copy(
//                status = UploadFileItem.Status.Failed("")
//            )
//            allImages.toMutableList().apply {
//                this[0] = testItem
//                allImages = this
//            }
            //todo ----- for test end -----

            //圖片之外的檔案
            val otherFiles = uploadList.filter {
                it.first != AttachmentType.Image && it.second.status == AttachmentInfoItem.Status.Pending
            }.map { it.second.uri }

            val allOtherFiles = attachmentUseCase.uploadFile(otherFiles).toList()

            updateAttachmentList(
                imageFiles = allImages,
                otherFiles = allOtherFiles,
                other = other
            )
        }
    }

    /**
     * 更新 資料, 刷新附加檔案畫面
     * 並檢查是否有失敗的
     */
    private fun updateAttachmentList(
        imageFiles: List<AttachmentInfoItem>,
        otherFiles: List<AttachmentInfoItem>,
        other: Any? = null
    ) {
        val allItems = imageFiles.reversed().distinctBy {
            it.uri
        } + otherFiles.reversed().distinctBy {
            it.uri
        }

        val newList = _attachmentList.value.map {
            val key = it.first
            val oldItem = it.second

            val newStatusItem = allItems.firstOrNull { newItem ->
                newItem.uri == oldItem.uri
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

        val unionList = newList.groupBy(
            { it.first }, { it.second }
        )

        _attachment.update {
            unionList
        }

        val hasFailed = _attachmentList.value.any { pairItem ->
            val item = pairItem.second
            item.status is AttachmentInfoItem.Status.Failed
        }

        if (hasFailed) {
            _uploadFailed.value = true
        } else {
            _uploadComplete.value = (true to other)
        }
    }

    /**
     * 將檔案狀態 Undefine 改為 Pending
     */
    private fun statusToPending() {
        KLog.i(TAG, "statusToPending")

        val newList = _attachmentList.value.map {
            val key = it.first
            val uploadItem = it.second

            val newUploadItem = uploadItem.copy(
                status = if (uploadItem.status == AttachmentInfoItem.Status.Undefined) {
                    AttachmentInfoItem.Status.Pending
                } else {
                    uploadItem.status
                }
            )
            key to newUploadItem
        }

        _attachmentList.update {
            newList
        }

        _attachment.update {
            newList.groupBy(
                { it.first }, { it.second }
            )
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

    /**
     * 重新再次上傳,針對單一檔案處理
     *
     * @param uploadFileItem 需要重新上傳的檔案
     * @param other 檔案之外的東西, ex: text 內文
     */
    fun onResend(uploadFileItem: ReSendFile, other: Any? = null) {
        KLog.i(TAG, "onResend:$uploadFileItem")
        viewModelScope.launch {
            val file = uploadFileItem.file

            var allImages = emptyList<AttachmentInfoItem>()
            if (uploadFileItem.type == AttachmentType.Image) {
                allImages = uploadImageUseCase.uploadImage2(listOf(file)).toList()
            }

            val allOtherFiles = attachmentUseCase.uploadFile(listOf(file)).toList()

            updateAttachmentList(
                imageFiles = allImages,
                otherFiles = allOtherFiles,
                other = other
            )
        }
    }

    fun clear() {
        KLog.i(TAG, "clear")
        _attachment.update { emptyMap() }
        _attachmentList.update { emptyList() }
    }

    /**
     * 將已經上傳的資料 加入清單
     */
    fun addAttachment(mediaList: List<Media>) {
        KLog.i(TAG, "addAttachment:" + mediaList.size)
        viewModelScope.launch {
            val attachmentMap = mediaList.toUploadFileItemMap()
            val attachmentList = attachmentMap.map { mapItem ->
                val key = mapItem.key
                val value = mapItem.value
                value.map {
                    key to it
                }
            }.flatten()

            _attachment.update { attachmentMap }
            _attachmentList.update { attachmentList }
        }
    }

    /**
     * 取得 目前該 uri 所屬的類別
     */
    fun getAttachmentType(uri: Uri): AttachmentType? = _attachmentList.value.filter {
        it.second.uri == uri
    }.map {
        it.first
    }.firstOrNull()

    /**
     * 點擊 附加功能
     */
    fun onAttachClick() {
        KLog.i(TAG, "onAttachClick")
        _isOnlyPhotoSelector.update {
            false
        }
    }

    /**
     * 附加圖片 點擊更多圖片
     */
    fun onAttachImageAddClick() {
        KLog.i(TAG, "onImageAddClick")
        _isOnlyPhotoSelector.update {
            true
        }
    }
}