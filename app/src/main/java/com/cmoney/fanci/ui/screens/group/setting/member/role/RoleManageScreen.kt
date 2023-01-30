package com.cmoney.fanci.ui.screens.group.setting.member.role

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.cmoney.fanci.destinations.AddRoleScreenDestination
import com.cmoney.fanci.ui.common.BlueButton
import com.cmoney.fanci.ui.common.BorderButton
import com.cmoney.fanci.ui.screens.group.setting.member.role.viewmodel.FanciRoleCallback
import com.cmoney.fanci.ui.screens.group.setting.member.role.viewmodel.RoleManageViewModel
import com.cmoney.fanci.ui.screens.shared.TopBarScreen
import com.cmoney.fanci.ui.screens.shared.role.RoleItemScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.FanciRole
import com.cmoney.fanciapi.fanci.model.Group
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
    roleResult: ResultRecipient<AddRoleScreenDestination, FanciRoleCallback>
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
                }
                else {
                    viewModel.removeRole(fanciRoleCallback.fanciRole)
                }
            }
        }
    }

    RoleManageScreenView(
        modifier,
        navigator,
        viewModel.uiState.fanciRole.orEmpty(),
        group
    ) {
        KLog.i(TAG, "on save click.")
        // TODO()
    }

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
    onSave: () -> Unit
) {
    val TAG = "RoleManageScreenView"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        scaffoldState = rememberScaffoldState(),
        topBar = {
            TopBarScreen(
                title = "角色管理",
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
            Row(modifier = Modifier.padding(24.dp)) {
                BorderButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    text = "新增角色",
                    borderColor = LocalColor.current.component.other,
                    textColor = Color.White
                ) {
                    navigator.navigate(AddRoleScreenDestination(group = group))
                }

                Spacer(modifier = Modifier.width(23.dp))

                BorderButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    text = "重新排列",
                    borderColor = LocalColor.current.component.other,
                    textColor = Color.White
                ) {
                    // TODO:
                }
            }

            if (roleList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "尚未建立任何角色",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = LocalColor.current.component.other
                    )
                }
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
            group = Group()
        ) {

        }
    }
}