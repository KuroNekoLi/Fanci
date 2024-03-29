package com.cmoney.kolfanci.model.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.MessageServiceType
import com.cmoney.kolfanci.extension.toBulletinboardMessage
import com.cmoney.kolfanci.model.notification.NotificationHelper
import com.cmoney.kolfanci.model.notification.Payload
import com.cmoney.kolfanci.model.notification.TargetType
import com.cmoney.kolfanci.model.usecase.ChatRoomUseCase
import com.cmoney.kolfanci.model.usecase.GroupUseCase
import com.cmoney.kolfanci.model.usecase.PermissionUseCase
import com.cmoney.kolfanci.ui.screens.follow.model.GroupItem
import com.socks.library.KLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val notificationHelper: NotificationHelper,
    private val groupUseCase: GroupUseCase,
    private val chatRoomUseCase: ChatRoomUseCase,
    private val permissionUseCase: PermissionUseCase
) : ViewModel() {
    private val TAG = NotificationViewModel::class.java.simpleName

    private val _inviteGroup: MutableStateFlow<Group?> = MutableStateFlow(null)
    val inviteGroup = _inviteGroup.asStateFlow()

    //收到 推播訊息
    private val _targetType: MutableStateFlow<TargetType?> = MutableStateFlow(null)
    val targetType = _targetType.asStateFlow()

    //前往指定頻道, 指定貼文/指定訊息...
    private val _jumpToChannelDest = MutableStateFlow<PushDataWrapper?>(null)
    val jumpToChannelDest = _jumpToChannelDest.asStateFlow()

    //目前沒加入該社團, 彈窗
    private val _showNotJoinAlert = MutableStateFlow(false)
    val showNotJoinAlert = _showNotJoinAlert.asStateFlow()

    //解散社團 彈窗
    private val _showDissolveGroupDialog = MutableStateFlow<String?>(null)
    val showDissolveGroupDialog = _showDissolveGroupDialog.asStateFlow()

    //刷新 我的社團
    private val _refreshGroup = MutableStateFlow(false)
    val refreshGroup = _refreshGroup.asStateFlow()

    //前往加入審核畫面
    private val _groupApprovePage = MutableStateFlow<Group?>(null)
    val groupApprovePage = _groupApprovePage.asStateFlow()

    //打開指定社團
    private val _openGroup = MutableStateFlow<Group?>(null)
    val openGroup = _openGroup.asStateFlow()

    //沒有權限 執行動作
    private val _showNoPermissionTip = MutableStateFlow(false)
    val showNoPermissionTip = _showNoPermissionTip.asStateFlow()

    /**
     * 推播 or dynamic link 資料
     */
    fun setNotificationBundle(payLoad: Payload) {
        KLog.i(TAG, "setNotificationBundle payLoad:$payLoad")
        val targetType =
            notificationHelper.convertPayloadToTargetType(payLoad) ?: TargetType.MainPage

        KLog.i(TAG, "setNotificationBundle targetType:${targetType}")

        _targetType.value = targetType
    }


    /**
     * reset state
     */
    fun clearPushDataState() {
        KLog.i(TAG, "clearPushDataState")
        _targetType.value = null
    }

    /**
     * 抓取邀請連結社團的資訊
     */
    fun fetchInviteGroup(groupId: String) {
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

    /**
     * 收到新訊息 推播
     */
    fun receiveNewMessage(
        receiveNewMessage: TargetType.ReceiveMessage?,
        myGroupList: List<GroupItem>
    ) {
        KLog.i(TAG, "receiveNewMessage:$receiveNewMessage")
        receiveNewMessage?.let {
            viewModelScope.launch {
                val groupId = receiveNewMessage.groupId
                val channelId = receiveNewMessage.channelId
                val messageId = receiveNewMessage.messageId

                myGroupList.firstOrNull { groupItem ->
                    groupItem.groupModel.id == groupId
                }?.also {
                    it.groupModel.categories?.flatMap { category ->
                        category.channels.orEmpty()
                    }?.firstOrNull { channel ->
                        channel.id == channelId
                    }?.also { channel ->

                        //更新該頻道權限
                        permissionUseCase.updateChannelPermissionAndBuff(channelId = channel.id.orEmpty())
                            .onSuccess { _ ->
                                _jumpToChannelDest.value = PushDataWrapper.ChannelMessage(
                                    group = it.groupModel,
                                    channel = channel,
                                    messageId = messageId
                                )
                            }
                    }
                } ?: kotlin.run {
                    _showNotJoinAlert.value = true
                }
            }
        }
    }

    /**
     * 收到 新貼文 推播
     *
     * 檢查是否已經加入該社團並打開該社團
     * 取得指定文章資訊
     */
    fun receiveNewPost(
        receivePostMessage: TargetType.ReceivePostMessage?,
        myGroupList: List<GroupItem>
    ) {
        KLog.i(TAG, "receivePostMessage:$receivePostMessage")
        receivePostMessage?.let {
            viewModelScope.launch {
                val groupId = receivePostMessage.groupId
                val channelId = receivePostMessage.channelId
                val messageId = receivePostMessage.messageId

                myGroupList.firstOrNull { groupItem ->
                    groupItem.groupModel.id == groupId
                }?.also {
                    it.groupModel.categories?.flatMap { category ->
                        category.channels.orEmpty()
                    }?.firstOrNull { channel ->
                        channel.id == channelId
                    }?.also { channel ->

                        //取得指定文章資訊
                        chatRoomUseCase.getSingleMessage(
                            messageId = messageId,
                            messageServiceType = MessageServiceType.bulletinboard
                        ).onSuccess { chatMessage ->

                            //更新該頻道權限
                            permissionUseCase.updateChannelPermissionAndBuff(channelId = channel.id.orEmpty())
                                .onSuccess { _ ->
                                    _jumpToChannelDest.value = PushDataWrapper.ChannelPost(
                                        group = it.groupModel,
                                        channel = channel,
                                        bulletinboardMessage = chatMessage.toBulletinboardMessage()
                                    )
                                }
                        }.onFailure { err ->
                            KLog.e(TAG, err)
                        }
                    }
                } ?: kotlin.run {
                    _showNotJoinAlert.value = true
                }
            }
        }
    }

    /**
     * 解散 社團
     */
    fun dissolveGroup(dissolveGroup: TargetType.DissolveGroup) {
        KLog.i(TAG, "dissolveGroup:$dissolveGroup")
        _showDissolveGroupDialog.value = dissolveGroup.groupId
    }

    /**
     *  檢查 解散的社團跟目前選中的社團 是否一樣
     *  如果一樣-> 就執行刷新動作
     *  不一樣時-> 不動作
     */
    fun onCheckDissolveGroup(groupId: String, currentGroup: Group?) {
        KLog.i(TAG, "onCheckDissolveGroup:$groupId")
        if (currentGroup?.id == groupId) {
            _refreshGroup.value = true
        }
    }

    fun finishJumpToChannelDest() {
        _jumpToChannelDest.value = null
    }

    fun dismissNotJoinAlert() {
        _showNotJoinAlert.value = false
    }

    fun dismissDissolveDialog() {
        _showDissolveGroupDialog.value = null
    }

    fun afterRefreshGroup() {
        _refreshGroup.value = false
    }

    /**
     * 管理者, 前往申請加入審核頁面
     */
    fun groupApprove(groupId: String) {
        KLog.i(TAG, "groupApprove:$groupId")
        viewModelScope.launch {
            //檢查是否有權限進入
            permissionUseCase.getPermissionByGroup(groupId = groupId)
                .onSuccess {
                    //有權限審核
                    if (it.approveJoinApplies == true) {
                        groupUseCase.getGroupById(groupId = groupId)
                            .onSuccess { group ->
                                _groupApprovePage.value = group
                            }
                            .onFailure { err ->
                                KLog.e(TAG, err)
                            }
                    }
                    //沒有該權限審核
                    else {
                        _showNoPermissionTip.value = true
                    }
                }
                .onFailure { err ->
                    KLog.e(TAG, err)
                }
        }
    }

    fun afterOpenApprovePage() {
        _groupApprovePage.value = null
        _showNoPermissionTip.value = false
    }

    /**
     * 打開 指定社團
     */
    fun openGroup(groupId: String, myGroupList: List<GroupItem>) {
        KLog.i(TAG, "openGroup:$groupId")
        viewModelScope.launch {
            myGroupList.firstOrNull { groupItem ->
                groupItem.groupModel.id == groupId
            }?.also {
                _openGroup.value = it.groupModel
            } ?: kotlin.run {
                _showNotJoinAlert.value = true
            }
        }
    }

    fun afterOpenGroup() {
        _openGroup.value = null
    }
}