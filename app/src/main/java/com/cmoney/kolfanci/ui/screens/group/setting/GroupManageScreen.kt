package com.cmoney.kolfanci.ui.screens.group.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fanciapi.fanci.model.Group
import com.cmoney.fanciapi.fanci.model.PushNotificationSetting
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.openNotificationSetting
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.model.mock.MockData
import com.cmoney.kolfanci.ui.common.BlueButton
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.destinations.ChannelSettingScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupOpennessScreenDestination
import com.cmoney.kolfanci.ui.destinations.GroupSettingSettingScreenDestination
import com.cmoney.kolfanci.ui.destinations.NotificationSettingScreenDestination
import com.cmoney.kolfanci.ui.screens.group.setting.SettingItemParam.settingItem
import com.cmoney.kolfanci.ui.screens.shared.dialog.DialogScreen
import com.cmoney.kolfanci.ui.screens.shared.item.NarrowItem
import com.cmoney.kolfanci.ui.screens.shared.item.NarrowItemDefaults
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

object SettingItemParam {
    fun Modifier.settingItem() = composed {
        then(this)
            .fillMaxWidth()
            .background(LocalColor.current.background)
            .padding(
                top = 10.dp,
                bottom = 10.dp,
                start = 25.dp,
                end = 10.dp
            )
    }

    val titleFontWeight = FontWeight.Normal

    val titleFontSize = 17.sp

    val subTitleFontSize = 17.sp
}

/**
 * 社團管理
 */
@Composable
fun GroupManageScreen(
    modifier: Modifier = Modifier,
    group: Group,
    navController: DestinationsNavigator,
    pushNotificationSetting: PushNotificationSetting? = null
) {
    var openNotificationSettingTipDialog by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.padding(start = 25.dp, bottom = 9.dp),
            text = stringResource(id = R.string.group_manage),
            fontSize = 14.sp,
            color = LocalColor.current.text.default_100
        )

        if (Constant.MyGroupPermission.editGroup == true) {
            NarrowItem(
                modifier = Modifier.settingItem(),
                title = stringResource(id = R.string.group_setting),
                titleFontWeight = SettingItemParam.titleFontWeight,
                titleFontSize = SettingItemParam.titleFontSize,
                prefixIcon = painterResource(id = R.drawable.info),
                prefixIconColor = LocalColor.current.component.other,
                actionContent = NarrowItemDefaults.nextIcon(),
                onClick = {
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
            NarrowItem(
                modifier = Modifier.settingItem(),
                title = stringResource(id = R.string.channel_manage),
                titleFontWeight = SettingItemParam.titleFontWeight,
                titleFontSize = SettingItemParam.titleFontSize,
                prefixIcon = painterResource(id = R.drawable.channel_setting),
                prefixIconColor = LocalColor.current.component.other,
                actionContent = NarrowItemDefaults.nextIcon(),
                onClick = {
                    AppUserLogger.getInstance()
                        .log(Clicked.GroupSettingsChannelManagement)
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

            NarrowItem(
                modifier = Modifier.settingItem(),
                title = stringResource(id = R.string.group_openness),
                titleFontWeight = SettingItemParam.titleFontWeight,
                titleFontSize = SettingItemParam.titleFontSize,
                prefixIcon = painterResource(id = R.drawable.lock),
                prefixIconColor = LocalColor.current.component.other,
                actionContent = NarrowItemDefaults.nextIcon(),
                subTitle = if (group.isNeedApproval == true) {
                    "不公開"
                } else {
                    "公開"
                },
                subTitleColor = LocalColor.current.specialColor.red,
                subTitleFontSize = SettingItemParam.subTitleFontSize,
                onClick = {
                    AppUserLogger.getInstance()
                        .log(Clicked.GroupSettingsGroupOpenness)
                    navController.navigate(
                        GroupOpennessScreenDestination(
                            group = group
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(1.dp))
        }

        //推播設定
        NarrowItem(
            modifier = Modifier.settingItem(),
            title = stringResource(id = R.string.notification_setting),
            titleFontWeight = SettingItemParam.titleFontWeight,
            titleFontSize = SettingItemParam.titleFontSize,
            prefixIcon = painterResource(id = R.drawable.bell),
            prefixIconColor = LocalColor.current.component.other,
            actionContent = NarrowItemDefaults.nextIcon(),
            subTitle = pushNotificationSetting?.shortTitle.orEmpty(),
            subTitleFontSize = SettingItemParam.subTitleFontSize,
            onClick = {
                AppUserLogger.getInstance()
                    .log(Clicked.GroupSettingsNotificationSetting)
                if (pushNotificationSetting == null) {
                    openNotificationSettingTipDialog = true
                } else {
                    navController.navigate(
                        NotificationSettingScreenDestination(
                            groupId = group.id.orEmpty(),
                            pushNotificationSetting = pushNotificationSetting
                        )
                    )
                }
            }
        )
    }


    //========== Dialog ==========
    if (openNotificationSettingTipDialog) {
        DialogScreen(
            title = stringResource(id = R.string.open_notification_setting),
            subTitle = stringResource(id = R.string.open_notification_setting_desc),
            onDismiss = {
                openNotificationSettingTipDialog = false
            }) {
            Column {
                BlueButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = stringResource(id = R.string.forward_system_setting)
                ) {
                    AppUserLogger.getInstance()
                        .log(Clicked.NotificationSettingGoSystem)
                    context.openNotificationSetting()
                    openNotificationSettingTipDialog = false
                }

                Spacer(modifier = Modifier.height(20.dp))

                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = stringResource(id = R.string.back),
                    borderColor = LocalColor.current.text.default_100
                ) {
                    openNotificationSettingTipDialog = false
                }
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

@Preview
@Composable
fun GroupManageScreenPreview() {
    FanciTheme {
        GroupManageScreen(
            group = Group(),
            navController = EmptyDestinationsNavigator,
            pushNotificationSetting = MockData.mockNotificationSettingItem
        )
    }
}