package com.cmoney.fanci.ui.screens.group.setting.member.all

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanci.destinations.ShareAddRoleScreenDestination
import com.cmoney.fanci.extension.fromJsonTypeToken
import com.cmoney.fanci.ui.common.BorderButton
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.screens.shared.role.RoleItemScreen
import com.cmoney.fanci.ui.screens.shared.setting.BottomButtonScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.GroupMember
import com.google.gson.Gson
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.EmptyResultRecipient
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient

@Destination
@Composable
fun MemberRoleManageScreen(
    modifier: Modifier = Modifier,
    navController: DestinationsNavigator,
    group: Group,
    groupMember: GroupMember,
    setRoleResult: ResultRecipient<ShareAddRoleScreenDestination, String>
) {
    val roleList = remember {
        mutableStateOf(groupMember.roleInfos.orEmpty())
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
    ) {
        Column {
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
                repeat(roleList.value.size) { index ->
                    val role = roleList.value[index]
                    RoleItemScreen(
                        index = index,
                        isShowIndex = false,
                        fanciRole = role,
                        editText = "移除"
                    ) {
                        roleList.value = roleList.value.toMutableList().filter { fanciRole ->
                            it.id != fanciRole.id
                        }
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
                                existsRole = roleList.value.toTypedArray()
                            )
                        )
                    }
                )
            }

            BottomButtonScreen(
                text = "儲存"
            ) {
                // TODO:
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MemberRoleManageScreenPreview() {
    FanciTheme {
        MemberRoleManageScreen(
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
            setRoleResult = EmptyResultRecipient()
        )
    }
}