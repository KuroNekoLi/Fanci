package com.cmoney.kolfanci.model.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.cmoney.kolfanci.utils.PhotoUtils
import com.cmoney.xlogin.XLoginHelper
import com.socks.library.KLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 設定用戶資料
 */
class UserViewModel(
    private val context: Application,
) : AndroidViewModel(context) {

    private val TAG = UserViewModel::class.java.simpleName

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading.asFlow()

    private val _isComplete = MutableLiveData<Boolean>()
    val isComplete = _isComplete.asFlow()

    private val _showEmptyNameDialog = MutableStateFlow(false)
    val showEmptyNameDialog = _showEmptyNameDialog.asStateFlow()

    /**
     * 更換暱稱 以及 大頭貼
     */
    fun changeNicknameAndAvatar(nickName: String, avatarUri: Uri?) {
        KLog.i(TAG, "changeNicknameAndAvatar")
        viewModelScope.launch {
            _isLoading.value = true
            //更換暱稱
            val nickName = nickName.trim()
            if (nickName.isEmpty()) {
                _isLoading.value = false
                _showEmptyNameDialog.value = true
                return@launch
            }

            //不一樣才動作
            if (nickName != XLoginHelper.nickName) {
                val isSuccess = XLoginHelper.changeNickNameRealTime(nickName).isSuccess
                KLog.i(TAG, "changeNickNameIsSuccess:$isSuccess")
            }

            //更換頭貼
            avatarUri?.let {
                val file = withContext(Dispatchers.IO) {
                    File(
                        PhotoUtils.createUploadImage(avatarUri, context)
                            ?: return@withContext null
                    )
                } ?: return@launch

                val isSuccess = XLoginHelper.changeAvatar(file).isSuccess
                KLog.i(TAG, "changeAvatarIsSuccess:$isSuccess")
            }

            _isComplete.value = true
            _isLoading.value = false
        }
    }

    fun dismissEmptyNameDialog() {
        _showEmptyNameDialog.value = false
    }

}