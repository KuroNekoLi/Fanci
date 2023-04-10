package com.cmoney.kolfanci.ui.screens.group.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.destinations.AllMemberScreenDestination
import com.cmoney.kolfanci.destinations.GroupApplyScreenDestination
import com.cmoney.kolfanci.destinations.RoleManageScreenDestination
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.ui.screens.shared.SettingItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.kolfanci.model.Constant.isShowRoleManage
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

/**
 * 成員管理
 */
@Composable
fun GroupMemberManageScreen(
    modifier: Modifier = Modifier,
    group: Group,
    navController: DestinationsNavigator,
    unApplyCount: Long
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(start = 25.dp, bottom = 9.dp),
            text = "成員管理", fontSize = 14.sp, color = LocalColor.current.text.default_100
        )

        if (isShowRoleManage()) {
            SettingItemScreen(
                iconRes = R.drawable.rule_manage,
                text = "角色管理",
                onItemClick = {
                    navController.navigate(RoleManageScreenDestination(group = group))
                }
            )

            Spacer(modifier = Modifier.height(1.dp))
        }

        SettingItemScreen(
            iconRes = R.drawable.all_member,
            text = "所有成員",
            onItemClick = {
                navController.navigate(
                    AllMemberScreenDestination(
                        group = group
                    )
                )
            }
        )
        Spacer(modifier = Modifier.height(1.dp))

        if (Constant.MyGroupPermission.approveJoinApplies == true) {
            SettingItemScreen(
                iconRes = R.drawable.join_apply,
                text = "加入申請",
                onItemClick = {
                    navController.navigate(
                        GroupApplyScreenDestination(
                            group = group
                        )
                    )
                }
            ) {
                val text = if (unApplyCount != 0L) unApplyCount.toString() else ""
                Text(text = text, fontSize = 17.sp, color = LocalColor.current.text.default_100)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupMemberManageScreenPreview() {
    FanciTheme {
        GroupMemberManageScreen(
            group = Group(),
            navController = EmptyDestinationsNavigator,
            unApplyCount = 20
        )
    }
}