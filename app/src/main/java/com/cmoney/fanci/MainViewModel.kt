package com.cmoney.fanci

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.model.usecase.UserUseCase
import com.cmoney.fanciapi.fanci.model.Group
import com.socks.library.KLog
import kotlinx.coroutines.launch

sealed class ThemeSetting {
    object Default : ThemeSetting()
    object Coffee : ThemeSetting()
}

data class UiState(
    val currentGroup: Group? = null //目前選擇中的社團
)

class MainViewModel(private val userUseCase: UserUseCase) :
    ViewModel() {
    private val TAG = MainViewModel::class.java.simpleName

    private val _theme = MutableLiveData<ThemeSetting>(ThemeSetting.Default)
    val theme: LiveData<ThemeSetting> = _theme

    var uiState by mutableStateOf(UiState())
        private set

    fun setCoffeeTheme() {
        KLog.i(TAG, "setCoffeeTheme")
        _theme.value = ThemeSetting.Coffee
    }

    fun settingTheme(themeSetting: ThemeSetting) {
        KLog.i(TAG, "settingTheme:$themeSetting")
        _theme.value = themeSetting
    }

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
     * 設定 目前所選的社團
     */
    fun setCurrentGroup(group: Group) {
        uiState = uiState.copy(
            currentGroup = group
        )
    }
}