package com.cmoney.fanci.ui.screens.group.setting.member.all

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
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
import com.cmoney.fanci.R
import com.cmoney.fanci.destinations.MemberRoleManageScreenDestination
import com.cmoney.fanci.extension.fromJsonTypeToken
import com.cmoney.fanci.ui.common.BorderButton
import com.cmoney.fanci.ui.common.CircleImage
import com.cmoney.fanci.ui.common.HexStringMapRoleColor
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.screens.shared.dialog.AlertDialogScreen
import com.cmoney.fanci.ui.screens.shared.dialog.item.BanDayItemScreen
import com.cmoney.fanci.ui.screens.shared.dialog.item.DisBanItemScreen
import com.cmoney.fanci.ui.screens.shared.dialog.item.KickOutItemScreen
import com.cmoney.fanci.ui.screens.shared.member.viewmodel.MemberViewModel
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
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

    MemberManageScreenView(
        modifier = modifier,
        navController = navController,
        group = group,
        groupMember = member.value,
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
        }
    )

    BackHandler {
        MemberManageResult(
            member.value,
            MemberManageResult.Type.Update
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
                    viewModel.banUser(
                        groupId = group.id.orEmpty(),
                        userId = groupMember.id.orEmpty(),
                        banPeriodOption = it
                    )
                },
                onDismiss = {
                    showBanDialog.value = false
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
                    //TODO api
                },
                onDismiss = {
                    showDisBanDialog.value = false
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
            KickOutItemScreen(
                name = groupMember.name.orEmpty(),
                onConfirm = {
                    viewModel.kickOutMember(
                        groupId = group.id.orEmpty(),
                        groupMember
                    )
                    showKickOutDialog.value = false
                },
                onDismiss = {
                    showKickOutDialog.value = false
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
    onBack: () -> Unit,
    onBanClick: () -> Unit,
    onKickClick: () -> Unit
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
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
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
            BanItem(
                banTitle = "禁言「%s」".format(groupMember.name),
                desc = "讓 %s 無法繼續在社團中發表言論".format(groupMember.name)
            ) {
                onBanClick.invoke()
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
            onKickClick = {}
        )
    }
}