package com.cmoney.kolfanci.ui.screens.group.setting.member.all

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.fromJsonTypeToken
import com.cmoney.kolfanci.extension.isVip
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.usecase.VipManagerUseCase
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.common.HexStringMapRoleColor
import com.cmoney.kolfanci.ui.destinations.ShareAddRoleScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.ban.viewmodel.BanUiModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel
import com.cmoney.kolfanci.ui.screens.group.setting.vip.viewmodel.VipManagerViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.BanDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DisBanDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.KickOutDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.member.MemberItemScreen
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.MemberViewModel
import com.cmoney.kolfanci.ui.screens.shared.vip.VipPlanItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.google.gson.Gson
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

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
    vipManagerViewModel: VipManagerViewModel = koinViewModel {
        parametersOf(group)
    },
    setRoleResult: ResultRecipient<ShareAddRoleScreenDestination, String>,
    resultNavigator: ResultBackNavigator<MemberManageResult>
) {
    val TAG = "MemberManageScreen"

    val uiState = viewModel.uiState

    val showBanDialog = remember { mutableStateOf(false) }

    val showDisBanDialog = remember { mutableStateOf(false) }

    val showKickOutDialog = remember { mutableStateOf(false) }

    //目前會員
    val member by viewModel.managerMember.collectAsState()

    //目前購買方案
    val purchasesPlan by vipManagerViewModel.alreadyPurchasePlan.collectAsState()

    //禁言資訊
    val banInfo by viewModel.banUiModel.collectAsState()

    //Add role callback
    setRoleResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val roleListStr = result.value
                val gson = Gson()
                val resultRoleList = gson.fromJsonTypeToken<List<FanciRole>>(roleListStr)
                viewModel.onAddRole(
                    groupId = group.id.orEmpty(),
                    userId = groupMember.id.orEmpty(),
                    addRole = resultRoleList
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchUserBanInfo(
            groupId = group.id.orEmpty(),
            userId = groupMember.id.orEmpty()
        )

        vipManagerViewModel.fetchAlreadyPurchasePlan(groupMember)

        if (member == null) {
            viewModel.setManageGroupMember(groupMember)
        }

    }

    member?.apply {
        MemberManageScreenView(
            modifier = modifier,
            navController = navController,
            group = group,
            groupMember = this,
            banInfo = banInfo,
            purchasesPlan = purchasesPlan,
            onBack = {
                resultNavigator.navigateBack(
                    MemberManageResult(
                        this,
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
            },
            onRemoveRole = { role ->
                KLog.i(TAG, "onRemoveRole:$role")
                viewModel.onRemoveRole(
                    groupId = group.id.orEmpty(),
                    userId = groupMember.id.orEmpty(),
                    fanciRole = role
                )
            }
        )

        BackHandler {
            resultNavigator.navigateBack(
                MemberManageResult(
                    this,
                    MemberManageResult.Type.Update
                )
            )
        }

        if (uiState.kickMember != null) {
            resultNavigator.navigateBack(
                MemberManageResult(
                    this,
                    MemberManageResult.Type.Delete
                )
            )
        }
    }

    //==================== Dialog ====================
    //禁言 彈窗
    if (showBanDialog.value) {
        BanDialogScreen(
            name = groupMember.name.orEmpty(),
            isVip = groupMember.isVip(),
            onDismiss = {
                showBanDialog.value = false
            },
            onConfirm = {
                showBanDialog.value = false
                viewModel.banUser(
                    groupId = group.id.orEmpty(),
                    userId = groupMember.id.orEmpty(),
                    banPeriodOption = it
                )
            }
        )
    }

    //解除禁言 彈窗
    if (showDisBanDialog.value) {
        DisBanDialogScreen(
            name = groupMember.name.orEmpty(),
            onDismiss = {
                showDisBanDialog.value = false
            },
            onConfirm = {
                showDisBanDialog.value = false
                viewModel.liftBanUser(
                    groupId = group.id.orEmpty(),
                    userId = groupMember.id.orEmpty()
                )
            }
        )
    }

    //踢出社團 彈窗
    if (showKickOutDialog.value) {
        KickOutDialogScreen(
            name = groupMember.name.orEmpty(),
            isVip = groupMember.isVip(),
            onDismiss = {
                showKickOutDialog.value = false
            },
            onConfirm = {
                showKickOutDialog.value = false
                viewModel.kickOutMember(
                    groupId = group.id.orEmpty(),
                    groupMember
                )
            }
        )
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
    onDisBanClick: () -> Unit,
    purchasesPlan: List<VipPlanModel>,
    onRemoveRole: (FanciRole) -> Unit
) {
    val TAG = "MemberManageScreenView"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "管理 " + groupMember.name,
                backClick = {
                    onBack.invoke()
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            //會員資訊
            item {
                MemberItemScreen(groupMember = groupMember, isShowRemove = false)
            }

            //購買的VIP方案
            item {
                if (purchasesPlan.isNotEmpty()) {
                    Text(
                        modifier = Modifier.padding(top = 20.dp, start = 24.dp, bottom = 10.dp),
                        text = stringResource(id = R.string.already_purchases_plan),
                        fontSize = 14.sp,
                        color = LocalColor.current.text.default_80
                    )
                }
            }

            items(purchasesPlan) { plan ->
                VipPlanItemScreen(
                    modifier = Modifier.fillMaxWidth(),
                    vipPlanModel = plan,
                    subTitle = plan.description,
                    endContent = null
                )
                Spacer(modifier = Modifier.height(1.dp))
            }

            //身份組
            item {
                if (groupMember.roleInfos?.isNotEmpty() == true) {
                    Text(
                        modifier = Modifier.padding(top = 20.dp, start = 24.dp, bottom = 10.dp),
                        text = stringResource(id = R.string.exists_role),
                        fontSize = 14.sp,
                        color = LocalColor.current.text.default_80
                    )
                }
            }

            //身份清單
            groupMember.roleInfos?.let {
                items(it) { roleInfo ->
                    val roleColor = if (roleInfo.color?.isNotEmpty() == true) {
                        HexStringMapRoleColor(roleInfo.color.orEmpty())
                    } else {
                        LocalColor.current.specialColor.red
                    }
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .background(LocalColor.current.background)
                            .clickable(enabled = Constant.isCanEditRole()) {
                                AppUserLogger.getInstance()
                                    .log(Clicked.ManageMembersRemove)
                                onRemoveRole.invoke(roleInfo)
                            }
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

                        Text(
                            text = roleInfo.name.orEmpty(),
                            fontSize = 16.sp,
                            color = LocalColor.current.text.default_100
                        )

                        if (Constant.isCanEditRole()) {
                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = stringResource(id = R.string.remove),
                                fontSize = 14.sp,
                                color = LocalColor.current.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(1.dp))
                }
            }

            //新增角色 按鈕
            if (Constant.isCanEditRole()) {
                item {
                    BorderButton(
                        modifier = Modifier
                            .padding(
                                top = 20.dp,
                                start = 24.dp,
                                end = 24.dp
                            )
                            .fillMaxWidth()
                            .height(45.dp),
                        text = stringResource(id = R.string.add_role),
                        borderColor = LocalColor.current.text.default_50,
                        textColor = LocalColor.current.text.default_100,
                        onClick = {
                            KLog.i(TAG, "onAddRole click.")
                            AppUserLogger.getInstance()
                                .log(Clicked.ManageMembersAddMember)
                            groupMember.roleInfos?.let {
                                navController.navigate(
                                    ShareAddRoleScreenDestination(
                                        group = group,
                                        existsRole = it.toTypedArray(),
                                        from = From.AddRole
                                    )
                                )
                            }
                        }
                    )
                }
            }

            //權限管理
            if (Constant.isCanBanKickMember()) {
                item {
                    Text(
                        modifier = Modifier.padding(top = 40.dp, start = 24.dp, bottom = 10.dp),
                        text = stringResource(id = R.string.rule_manage),
                        fontSize = 14.sp,
                        color = LocalColor.current.text.default_80
                    )
                }

                //禁言
                item {
                    if (banInfo == null) {
                        BanItem(
                            banTitle = stringResource(id = R.string.silence_who).format(groupMember.name),
                            desc = stringResource(id = R.string.silence_who_desc).format(groupMember.name)
                        ) {
                            AppUserLogger.getInstance()
                                .log(Clicked.ManageMembersMute)
                            onBanClick.invoke()
                        }
                    } else {
                        BanInfo(
                            banTitle = stringResource(id = R.string.silence_ing).format(banInfo.user?.name.orEmpty()),
                            banStartDay = banInfo.startDay,
                            banDuration = banInfo.duration,
                            onClick = {
                                onDisBanClick.invoke()
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(1.dp))
                }

                //踢出社團
                item {
                    BanItem(
                        banTitle = stringResource(id = R.string.remove_group_who).format(groupMember.name),
                        desc = stringResource(id = R.string.remove_group_who_desc).format(
                            groupMember.name
                        )
                    ) {
                        AppUserLogger.getInstance()
                            .log(Clicked.ManageMembersKickOut)
                        onKickClick.invoke()
                    }
                }
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
                text = stringResource(R.string.ban_start_at, banStartDay),
                fontSize = 12.sp,
                color = LocalColor.current.text.default_80
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.ban_duration, banDuration),
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
            banInfo = BanUiModel(
                user = null,
                startDay = "2020/10/22",
                duration = "3日"
            ),
            onBack = {},
            onBanClick = {},
            onKickClick = {},
            onDisBanClick = {},
            purchasesPlan = VipManagerUseCase.getVipPlanMockData(),
            onRemoveRole = {}
        )
    }
}