package com.cmoney.kolfanci.ui.screens.post.edit.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditPostViewModel : ViewModel() {
    private val TAG = EditPostViewModel::class.java.simpleName

    private val _attachImages: MutableStateFlow<List<Uri>> = MutableStateFlow(emptyList())
    val attachImages = _attachImages.asStateFlow()

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

}