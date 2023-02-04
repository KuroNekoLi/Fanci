package com.cmoney.kolfanci.ui.screens.group.setting.member.role.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.LocalDependencyContainer
import com.cmoney.kolfanci.MainActivity
import com.cmoney.kolfanci.destinations.AddMemberScreenDestination
import com.cmoney.kolfanci.extension.showToast
import com.cmoney.kolfanci.ui.screens.group.setting.member.role.viewmodel.FanciRoleCallback
import com.cmoney.kolfanci.ui.screens.group.setting.member.role.viewmodel.RoleManageViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.setting.BottomButtonScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun AddRoleScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    fanciRole: FanciRole? = null,
    viewModel: RoleManageViewModel = koinViewModel(),
    memberResult: ResultRecipient<AddMemberScreenDestination, String>,
    resultNavigator: ResultBackNavigator<FanciRoleCallback>
) {
    val mainActivity = LocalDependencyContainer.current
    val uiState = viewModel.uiState

    //Add Member Callback
    memberResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }
            is NavResult.Value -> {
                val member = result.value
                viewModel.addMember(member)
            }
        }
    }

    AddRoleScreenView(
        modifier,
        navigator,
        uiState.tabSelected,
        group,
        fanciRole = fanciRole,
        mainActivity,
        uiState.permissionList.orEmpty(),
        uiState.permissionSelected,
        uiState.memberList,
        roleName = uiState.roleName,
        roleColor = uiState.roleColor,
        onTabSelected = {
            viewModel.onTabSelected(it)
        },
        onMemberRemove = {
            viewModel.onMemberRemove(it)
        },
        onConfirm = {
            viewModel.onConfirmAddRole(group)
        },
        onPermissionSwitch = { key, selected ->
            viewModel.onPermissionSelected(key, selected)
        },
        onRoleStyleChange = { name, color ->
            viewModel.setRoleStyle(name, color)
        },
        onDelete = {
            fanciRole?.let {
                viewModel.onDelete(it, group)
            }
        }
    )

    //是否為編輯
    if (fanciRole != null) {
        viewModel.setRoleEdit(group.id.orEmpty(), fanciRole, LocalColor.current.roleColor.colors)
    } else {
        if (uiState.permissionList == null) {
            viewModel.fetchPermissionList()
        }
    }

    if (uiState.addRoleError.isNotEmpty()) {
        LocalContext.current.showToast(uiState.addRoleError)
        viewModel.errorShowDone()
    }

    uiState.fanciRoleCallback?.let {
        resultNavigator.navigateBack(it)
    }
}

@Composable
private fun AddRoleScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    selectedIndex: Int,
    group: Group,
    fanciRole: FanciRole? = null,
    mainActivity: MainActivity,
    permissionList: List<PermissionCategory>,
    permissionSelected: Map<String, Boolean>,
    memberList: List<GroupMember>,
    roleName: String,
    roleColor: com.cmoney.fanciapi.fanci.model.Color,
    onTabSelected: (Int) -> Unit,
    onMemberRemove: (GroupMember) -> Unit,
    onConfirm: () -> Unit,
    onDelete: () -> Unit,
    onPermissionSwitch: (String, Boolean) -> Unit,
    onRoleStyleChange: (String, com.cmoney.fanciapi.fanci.model.Color) -> Unit
) {
    val tabList = listOf("樣式", "權限", "成員")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = fanciRole?.name ?: "新增角色",
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    navigator.popBackStack()
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalColor.current.env_80)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            TabRow(
                selectedTabIndex = selectedIndex,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .height(40.dp)
                    .padding(1.dp)
                    .clip(RoundedCornerShape(35)),
                indicator = {
                    Box {}
                },
                backgroundColor = LocalColor.current.env_100
            ) {
                tabList.forEachIndexed { index, text ->
                    val selected = selectedIndex == index
                    Tab(
                        modifier = if (selected) Modifier
                            .padding(2.dp)
                            .clip(RoundedCornerShape(25))
                            .background(
                                LocalColor.current.env_60
                            )
                        else Modifier
                            .clip(RoundedCornerShape(15))
                            .background(
                                Color.Transparent
                            ),
                        selected = selected,
                        onClick = {
                            onTabSelected.invoke(index)
                        },
                        text = {
                            Text(
                                text = text, color = Color.White, fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                when (selectedIndex) {
                    //樣式
                    0 -> {
                        StyleScreen(
                            mainActivity = mainActivity,
                            roleName = roleName,
                            roleColor = roleColor,
                            onChange = onRoleStyleChange,
                            showDelete = fanciRole != null,
                            onDelete = onDelete
                        )
                    }
                    //權限
                    1 -> {
                        PermissionScreen(
                            modifier = Modifier.fillMaxSize(),
                            permissionList,
                            permissionSelected
                        ) { key, selected ->
                            onPermissionSwitch.invoke(key, selected)
                        }
                    }
                    //成員
                    else -> {
                        MemberScreen(
                            navigator = navigator,
                            group = group,
                            memberList = memberList
                        ) {
                            onMemberRemove.invoke(it)
                        }
                    }
                }
            }

            //========== 儲存 ==========
            BottomButtonScreen(
                text = "儲存"
            ) {
                onConfirm.invoke()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddRoleScreenPreview() {
    FanciTheme {
        AddRoleScreenView(
            navigator = EmptyDestinationsNavigator,
            selectedIndex = 0,
            group = Group(),
            mainActivity = MainActivity(),
            permissionList = listOf(
                PermissionCategory(
                    displayCategoryName = "社團編輯",
                    permissions = listOf(
                        Permission(
                            name = "編輯社團",
                            description = "編輯社團名稱、簡介、頭像與背景。",
                            displayName = "編輯社團",
                            highlight = false,
                        )
                    )
                )
            ),
            permissionSelected = emptyMap(),
            memberList = emptyList(),
            roleColor = Color(),
            roleName = "Hi",
            onTabSelected = {},
            onMemberRemove = {},
            onConfirm = {},
            onPermissionSwitch = { key, selected -> },
            onRoleStyleChange = { _, _ -> },
            onDelete = {}
        )
    }
}