package com.cmoney.kolfanci.ui.screens.group.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.common.BorderButton
import com.cmoney.kolfanci.ui.screens.shared.dialog.AlertDialogScreen
import com.cmoney.kolfanci.ui.screens.shared.setting.SettingItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 關於社團
 */
@Composable
fun GroupAboutScreen(
    modifier: Modifier = Modifier,
    onInviteClick: () -> Unit,
    onLeaveGroup: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        var showLeaveGroupDialog by remember {
            mutableStateOf(false)
        }
        Text(
            modifier = Modifier.padding(start = 25.dp, bottom = 9.dp),
            text = stringResource(id = R.string.about_group), fontSize = 14.sp, color = LocalColor.current.text.default_100
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            SettingItemScreen(
                iconRes = R.drawable.invite_member,
                text = stringResource(id = R.string.invite_group_member),
                onItemClick = {
                    onInviteClick.invoke()
                }
            )
            SettingItemScreen(
                iconRes = R.drawable.logout,
                text = stringResource(id = R.string.leave_group),
                onItemClick = {
                    showLeaveGroupDialog = true
                }
            )
        }
        if (showLeaveGroupDialog) {
            AlertDialogScreen(
                title = stringResource(id = R.string.leave_group),
                onDismiss = {
                    showLeaveGroupDialog = false
                }
            ) {
                Text(
                    text = stringResource(id = R.string.leave_group_remind),
                    color = LocalColor.current.text.default_100,
                    fontSize = 17.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = stringResource(id = R.string.confirm),
                    borderColor = LocalColor.current.component.other,
                    textColor = LocalColor.current.specialColor.hintRed,
                    shape = RoundedCornerShape(4.dp),
                    onClick = {
                        showLeaveGroupDialog = false
                        onLeaveGroup()
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
                BorderButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    text = stringResource(id = R.string.cancel),
                    borderColor = LocalColor.current.component.other,
                    textColor = LocalColor.current.text.default_100,
                    shape = RoundedCornerShape(4.dp),
                    onClick = {
                        showLeaveGroupDialog = false
                        Unit
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun GroupAboutScreenPreview() {
    FanciTheme {
        GroupAboutScreen(
            modifier = Modifier.fillMaxWidth(),
            onInviteClick = {},
            onLeaveGroup = {}
        )
    }
}