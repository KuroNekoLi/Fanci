package com.cmoney.kolfanci.ui.screens.group.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmoney.kolfanci.R
import com.cmoney.kolfanci.ui.screens.shared.setting.SettingItemScreen
import com.cmoney.kolfanci.ui.theme.FanciTheme
import com.cmoney.kolfanci.ui.theme.LocalColor

/**
 * 關於社團
 */
@Composable
fun GroupAboutScreen(
    modifier: Modifier = Modifier,
    onInviteClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
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
        }
    }
}

@Preview
@Composable
private fun GroupAboutScreenPreview() {
    FanciTheme {
        GroupAboutScreen(
            modifier = Modifier.fillMaxWidth(),
            onInviteClick = {}
        )
    }
}