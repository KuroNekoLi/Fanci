package com.cmoney.kolfanci.ui.screens.group.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.destinations.ChannelSettingScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupOpennessScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupSettingSettingScreenDestination
import com.cmoney.kolfanci.ui.screens.shared.setting.SettingItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

/**
 * 社團管理
 */
@Composable
fun GroupManageScreen(
    modifier: Modifier = Modifier,
    group: Group,
    navController: DestinationsNavigator
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.padding(start = 25.dp, bottom = 9.dp),
            text = stringResource(id = R.string.group_manage), fontSize = 14.sp, color = LocalColor.current.text.default_100
        )

        if (Constant.MyGroupPermission.editGroup == true) {
            SettingItemScreen(
                iconRes = R.drawable.info,
                text = stringResource(id = R.string.group_setting),
                onItemClick = {
                    AppUserLogger.getInstance()
                        .log(Clicked.GroupSettingsGroupSettingsPage)
                    navController.navigate(
                        GroupSettingSettingScreenDestination(
                            group = group
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(1.dp))
        }

        if (isShowChannelManage()) {
            SettingItemScreen(
                iconRes = R.drawable.channel_setting,
                text = "頻道管理",
                onItemClick = {
                    navController.navigate(
                        ChannelSettingScreenDestination(
                            group = group
                        )
                    )
                }
            )
            Spacer(modifier = Modifier.height(1.dp))
        }

        if (Constant.MyGroupPermission.setGroupPublicity == true) {
            SettingItemScreen(
                iconRes = R.drawable.lock,
                text = "社團公開度",
                onItemClick = {
                    AppUserLogger.getInstance()
                        .log(Clicked.GroupSettingsGroupOpenness)
                    navController.navigate(
                        GroupOpennessScreenDestination(
                            group = group
                        )
                    )
                }
            ) {
                val publicText = if (group.isNeedApproval == true) {
                    "不公開"
                } else {
                    "公開"
                }
                Text(
                    text = publicText,
                    fontSize = 17.sp,
                    color = LocalColor.current.specialColor.red
                )
            }
        }
    }
}

/**
 * 是否呈現 頻道管理
 */
private fun isShowChannelManage(): Boolean {
    return Constant.MyGroupPermission.createOrEditChannel == true ||
            Constant.MyGroupPermission.rearrangeChannelCategory == true ||
            Constant.MyGroupPermission.createOrEditCategory == true ||
            Constant.MyGroupPermission.deleteCategory == true ||
            Constant.MyGroupPermission.deleteChannel == true
}

@Preview(showBackground = true)
@Composable
fun GroupManageScreenPreview() {
    FanciTheme {
        GroupManageScreen(group = Group(), navController = EmptyDestinationsNavigator)
    }
}