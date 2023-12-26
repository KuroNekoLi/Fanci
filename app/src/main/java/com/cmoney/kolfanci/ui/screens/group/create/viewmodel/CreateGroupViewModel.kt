package com.cmoney.kolfanci.ui.screens.group.create.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.ColorTheme
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.model.persistence.SettingsDataStore
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
import kotlinx.coroutines.flow.update
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
    val uploadImageUseCase: UploadImageUseCase,
    val settingsDataStore: SettingsDataStore
) : AndroidViewModel(context) {

    private val TAG = CreateGroupViewModel::class.java.simpleName

    private val _warningText = MutableSharedFlow<String>()  //Error msg
    val warningText = _warningText.asSharedFlow()

    private val _currentStep = MutableStateFlow(1)  //目前步驟
    val currentStep = _currentStep.asStateFlow()

    private val _group = MutableStateFlow(
        //要建立的社團
        Group()
    )
    val group = _group.asStateFlow()

    /**
     * 選擇的Theme Color
     */
    private val _fanciColor = MutableStateFlow<FanciColor?>(null)
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
        val currentStepValue = _currentStep.value
        when (currentStepValue) {
            1 -> {
                if (_group.value.name.isNullOrEmpty()) {
                    sendErrorMsg("請輸入社團名稱")
                    return
                }
            }

            2 -> {
                prepareDefaultAvatarAndCoverAndTheme()
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

    private fun prepareDefaultAvatarAndCoverAndTheme() {
        viewModelScope.launch {
            // 預設第一個 avatar, cover
            val thumbnailImageUrl = groupUseCase.fetchGroupAvatarLib()
                .getOrNull()
                ?.firstOrNull()
            val coverImageUrl = groupUseCase.fetchGroupCoverLib()
                .getOrNull()
                ?.firstOrNull()
            _group.update { old ->
                old.copy(coverImageUrl = coverImageUrl, thumbnailImageUrl = thumbnailImageUrl)
            }
            // 預設 theme 為 ColorTheme.themeFanciBlue
            setGroupTheme(ColorTheme.themeFanciBlue.value)
        }
    }

    /**
     * 設定 Logo
     */
    fun changeGroupLogo(data: ImageChangeData) {
        KLog.i(TAG, "changeGroupLogo")
        viewModelScope.launch {
            var imageUrl = data.url.orEmpty()
            if (data.uri != null) {
                imageUrl = withContext(Dispatchers.IO) {
                    val uploadResult = uploadImageUseCase.uploadImage(listOf(data.uri)).first()
                    uploadResult.second
                }
            }

            _group.value = _group.value.copy(
                logoImageUrl = imageUrl
            )
        }
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
            _group.update { oldGroup ->
                oldGroup.copy(
                    colorSchemeGroupKey = ColorTheme.decode(groupThemeId)
                )
            }
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
                logoImageUrl = _group.value.logoImageUrl.orEmpty(),
                themeId = preCreateGroup.colorSchemeGroupKey?.value.orEmpty()
            ).fold({ createdGroup ->
                KLog.i(TAG, "createGroup success")
                uiState = uiState.copy(
                    createdGroup = createdGroup,
                    createComplete = true
                )

                settingsDataStore.setHomeBubbleShow()
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