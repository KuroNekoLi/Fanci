package com.cmoney.fanci.ui.screens.group.create.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanci.ui.screens.group.setting.group.groupsetting.ImageChangeData
import com.cmoney.fanci.ui.screens.group.setting.group.groupsetting.theme.model.GroupTheme
import com.cmoney.fanciapi.fanci.model.Group
import com.socks.library.KLog
import kotlinx.coroutines.launch

data class UiState(
    val currentStep: Int = 1,           //目前步驟
    val groupName: String = "",         //社團名稱
    val warningText: String = "",       //錯誤提示
    val groupIcon: String = "",         //社團圖示
    val groupBackground: String = "",   //社團背景
    val groupTheme: GroupTheme? = null  //社團主題
)

class CreateGroupViewModel(val groupUseCase: GroupUseCase) : ViewModel() {
    private val TAG = CreateGroupViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    val finalStep = 3

    /**
     * 上一步
     */
    fun preStep() {
        uiState = uiState.copy(
            currentStep = uiState.currentStep - 1
        )
    }

    /**
     * 下一步
     */
    fun nextStep() {
        //step1 to next, check group name
        if (uiState.currentStep == 1) {
            if (uiState.groupName.isEmpty()) {
                uiState = uiState.copy(
                    warningText = "請輸入社團名稱"
                )
                return
            }
        }

        uiState = uiState.copy(
            currentStep = uiState.currentStep + 1
        )
    }

    /**
     * 設定 社團名稱
     */
    fun setGroupName(name: String) {
        KLog.i(TAG, "setGroupName:$name")
        uiState = uiState.copy(
            groupName = name
        )
    }

    /**
     * 關閉警告
     */
    fun resetWarning() {
        uiState = uiState.copy(
            warningText = ""
        )
    }

    /**
     * 設定 大頭貼
     */
    fun changeGroupAvatar(data: ImageChangeData) {
        KLog.i(TAG, "changeGroupAvatar")
        uiState = uiState.copy(
            groupIcon = data.url.orEmpty()
        )
    }

    /**
     * 設定 背景
     */
    fun changeGroupCover(data: ImageChangeData) {
        KLog.i(TAG, "changeGroupCover")
        uiState = uiState.copy(
            groupBackground = data.url.orEmpty()
        )
    }

    /**
     * 設定 主題
     */
    fun setGroupTheme(groupTheme: GroupTheme) {
        KLog.i(TAG, "setGroupTheme:$groupTheme")
        uiState = uiState.copy(
            groupTheme = groupTheme
        )
    }

    /**
     * 建立社團
     */
    fun createGroup(onComplete: (Group) -> Unit) {
        KLog.i(TAG, "onCreateGroup:$uiState")

        if (uiState.groupIcon.isEmpty()) {
            uiState = uiState.copy(
                warningText = "請選擇圖示"
            )
            return
        }

        if (uiState.groupBackground.isEmpty()) {
            uiState = uiState.copy(
                warningText = "請選擇背景"
            )
            return
        }

        if (uiState.groupTheme == null) {
            uiState = uiState.copy(
                warningText = "請選擇主題色彩"
            )
            return
        }

        viewModelScope.launch {
            groupUseCase.createGroup(
                name = uiState.groupName,
                isNeedApproval = false,
                coverImageUrl = uiState.groupBackground,
                thumbnailImageUrl = uiState.groupIcon,
                themeName = uiState.groupTheme?.name.orEmpty()
            ).fold({
                KLog.i(TAG, "createGroup success:$it")
                onComplete.invoke(it)
            }, {
            })
        }
    }
}