package com.cmoney.fanci.ui.screens.group.setting

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
import com.cmoney.fanci.R
import com.cmoney.fanci.destinations.BanListScreenDestination
import com.cmoney.fanci.ui.screens.shared.SettingItemScreen
import com.cmoney.fanci.ui.theme.FanciTheme
import com.cmoney.fanci.ui.theme.LocalColor
import com.cmoney.fanciapi.fanci.model.Group
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

/**
 * 秩序管理
 */
@Composable
fun GroupRuleManageScreen(
    modifier: Modifier = Modifier,
    group: Group,
    navController: DestinationsNavigator
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(start = 25.dp, bottom = 9.dp),
            text = "秩序管理", fontSize = 14.sp, color = LocalColor.current.text.default_100
        )

        SettingItemScreen(
            iconRes = R.drawable.report_apply,
            text = "檢舉審核",
            onItemClick = {}
        ) {
            Text(text = "121", fontSize = 17.sp, color = LocalColor.current.text.default_100)
        }

        Spacer(modifier = Modifier.height(1.dp))

        SettingItemScreen(
            iconRes = R.drawable.ban,
            text = "禁言列表",
            onItemClick = {
                navController.navigate(
                    BanListScreenDestination(
                        group = group
                    )
                )
            }
        )
        Spacer(modifier = Modifier.height(1.dp))

//        SettingItemScreen(
//            iconRes = R.drawable.block_usr,
//            text = "黑名單列表",
//            onItemClick= {}
//        )
    }
}

@Preview(showBackground = true)
@Composable
fun GroupRuleManageScreenPreview() {
    FanciTheme {
        GroupRuleManageScreen(group = Group(), navController = EmptyDestinationsNavigator)
    }
}