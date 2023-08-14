package com.cmoney.kolfanci.ui.screens.group.create.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.ColorTheme
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.cmoney.kolfanci.model.usecase.ThemeUseCase
import com.cmoney.kolfanci.model.usecase.UploadImageUseCase
import com.cmoney.kolfanci.ui.screens.group.setting.group.groupsetting.avatar.ImageChangeData
import com.cmoney.kolfanci.ui.theme.FanciColor
import com.socks.library.KLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class UiState(
    val createdGroup: Group? = null,
    val createComplete: Boolean? = null
)

class CreateGroupViewModel(
    val context: Application,
    val groupUseCase: GroupUseCase,
    private val themeUseCase: ThemeUseCase,
    val uploadImageUseCase: UploadImageUseCase
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

    var uiState by mutableStateOf(UiState())
        private set

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
                    val uploadResult = uploadImageUseCase.uploadImage(listOf(data.uri)).first()
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
                    val uploadResult = uploadImageUseCase.uploadImage(listOf(data.uri)).first()
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
     *
     * @param groupThemeId ex: ThemeSmokePink
     */
    fun setGroupTheme(groupThemeId: String) {
        KLog.i(TAG, "setGroupTheme:$groupThemeId")
        viewModelScope.launch {
            ColorTheme.decode(groupThemeId)?.let { colorTheme ->
                themeUseCase.fetchThemeConfig(colorTheme)
                    .onSuccess { groupTheme ->
                        _fanciColor.value = groupTheme.theme
                    }
                    .onFailure {
                        KLog.e(TAG, it)
                    }
            }

            _group.value = _group.value.copy(
                colorSchemeGroupKey = ColorTheme.decode(groupThemeId)
            )
        }
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
            ).fold({ createdGroup ->
                KLog.i(TAG, "createGroup success")
                uiState = uiState.copy(
                    createdGroup = createdGroup,
                    createComplete = true
                )
                // _group.value = it
            }, {
            })
        }
    }

    fun onCreateFinish() {
        uiState = uiState.copy(
            createComplete = null
        )
    }
}