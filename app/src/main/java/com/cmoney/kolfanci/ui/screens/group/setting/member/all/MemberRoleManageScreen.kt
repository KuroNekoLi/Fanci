package com.cmoney.kolfanci.ui.screens.group.setting.member.all

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.destinations.ShareAddRoleScreenDestination
import com.cmoney.kolfanci.extension.fromJsonTypeToken
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.member.viewmodel.MemberViewModel
import com.cmoney.kolfanci.ui.screens.shared.role.RoleItemScreen
import com.cmoney.kolfanci.ui.screens.shared.setting.BottomButtonScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.cmoney.kolfanci.ui.screens.shared.dialog.SaveConfirmDialogScreen
import com.google.gson.Gson
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.*
import org.koin.androidx.compose.koinViewModel

@Destination
@Composable
fun MemberRoleManageScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    groupMember: GroupMember,
    viewModel: MemberViewModel = koinViewModel(),
    setRoleResult: ResultRecipient<ShareAddRoleScreenDestination, String>,
    resultNavigator: ResultBackNavigator<String>
) {
    val roleList = remember {
        mutableStateOf(groupMember.roleInfos.orEmpty())
    }

    var showSaveTip by remember {
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
                val unionList = resultRoleList.union(roleList.value).toMutableList()
                roleList.value = unionList
            }
        }
    }

    MemberRoleManageScreenView(
        modifier = modifier,
        navController = navController,
        groupMember = groupMember,
        roleList = roleList.value,
        group = group,
        onRemove = {
            roleList.value = roleList.value.toMutableList().filter { fanciRole ->
                it.id != fanciRole.id
            }
        },
        onSave = {
            viewModel.assignMemberRole(
                groupId = group.id.orEmpty(),
                userId = groupMember.id.orEmpty(),
                oldFanciRole = groupMember.roleInfos.orEmpty(),
                newFanciRole = roleList.value
            )
            val gson = Gson()

            resultNavigator.navigateBack(
                gson.toJson(roleList.value)
            )
        },
        onBack = {
            showSaveTip = true
        }
    )

    SaveConfirmDialogScreen(
        isShow = showSaveTip,
        onContinue = {
            showSaveTip = false
        },
        onGiveUp = {
            showSaveTip = false
            navController.popBackStack()
        }
    )
}

@Composable
private fun MemberRoleManageScreenView(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    groupMember: GroupMember,
    roleList: List<FanciRole>,
    group: Group,
    onRemove: (FanciRole) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "%s的角色管理".format(groupMember.name),
                leadingEnable = true,
                moreEnable = false,
                backClick = {
                    navController.popBackStack()
                }
            )
        }
    ) {padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
            ) {
                Text(
                    modifier = Modifier.padding(
                        top = 15.dp,
                        bottom = 15.dp,
                        start = 24.dp,
                        end = 24.dp
                    ),
                    text = "目前 %s 擁有的角色別，可透過手動移除".format(groupMember.name),
                    fontSize = 14.sp,
                    color = LocalColor.current.component.other
                )

                //角色清單
                repeat(roleList.size) { index ->
                    val role = roleList[index]
                    RoleItemScreen(
                        index = index,
                        isShowIndex = false,
                        fanciRole = role,
                        editText = "移除"
                    ) {
                        onRemove.invoke(it)
                    }

                    Spacer(modifier = Modifier.height(1.dp))
                }

                //新增角色 按鈕
                BorderButton(
                    modifier = Modifier
                        .padding(
                            top = 20.dp,
                            start = 24.dp,
                            end = 24.dp
                        )
                        .fillMaxWidth()
                        .height(45.dp),
                    text = "新增角色",
                    borderColor = LocalColor.current.text.default_50,
                    textColor = LocalColor.current.text.default_100,
                    onClick = {
                        navController.navigate(
                            ShareAddRoleScreenDestination(
                                group = group,
                                buttonText = "新增角色給「%s」".format(groupMember.name.orEmpty()),
                                existsRole = roleList.toTypedArray()
                            )
                        )
                    }
                )
            }

            BottomButtonScreen(
                text = "儲存"
            ) {
                onSave.invoke()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MemberRoleManageScreenPreview() {
    FanciTheme {
        MemberRoleManageScreenView(
            navController = EmptyDestinationsNavigator,
            group = Group(),
            groupMember = GroupMember(
                name = "Hello",
                roleInfos = listOf(
                    FanciRole(
                        name = "Hi"
                    ),
                    FanciRole(
                        name = "Hi2"
                    )
                )
            ),
            roleList = listOf(),
            onRemove = {},
            onSave = {},
            onBack = {}
        )
    }
}