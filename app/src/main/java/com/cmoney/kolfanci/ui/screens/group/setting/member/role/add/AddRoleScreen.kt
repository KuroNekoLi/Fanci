package com.cmoney.kolfanci.ui.screens.group.setting.member.role.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.*
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.AddMemberScreenDestination
import com.cmoney.kolfanci.ui.destinations.EditInputScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.member.role.viewmodel.FanciRoleCallback
import com.cmoney.kolfanci.ui.screens.group.setting.member.role.viewmodel.RoleManageViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.AlertDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.dialog.SaveConfirmDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.toolbar.EditToolbarScreen
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

@Destination
@Composable
fun AddRoleScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    fanciRole: FanciRole? = null,
    viewModel: RoleManageViewModel = koinViewModel(),
    memberResult: ResultRecipient<AddMemberScreenDestination, String>,
    resultNavigator: ResultBackNavigator<FanciRoleCallback>,
    editRoleNameResult: ResultRecipient<EditInputScreenDestination, String>
) {
    val uiState = viewModel.uiState
    var showDeleteConfirmDialog by remember {
        mutableStateOf(false)
    }
    var showSaveTip by remember {
        mutableStateOf(false)
    }

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

    //Edit Role name Callback
    editRoleNameResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val roleName = result.value
                viewModel.setRoleName(roleName)
            }
        }
    }

    val isLoading by viewModel.loading.collectAsState()

    AddRoleScreenView(
        modifier,
        navigator,
        uiState.tabSelected,
        group,
        fanciRole = fanciRole,
        uiState.permissionList.orEmpty(),
        uiState.permissionSelected,
        uiState.memberList,
        roleName = uiState.roleName,
        roleColor = uiState.roleColor,
        isLoading = isLoading,
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
                showDeleteConfirmDialog = true
            }
        },
        onBack = {
            showSaveTip = true
        }
    )

    //Delete double check dialog
    if (showDeleteConfirmDialog) {
        AlertDialogScreen(
            onDismiss = {
                showDeleteConfirmDialog = false
            },
            title = "確定刪除角色「%s」".format(fanciRole?.name),
            content = {
                Column(
                    modifier = modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "角色刪除後，具有該角色的成員將會自動移除角色相關權限。",
                        fontSize = 17.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    BorderButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        text = "確定刪除",
                        borderColor = LocalColor.current.component.other,
                        textColor = Color.White
                    ) {
                        viewModel.onDelete(fanciRole!!, group)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    BorderButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        text = "返回",
                        borderColor = LocalColor.current.component.other,
                        textColor = Color.White
                    ) {
                        kotlin.run {
                            showDeleteConfirmDialog = false
                        }
                    }
                }
            }
        )
    }


    //是否為編輯
    if (fanciRole != null) {
        viewModel.setRoleEdit(group.id.orEmpty(), fanciRole, LocalColor.current.roleColor.colors)
    } else {
        if (uiState.permissionList == null) {
            viewModel.fetchPermissionList()
        }
    }

    uiState.addRoleError?.let {
        DialogScreen(
            titleIconRes = R.drawable.edit,
            onDismiss = {
                viewModel.errorShowDone()
            },
            title = it.first,
            subTitle = it.second
        ) {
            BlueButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                text = "修改",
            ) {
                run {
                    viewModel.errorShowDone()
                }
            }
        }
    }

//    if (uiState.addRoleError.isNotEmpty()) {
//        LocalContext.current.showToast(uiState.addRoleError)
//        viewModel.errorShowDone()
//    }

    uiState.fanciRoleCallback?.let {
        resultNavigator.navigateBack(it)
    }

    //離開再次 確認
    SaveConfirmDialogScreen(
        isShow = showSaveTip,
        onContinue = {
            showSaveTip = false
        },
        onGiveUp = {
            showSaveTip = false
            navigator.popBackStack()
        }
    )
}

@Composable
private fun AddRoleScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    selectedIndex: Int,
    group: Group,
    fanciRole: FanciRole? = null,
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
    onRoleStyleChange: (String, com.cmoney.fanciapi.fanci.model.Color) -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean
) {
    val tabList = listOf("樣式", "權限", "成員")
    val TAG = "AddRoleScreenView"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            if (fanciRole != null) {
                TopBarScreen(
                    title = "編輯角色",
                    backClick = {
                        KLog.i(TAG, "backClick click.")
                        onConfirm.invoke()
                    }
                )
            } else {
                EditToolbarScreen(
                    title = "新增角色",
                    backClick = onBack,
                    saveClick = {
                        KLog.i(TAG, "saveClick click.")
                        onConfirm.invoke()
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(LocalColor.current.env_80)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            if (Constant.isCanEditRole()) {
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
            }

            Column(modifier = Modifier.weight(1f)) {
                when (selectedIndex) {
                    //樣式
                    0 -> {
                        StyleScreen(
                            navigator = navigator,
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

//            //========== 儲存 ==========
//            BottomButtonScreen(
//                text = "儲存"
//            ) {
//                onConfirm.invoke()
//            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(size = 32.dp),
                    color = LocalColor.current.primary
                )
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
            roleName = "Hi",
            roleColor = Color(),
            onTabSelected = {},
            onMemberRemove = {},
            onConfirm = {},
            onDelete = {},
            onPermissionSwitch = { key, selected -> },
            onRoleStyleChange = { _, _ -> },
            onBack = {},
            isLoading = false
        )
    }
}