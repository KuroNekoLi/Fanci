package com.cmoney.kolfanci.ui.screens.group.setting.member.role

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.AddRoleScreenDestination
import com.cmoney.kolfanci.ui.destinations.RoleSortScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.member.role.sort.SortedRole
import com.cmoney.kolfanci.ui.screens.group.setting.member.role.viewmodel.FanciRoleCallback
import com.cmoney.kolfanci.ui.screens.group.setting.member.role.viewmodel.RoleManageViewModel
import com.cmoney.kolfanci.ui.screens.shared.TopBarScreen
import com.cmoney.kolfanci.ui.screens.shared.role.RoleItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.socks.library.KLog
import org.koin.androidx.compose.koinViewModel

/**
 * 角色管理
 */
@Destination
@Composable
fun RoleManageScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    group: Group,
    viewModel: RoleManageViewModel = koinViewModel(),
    roleResult: ResultRecipient<AddRoleScreenDestination, FanciRoleCallback>,
    sortRoleResult: ResultRecipient<RoleSortScreenDestination, SortedRole>
) {
    val TAG = "RoleManageScreen"

    roleResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val fanciRoleCallback = result.value
                if (fanciRoleCallback.isAdd) {
                    viewModel.addMemberRole(fanciRoleCallback.fanciRole)
                } else {
                    viewModel.removeRole(fanciRoleCallback.fanciRole)
                }
            }
        }
    }

    sortRoleResult.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
            }

            is NavResult.Value -> {
                val sortedRole = result.value
                viewModel.setSortResult(sortedRole.roleList)
            }
        }
    }

    RoleManageScreenView(
        modifier,
        navigator,
        viewModel.uiState.fanciRole.orEmpty(),
        group,
        loading = viewModel.uiState.loading
    )

    if (viewModel.uiState.fanciRole == null) {
        viewModel.fetchRoleList(group.id.orEmpty())
    }
}

@Composable
fun RoleManageScreenView(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    roleList: List<FanciRole>,
    group: Group,
    loading: Boolean
) {
    val TAG = "RoleManageScreenView"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "角色管理",
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
            Row(modifier = Modifier.padding(24.dp)) {

                if (Constant.isCanEditRole()) {
                    BorderButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        text = "新增角色",
                        borderColor = LocalColor.current.component.other,
                        textColor = LocalColor.current.text.default_100
                    ) {
                        navigator.navigate(AddRoleScreenDestination(group = group))
                    }
                }

                if (Constant.MyGroupPermission.rearrangeRoles == true && roleList.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(23.dp))

                    BorderButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        text = "重新排列",
                        borderColor = LocalColor.current.component.other,
                        textColor = LocalColor.current.text.default_100
                    ) {
                        navigator.navigate(
                            RoleSortScreenDestination(
                                group = group,
                                roleList = roleList.toTypedArray()
                            )
                        )
                    }
                }
            }

            if (loading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(size = 32.dp),
                        color = LocalColor.current.primary
                    )
                }
            } else {
                if (roleList.isEmpty()) {
                    EmptyRoleView(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                } else {
                    Text(
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 20.dp),
                        text = "成員們的暱稱顏色，會顯示最高階身份的顏色可透過重新排列來拖動角色的階級。",
                        fontSize = 14.sp,
                        color = LocalColor.current.component.other
                    )

                    //Role List
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        itemsIndexed(roleList) { index, item ->
                            RoleItemScreen(
                                index = index + 1,
                                fanciRole = item,
                                onEditClick = {
                                    KLog.i(TAG, "onEditClick")
                                    navigator.navigate(
                                        AddRoleScreenDestination(
                                            group = group,
                                            fanciRole = it
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }

            //========== 儲存 ==========
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(135.dp)
//                    .background(LocalColor.current.env_100),
//                contentAlignment = Alignment.Center
//            ) {
//                BlueButton(text = "儲存") {
//                    onSave.invoke()
//                }
//            }
        }
    }
}

@Composable
private fun EmptyRoleView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(LocalColor.current.env_80),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AsyncImage(
            modifier = Modifier.size(105.dp),
            model = R.drawable.empty_folwer, contentDescription = "empty message"
        )

        Spacer(modifier = Modifier.height(43.dp))

        Text(
            text = "目前沒有建立任何角色\n建立角色可以區分粉絲，讓管理更有效率！",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = LocalColor.current.text.default_30
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoleManageScreenPreview() {
    FanciTheme {
        RoleManageScreenView(
            navigator = EmptyDestinationsNavigator,
            roleList = listOf(
                FanciRole(
                    name = "角色1"
                ),
                FanciRole(
                    name = "角色2",
                    color = "FF38B035"
                )
            ),
            group = Group(),
            loading = true
        )
    }
}