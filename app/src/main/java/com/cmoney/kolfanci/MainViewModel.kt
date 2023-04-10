package com.cmoney.kolfanci

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.persistence.SettingsDataStore
import com.cmoney.kolfanci.model.usecase.PermissionUseCase
import com.cmoney.kolfanci.model.usecase.ThemeUseCase
import com.cmoney.kolfanci.model.usecase.UserUseCase
import com.cmoney.kolfanci.ui.theme.DefaultThemeColor
import com.cmoney.kolfanci.ui.theme.FanciColor
import com.cmoney.fanciapi.fanci.model.Category
import com.cmoney.fanciapi.fanci.model.Channel
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.ui.theme.CoffeeThemeColor
import com.socks.library.KLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ThemeSetting {
    object Default : ThemeSetting()
    object Coffee : ThemeSetting()
}

data class UiState(
    val currentGroup: Group? = null, //目前選擇中的社團
//    val theme: FanciColor = CoffeeThemeColor,
    val theme: FanciColor = DefaultThemeColor,
    val isLoginSuccess: Boolean = false,
    val isOpenTutorial: Boolean = false,
)

class MainViewModel(
    private val userUseCase: UserUseCase,
    private val themeUseCase: ThemeUseCase,
    private val settingsDataStore: SettingsDataStore,
    private val permissionUseCase: PermissionUseCase
) :
    ViewModel() {
    private val TAG = MainViewModel::class.java.simpleName

    var uiState by mutableStateOf(UiState())
        private set

    private val _fetchFollowData = MutableStateFlow(false)
    val fetchFollowData: StateFlow<Boolean>
        get() = _fetchFollowData

    init {
        viewModelScope.launch {
            settingsDataStore.isTutorial.collect {
                uiState = uiState.copy(
                    isOpenTutorial = it
                )
            }
        }
    }

    /**
     * 登入成功之後,要向 Fanci後台註冊
     */
    fun registerUser() {
        KLog.i(TAG, "registerUser")
        viewModelScope.launch {
            userUseCase.registerUser()
            userUseCase.fetchMyInfo().fold({
                Constant.MyInfo = it
            }, {
                KLog.e(TAG, it)
            })
        }
    }

    /**
     * 設定 目前所選的社團, 並設定Theme
     */
    fun setCurrentGroup(group: Group) {
        KLog.i(TAG, "setCurrentGroup")
        if (group != uiState.currentGroup && group.id != null) {
            KLog.i(TAG, "setCurrentGroup diff:$group")

            fetchGroupPermission(group)
            uiState = uiState.copy(
                currentGroup = group
            )
            viewModelScope.launch {
                group.colorSchemeGroupKey?.apply {
                    themeUseCase.fetchThemeConfig(this).fold({
                        uiState = uiState.copy(
                            theme = it.theme
                        )
                    }, {
                        KLog.e(TAG, it)
                    })
                }
            }
        }
    }

    /**
     * 抓取在該社團的權限
     */
    private fun fetchGroupPermission(group: Group) {
        KLog.i(TAG, "fetchGroupPermission:$group")
        viewModelScope.launch {
            permissionUseCase.getPermissionByGroup(groupId = group.id.orEmpty()).fold(
                {
                    KLog.i(TAG, it)
                    Constant.MyGroupPermission = it
                }, {
                    KLog.e(TAG, it)
                }
            )
        }
    }


    /**
     * 看過 tutorial
     */
    fun tutorialOnOpen() {
        KLog.i(TAG, "tutorialOnOpen")
        viewModelScope.launch {
            settingsDataStore.onTutorialOpen()
//            _isOpenTutorial.value = true
            uiState = uiState.copy(
                isOpenTutorial = true
            )
        }
    }

    /**
     * 登入成功
     */
    fun loginSuccess() {
        KLog.i(TAG, "loginSuccess")
        uiState = uiState.copy(
            isLoginSuccess = true,
        )
    }

    /**
     * 執行完抓取社團資料
     */
    fun fetchFollowDataDone() {
        _fetchFollowData.value = false
    }

    fun loginProcessDone() {
        _fetchFollowData.value = true
    }

//    fun sortCallback(categoryList: List<Category>) {
//        uiState = uiState.copy(
//            testCategory = categoryList
//        )
//    }

}