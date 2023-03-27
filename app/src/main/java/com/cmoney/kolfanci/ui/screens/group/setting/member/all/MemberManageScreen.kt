package com.cmoney.kolfanci.ui.screens.group.setting.member.all

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.BanPeriodOption
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.destinations.MemberRoleManageScreenDestination
import com.cmoney.kolfanci.extension.fromJsonTypeToken
import com.cmoney.kolfanci.extension.toDisplayDay
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.common.CircleImage
import com.cmoney.kolfanci.ui.common.HexStringMapRoleColor
import com.cmoney.kolfanci.ui.screens.group.setting.ban.viewmodel.BanUiModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.AlertDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogDefaultContentScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.item.BanDayItemScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.item.DisBanItemScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.item.KickOutItemScreen
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.MemberViewModel
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.google.gson.Gson
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel

@Parcelize
data class MemberManageResult(
    val groupMember: GroupMember,
    val type: Type
) : Parcelable {
    enum class Type {
        Update,
        Delete
    }
}

@Destination
@Composable
fun MemberManageScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    groupMember: GroupMember,
    viewModel: MemberViewModel = koinViewModel(),
    setRoleResult: ResultRecipient<MemberRoleManageScreenDestination, String>,
    resultNavigator: ResultBackNavigator<MemberManageResult>
) {
    val uiState = viewModel.uiState

    val showBanDialog = remember { mutableStateOf(false) }

    val showDisBanDialog = remember { mutableStateOf(false) }

    val showKickOutDialog = remember { mutableStateOf(false) }

    val member = remember {
        mutableStateOf(groupMember)
    }

    //再次確認禁言 彈窗
    val showBanDoubleConfirmDialog: MutableState<BanPeriodOption?> =
        remember { mutableStateOf(null) }

    //再次確認 解除禁言 彈窗
    val showDisBanDoubleConfirmDialog = remember {
        mutableStateOf(false)
    }

    //再次確認 踢出社團 彈窗
    val showKickOutDoubleConfirmDialog = remember {
        mutableStateOf(false)
    }

    //Add role callback
    setRoleResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                val roleListStr = result.value
                val gson = Gson()
                val resultRoleList = gson.fromJsonTypeToken<List<FanciRole>>(roleListStr)
                member.value = member.value.copy(
                    roleInfos = resultRoleList
                )
            }
        }
    }

    viewModel.fetchUserBanInfo(
        groupId = group.id.orEmpty(),
        userId = groupMember.id.orEmpty()
    )

    MemberManageScreenView(
        modifier = modifier,
        navController = navController,
        group = group,
        groupMember = member.value,
        banInfo = uiState.banUiModel,
        onBack = {
            resultNavigator.navigateBack(
                MemberManageResult(
                    member.value,
                    MemberManageResult.Type.Update
                )
            )
        },
        onBanClick = {
            showBanDialog.value = true
        },
        onKickClick = {
            showKickOutDialog.value = true
        },
        onDisBanClick = {
            showDisBanDialog.value = true
        }
    )

    BackHandler {
        resultNavigator.navigateBack(
            MemberManageResult(
                member.value,
                MemberManageResult.Type.Update
            )
        )
    }

    if (uiState.kickMember != null) {
        resultNavigator.navigateBack(
            MemberManageResult(
                member.value,
                MemberManageResult.Type.Delete
            )
        )
    }

    //==================== Dialog ====================

    //禁言 彈窗
    if (showBanDialog.value) {
        AlertDialogScreen(
            onDismiss = {
                showBanDialog.value = false
            },
            title = "禁言 " + groupMember.name,
        ) {
            BanDayItemScreen(
                name = groupMember.name.orEmpty(),
                onClick = {
                    showBanDialog.value = false
                    showBanDoubleConfirmDialog.value = it
                },
                onDismiss = {
                    showBanDialog.value = false
                }
            )
        }
    }

    //再次確認 禁言彈窗
    showBanDoubleConfirmDialog.value?.let {
        AlertDialogScreen(
            onDismiss = {
                showBanDoubleConfirmDialog.value = null
            },
            title = "確定禁言 %s %s".format(groupMember.name.orEmpty(), it.toDisplayDay())
        ) {
            DialogDefaultContentScreen(
                content = "一旦被禁言，將會無法對頻道做出任何社群行為：留言、按讚等等。",
                confirmTitle = "確定禁言",
                cancelTitle = "返回",
                onConfirm = {
                    viewModel.banUser(
                        groupId = group.id.orEmpty(),
                        userId = groupMember.id.orEmpty(),
                        banPeriodOption = it
                    )
                },
                onCancel = {
                    showBanDoubleConfirmDialog.value = null
                }
            )
        }
    }

    //解除禁言 彈窗
    if (showDisBanDialog.value) {
        AlertDialogScreen(
            onDismiss = {
                showDisBanDialog.value = false
            },
            title = groupMember.name + " 禁言中",
        ) {
            DisBanItemScreen(
                onConfirm = {
                    showDisBanDialog.value = false
                    showDisBanDoubleConfirmDialog.value = true
                },
                onDismiss = {
                    showDisBanDialog.value = false
                }
            )
        }
    }

    //再次確認 解除禁言 彈窗
    if (showDisBanDoubleConfirmDialog.value) {
        AlertDialogScreen(
            onDismiss = {
                showDisBanDoubleConfirmDialog.value = false
            },
            title = "解除 %s 禁言".format(groupMember.name.orEmpty())
        ) {
            DialogDefaultContentScreen(
                content = "你確定要將 %s 解除禁言嗎？".format(groupMember.name.orEmpty()),
                confirmTitle = "確定解除 放他自由",
                cancelTitle = "返回",
                onConfirm = {
                    viewModel.liftBanUser(
                        groupId = group.id.orEmpty(),
                        userId = groupMember.id.orEmpty()
                    )
                },
                onCancel = {
                    showDisBanDoubleConfirmDialog.value = false
                }
            )
        }
    }

    //踢出社團 彈窗
    if (showKickOutDialog.value) {
        AlertDialogScreen(
            onDismiss = {
                showKickOutDialog.value = false
            },
            title = "將 " + groupMember.name + " 踢出社團",
        ) {
            DialogDefaultContentScreen(
                content = "你確定要將 %s 踢出社團嗎？\n".format(groupMember.name) +
                        "一旦踢出他下次要進入，需要重新申請",
                confirmTitle = "確定",
                cancelTitle = "返回",
                onConfirm = {
                    showKickOutDialog.value = false
                    showKickOutDoubleConfirmDialog.value = true
                },
                onCancel = {
                    showKickOutDialog.value = false
                }
            )
        }
    }

    //再次確認 踢出社團 彈窗
    if (showKickOutDoubleConfirmDialog.value) {
        AlertDialogScreen(
            onDismiss = {
                showKickOutDoubleConfirmDialog.value = false
            },
            title = "確定要將 " + groupMember.name + " 踢出社團",
        ) {
            DialogDefaultContentScreen(
                content = "你確定要將 %s 踢出社團嗎？\n".format(groupMember.name) +
                        "一旦踢出他下次要進入，需要重新申請",
                confirmTitle = "確定踢出",
                cancelTitle = "返回",
                onConfirm = {
                    showKickOutDoubleConfirmDialog.value = true
                    viewModel.kickOutMember(
                        groupId = group.id.orEmpty(),
                        groupMember
                    )
                },
                onCancel = {
                    showKickOutDoubleConfirmDialog.value = false
                }
            )
        }
    }
}

@Composable
private fun MemberManageScreenView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    groupMember: GroupMember,
    banInfo: BanUiModel?,
    onBack: () -> Unit,
    onBanClick: () -> Unit,
    onKickClick: () -> Unit,
    onDisBanClick: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "管理 " + groupMember.name,
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    onBack.invoke()
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            //大頭貼, 名稱
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .background(LocalColor.current.background)
                    .padding(start = 30.dp, end = 24.dp, top = 9.dp, bottom = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleImage(
                    modifier = Modifier
                        .size(34.dp),
                    imageUrl = groupMember.thumbNail.orEmpty()
                )

                Spacer(modifier = Modifier.width(15.dp))

                Column {
                    //名字
                    Text(
                        text = groupMember.name.orEmpty(),
                        fontSize = 16.sp,
                        color = LocalColor.current.text.default_100
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    //代號
                    Text(
                        text = groupMember.serialNumber.toString(),
                        fontSize = 12.sp,
                        color = LocalColor.current.text.default_50
                    )
                }
            }

            //身份組
            Text(
                modifier = Modifier.padding(top = 20.dp, start = 24.dp, bottom = 10.dp),
                text = "身份組", fontSize = 14.sp, color = LocalColor.current.text.default_80
            )

            //身份清單
            groupMember.roleInfos?.let {
                repeat(it.size) { index ->
                    val roleInfo = it[index]
                    val roleColor = if (roleInfo.color?.isNotEmpty() == true) {
                        HexStringMapRoleColor(roleInfo.color.orEmpty())
                    } else {
                        LocalColor.current.specialColor.red
                    }
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .background(LocalColor.current.background)
                            .padding(start = 25.dp, end = 25.dp, top = 14.dp, bottom = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier.size(25.dp),
                            contentScale = ContentScale.FillBounds,
                            painter = painterResource(id = R.drawable.rule_manage),
                            colorFilter = ColorFilter.tint(color = roleColor),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(text = roleInfo.name.orEmpty(), fontSize = 16.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(1.dp))
                }
            }

            //編輯角色 按鈕
            BorderButton(
                modifier = Modifier
                    .padding(
                        top = 20.dp,
                        start = 24.dp,
                        end = 24.dp
                    )
                    .fillMaxWidth()
                    .height(45.dp),
                text = "編輯角色",
                borderColor = LocalColor.current.text.default_50,
                textColor = LocalColor.current.text.default_100,
                onClick = {
                    navController.navigate(
                        MemberRoleManageScreenDestination(
                            group = group,
                            groupMember = groupMember
                        )
                    )
                }
            )

            //權限管理
            Text(
                modifier = Modifier.padding(top = 40.dp, start = 24.dp, bottom = 10.dp),
                text = "權限管理", fontSize = 14.sp, color = LocalColor.current.text.default_80
            )

            //禁言
            if (banInfo == null) {
                BanItem(
                    banTitle = "禁言「%s」".format(groupMember.name),
                    desc = "讓 %s 無法繼續在社團中發表言論".format(groupMember.name)
                ) {
                    onBanClick.invoke()
                }
            } else {
                BanInfo(
                    banTitle = "「%s」正在禁言中".format(banInfo.user?.name.orEmpty()),
                    banStartDay = banInfo.startDay,
                    banDuration = banInfo.duration,
                    onClick = {
                        onDisBanClick.invoke()
                    }
                )
            }

            Spacer(modifier = Modifier.height(1.dp))

            //踢出社團
            BanItem(
                banTitle = "將「%s」踢出社團".format(groupMember.name),
                desc = "讓 %s 離開社團".format(groupMember.name)
            ) {
                onKickClick.invoke()
            }
        }

    }
}

@Composable
private fun BanItem(banTitle: String, desc: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalColor.current.background)
            .clickable {
                onClick.invoke()
            }
            .padding(start = 30.dp, end = 24.dp, top = 9.dp, bottom = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = banTitle, fontSize = 16.sp, color = LocalColor.current.specialColor.red)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = desc, fontSize = 12.sp, color = LocalColor.current.text.default_80)
        }
    }
}

@Composable
private fun BanInfo(
    banTitle: String,
    banStartDay: String,
    banDuration: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalColor.current.background)
            .clickable {
                onClick.invoke()
            }
            .padding(start = 30.dp, end = 24.dp, top = 9.dp, bottom = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = banTitle, fontSize = 16.sp, color = LocalColor.current.text.default_100)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "被禁言日：%s".format(banStartDay),
                fontSize = 12.sp,
                color = LocalColor.current.text.default_80
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "禁言時長：%s".format(banDuration),
                fontSize = 12.sp,
                color = LocalColor.current.text.default_80
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Text(
            text = "調整", fontSize = 16.sp, color = LocalColor.current.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MemberManageScreenPreview() {
    FanciTheme {
        MemberManageScreenView(
            navController = EmptyDestinationsNavigator,
            group = Group(),
            groupMember = GroupMember(
                thumbNail = "",
                name = "Hello",
                serialNumber = 1234,
                roleInfos = listOf(
                    FanciRole(
                        name = "Hi"
                    )
                )
            ),
            onBack = {},
            onBanClick = {},
            onKickClick = {},
            onDisBanClick = {},
            banInfo = BanUiModel(
                user = null,
                startDay = "2020/10/22",
                duration = "3日"
            )
        )
    }
}