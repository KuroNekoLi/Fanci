package com.cmoney.kolfanci.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.persistence.SettingsDataStore
import com.cmoney.kolfanci.model.usecase.PermissionUseCase
import com.cmoney.kolfanci.model.usecase.ThemeUseCase
import com.cmoney.kolfanci.model.usecase.UserUseCase
import com.cmoney.kolfanci.ui.theme.DefaultThemeColor
import com.cmoney.fanciapi.fanci.model.Group
import com.socks.library.KLog
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ThemeSetting {
    object Default : ThemeSetting()
    object Coffee : ThemeSetting()
}

class MainViewModel(
    private val userUseCase: UserUseCase,
    private val themeUseCase: ThemeUseCase,
    private val settingsDataStore: SettingsDataStore,
    private val permissionUseCase: PermissionUseCase
) :
    ViewModel() {
    private val TAG = MainViewModel::class.java.simpleName

    private val _fetchFollowData = MutableStateFlow(false)
    val fetchFollowData: StateFlow<Boolean>
        get() = _fetchFollowData

    private val _isOpenTutorial: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isOpenTutorial = _isOpenTutorial.asStateFlow()

    private val _theme = MutableStateFlow(DefaultThemeColor)
    val theme = _theme.asStateFlow()

    private val _isLoginSuccess = MutableStateFlow(false)
    val isLoginSuccess = _isLoginSuccess.asStateFlow()

    private val _currentGroup: MutableStateFlow<Group?> = MutableStateFlow(null)
    val currentGroup = _currentGroup.asStateFlow()

    init {
        viewModelScope.launch {
            _isOpenTutorial.value = settingsDataStore.isTutorial.first()
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
        if (group != _currentGroup.value && group.id != null) {
            KLog.i(TAG, "setCurrentGroup diff:$group")
            fetchGroupPermission(group)
            _currentGroup.value = group
            viewModelScope.launch {
                group.colorSchemeGroupKey?.apply {
                    themeUseCase.fetchThemeConfig(this).fold({
                        _theme.value = it.theme
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
            _isOpenTutorial.value = true
        }
    }

    /**
     * 登入成功
     */
    fun loginSuccess() {
        KLog.i(TAG, "loginSuccess")
        _isLoginSuccess.value = true
    }

    /**
     * 執行完抓取社團資料
     */
    fun fetchFollowDataDone() {
        _fetchFollowData.value = false
    }

    fun startFetchFollowData() {
        KLog.i(TAG, "startFetchFollowData")
        _fetchFollowData.value = true
    }
}