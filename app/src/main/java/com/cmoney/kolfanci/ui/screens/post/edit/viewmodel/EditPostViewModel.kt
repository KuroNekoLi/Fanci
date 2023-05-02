package com.cmoney.kolfanci.ui.screens.post.edit.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.BulletinboardMessage
import com.cmoney.imagelibrary.UploadImage
import com.cmoney.kolfanci.BuildConfig
import com.cmoney.kolfanci.model.usecase.PostUseCase
import com.cmoney.kolfanci.ui.screens.chat.message.viewmodel.MessageViewModel
import com.cmoney.xlogin.XLoginHelper
import com.socks.library.KLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
    val postUseCase: PostUseCase,
    val channelId: String
) : AndroidViewModel(context) {

    private val TAG = EditPostViewModel::class.java.simpleName

    private val _attachImages: MutableStateFlow<List<Uri>> = MutableStateFlow(emptyList())
    val attachImages = _attachImages.asStateFlow()

    private val _uiState: MutableStateFlow<UiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    private val _postSuccess: MutableStateFlow<BulletinboardMessage?> = MutableStateFlow(null)
    val postSuccess = _postSuccess.asStateFlow()

    fun addAttachImage(uri: Uri) {
        KLog.i(TAG, "addAttachImage")
        val imageList = _attachImages.value.toMutableList()
        imageList.add(uri)
        _attachImages.value = imageList
    }

    fun onDeleteImageClick(uri: Uri) {
        KLog.i(TAG, "onDeleteImageClick")
        _attachImages.value = _attachImages.value.filter {
            it != uri
        }
    }

    /**
     * 按下 發文,
     * 沒文案, 沒附加圖片 -> 跳提示彈窗
     *
     */
    fun onPost(text: String) {
        KLog.i(TAG, "onPost:$text")
        viewModelScope.launch {
            if (text.isEmpty() && _attachImages.value.isEmpty()) {
                showEditTip()
                return@launch
            }

            loading()

            //附加圖片, 獲取圖片 Url
            if (_attachImages.value.isNotEmpty()) {
                uploadImages(_attachImages.value, object : MessageViewModel.ImageUploadCallback {
                    override fun complete(images: List<String>) {
                        KLog.i(TAG, "all image upload complete:" + images.size)
                        sendPost(text, images)
                    }

                    override fun onFailure(e: Throwable) {
                        KLog.e(TAG, "onFailure:$e")
                        dismissLoading()
                    }
                })
                _attachImages.value = emptyList()
            } else {
                sendPost(text, emptyList())
            }
        }
    }

    private fun sendPost(text: String, images: List<String>) {
        KLog.i(TAG, "sendPost")
        viewModelScope.launch {
            loading()
            postUseCase.writePost(
                channelId = channelId,
                text = text,
                images = images
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
        val uploadImage = UploadImage(
            context,
            uriLis,
            XLoginHelper.accessToken,
            isStaging = BuildConfig.DEBUG
        )

        val completeImageUrl = mutableListOf<String>()

        withContext(Dispatchers.IO) {
            uploadImage.upload().catch { e ->
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
}