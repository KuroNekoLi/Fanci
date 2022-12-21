package com.cmoney.fanci.ui.screens.group.setting.role.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.LocalDependencyContainer
import com.cmoney.fanci.MainActivity
import com.cmoney.fanci.destinations.AddMemberScreenDestination
import com.cmoney.fanci.ui.screens.group.setting.role.viewmodel.RoleManageViewModel
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.screens.shared.setting.BottomButtonScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.fanciapi.fanci.model.Permission
import com.cmoney.fanciapi.fanci.model.PermissionCategory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import org.koin.androidx.compose.koinViewModel
import java.lang.reflect.Type

@Destination
@Composable
fun AddRoleScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    viewModel: RoleManageViewModel = koinViewModel(),
    memberResult: ResultRecipient<AddMemberScreenDestination, String>
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
                val gson = Gson()
                val listType: Type =
                    object : TypeToken<List<GroupMember>>() {}.type
                val responseMemberList = gson.fromJson(member, listType) as List<GroupMember>
                viewModel.addMember(responseMemberList)
            }
        }
    }

    AddRoleScreenView(
        modifier,
        navigator,
        uiState.tabSelected,
        group,
        mainActivity,
        uiState.permissionList.orEmpty(),
        uiState.permissionSelected,
        uiState.memberList,
        onTabSelected = {
            viewModel.onTabSelected(it)
        },
        onMemberRemove = {
            viewModel.onMemberRemove(it)
        }
    ) { key, selected ->
        viewModel.onPermissionSelected(key, selected)
    }

    if (uiState.permissionList == null) {
        viewModel.fetchPermissionList()
    }
}

@Composable
private fun AddRoleScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    selectedIndex: Int,
    group: Group,
    mainActivity: MainActivity,
    permissionList: List<PermissionCategory>,
    permissionSelected: Map<String, Boolean>,
    memberList: List<GroupMember>,
    onTabSelected: (Int) -> Unit,
    onMemberRemove: (GroupMember) -> Unit,
    onPermissionSwitch: (String, Boolean) -> Unit,
) {
    val tabList = listOf("樣式", "權限", "成員")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "新增角色",
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    navigator.popBackStack()
                }
            )
        }
    ) {
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
                indicator = { tabPositions: List<TabPosition> ->
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
                        StyleScreen(mainActivity = mainActivity)
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
                text = "確定新增"
            ) {

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
                    categoryName = "01-GroupEdit",
                    displayCategoryName = "社團編輯",
                    permissions = listOf(
                        Permission(
                            name = "編輯社團",
                            description = "編輯社團名稱、簡介、頭像與背景。",
                            displayName = "編輯社團",
                            highlight = false,
                            displayCategoryName = "社團編輯"
                        )
                    )
                )
            ),
            permissionSelected = emptyMap(),
            memberList = emptyList(),
            onTabSelected = {},
            onMemberRemove = {}
        ) { key, selected ->

        }
    }
}