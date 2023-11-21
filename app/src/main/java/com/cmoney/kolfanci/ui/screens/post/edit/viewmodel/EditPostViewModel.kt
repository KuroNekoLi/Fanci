package com.cmoney.kolfanci.ui.screens.post.edit.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.fanciapi.fanci.model.MessageServiceType
import com.cmoney.kolfanci.model.attachment.AttachmentInfoItem
import com.cmoney.kolfanci.model.attachment.AttachmentType
import com.cmoney.kolfanci.model.attachment.toUploadMedia
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.model.usecase.PostUseCase
import com.cmoney.kolfanci.model.usecase.UploadImageUseCase
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.MessageViewModel
import com.socks.library.KLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class UiState {
    object ShowEditTip : UiState()
    object HideEditTip : UiState()
    object ShowLoading : UiState()
    object DismissLoading : UiState()
}

class EditPostViewModel(
    val context: Application,
    private val postUseCase: PostUseCase,
    private val chatRoomUseCase: ChatRoomUseCase,
    val channelId: String,
    private val uploadImageUseCase: UploadImageUseCase
) : AndroidViewModel(context) {

    private val TAG = EditPostViewModel::class.java.simpleName

    private val _attachImages: MutableStateFlow<List<Uri>> = MutableStateFlow(emptyList())
    val attachImages = _attachImages.asStateFlow()

    private val _uiState: MutableStateFlow<UiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    private val _postSuccess: MutableStateFlow<BulletinboardMessage?> = MutableStateFlow(null)
    val postSuccess = _postSuccess.asStateFlow()

    private val _userInput: MutableStateFlow<String> = MutableStateFlow("")
    val userInput = _userInput.asStateFlow()

    fun setUserInput(text: String) {
        _userInput.update {
            text
        }
    }

    fun addAttachImage(uris: List<Uri>) {
        KLog.i(TAG, "addAttachImage")
        val imageList = _attachImages.value.toMutableList()
        imageList.addAll(uris)
        _attachImages.value = imageList
    }

    fun onDeleteImageClick(uri: Uri) {
        KLog.i(TAG, "onDeleteImageClick")
        _attachImages.value = _attachImages.value.filter {
            it != uri
        }
    }

    /**
     * 按下 發文
     *
     * @param text 內文
     * @param attachment 附加檔案
     */
    fun onPost(text: String, attachment: List<Pair<AttachmentType, AttachmentInfoItem>>) {
        KLog.i(TAG, "onPost:$text, attachment:$attachment")
        viewModelScope.launch {
            if (text.isEmpty() && _attachImages.value.isEmpty()) {
                showEditTip()
                return@launch
            }

            sendPost(text, attachment)
        }
    }

    /**
     * 發送貼文
     *
     * @param text 內文
     * @param attachment 附加檔案
     */
    private fun sendPost(text: String, attachment: List<Pair<AttachmentType, AttachmentInfoItem>>) {
        KLog.i(TAG, "sendPost")
        viewModelScope.launch {
            loading()
            postUseCase.writePost(
                channelId = channelId,
                text = text,
                attachment = attachment
            ).fold({
                dismissLoading()
                KLog.i(TAG, "sendPost complete.")
                _postSuccess.value = it
            }, {
                dismissLoading()
                KLog.e(TAG, it)
            })
        }
    }

    private fun loading() {
        _uiState.value = UiState.ShowLoading
    }

    private fun dismissLoading() {
        _uiState.value = UiState.DismissLoading
    }

    private fun showEditTip() {
        _uiState.value = UiState.ShowEditTip
    }

    fun dismissEditTip() {
        _uiState.value = UiState.HideEditTip
    }

    private suspend fun uploadImages(
        uriLis: List<Uri>,
        imageUploadCallback: MessageViewModel.ImageUploadCallback
    ) {
        KLog.i(TAG, "uploadImages:" + uriLis.size)

        val completeImageUrl = mutableListOf<String>()

        withContext(Dispatchers.IO) {
            uploadImageUseCase.uploadImage(uriLis).catch { e ->
                KLog.e(TAG, e)
                imageUploadCallback.onFailure(e)
            }.collect {
                KLog.i(TAG, "uploadImage:$it")
                val uri = it.first
                val imageUrl = it.second
                completeImageUrl.add(imageUrl)
                if (completeImageUrl.size == uriLis.size) {
                    imageUploadCallback.complete(completeImageUrl)
                }
            }
        }
    }

    /**
     * 編輯貼文,初始化資料至UI
     */
    fun editPost(editPost: BulletinboardMessage) {
        KLog.i(TAG, "editPost:$editPost")
        _attachImages.value = editPost.content?.medias?.map {
            Uri.parse(it.resourceLink)
        }.orEmpty()
    }

    /**
     * 更新貼文
     *
     * @param attachment 附加檔案
     */
    fun onUpdatePostClick(
        editPost: BulletinboardMessage,
        text: String,
        attachment: List<Pair<AttachmentType, AttachmentInfoItem>>
    ) {
        KLog.i(TAG, "onUpdatePost:$text")
        viewModelScope.launch {
            if (text.isEmpty() && _attachImages.value.isEmpty()) {
                showEditTip()
                return@launch
            }
            chatRoomUseCase.updateMessage(
                messageServiceType = MessageServiceType.bulletinboard,
                messageId = editPost.id.orEmpty(),
                text = text,
                attachment = attachment
            ).fold({
                _postSuccess.value = editPost.copy(
                    content = editPost.content?.copy(
                        text = text,
                        medias = attachment.toUploadMedia(context)
                    )
                )
            }, {
                it.printStackTrace()
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 更新留言
     */
    fun onUpdateComment(bulletinboardMessage: BulletinboardMessage, text: String) {
        KLog.i(TAG, "onUpdateComment:$bulletinboardMessage")
        viewModelScope.launch {


        }
    }
}