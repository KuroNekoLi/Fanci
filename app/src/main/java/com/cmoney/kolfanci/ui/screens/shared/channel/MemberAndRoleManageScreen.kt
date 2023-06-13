package com.cmoney.kolfanci.ui.screens.shared.channel

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.usecase.VipManagerUseCase
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.AddMemberScreenDestination
import com.cmoney.kolfanci.ui.destinations.AddVipPlanScreenDestination
import com.cmoney.kolfanci.ui.destinations.ShareAddRoleScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.vip.model.VipPlanModel
import com.cmoney.kolfanci.ui.screens.shared.TabScreen
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.member.MemberItemScreen
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.MemberViewModel
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.SelectedModel
import com.cmoney.kolfanci.ui.screens.shared.role.RoleItemScreen
import com.cmoney.kolfanci.ui.screens.shared.vip.VipPlanItemScreen
import com.cmoney.kolfanci.ui.theme.Color_80FFFFFF
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

/**
 * 成員 and 角色 挑選畫面
 */
@Destination
@Composable
fun MemberAndRoleManageScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    topBarTitle: String,
    selectedModel: SelectedModel,
    viewModel: MemberViewModel = koinViewModel(),
    addMemberResult: ResultRecipient<AddMemberScreenDestination, String>,
    addRoleResult: ResultRecipient<ShareAddRoleScreenDestination, String>,
    addVipPlanResult: ResultRecipient<AddVipPlanScreenDestination, VipPlanModel>,
    selectedCallback: ResultBackNavigator<SelectedModel>
) {
    val uiState = viewModel.uiState

    //init default data
    LaunchedEffect(Unit) {
        viewModel.initialUiStateFromModel(selectedModel = selectedModel)
    }

    //Add Member Callback
    addMemberResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val member = result.value
                viewModel.addSelectedMember(member)
            }
        }
    }

    //Add Role Callback
    addRoleResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                viewModel.addSelectedRole(result.value)
            }
        }
    }

    // add vip plan callback
    addVipPlanResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                viewModel.addSelectedVipPlanModel(model = result.value)
            }
        }
    }

    val selectedMember by viewModel.selectedMember.collectAsState()

    KLog.i("TAG", "selectedMember:$selectedMember")

    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current
        ?.onBackPressedDispatcher
    MemberAndRoleManageScreenView(
        modifier = modifier,
        navigator = navigator,
        group = group,
        topBarTitle = topBarTitle,
        selectedIndex = uiState.tabIndex,
        selectedMember = selectedMember,
        selectedRole = uiState.selectedRole,
        selectedVipPlanModels = uiState.selectedVipPlanModels,
        onTabClick = {
            viewModel.onTabClick(it)
        },
        onRemoveClick = {
            viewModel.removeSelectedMember(it)
        },
        onRoleRemoveClick = {
            viewModel.removeSelectedRole(it)
        },
        onVipPlanRemoveClick = {
            viewModel.removeSelectedVipPlan(it)
        },
        onBackClick = {
            onBackPressedDispatcher?.onBackPressed()
        }
    )

    BackHandler {
        val newSelectedModel = viewModel.fetchSelected()
        selectedCallback.navigateBack(result = newSelectedModel)
    }
}

@Composable
private fun MemberAndRoleManageScreenView(
    modifier: Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    topBarTitle: String,
    selectedIndex: Int,
    selectedMember: List<GroupMember>,
    selectedRole: List<FanciRole>,
    selectedVipPlanModels: List<VipPlanModel>,
    onTabClick: (Int) -> Unit,
    onRemoveClick: (GroupMember) -> Unit,
    onRoleRemoveClick: (FanciRole) -> Unit,
    onVipPlanRemoveClick: (VipPlanModel) -> Unit,
    onBackClick: () -> Unit
) {
    val list = listOf("成員", "角色", stringResource(id = R.string.vip_plan))

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = topBarTitle,
                leadingEnable = true,
                moreEnable = false,
                backClick = onBackClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(LocalColor.current.env_80)
        ) {

            Column(modifier = Modifier.weight(1f)) {
                TabScreen(
                    modifier = Modifier
                        .padding(18.dp)
                        .height(40.dp),
                    selectedIndex = selectedIndex,
                    listItem = list,
                    onTabClick = {
                        onTabClick.invoke(it)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                when (selectedIndex) {
                    //成員
                    0 -> {
                        AddMemberListScreen(
                            navigator = navigator,
                            group = group,
                            member = selectedMember,
                            title = topBarTitle,
                            onRemoveClick = onRemoveClick
                        )
                    }
                    //角色
                    1 -> {
                        AddRoleListScreen(
                            navigator = navigator,
                            group = group,
                            roles = selectedRole,
                            title = topBarTitle,
                            onRemoveClick = onRoleRemoveClick
                        )
                    }
                    // VIP方案
                    2 -> {
                        AddVipPlanScreen(
                            navigator = navigator,
                            title = topBarTitle,
                            vipPlanModels = selectedVipPlanModels,
                            onVipPlanRemoveClick = onVipPlanRemoveClick
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

/**
 * 新增成員 UI
 */
@Composable
private fun AddMemberListScreen(
    navigator: DestinationsNavigator,
    group: Group,
    member: List<GroupMember> = emptyList(),
    title: String,
    onRemoveClick: (GroupMember) -> Unit
) {
    val TAG = "AddMemberListScreen"

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .background(LocalColor.current.background)
                    .padding(top = 12.dp, bottom = 8.dp, start = 24.dp, end = 24.dp)
            ) {
                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    text = "新增成員",
                    textColor = Color.White,
                    borderColor = Color_80FFFFFF
                ) {
                    KLog.i(TAG, "BorderButton click.")

                    navigator.navigate(
                        AddMemberScreenDestination(
                            group = group,
                            excludeMember = member.toTypedArray(),
                            title = "新增「%s」成員".format(title)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "下列成員可以獲得「%s」並且在頻道中執行對應的權限資格。".format(title),
                    fontSize = 14.sp,
                    color = LocalColor.current.text.default_50
                )

            }
        }

        //Member block
        items(member) {
            MemberItemScreen(
                groupMember = it
            ) { groupMember ->
                onRemoveClick.invoke(groupMember)
            }
        }
    }
}

/**
 * 新增角色 UI
 */
@Composable
private fun AddRoleListScreen(
    navigator: DestinationsNavigator,
    group: Group,
    roles: List<FanciRole> = emptyList(),
    title: String,
    onRemoveClick: (FanciRole) -> Unit
) {
    val TAG = "AddRoleListScreen"
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .background(LocalColor.current.background)
                    .padding(top = 12.dp, bottom = 8.dp, start = 24.dp, end = 24.dp)
            ) {
                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    text = "新增角色",
                    textColor = Color.White,
                    borderColor = Color_80FFFFFF
                ) {
                    KLog.i(TAG, "BorderButton click.")

                    navigator.navigate(
                        ShareAddRoleScreenDestination(
                            group = group,
                            title = "新增「%s」角色".format(title),
                            subTitle = "直接指定角色，讓一批成員進入私密頻道。",
                            existsRole = roles.toTypedArray()
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "獲得下列角色的成員，可以獲得「%s」並且在頻道中執行對應的權限資格。".format(title),
                    fontSize = 14.sp,
                    color = LocalColor.current.text.default_50
                )

            }
        }

        //Role block
        itemsIndexed(roles) { index, role ->
            RoleItemScreen(
                index = index,
                fanciRole = role,
                editText = "移除",
                onEditClick = {
                    onRemoveClick.invoke(it)
                }
            )
        }
    }
}

@Composable
private fun AddVipPlanScreen(
    navigator: DestinationsNavigator,
    title: String,
    vipPlanModels: List<VipPlanModel>,
    onVipPlanRemoveClick: (VipPlanModel) -> Unit
) {
    val TAG = "AddVipPlanScreen"

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .background(LocalColor.current.background)
                    .padding(top = 12.dp, bottom = 8.dp, start = 24.dp, end = 24.dp)
            ) {
                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    text = stringResource(id = R.string.add_plan),
                    textColor = LocalColor.current.text.default_100,
                    borderColor = LocalColor.current.text.default_50
                ) {
                    KLog.i(TAG, "BorderButton click.")
                    navigator.navigate(
                        AddVipPlanScreenDestination(
                            authTitle = title,
                            selectedVipPlanModels = vipPlanModels.toTypedArray()
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(R.string.vip_plan_in_x_channel_permission_description, title),
                    fontSize = 14.sp,
                    color = LocalColor.current.text.default_50
                )
            }
        }

        //Role block
        items(vipPlanModels) { plan ->
            VipPlanItemScreen(
                modifier = Modifier.fillMaxWidth(),
                vipPlanModel = plan,
                endText = stringResource(id = R.string.remove),
                subTitle = stringResource(id = R.string.n_member).format(plan.memberCount),
                onPlanClick = {
                    onVipPlanRemoveClick(plan)
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MemberAndRoleManageScreenPreview() {
    FanciTheme {
        MemberAndRoleManageScreenView(
            modifier = Modifier,
            navigator = EmptyDestinationsNavigator,
            group = Group(),
            topBarTitle = "基本權限",
            selectedIndex = 0,
            selectedMember = emptyList(),
            selectedRole = listOf(
                FanciRole(
                    name = "角色2",
                    color = "FF38B035",
                    userCount = 9
                )
            ),
            selectedVipPlanModels = VipManagerUseCase.getVipPlanMockData(),
            onTabClick = {},
            onRemoveClick = {},
            onRoleRemoveClick = {},
            onVipPlanRemoveClick = {},
            onBackClick = {}
        )
    }
}