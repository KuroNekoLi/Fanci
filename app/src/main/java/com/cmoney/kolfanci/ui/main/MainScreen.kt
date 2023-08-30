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
import com.cmoney.kolfanci.model.viewmodel.PushDataWrapper
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.ChannelScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupSettingScreenDestination
import com.cmoney.kolfanci.ui.destinations.PostInfoScreenDestination
import com.cmoney.kolfanci.ui.screens.chat.viewmodel.ChatRoomViewModel
import com.cmoney.kolfanci.ui.screens.follow.FollowScreen
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
 * @param leaveResultRecipient 如果有收到則退出此社團
 */
@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
    navigator: DestinationsNavigator,
    leaveResultRecipient: ResultRecipient<GroupSettingScreenDestination, String>,
    chatRoomViewModel: ChatRoomViewModel = koinViewModel()
) {
    val TAG = "MainScreen"
    val context = LocalContext.current
    val globalViewModel = koinViewModel<MainViewModel>(
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
    //server 入門社團清單
    val serverGroupList by globalGroupViewModel.groupList.collectAsState()
    //邀請加入社團
    val inviteGroup by globalViewModel.inviteGroup.collectAsState()

    //禁止進入頻道彈窗
    val channelAlertDialog = remember { mutableStateOf(false) }

    //前往指定 訊息/文章...
    val pushDataWrapper by globalGroupViewModel.jumpToChannelDest.collectAsState()
    LaunchedEffect(pushDataWrapper) {
        /**
         * 處理推播資料
         * 進入頻道前, 權限檢查
         */
        pushDataWrapper?.let { pushDataWrapper ->
            when (pushDataWrapper) {
                is PushDataWrapper.ChannelMessage -> {
                    chatRoomViewModel.fetchChannelPermission(pushDataWrapper.channel)
                }

                is PushDataWrapper.ChannelPost -> {
                    chatRoomViewModel.fetchChannelPermission(pushDataWrapper.channel)
                }
            }
        }
    }

    // channel權限檢查 結束
    val updatePermissionDone by chatRoomViewModel.updatePermissionDone.collectAsState()
    updatePermissionDone?.let { channel ->
        if (Constant.canReadMessage()) {
            //是否有推播
            pushDataWrapper?.let { pushDataWrapper ->
                when (pushDataWrapper) {
                    //前往指定訊息
                    is PushDataWrapper.ChannelMessage -> {
                        navigator.navigate(
                            ChannelScreenDestination(
                                channel = channel,
                                jumpChatMessage = ChatMessage(
                                    serialNumber =
                                    pushDataWrapper.serialNumber.toLong()
                                )
                            )
                        )
                    }

                    //打開貼文
                    is PushDataWrapper.ChannelPost -> {
                        navigator.navigate(
                            PostInfoScreenDestination(
                                channel = channel,
                                post = pushDataWrapper.bulletinboardMessage
                            )
                        )
                    }
                }
            } ?: run {
                //前往頻道
                navigator.navigate(
                    ChannelScreenDestination(
                        channel = channel
                    )
                )
            }
        } else {
            //禁止進入該頻道,show dialog
            channelAlertDialog.value = true
        }

        globalGroupViewModel.finishJumpToChannelDest()
        chatRoomViewModel.afterUpdatePermissionDone()
    }

    FollowScreen(
        modifier = Modifier,
        group = currentGroup,
        serverGroupList = serverGroupList,
        inviteGroup = inviteGroup,
        navigator = navigator,
        myGroupList = myGroupList,
        onGroupItemClick = {
            globalGroupViewModel.setCurrentGroup(it)
        },
        onLoadMoreServerGroup = {
            globalGroupViewModel.onLoadMore()
        },
        onRefreshMyGroupList = { isSilent ->
            globalGroupViewModel.fetchMyGroup(isSilent = isSilent)
        },
        isLoading = isLoading,
        onDismissInvite = {
            globalViewModel.openedInviteGroup()
        },
        onChannelClick = {
            chatRoomViewModel.fetchChannelPermission(it)
            activity.intent?.replaceExtras(Bundle())
        },
        onChangeGroup = {
            globalGroupViewModel.setCurrentGroup(it)
        }
    )

    leaveResultRecipient.onNavResult { navResult ->
        when (navResult) {
            NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                context.showToast(context.getString(R.string.leaving_group))
                val groupId = navResult.value
                globalGroupViewModel.leaveGroup(id = groupId)
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
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FanciTheme {
        MainScreen(
            navigator = EmptyDestinationsNavigator,
            leaveResultRecipient = EmptyResultRecipient()
        )
    }
}