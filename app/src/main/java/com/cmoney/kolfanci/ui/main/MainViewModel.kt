package com.cmoney.kolfanci.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.notification.NotificationHelper
import com.cmoney.kolfanci.model.notification.Payload
import com.cmoney.kolfanci.model.notification.TargetType
import com.cmoney.kolfanci.model.persistence.SettingsDataStore
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.cmoney.kolfanci.model.usecase.UserUseCase
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(
    private val userUseCase: UserUseCase,
    private val settingsDataStore: SettingsDataStore,
    private val notificationHelper: NotificationHelper,
    private val groupUseCase: GroupUseCase
) :
    ViewModel() {
    private val TAG = MainViewModel::class.java.simpleName

    //自動登入中
    private val _loginLoading = MutableStateFlow(true)
    val loginLoading = _loginLoading.asStateFlow()

    private val _isOpenTutorial: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isOpenTutorial = _isOpenTutorial.asStateFlow()

    private val _isLoginSuccess = MutableStateFlow(false)
    val isLoginSuccess = _isLoginSuccess.asStateFlow()

    private val _inviteGroup: MutableStateFlow<Group?> = MutableStateFlow(null)
    val inviteGroup = _inviteGroup.asStateFlow()

    //收到 推播 新訊息
    private val _receiveNewMessage: MutableStateFlow<TargetType.ReceiveMessage?> =
        MutableStateFlow(null)
    val receiveNewMessage = _receiveNewMessage.asStateFlow()

    //收到 推播訊息
    private val _targetType: MutableStateFlow<TargetType?> = MutableStateFlow(null)
    val targetType = _targetType.asStateFlow()

    init {
        viewModelScope.launch {
            _isOpenTutorial.value = settingsDataStore.isTutorial.first()
        }
    }

    /**
     * 登入成功之後,要向 Fanci後台註冊
     */
    private fun registerUser() {
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
     * 自動登入作業完成
     */
    private fun loginProcessDone() {
        KLog.i(TAG, "loginProcessDone")
        _loginLoading.value = false
    }

    /**
     * 登入成功, 並註冊使用者資訊
     */
    fun loginSuccess() {
        KLog.i(TAG, "loginSuccess")
        registerUser()
        loginProcessDone()
    }

    /**
     * 登入失敗
     */
    fun loginFail(errorMessage: String) {
        KLog.i(TAG, "loginFail:$errorMessage")
        loginProcessDone()
    }

    /**
     * 推播 or dynamic link 資料
     */
    fun setNotificationBundle(payLoad: Payload) {
        KLog.i(TAG, "setNotificationBundle payLoad:$payLoad")
        val targetType =
            notificationHelper.convertPayloadToTargetType(payLoad) ?: TargetType.MainPage

        KLog.i(TAG, "setNotificationBundle targetType:${targetType}")

        _targetType.value = targetType

        when (targetType) {
            is TargetType.InviteGroup -> {
                val groupId = targetType.groupId
                fetchInviteGroup(groupId)
            }

            is TargetType.ReceiveMessage -> {
                KLog.i(TAG, "receiveNewMessage:$targetType")
                _receiveNewMessage.value = targetType
            }

            TargetType.MainPage -> {}
        }
    }

    /**
     * 抓取邀請連結社團的資訊
     */
    private fun fetchInviteGroup(groupId: String) {
        KLog.i(TAG, "fetchInviteGroup:$groupId")
        viewModelScope.launch {
            groupUseCase.getGroupById(
                groupId
            ).fold({
                KLog.i(TAG, "fetchInviteGroup success:$it")
                _inviteGroup.value = it
            }, {
                it.printStackTrace()
                KLog.e(TAG, it)
            })
        }
    }

    fun openedInviteGroup() {
        _inviteGroup.value = null
    }

    fun clearPushData() {
        _receiveNewMessage.value = null
    }
}