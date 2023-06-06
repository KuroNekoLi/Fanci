package com.cmoney.kolfanci.ui.screens.shared.channel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.AddMemberScreenDestination
import com.cmoney.kolfanci.ui.destinations.ShareAddRoleScreenDestination
import com.cmoney.kolfanci.ui.screens.shared.TabScreen
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.member.MemberItemScreen
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.MemberViewModel
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.SelectedModel
import com.cmoney.kolfanci.ui.screens.shared.role.RoleItemScreen
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
    selectedCallback: ResultBackNavigator<SelectedModel>
) {
    val uiState = viewModel.uiState

    //init default data
    LaunchedEffect(Unit) {
        if (selectedModel.selectedMember.isNotEmpty()) {
            viewModel.addSelectedMember(selectedModel.selectedMember)
        }
        if (selectedModel.selectedRole.isNotEmpty()) {
            viewModel.addSelectedRole(selectedModel.selectedRole)
        }
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

    val selectedMember by viewModel.selectedMember.collectAsState()

    KLog.i("TAG", "selectedMember:$selectedMember")

    MemberAndRoleManageScreenView(
        modifier = modifier,
        navigator = navigator,
        group = group,
        topBarTitle = topBarTitle,
        selectedIndex = uiState.tabIndex,
        selectedMember = selectedMember,
        selectedRole = uiState.selectedRole,
        onTabClick = {
            viewModel.onTabClick(it)
        },
        onRemoveClick = {
            viewModel.removeSelectedMember(it)
        },
        onRoleRemoveClick = {
            viewModel.removeSelectedRole(it)
        },
        onSaveClick = {
            val selectedModel = viewModel.fetchSelected()
            selectedCallback.navigateBack(result = selectedModel)
        }
    )
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
    onTabClick: (Int) -> Unit,
    onRemoveClick: (GroupMember) -> Unit,
    onRoleRemoveClick: (FanciRole) -> Unit,
    onSaveClick: () -> Unit
) {
    val list = listOf("成員", "角色")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = topBarTitle,
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
                    else -> {}
                }
            }

            //========== 儲存 ==========
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
                    .background(LocalColor.current.env_100),
                contentAlignment = Alignment.Center
            ) {
                BlueButton(
                    text = "儲存變更"
                ) {
                    onSaveClick.invoke()
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
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        item {
            Box(modifier = Modifier.background(LocalColor.current.background)) {
                BorderButton(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp, start = 24.dp, end = 24.dp)
                        .fillMaxWidth()
                        .height(40.dp),
                    text = "新增成員",
                    textColor = Color.White,
                    borderColor = Color_80FFFFFF
                ) {
                    navigator.navigate(
                        AddMemberScreenDestination(
                            group = group,
                            excludeMember = member.toTypedArray(),
                            title = "新增「%s」成員".format(title)
                        )
                    )
                }
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
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        item {
            Box(modifier = Modifier.background(LocalColor.current.background)) {
                BorderButton(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp, start = 24.dp, end = 24.dp)
                        .fillMaxWidth()
                        .height(40.dp),
                    text = "新增角色",
                    textColor = Color.White,
                    borderColor = Color_80FFFFFF
                ) {
                    navigator.navigate(
                        ShareAddRoleScreenDestination(
                            group = group,
                            title = "新增「%s」角色".format(title),
                            subTitle = "直接指定角色，讓一批成員進入私密頻道。",
                            buttonText = "新增",
                            existsRole = roles.toTypedArray()
                        )
                    )
                }
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


@Preview(showBackground = true)
@Composable
fun MemberAndRoleManageScreenPreview() {
    FanciTheme {
        MemberAndRoleManageScreenView(
            modifier = Modifier,
            navigator = EmptyDestinationsNavigator,
            group = Group(),
            topBarTitle = "基本權限",
            selectedIndex = 1,
            selectedMember = emptyList(),
            selectedRole = listOf(
                FanciRole(
                    name = "角色2",
                    color = "FF38B035",
                    userCount = 9
                )
            ),
            onTabClick = {},
            onRemoveClick = {},
            onRoleRemoveClick = {},
            onSaveClick = {}
        )
    }
}