package com.cmoney.kolfanci.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.cmoney.fanciapi.fanci.model.ChatMessage
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.findActivity
import com.cmoney.kolfanci.extension.globalGroupViewModel
import com.cmoney.kolfanci.extension.showToast
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.viewmodel.NotificationViewModel
import com.cmoney.kolfanci.model.viewmodel.PushDataWrapper
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.ChannelScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupSettingScreenDestination
import com.cmoney.kolfanci.ui.destinations.PostInfoScreenDestination
import com.cmoney.kolfanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import com.cmoney.kolfanci.ui.screens.follow.FollowScreen
import com.cmoney.kolfanci.ui.screens.follow.viewmodel.FollowViewModel
import com.cmoney.kolfanci.ui.screens.group.setting.GroupSettingResult
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.EmptyResultRecipient
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import org.koin.androidx.compose.koinViewModel

/**
 * Main screen
 *
 * @param navigator
 * @param groupSettingResultResultRecipient 從社團設定頁收到的結果,
 * 如果有收到且[GroupSettingResult.leaveGroupId] 不為空則退出此社團,
 * 若是[GroupSettingResult.notificationEnabled] 為 true 需要檢查是否通知過顯示允許權限提醒
 */
@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
    navigator: DestinationsNavigator,
    groupSettingResultResultRecipient: ResultRecipient<GroupSettingScreenDestination, GroupSettingResult>,
    chatRoomViewModel: ChatRoomViewModel = koinViewModel(),
    followViewModel: FollowViewModel = koinViewModel()
) {
    val TAG = "MainScreen"
    val context = LocalContext.current
    val notificationViewModel = koinViewModel<NotificationViewModel>(
        viewModelStoreOwner = LocalContext.current as? ComponentActivity ?: checkNotNull(
            LocalViewModelStoreOwner.current
        )
    )
    val globalGroupViewModel = globalGroupViewModel()
    val activity = LocalContext.current.findActivity()
    val isLoading by globalGroupViewModel.loading.collectAsState()

    //我的社團清單
    val myGroupList by globalGroupViewModel.myGroupList.collectAsState()
    //目前選中社團
    val currentGroup by globalGroupViewModel.currentGroup.collectAsState()
    //邀請加入社團
    val inviteGroup by notificationViewModel.inviteGroup.collectAsState()

    //禁止進入頻道彈窗
    val channelAlertDialog = remember { mutableStateOf(false) }

    //目前不屬於此社團
    val showNotJoinAlert by notificationViewModel.showNotJoinAlert.collectAsState()

    //前往指定 訊息/文章...
    val pushDataWrapper by notificationViewModel.jumpToChannelDest.collectAsState()
    LaunchedEffect(pushDataWrapper) {
        if (Constant.canReadMessage()) {
            pushDataWrapper?.let { pushDataWrapper ->
                when (pushDataWrapper) {
                    //設定指定社團並前往指定訊息
                    is PushDataWrapper.ChannelMessage -> {
                        val group = pushDataWrapper.group
                        globalGroupViewModel.setCurrentGroup(group)

                        navigator.navigate(
                            ChannelScreenDestination(
                                channel = pushDataWrapper.channel,
                                jumpChatMessage = ChatMessage(
                                    id = pushDataWrapper.messageId
                                )
                            )
                        )
                    }

                    //設定指定社團並打開貼文
                    is PushDataWrapper.ChannelPost -> {
                        val group = pushDataWrapper.group
                        globalGroupViewModel.setCurrentGroup(group)

                        navigator.navigate(
                            PostInfoScreenDestination(
                                channel = pushDataWrapper.channel,
                                post = pushDataWrapper.bulletinboardMessage
                            )
                        )
                    }
                }
            }
        }
        else {
            //禁止進入該頻道,show dialog
            channelAlertDialog.value = true
        }
        notificationViewModel.finishJumpToChannelDest()
    }

    // channel權限檢查 結束
    val updatePermissionDone by chatRoomViewModel.updatePermissionDone.collectAsState()
    updatePermissionDone?.let { channel ->
        if (Constant.canReadMessage()) {
            //前往頻道
            navigator.navigate(
                ChannelScreenDestination(
                    channel = channel
                )
            )
        } else {
            //禁止進入該頻道,show dialog
            channelAlertDialog.value = true
        }

        notificationViewModel.finishJumpToChannelDest()
        chatRoomViewModel.afterUpdatePermissionDone()
    }

    FollowScreen(
        modifier = Modifier,
        group = currentGroup,
        inviteGroup = inviteGroup,
        navigator = navigator,
        myGroupList = myGroupList,
        onGroupItemClick = {
            globalGroupViewModel.setCurrentGroup(it)
        },
        onRefreshMyGroupList = { isSilent ->
            globalGroupViewModel.fetchMyGroup(isSilent = isSilent)
        },
        isLoading = isLoading,
        onDismissInvite = {
            notificationViewModel.openedInviteGroup()
        },
        onChannelClick = {
            chatRoomViewModel.fetchChannelPermission(it)
            activity.intent?.replaceExtras(Bundle())
        },
        onChangeGroup = {
            globalGroupViewModel.setCurrentGroup(it)
        }
    )

    groupSettingResultResultRecipient.onNavResult { navResult ->
        when (navResult) {
            NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val groupSettingResult = navResult.value
                if (groupSettingResult.leaveGroupId.isNotBlank()) {
                    context.showToast(context.getString(R.string.leaving_group))
                    val groupId = groupSettingResult.leaveGroupId
                    globalGroupViewModel.leaveGroup(id = groupId)
                    return@onNavResult
                }
                if (groupSettingResult.notificationEnabled) {
                    followViewModel.checkNeedNotifyAllowNotificationPermission()
                }
            }
        }
    }

    LaunchedEffect(key1 = currentGroup) {
        if (currentGroup != null) {
            AppUserLogger.getInstance()
                .log(page = Page.Home)
        }
    }

    if (channelAlertDialog.value) {
        DialogScreen(
            title = "不具有此頻道的權限",
            subTitle = "這是個上了鎖的頻道，你目前沒有權限能夠進入喔！",
            onDismiss = { channelAlertDialog.value = false }
        ) {
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = "返回",
                borderColor = LocalColor.current.text.default_50,
                textColor = LocalColor.current.text.default_100
            ) {
                run {
                    channelAlertDialog.value = false
                }
            }
        }
    }

    if (showNotJoinAlert) {
        DialogScreen(
            title = stringResource(id = R.string.not_belong_group),
            subTitle = stringResource(id = R.string.rejoin_group),
            onDismiss = { channelAlertDialog.value = false }
        ) {
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = stringResource(id = R.string.back),
                borderColor = LocalColor.current.text.default_50,
                textColor = LocalColor.current.text.default_100
            ) {
                notificationViewModel.dismissNotJoinAlert()
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FanciTheme {
        MainScreen(
            navigator = EmptyDestinationsNavigator,
            groupSettingResultResultRecipient = EmptyResultRecipient()
        )
    }
}