package com.cmoney.kolfanci.ui.screens.group.create.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.ColorTheme
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.imagelibrary.UploadImage
import com.cmoney.kolfanci.BuildConfig
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.avatar.ImageChangeData
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.theme.model.GroupTheme
import com.cmoney.kolfanci.ui.theme.FanciColor
import com.cmoney.xlogin.XLoginHelper
import com.socks.library.KLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateGroupViewModel(
    val context: Application,
    val groupUseCase: GroupUseCase
) : AndroidViewModel(context) {

    private val TAG = CreateGroupViewModel::class.java.simpleName

    private val _warningText = MutableSharedFlow<String>()  //Error msg
    val warningText = _warningText.asSharedFlow()

    private val _currentStep = MutableStateFlow(1)  //目前步驟
    val currentStep = _currentStep.asStateFlow()

    private val _group = MutableStateFlow(Group())     //要建立的社團
    val group = _group.asStateFlow()

    private val _fanciColor = MutableStateFlow<FanciColor?>(null)   //選擇的Theme Color
    val fanciColor = _fanciColor.asStateFlow()

    private val _createComplete = MutableSharedFlow<Group>()    //建立完成
    val createComplete = _createComplete.asSharedFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    val finalStep = 3

    private fun loading() {
        _loading.value = true
    }

    fun dismissLoading() {
        _loading.value = false
    }

    private fun sendErrorMsg(msg: String) {
        viewModelScope.launch {
            _warningText.emit(msg)
        }
    }

    /**
     * 上一步
     */
    fun preStep() {
        _currentStep.value = _currentStep.value - 1
    }

    /**
     * 下一步
     */
    fun nextStep() {
        //step1 to next, check group name
        if (_currentStep.value == 1) {
            if (_group.value.name.isNullOrEmpty()) {
                sendErrorMsg("請輸入社團名稱")
                return
            }
        }

        _currentStep.value = _currentStep.value + 1
    }

    /**
     * 設定 社團名稱
     */
    fun setGroupName(name: String) {
        KLog.i(TAG, "setGroupName:$name")
        _group.value = _group.value.copy(
            name = name
        )
    }

    /**
     * 設定 大頭貼
     */
    fun changeGroupAvatar(data: ImageChangeData) {
        KLog.i(TAG, "changeGroupAvatar")
        viewModelScope.launch {
            var imageUrl = data.url.orEmpty()
            if (data.uri != null) {
                imageUrl = withContext(Dispatchers.IO) {
                    val uploadImage =
                        UploadImage(
                            context,
                            listOf(data.uri),
                            XLoginHelper.accessToken,
                            BuildConfig.DEBUG
                        )

                    val uploadResult = uploadImage.upload().first()
                    uploadResult.second
                }
            }

            _group.value = _group.value.copy(
                thumbnailImageUrl = imageUrl
            )
        }
    }

    /**
     * 設定 背景
     */
    fun changeGroupCover(data: ImageChangeData) {
        KLog.i(TAG, "changeGroupCover")
        viewModelScope.launch {
            var imageUrl = data.url.orEmpty()
            if (data.uri != null) {
                imageUrl = withContext(Dispatchers.IO) {
                    val uploadImage =
                        UploadImage(
                            context,
                            listOf(data.uri),
                            XLoginHelper.accessToken,
                            BuildConfig.DEBUG
                        )

                    val uploadResult = uploadImage.upload().first()
                    uploadResult.second
                }
            }
            _group.value = _group.value.copy(
                coverImageUrl = imageUrl
            )
        }
    }

    /**
     * 設定 主題
     */
    fun setGroupTheme(groupTheme: GroupTheme) {
        KLog.i(TAG, "setGroupTheme:$groupTheme")
        _fanciColor.value = groupTheme.theme

        _group.value = _group.value.copy(
            colorSchemeGroupKey = ColorTheme.decode(groupTheme.id)
        )
    }

    /**
     * 建立社團
     */
    fun createGroup(
        isNeedApproval: Boolean
    ) {
        val preCreateGroup = _group.value
        KLog.i(TAG, "onCreateGroup:$preCreateGroup")

        if (_group.value.thumbnailImageUrl.isNullOrEmpty()) {
            sendErrorMsg("請選擇圖示")
            return
        }

        if (_group.value.coverImageUrl.isNullOrEmpty()) {
            sendErrorMsg("請選擇背景")
            return
        }

        if (preCreateGroup.colorSchemeGroupKey == null) {
            sendErrorMsg("請選擇主題色彩")
            return
        }

        viewModelScope.launch {
            loading()

            groupUseCase.createGroup(
                name = _group.value.name.orEmpty(),
                isNeedApproval = isNeedApproval,
                coverImageUrl = _group.value.coverImageUrl.orEmpty(),
                thumbnailImageUrl = _group.value.thumbnailImageUrl.orEmpty(),
                themeId = preCreateGroup.colorSchemeGroupKey?.value.orEmpty()
            ).fold({
                KLog.i(TAG, "createGroup success:$it")
                _createComplete.emit(it)
//                _group.value = it
            }, {
            })
        }
    }
}