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
import com.socks.library.KLog
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
    val testCategory: List<Category> = listOf(
        Category(
            id = "1",
            name = "Title1",
            channels = listOf(
                Channel(
                    id = "1",
                    name = "Channel1"
                ),
                Channel(
                    id = "2",
                    name = "Channel2"
                ),
                Channel(
                    id = "3",
                    name = "Channel3"
                )
            )
        ),
        Category(
            id = "2",
            name = "Title2",
            channels = listOf(
                Channel(
                    id = "4",
                    name = "Channel4"
                ),
                Channel(
                    id = "5",
                    name = "Channel5"
                ),
                Channel(
                    id = "6",
                    name = "Channel6"
                )
            )
        )
    )
)

class MainViewModel(
    private val userUseCase: UserUseCase,
    private val themeUseCase: ThemeUseCase,
    private val settingsDataStore: SettingsDataStore,
    private val permissionUseCase: PermissionUseCase
) :
    ViewModel() {
    private val TAG = MainViewModel::class.java.simpleName

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
        KLog.i(TAG, "setCurrentGroup:$group")
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

    fun sortCallback(categoryList: List<Category>) {
        uiState = uiState.copy(
            testCategory = categoryList
        )
    }

}