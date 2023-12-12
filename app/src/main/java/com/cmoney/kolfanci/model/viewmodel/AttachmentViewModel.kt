package com.cmoney.kolfanci.model.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.Media
import com.cmoney.fanciapi.fanci.model.Voting
import com.cmoney.kolfanci.extension.getAttachmentType
import com.cmoney.kolfanci.extension.toUploadFileItem
import com.cmoney.kolfanci.extension.toUploadFileItemMap
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.ReSendFile
import com.cmoney.kolfanci.model.usecase.AttachmentUseCase
import com.cmoney.kolfanci.model.usecase.UploadImageUseCase
import com.cmoney.kolfanci.model.usecase.VoteUseCase
import com.cmoney.kolfanci.model.vote.VoteModel
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AttachmentViewModel(
    val context: Application,
    val attachmentUseCase: AttachmentUseCase,
    val uploadImageUseCase: UploadImageUseCase,
    val voteUseCase: VoteUseCase
) : AndroidViewModel(context) {
    private val TAG = AttachmentViewModel::class.java.simpleName

    //附加檔案 (依照加入順序)
    private val _attachmentList =
        MutableStateFlow<List<Pair<AttachmentType, AttachmentInfoItem>>>(emptyList())
    val attachmentList = _attachmentList.asStateFlow()

    //附加檔案 (有分類聚合) - 來源 -> _attachmentList
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

    //資料是否初始化完成
    private val _setInitDataSuccess = MutableStateFlow(false)
    val setInitDataSuccess = _setInitDataSuccess.asStateFlow()

    init {
        viewModelScope.launch {
            _attachmentList.collect { attachmentList ->
                _attachment.update {
                    attachmentList.groupBy {
                        it.first
                    }.mapValues { entry ->
                        entry.value.map {
                            it.second
                        }
                    }
                }
            }
        }
    }

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
    }

    /**
     * 移除 附加 檔案
     * @param attachmentInfoItem
     */
    fun removeAttach(attachmentInfoItem: AttachmentInfoItem) {
        KLog.i(TAG, "removeAttach:$attachmentInfoItem")
        _attachmentList.update {
            _attachmentList.value.filter {
                it.second != attachmentInfoItem
            }
        }
    }

    /**
     * 執行上傳動作, 將未處理檔案改為 pending, 並執行上傳
     *
     * @param channelId 頻道 id
     * @param other 檔案之外的東西, ex: text 內文
     */
    fun upload(
        channelId: String,
        other: Any? = null
    ) {
        KLog.i(TAG, "upload file:$channelId")
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

            //投票
            val voteModelList = uploadList.filter {
                it.first == AttachmentType.Choice &&
                        it.second.status == AttachmentInfoItem.Status.Pending &&
                        it.second.other is VoteModel
            }.map {
                it.second.other as VoteModel
            }

            val voteFiles = voteUseCase.createVotes(
                channelId = channelId,
                voteModels = voteModelList
            ).toList().map { vote ->
                AttachmentInfoItem(
                    status = AttachmentInfoItem.Status.Success,
                    other = vote,
                    attachmentType = AttachmentType.Choice
                )
            }

            //其他
            val otherFiles = uploadList.filter {
                it.first != AttachmentType.Image &&
                        it.first != AttachmentType.Choice &&
                        it.second.status == AttachmentInfoItem.Status.Pending
            }.map { it.second.uri }

            val allOtherFiles = attachmentUseCase.uploadFile(otherFiles).toList()

            updateAttachmentList(
                imageFiles = allImages,
                voteFiles = voteFiles,
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
        voteFiles: List<AttachmentInfoItem>,
        otherFiles: List<AttachmentInfoItem>,
        other: Any? = null
    ) {
        val allItems = imageFiles.reversed().distinctBy {
            it.uri
        } + voteFiles.reversed().distinctBy {
            it.other
        } + otherFiles.reversed().distinctBy {
            it.uri
        }

        val newList = _attachmentList.value.map {
            val key = it.first
            val oldItem = it.second

            val newStatusItem = allItems.firstOrNull { newItem ->
                if (newItem.attachmentType == AttachmentType.Choice) {
                    (newItem.other as VoteModel).question == (oldItem.other as VoteModel).question
                } else {
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
     * @param channelId 頻道 id
     * @param uploadFileItem 需要重新上傳的檔案
     * @param other 檔案之外的東西, ex: text 內文
     */
    fun onResend(
        channelId: String,
        uploadFileItem: ReSendFile,
        other: Any? = null
    ) {
        KLog.i(TAG, "onResend:$uploadFileItem")
        viewModelScope.launch {
            val file = uploadFileItem.attachmentInfoItem.uri

            var allImages = emptyList<AttachmentInfoItem>()
            if (uploadFileItem.type == AttachmentType.Image) {
                allImages = uploadImageUseCase.uploadImage2(listOf(file)).toList()
            }

            var allVotes = emptyList<AttachmentInfoItem>()
            if (uploadFileItem.type == AttachmentType.Choice) {
                uploadFileItem.attachmentInfoItem.other?.let { other ->
                    if (other is VoteModel) {
                        allVotes = voteUseCase.createVotes(
                            channelId = channelId,
                            voteModels = listOf(other)
                        ).toList().map { vote ->
                            AttachmentInfoItem(
                                status = AttachmentInfoItem.Status.Success,
                                other = vote
                            )
                        }
                    }
                }
            }

            val allOtherFiles = attachmentUseCase.uploadFile(listOf(file)).toList()

            updateAttachmentList(
                imageFiles = allImages,
                voteFiles = allVotes,
                otherFiles = allOtherFiles,
                other = other
            )
        }
    }

    fun clear() {
        KLog.i(TAG, "clear")
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

    /**
     * 新增/更新 選擇題
     * 檢查 id 是否存在 就更新 否則 新增
     */
    fun addChoiceAttachment(voteModel: VoteModel) {
        KLog.i(TAG, "addChoiceAttachment")

        val oldList = _attachmentList.value.toMutableList()

        val filterList = oldList.filter { pairItem ->
            val otherModel = pairItem.second.other
            if (otherModel is VoteModel) {
                return@filter otherModel.id != voteModel.id
            }
            true
        }.toMutableList()

        filterList.add(
            AttachmentType.Choice to AttachmentInfoItem(
                other = voteModel,
                attachmentType = AttachmentType.Choice
            )
        )

        val distinctList = filterList.distinctBy { it ->
            val other = it.second.other
            if (other is VoteModel) {
                other.id
            } else {
                it
            }
        }

        _attachmentList.update {
            distinctList
        }
    }

    fun setDataInitFinish() {
        _setInitDataSuccess.update { true }
    }

    /**
     * 刪除多餘的投票
     *
     * @param channelId 頻道 id
     * @param oldVoting 原本的投票 List
     */
    fun deleteVotingCheck(
        channelId: String,
        oldVoting: List<Voting>
    ) {
        KLog.i(TAG, "deleteVotingCheck:$channelId")

        viewModelScope.launch {
            val newVoting = _attachmentList.value.filter {
                it.first == AttachmentType.Choice && (it.second.other is VoteModel)
            }.map {
                (it.second.other as VoteModel).id
            }

            val deleteItem = oldVoting.filter {
                !newVoting.contains(it.id.toString())
            }

            voteUseCase.deleteVote(
                channelId = channelId,
                voteIds = deleteItem.map { it.id ?: "" }
            ).onSuccess {
                KLog.i(TAG, it)
            }.onFailure {
                KLog.e(TAG, it)
            }
        }
    }
}