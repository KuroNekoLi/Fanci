package com.cmoney.kolfanci.model.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.cmoney.kolfanci.extension.getAttachmentType
import com.cmoney.kolfanci.model.usecase.AttachmentUseCase
import com.cmoney.kolfanci.model.usecase.UploadFileItem
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.AttachmentType
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AttachmentViewModel(
    val context: Application,
    val attachmentUseCase: AttachmentUseCase
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
     */
    fun upload() {

    }
}