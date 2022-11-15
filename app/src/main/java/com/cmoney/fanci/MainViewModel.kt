package com.cmoney.fanci

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.repository.Network
import com.cmoney.fanci.repository.NetworkImpl
import com.socks.library.KLog
import kotlinx.coroutines.launch

sealed class ThemeSetting {
    object Default : ThemeSetting()
    object Coffee : ThemeSetting()
}

class MainViewModel(val network: Network) : ViewModel() {
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

    fun test() {
        viewModelScope.launch {
            network.testGroup().fold({
                KLog.i(TAG, it)
            },{
                KLog.e(TAG, it)
            })
        }
    }
}