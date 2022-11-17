package com.cmoney.fanci

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanci.model.usecase.GroupUseCase
import com.cmoney.fanci.model.usecase.UserUseCase
import com.cmoney.fanci.ui.screens.follow.model.GroupItem
import com.socks.library.KLog
import kotlinx.coroutines.launch

sealed class ThemeSetting {
    object Default : ThemeSetting()
    object Coffee : ThemeSetting()
}

class MainViewModel(private val groupUseCase: GroupUseCase, private val userUseCase: UserUseCase) :
    ViewModel() {
    private val TAG = MainViewModel::class.java.simpleName

    private val _theme = MutableLiveData<ThemeSetting>(ThemeSetting.Coffee)
    val theme: LiveData<ThemeSetting> = _theme

    private val _groupList = MutableLiveData<List<GroupItem>>()
    val groupList: LiveData<List<GroupItem>> = _groupList

    init {
        viewModelScope.launch {
            groupUseCase.groupToSelectGroupItem().fold({
                if (it.isNotEmpty()) {
                    //所有群組
                    _groupList.value = it
                }
            }, {
                KLog.e(TAG, it)
            })
        }
    }

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

//    fun stopPolling() {
//        chatRoomPollUseCase.close()
//    }
//
//    fun startPolling() {
//        viewModelScope.launch {
//            chatRoomPollUseCase.poll(1500, "2177").collect {
//                KLog.i(TAG, it)
//            }
//        }
//    }
}