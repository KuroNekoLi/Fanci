package com.cmoney.fanci

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.socks.library.KLog

sealed class ThemeSetting {
    object Default : ThemeSetting()
    object Coffee : ThemeSetting()
}

class MainViewModel : ViewModel() {
    private val TAG = MainViewModel::class.java.simpleName

    private val _theme = MutableLiveData<ThemeSetting>(ThemeSetting.Default)
    val theme: LiveData<ThemeSetting> = _theme

    fun setCoffeeTheme() {
        KLog.i(TAG, "setCoffeeTheme")
        _theme.value = ThemeSetting.Coffee
    }

    fun settingTheme(themeSetting: ThemeSetting) {
        KLog.i(TAG, "settingTheme:$themeSetting")
        _theme.value = themeSetting
    }
}