package com.cmoney.kolfanci.ui.screens.group.setting.member.role.add

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.fancylog.model.data.Clicked
import com.cmoney.fancylog.model.data.From
import com.cmoney.fancylog.model.data.Page
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.extension.showColorPickerDialogBottomSheet
import com.cmoney.kolfanci.extension.toColor
import com.cmoney.kolfanci.model.Constant
import com.cmoney.kolfanci.model.analytics.AppUserLogger
import com.cmoney.kolfanci.ui.destinations.EditInputScreenDestination
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.socks.library.KLog

/**
 * 樣式 View
 */
@Composable
fun StyleScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    roleName: String,
    roleColor: com.cmoney.fanciapi.fanci.model.Color,
    showDelete: Boolean,
    from: From = From.Create,
    onChange: (String, com.cmoney.fanciapi.fanci.model.Color) -> Unit,
    onDelete: () -> Unit
) {
    val TAG = "StyleView"

    val defaultColor = if (roleColor.hexColorCode.orEmpty().isEmpty()) {
        LocalColor.current.roleColor.colors.first()
    } else {
        roleColor
    }

    Column(modifier = modifier.background(LocalColor.current.env_80)) {
        Text(
            modifier = Modifier.padding(
                top = 20.dp,
                start = 24.dp,
                end = 24.dp,
                bottom = 20.dp
            ),
            text = "角色名稱",
            fontSize = 14.sp,
            color = LocalColor.current.text.default_100
        )

        val context = LocalContext.current

        Row(
            modifier = Modifier
                .background(LocalColor.current.background)
                .clickable {
                    with(AppUserLogger.getInstance()) {
                        log(Page.GroupSettingsRoleManagementAddRoleStyleRoleName)
                        log(Clicked.StyleRoleName, from)
                    }
                    navigator.navigate(
                        EditInputScreenDestination(
                            defaultText = roleName,
                            toolbarTitle = "角色名稱",
                            placeholderText = context.getString(R.string.input_role_name),
                            emptyAlertTitle = context.getString(R.string.role_name_empty),
                            emptyAlertSubTitle = context.getString(R.string.role_name_empty_desc),
                            from = if (from == From.Create) {
                                From.RoleName
                            } else {
                                From.EditName
                            },
                            textFieldFrom = from
                        )
                    )
                }
                .padding(
                    top = 10.dp,
                    bottom = 10.dp,
                    start = 25.dp,
                    end = 10.dp
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (roleName.isEmpty()) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "輸入角色名稱",
                    fontSize = 17.sp,
                    color = LocalColor.current.text.default_30
                )
            } else {
                Text(
                    modifier = Modifier.weight(1f),
                    text = roleName,
                    fontSize = 17.sp,
                    color = LocalColor.current.text.default_100
                )
            }

            Spacer(modifier = Modifier.width(5.dp))

            Image(
                painter = painterResource(id = R.drawable.next),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = LocalColor.current.text.default_80)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            modifier = Modifier.padding(start = 24.dp, bottom = 10.dp),
            text = "角色顯示顏色", fontSize = 14.sp, color = LocalColor.current.text.default_100
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(LocalColor.current.background)
                .clickable {
                    AppUserLogger
                        .getInstance()
                        .log(Clicked.StyleSelectColor)
                    (context as? Activity)?.showColorPickerDialogBottomSheet(
                        selectedColor = defaultColor
                    ) {
                        KLog.i(TAG, "color pick:$it")
                        onChange.invoke(roleName, it)
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.padding(start = 25.dp),
                painter = painterResource(id = R.drawable.rule_manage),
                contentDescription = null,
                colorFilter = ColorFilter.tint(
                    color = defaultColor.hexColorCode.orEmpty().toColor()
                )
            )

            Text(
                modifier = Modifier.padding(start = 17.dp),
                text = defaultColor.displayName.orEmpty(),
                fontSize = 17.sp,
                color = LocalColor.current.text.default_100
            )
        }

        if (showDelete && Constant.isCanDeleteRole()) {
            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(LocalColor.current.background)
                    .clickable {
                        onDelete.invoke()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "刪除角色",
                    fontSize = 17.sp,
                    color = LocalColor.current.specialColor.red
                )
            }

            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StyleScreenPreview() {
    FanciTheme {
        StyleScreen(
            navigator = EmptyDestinationsNavigator,
            roleName = "",
            roleColor = com.cmoney.fanciapi.fanci.model.Color(),
            showDelete = true,
            onDelete = {},
            onChange = { _, _ ->

            }
        )
    }
}