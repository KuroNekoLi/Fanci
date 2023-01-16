package com.cmoney.fanci

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.model.persistence.SettingsDataStore
import com.cmoney.fanci.model.usecase.ThemeUseCase
import com.cmoney.fanci.model.usecase.UserUseCase
import com.cmoney.fanci.ui.theme.CoffeeThemeColor
import com.cmoney.fanci.ui.theme.DefaultThemeColor
import com.cmoney.fanci.ui.theme.FanciColor
import com.cmoney.fanciapi.fanci.model.Group
import com.socks.library.KLog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

sealed class ThemeSetting {
    object Default : ThemeSetting()
    object Coffee : ThemeSetting()
}

data class UiState(
    val currentGroup: Group? = null, //目前選擇中的社團
//    val theme: FanciColor = CoffeeThemeColor,
    val theme: FanciColor = DefaultThemeColor,
    val isLoginSuccess: Boolean = false
)

class MainViewModel(
    private val userUseCase: UserUseCase,
    private val themeUseCase: ThemeUseCase,
    private val settingsDataStore: SettingsDataStore
) :
    ViewModel() {
    private val TAG = MainViewModel::class.java.simpleName

//    private val _theme = MutableLiveData<ThemeSetting>(ThemeSetting.Coffee)
//    val theme: LiveData<ThemeSetting> = _theme

    private val _isOpenTutorial = MutableLiveData<Boolean>()
    val isOpenTutorial: LiveData<Boolean> = _isOpenTutorial

    var uiState by mutableStateOf(UiState())
        private set

    init {
        viewModelScope.launch {
            settingsDataStore.isTutorial.collect {
                _isOpenTutorial.value = it
            }
        }
    }


//    fun setCoffeeTheme() {
//        KLog.i(TAG, "setCoffeeTheme")
//        _theme.value = ThemeSetting.Coffee
//    }
//
//    fun settingTheme(themeSetting: ThemeSetting) {
//        KLog.i(TAG, "settingTheme:$themeSetting")
//        _theme.value = themeSetting
//    }

    /**
     * 登入成功之後,要向 Fanci後台註冊
     */
    fun registerUser() {
        KLog.i(TAG, "registerUser")
        viewModelScope.launch {
            userUseCase.registerUser()
        }
    }

    /**
     * 設定 目前所選的社團, 並設定Theme
     */
    fun setCurrentGroup(group: Group) {
        KLog.i(TAG, "setCurrentGroup:$group")
        if (group != uiState.currentGroup && group.id != null) {
            KLog.i(TAG, "setCurrentGroup diff:$group")
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
        uiState = uiState.copy(
            isLoginSuccess = true
        )
    }

}