package com.cmoney.kolfanci.ui.screens.media.txt

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.kolfanci.model.usecase.AttachmentUseCase
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class TextPreviewViewModel(
    val attachmentUseCase: AttachmentUseCase
) : ViewModel() {
    private val TAG = TextPreviewViewModel::class.java.simpleName

    private val _text = MutableStateFlow<String>("")
    val text = _text.asStateFlow()

    /**
     * 讀取檔案 內容
     */
    fun show(file: File?) {
        KLog.i(TAG, "show.")

        file?.let {
            _text.value = it.readText()
        }
    }

    /**
     * 讀取網路內容
     */
    fun show(url: Uri) {
        KLog.i(TAG, "show:$url")
        viewModelScope.launch {
            attachmentUseCase.getUrlContent(url.toString())
                .onSuccess {
                    _text.value = it
                }
                .onFailure {
                    KLog.e(TAG, it)
                }
        }
    }
}